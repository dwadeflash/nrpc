package com.github.dwade.nrpc.server.zk;

import java.net.InetAddress;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.UriSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.github.dwade.nrpc.core.register.IServiceRegistator;

public class ZkServiceRegistrator implements IServiceRegistator {
	
	@Value("${zookeeper.address}")
	private String zookeeperAddress;
	
	@Value("${zookeeper.port}")
	private int zookeeperPort;
	
	private final static Logger logger = LoggerFactory.getLogger(ZkServiceRegistrator.class);

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void registerService(Class<?> interfaceClass, int port) throws Exception {
		CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(getZkConnStr(),
				new RetryNTimes(5, 1000));
		curatorFramework.start();
		String interfaceName = interfaceClass.getName();
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

}
