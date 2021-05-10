package com.productmanagement.ingress.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.productmanagement.common.Product;
import com.productmanagement.ingress.model.PublishingStatus;
import com.productmanagement.ingress.service.KafkaProducerService;

@RestController
public class ProductIngestionController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductIngestionController.class);
	@Autowired
	private KafkaProducerService kafkaProducerService;

	@PostMapping(path = "/v1/injestions/product", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Map<Long, PublishingStatus>> ingestProduct(@RequestBody List<Product> products) {
		LOGGER.info("Starting the method ingestProduct() of :" + this.getClass().getName());
		final Map<Long, PublishingStatus> publishingStatuses = this.kafkaProducerService.pushProductsToTopic(products);
		return new ResponseEntity<>(publishingStatuses, HttpStatus.OK);
	}

}
