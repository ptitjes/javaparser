package com.github.javaparser.model;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Didier Villevalois
 */
public class Registry {

	public interface Participant {

		public void configure(Registry registry);
	}

	private Map<Class<?>, Object> services = new HashMap<Class<?>, Object>();

	public void register(Object service) {
		register(service.getClass(), service);
	}

	public void register(Class<?> serviceClass, Object service) {
		services.put(serviceClass, service);
	}

	@SuppressWarnings("unchecked")
	public <S> S get(Class<S> serviceClass) {
		Object service = services.get(serviceClass);
		if (service == null)
			throw new IllegalStateException("No analysis service with class " + serviceClass.getName());
		return (S) service;
	}

	public void configure() {
		for (Object service : services.values()) {
			if (service instanceof Participant) {
				((Participant) service).configure(this);
			}
		}
	}
}
