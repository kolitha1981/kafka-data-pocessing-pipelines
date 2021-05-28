package com.productmanagement.connector.service;

import java.util.HashMap;
import java.util.Map;

public class ServiceFactoryImpl implements ServiceFactory {

	private Map<Class<?>, Object> services = new HashMap<>();

	@Override
	public <T> T getService(Class<T> serviceClazz) {
		return (T) services.get(serviceClazz);
	}

	public void registerService(Object serviceObj) {
		if (!services.containsKey(serviceObj.getClass())) {
			services.put(serviceObj.getClass(), serviceObj);
		}
	}

}
