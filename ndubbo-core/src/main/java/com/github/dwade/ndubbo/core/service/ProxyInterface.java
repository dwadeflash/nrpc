package com.github.dwade.ndubbo.core.service;

import java.lang.reflect.Method;

import org.apache.curator.x.discovery.ServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import com.github.dwade.ndubbo.core.InvokeInfo;
import com.github.dwade.ndubbo.core.WrapppedResult;
import com.github.dwade.ndubbo.core.discover.IServiceDiscovery;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class ProxyInterface<T> implements MethodInterceptor, FactoryBean<T>, InitializingBean {
	
	private final static Logger logger = LoggerFactory.getLogger(ProxyInterface.class);
	
	private String interfaceName;
	
	private transient Class<?> interfaceClass;
	
	private IServiceDiscovery serverDiscovery;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		InvokeInfo info = new InvokeInfo(interfaceName, method.getName(), args);
		WrapppedResult result = new WrapppedResult();
		
		ServiceInstance service = serverDiscovery.discoverService(interfaceClass);
		
	    if(service != null) {
	    	new NpcClient(service.getAddress(), service.getPort(), info, result).start();
			return result.getResult();
	    }
	    throw new Exception("there is no applicable service!");
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getObject() throws Exception {
		logger.debug("create instance...");
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
