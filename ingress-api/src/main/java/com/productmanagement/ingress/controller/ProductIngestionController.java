package com.productmanagement.ingress.controller;

import java.util.ArrayList;
import java.util.Calendar;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.productmanagement.common.DiscountType;

import com.productmanagement.common.ProductType;
import com.productmanagement.common.PurchasingSource;
import com.productmanagement.ingress.model.Product;
import com.productmanagement.ingress.model.PublishingStatus;
import com.productmanagement.ingress.service.KafkaProducerService;

@RestController
public class ProductIngestionController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductIngestionController.class);
	@Autowired
	private KafkaProducerService kafkaProducerService;

	@PostMapping(path = "/v1/injestions/product", consumes = MediaType.APPLICATION_JSON_VALUE, 
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Map<Long, PublishingStatus>> ingestProduct(@RequestBody List<Product> products) {
		LOGGER.info("Starting the method ingestProduct() of :" + this.getClass().getName());
		final Map<Long, PublishingStatus> publishingStatuses = this.kafkaProducerService.pushProductsToTopic(products);
		return new ResponseEntity<>(publishingStatuses, HttpStatus.OK);
	}
	
	public static void main(String[] args) {
		final List<Product> products =  new ArrayList<>();
		for(int j =0; j < 9; j++) {
			final Product product =  new Product();
			product.setProductId(Long.valueOf(j+1));
			product.setDescription("Pair of nike shoes.");
			product.setProductName("Nike Air");
			product.setPrice(9.55);
			products.add(product);			
		}
		ObjectMapper objectMapper =  new ObjectMapper();
		try {
			System.out.println("@@@Json: "+ objectMapper.writeValueAsString(products) );
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

}
