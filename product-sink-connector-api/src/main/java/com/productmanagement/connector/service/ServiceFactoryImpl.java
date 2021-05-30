package com.productmanagement.connector.service;

import java.util.HashMap;
import java.util.Map;

public class ServiceFactoryImpl implements ServiceFactory {

	private Map<Class<?>, Object> services = new HashMap<>();

	@Override
	public <T> T getService(Class<T> serviceClazz) {
		return (T) services.get(serviceClazz);
	}

	@Override
	public void registerService(Class<?> serviceInterface, Object serviceObj) {
		services.computeIfAbsent(serviceInterface, s -> {
			return serviceObj;
		});
	}

	@Override
	public void deregisterServices() {
		services.forEach((serviceKey, service) -> {
			serviceKey = null;
		});
	}

}
