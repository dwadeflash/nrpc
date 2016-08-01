package com.github.dwade.ndubbo.core.service;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.UriSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.github.dwade.ndubbo.core.server.NpcServer;

public class ProxyService<T> implements FactoryBean<T>, InitializingBean, ApplicationContextAware {
	
	private final static Logger logger = LoggerFactory.getLogger(ProxyService.class);
	
	private static ApplicationContext applicationContext;
	
	private String interfaceName;
	
	private String ref;
	
	private Class<?> interfaceClass;
	
	private T target;
	
	@Value("${zookeeper.address}")
	private String zookeeperAddress;
	
	@Value("${zookeeper.port}")
	private int zookeeperPort;
	
	List<String> addressList = new ArrayList<String>();

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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void afterPropertiesSet() throws Exception {
		interfaceClass = Class.forName(interfaceName);
		target = (T) applicationContext.getBean(ref);
		NpcServer server = applicationContext.getBean(NpcServer.class);
		int port = server.getPort();
		CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(getZkConnStr(),
				new RetryNTimes(5, 1000));
		curatorFramework.start();
		ServiceInstance serviceInstance = ServiceInstance.builder().uriSpec(new UriSpec("{scheme}://{address}:{port}"))
				.address(InetAddress.getLocalHost().getHostAddress()).port(port).name(interfaceName).build();
		ServiceDiscovery<?> sd = ServiceDiscoveryBuilder.builder(interfaceClass).basePath(interfaceName)
				.client(curatorFramework).build();
		sd.registerService(serviceInstance);
		sd.start();
		logger.debug("registered service {} to zookeeper", interfaceName);
	}

	private String getZkConnStr() {
		return zookeeperAddress + ":" + zookeeperPort;
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

}
