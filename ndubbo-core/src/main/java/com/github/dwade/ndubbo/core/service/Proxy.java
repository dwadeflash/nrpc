package com.github.dwade.ndubbo.core.service;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class Proxy<T> implements MethodInterceptor, FactoryBean<T>, InitializingBean {
	
	private final static Logger logger = LoggerFactory.getLogger(Proxy.class);
	
	private String interfaceName;
	
	private Class<?> interfaceClass;

	@Override
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		logger.debug("[intercept] invoked before");
		String result = (String) args[0] + "...";
		logger.debug(result);
		logger.debug("[intercept] invoked after");
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getObject() throws Exception {
		logger.debug("create instance...");
		Enhancer enhancer = new Enhancer();
		enhancer.setInterfaces(new Class[] { interfaceClass });
		enhancer.setSuperclass(Proxy.class);
		enhancer.setCallback(this);
		return (T) enhancer.create();
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
