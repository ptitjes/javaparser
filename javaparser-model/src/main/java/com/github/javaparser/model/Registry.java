/*
 * Copyright (C) 2007-2010 Júlio Vilmar Gesser.
 * Copyright (C) 2011, 2013-2015 The JavaParser Team.
 *
 * This file is part of JavaParser.
 *
 * JavaParser is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JavaParser is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JavaParser.  If not, see <http://www.gnu.org/licenses/>.
 */

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
