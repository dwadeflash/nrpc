package com.github.dwade.ndubbo.core.service;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

public class ProxyService<T> implements FactoryBean<T>, InitializingBean{
	
	private String interfaceName;
	
	private Class<?> interfaceClass;

	@SuppressWarnings("unchecked")
	@Override
	public T getObject() throws Exception {
		return (T) interfaceClass.newInstance();
	}

	@Override
	public Class<?> getObjectType() {
		return interfaceClass;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		interfaceClass = Class.forName(interfaceName);
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

}
