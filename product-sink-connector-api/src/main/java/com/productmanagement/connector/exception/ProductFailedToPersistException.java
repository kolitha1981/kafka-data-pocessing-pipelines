package com.productmanagement.connector.exception;

public class ProductFailedToPersistException extends RuntimeException{

	public ProductFailedToPersistException(String message, Throwable cause) {
		super(message, cause);
	}

	public ProductFailedToPersistException(String message) {
		super(message);
	}

}
