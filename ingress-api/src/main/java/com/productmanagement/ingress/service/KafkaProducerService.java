package com.productmanagement.ingress.service;

import java.util.List;
import java.util.Map;

import com.productmanagement.common.Product;
import com.productmanagement.ingress.model.PublishingStatus;

public interface KafkaProducerService {
	
	Map<Long, PublishingStatus> pushProductsToTopic(final List<Product> prouct);

}
