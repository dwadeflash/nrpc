package com.github.dwade.ndubbo.core.service;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.github.dwade.ndubbo.core.ServerUtils;
import com.github.dwade.ndubbo.core.register.IServiceRegistator;

public class ProxyService<T> implements FactoryBean<T>, InitializingBean, ApplicationContextAware {
	
	private static ApplicationContext applicationContext;
	
	private String interfaceName;
	
	private String ref;
	
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

	@SuppressWarnings({ "unchecked" })
	@Override
	public void afterPropertiesSet() throws Exception {
		interfaceClass = Class.forName(interfaceName);
		target = (T) applicationContext.getBean(ref);
		serviceRegistrator.registerService(interfaceClass, ServerUtils.getServicePort());
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ProxyService.applicationContext = applicationContext;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public IServiceRegistator getServiceRegistrator() {
		return serviceRegistrator;
	}

	public void setServiceRegistrator(IServiceRegistator serviceRegistrator) {
		this.serviceRegistrator = serviceRegistrator;
	}

}
