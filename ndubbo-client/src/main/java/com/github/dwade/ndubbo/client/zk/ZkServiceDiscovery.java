package com.github.dwade.ndubbo.client.zk;

import java.util.Collection;
import java.util.Random;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.omg.CORBA.ServiceDetail;
import org.springframework.beans.factory.annotation.Value;

import com.github.dwade.ndubbo.core.discover.IServiceDiscovery;
import com.github.dwade.ndubbo.core.service.IHelloWorld;

public class ZkServiceDiscovery implements IServiceDiscovery {
	
	@Value("${zookeeper.address}")
	private String zookeeperAddress;
	
	@Value("${zookeeper.port}")
	private int zookeeperPort;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public ServiceInstance<?> discoverService(Class<?> interfaceClass) throws Exception {
		CuratorFramework client = CuratorFrameworkFactory.newClient(getZkConnStr(), new RetryNTimes(5, 1000));
		client.start();

		String interfaceName = interfaceClass.getName();
		ServiceDiscovery serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceDetail.class).client(client)
				.basePath(interfaceName).build();
		serviceDiscovery.start();

		Collection<ServiceInstance> services = serviceDiscovery.queryForInstances(IHelloWorld.class.getName());
		if (services != null && services.size() > 0) {
			int index = new Random().nextInt(services.size());
			return (ServiceInstance) services.toArray()[index];
		}
		return null;
	}

	private String getZkConnStr() {
		return zookeeperAddress + ":" + zookeeperPort;
	}

}
