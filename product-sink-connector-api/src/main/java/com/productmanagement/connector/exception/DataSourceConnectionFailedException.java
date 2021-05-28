package com.productmanagement.connector.exception;

public class DataSourceConnectionFailedException extends RuntimeException {

	public DataSourceConnectionFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public DataSourceConnectionFailedException(String message) {
		super(message);
	}

}
