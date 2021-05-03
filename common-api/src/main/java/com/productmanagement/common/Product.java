package com.productmanagement.common;

import java.util.Date;


public class Product {
	
	/** The identification of the product.*/
	private Long productId;
	/** The data that the product was added to the inventory.*/
	private Date addedToInventoryOn;
	/** The location of the store this product was picked.*/
	private Long storeId;
	/** The description of the product.*/
	private String description;
	/** The name of the product.*/
	private String productName;
	/** The type of the product.*/
	private ProductType productType;
	/** The discount type of the product.*/
	private DiscountType discountType;
	/** The original price of the product .*/
	private Double originalPrice;
	/** The purchased price of the product .*/
	private Double purchasedPrice;
	/** The customer id that purchased the product .*/
	private Long purchasedCustomerId;
	/** The source of purchasing .*/
	private PurchasingSource purchasingSource;
	/** The brand of the product .*/
	private String brand;
	
	
	
	
	
	

}
