package com.productmanagement.ingress.exception;

public class ConsumerProcessingException extends RuntimeException {

	public ConsumerProcessingException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConsumerProcessingException(String message) {
		super(message);
	}

}
