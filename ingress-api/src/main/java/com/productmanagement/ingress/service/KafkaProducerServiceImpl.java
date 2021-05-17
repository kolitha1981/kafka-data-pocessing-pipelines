package com.productmanagement.ingress.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.avro.generic.IndexedRecord;
import org.apache.avro.specific.SpecificRecordBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.productmanagement.ingress.model.Product;
import com.productmanagement.ingress.model.PublishingStatus;

@Service
public class KafkaProducerServiceImpl implements KafkaProducerService {

	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducerServiceImpl.class);
	@Autowired
	private KafkaTemplate<Long, GenericRecord> kafkaTemplate;
	@Value("${kafka.product.topic.name}")
	private String topicName;
	@Value("${kafka.product.error.topic.name}")
	private String errorTopicName;
	@Value("${kafka.product.topic.numberofpartitions}")
	private int numberOfpartitions;
	private ObjectMapper objectMapper = new ObjectMapper();
	//The avro schema for the product
	private static final String PRODUCT_AVRO_SCHEMA = "{\"type\":\"record\","
			+"\"name\":\"ProductLog\",\"namespace\":\"productlogs\","
			+"\"fields\":["
			+ "{\"name\":\"productId\",\"type\" : \"long\", \"default\" : -1},"
			+ "{\"name\":\"description\",\"type\" : \"string\", \"default\" : \"NONE\"},"
			+ "{\"name\":\"productName\",\"type\" : \"string\", \"default\" : \"NONE\"},"
			+ "{\"name\":\"price\",\"type\" : \"double\", \"default\" : 0.0}"
			+ "]}";

	@Override
	public Map<Long, PublishingStatus> pushProductsToTopic(final List<Product> proucts) {
		LOGGER.info("@@@@@Starting method pushProductToTopic() of : " + this.getClass().getName());
		final List<String> productIds = proucts.stream().map(product -> {
			return String.valueOf(product.getProductId());
		}).collect(Collectors.toList());
		final Map<Long, PublishingStatus> publishingStatuses = new HashMap<>();
		LOGGER.info("@@@@@ Pushing the following ids to kafka =====>:" + String.join(",", productIds));
		proucts.forEach((product) -> {
			try {
				final Long partition = product.getProductId() % numberOfpartitions;
				LOGGER.info("@@@@@ Partition :" + partition + " for product id :" + product.getProductId());
				Schema.Parser parser = new Schema.Parser();
				final Schema productAvroSchema = parser.parse(PRODUCT_AVRO_SCHEMA);
				final GenericRecord genericRecord = new GenericRecordBuilder(productAvroSchema).build();
				genericRecord.put("productId", product.getProductId());
				genericRecord.put("description", product.getDescription());
				genericRecord.put("productName", product.getProductName());
				genericRecord.put("price", product.getPrice());
				final ListenableFuture<SendResult<Long, GenericRecord>> future = kafkaTemplate.send(topicName,
						partition.intValue(), System.currentTimeMillis(), product.getProductId(), genericRecord);
				future.addCallback(new ListenableFutureCallback<SendResult<Long, GenericRecord>>() {
					@Override
					public void onSuccess(SendResult<Long, GenericRecord> result) {
						LOGGER.info("@@@@@ Sent message=[" + result.getProducerRecord().value() + "] " + "with offset=["
								+ result.getRecordMetadata().offset() + "]");
					}

					@Override
					public void onFailure(Throwable ex) {
						String errorMessage = "@@@@@ Unable to send message=[" + product + "] due to : "
								+ ex.getMessage();
						LOGGER.info(errorMessage);
						// Just send the error message to the error topic.
						kafkaTemplate.send(errorTopicName, genericRecord);
					}
				});
				publishingStatuses.put(product.getProductId(), PublishingStatus.STATUS_PENDING);
			} catch (Exception e) {
				LOGGER.info("@@@@@@Error when processing student message:" + e);
				throw new RuntimeException(e);
			}
		});
		return publishingStatuses;
	}

}
