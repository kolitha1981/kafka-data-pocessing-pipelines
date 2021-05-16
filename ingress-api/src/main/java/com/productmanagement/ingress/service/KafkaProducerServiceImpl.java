package com.productmanagement.ingress.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import com.productmanagement.common.Product;
import com.productmanagement.ingress.model.PublishingStatus;

@Service
public class KafkaProducerServiceImpl implements KafkaProducerService {

	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducerServiceImpl.class);
	@Autowired
	private KafkaTemplate<Long, String> kafkaTemplate;
	@Value("${kafka.product.topic.name}")
	private String topicName;
	@Value("${kafka.product.error.topic.name}")
	private String errorTopicName;
	@Value("${kafka.product.topic.numberofpartitions}")
	private int numberOfpartitions;
	private ObjectMapper objectMapper = new ObjectMapper();

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
				final String productJson = objectMapper.writer().writeValueAsString(product);
				final ListenableFuture<SendResult<Long, String>> future = kafkaTemplate.send(topicName,
						partition.intValue(), System.currentTimeMillis(), product.getProductId(), productJson);
				future.addCallback(new ListenableFutureCallback<SendResult<Long, String>>() {
					@Override
					public void onSuccess(SendResult<Long, String> result) {
						LOGGER.info("@@@@@ Sent message=[" + productJson + "] " + "with offset=["
								+ result.getRecordMetadata().offset() + "]");
					}

					@Override
					public void onFailure(Throwable ex) {
						String errorMessage = "@@@@@ Unable to send message=[" + productJson + "] due to : "
								+ ex.getMessage();
						LOGGER.info(errorMessage);
						// Just send the error message to the error topic.
						kafkaTemplate.send(errorTopicName, productJson);
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
