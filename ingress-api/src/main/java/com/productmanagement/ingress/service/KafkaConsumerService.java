package com.productmanagement.ingress.service;

import java.util.concurrent.CountDownLatch;

import org.apache.avro.generic.GenericRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.productmanagement.ingress.exception.ConsumerProcessingException;
import com.productmanagement.ingress.model.Product;

@Component
public class KafkaConsumerService {

	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerService.class);

	private CountDownLatch partitionLatch = new CountDownLatch(2);

	@KafkaListener(topicPartitions = @TopicPartition(topic = "im_product_logs", partitions = { "0", "1",
			"2" }), containerFactory = "kafkaListenerContainerFactory")
	public void listenToTopic(@Payload GenericRecord genericRecord,
			@Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
		try {
			final Product consumedProduct =  new Product();
			consumedProduct.setProductId(Long.valueOf(genericRecord.get("productId").toString()));
			consumedProduct.setDescription(genericRecord.get("description").toString());
			consumedProduct.setProductName(genericRecord.get("productName").toString());
			consumedProduct.setPrice(Double.valueOf(genericRecord.get("price").toString()));
			LOGGER.info("@@@@ ConsumedProduct :"+ consumedProduct);
			String notoficationMessage = "########Received product json :"+genericRecord 
					+" with id: " + consumedProduct.getProductId()
					+ "from partition :" + partition;
			LOGGER.info(notoficationMessage);
			this.partitionLatch.countDown();
		} catch (Exception e) {
			String errorMessage = "Error in processing consumer record";
			LOGGER.error(errorMessage);
			throw new ConsumerProcessingException("Error in processing consumer record.", e);
		}
	}

}
