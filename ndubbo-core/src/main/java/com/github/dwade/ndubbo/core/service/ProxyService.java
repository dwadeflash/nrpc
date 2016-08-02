package com.github.dwade.ndubbo.core.service;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.github.dwade.ndubbo.core.INpcServer;
import com.github.dwade.ndubbo.core.register.IServiceRegistator;

public class ProxyService<T> implements FactoryBean<T>, InitializingBean, ApplicationContextAware {
	
	private ApplicationContext applicationContext;
	
	private String interfaceName;
	
	private Class<?> interfaceClass;
	
	private T target;
	
	private IServiceRegistator serviceRegistrator;
	
	@Override
	public T getObject() throws Exception {
		return (T) target;
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
		INpcServer server = applicationContext.getBean(INpcServer.class);
		serviceRegistrator.registerService(interfaceClass, server.getPort());
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}
	
	public T getTarget() {
		return target;
	}

	public void setTarget(T target) {
		this.target = target;
	}

	public IServiceRegistator getServiceRegistrator() {
		return serviceRegistrator;
	}

	public void setServiceRegistrator(IServiceRegistator serviceRegistrator) {
		this.serviceRegistrator = serviceRegistrator;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
