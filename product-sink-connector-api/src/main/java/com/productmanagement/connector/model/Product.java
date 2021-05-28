package com.productmanagement.connector.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class Product {
	
	private Long productId;
	private String description;
	private String productName;
	private Double price;	
	
}
