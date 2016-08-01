package com.github.dwade.ndubbo.core.service;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Random;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.omg.CORBA.ServiceDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import com.github.dwade.ndubbo.core.InvokeInfo;
import com.github.dwade.ndubbo.core.WrapppedResult;
import com.github.dwade.ndubbo.core.client.NpcClient;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class ProxyInterface<T> implements MethodInterceptor, FactoryBean<T>, InitializingBean {
	
	private final static Logger logger = LoggerFactory.getLogger(ProxyInterface.class);
	
	private String interfaceName;
	
	private transient Class<?> interfaceClass;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		InvokeInfo info = new InvokeInfo(interfaceName, method.getName(), args);
		WrapppedResult result = new WrapppedResult();
		
		CuratorFramework client = CuratorFrameworkFactory.newClient("localhost:2181",
				new RetryNTimes(5, 1000));
		client.start();
		
		ServiceDiscovery serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceDetail.class).client(client)
				.basePath(interfaceName).build();
		serviceDiscovery.start();
	      
	    //根据名称获取服务  
	    Collection<ServiceInstance> services = serviceDiscovery.queryForInstances(IHelloWorld.class.getName());  
	    if(services != null && services.size()>0) {
	    	int index = new Random().nextInt(services.size());
	    	ServiceInstance service = (ServiceInstance) services.toArray()[index];
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
