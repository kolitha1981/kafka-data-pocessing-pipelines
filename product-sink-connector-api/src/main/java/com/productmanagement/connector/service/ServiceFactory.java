package com.productmanagement.connector.service;

public interface ServiceFactory {
	
	<T extends Object> T getService(Class<T> serviceClazz);
	
	void registerService(Object serviceObj);
	
	void deregisterServices();

}
