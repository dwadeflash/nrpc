package com.github.dwade.nrpc.core.service;

import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;

import org.apache.curator.x.discovery.ServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import com.github.dwade.nrpc.core.INrpcClient;
import com.github.dwade.nrpc.core.InvokeContext;
import com.github.dwade.nrpc.core.InvokeInfo;
import com.github.dwade.nrpc.core.discover.IServiceDiscovery;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class ProxyInterface<T> implements MethodInterceptor, FactoryBean<T>, InitializingBean {
	
	private final static Logger logger = LoggerFactory.getLogger(ProxyInterface.class);
	
	private String interfaceName;
	
	private transient Class<?> interfaceClass;
	
	private IServiceDiscovery serviceDiscovery;
	
	private INrpcClient client;

	@SuppressWarnings({ "rawtypes" })
	@Override
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		InvokeInfo info = new InvokeInfo(interfaceName, method.getName(), args);
		
		ServiceInstance service = serviceDiscovery.discoverService(interfaceClass);
		
	    if(service != null) {
	    	InvokeContext context = new InvokeContext(service.getAddress(), service.getPort(), info);
	    	CountDownLatch latch = new CountDownLatch(1);
	    	try {
	    		client.start(context, latch);
	    	} catch (Exception e) {
	    		latch.countDown();
	    		throw e;
	    	}
	    	latch.await();
			return context.getResult();
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

	public void setClient(INrpcClient client) {
		this.client = client;
	}

	public void setServiceDiscovery(IServiceDiscovery serviceDiscovery) {
		this.serviceDiscovery = serviceDiscovery;
	}
	
}
