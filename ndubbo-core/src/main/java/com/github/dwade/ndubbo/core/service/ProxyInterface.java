package com.github.dwade.ndubbo.core.service;

import java.lang.reflect.Method;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import com.github.dwade.ndubbo.core.InvokeInfo;
import com.github.dwade.ndubbo.core.WrapppedResult;
import com.github.dwade.ndubbo.core.client.NpcClient;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class ProxyInterface<T> implements MethodInterceptor, FactoryBean<T>, InitializingBean {
	
	private String interfaceName;
	
	private transient Class<?> interfaceClass;

	@Override
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		InvokeInfo info = new InvokeInfo(interfaceName, method.getName(), args);
		WrapppedResult result = new WrapppedResult();
		new NpcClient("localhost", 8090, info, result).start();
		return result.getResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getObject() throws Exception {
		System.out.println("create instance...");
		Enhancer enhancer = new Enhancer();
		enhancer.setInterfaces(new Class[] { interfaceClass });
		enhancer.setSuperclass(ProxyInterface.class);
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
