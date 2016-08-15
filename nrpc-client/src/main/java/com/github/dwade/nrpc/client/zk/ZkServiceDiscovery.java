package com.github.dwade.nrpc.client.zk;

import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.omg.CORBA.ServiceDetail;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import com.github.dwade.nrpc.core.discover.IServiceDiscovery;

/**
 * zookeeper服务发现实现
 * @author mengwei
 * 
 */
@SuppressWarnings("rawtypes")
public class ZkServiceDiscovery implements IServiceDiscovery, InitializingBean {
	
	@Value("${zookeeper.address}")
	private String zookeeperAddress;
	
	@Value("${zookeeper.port}")
	private int zookeeperPort;
	
	private CuratorFramework client;
	
	private Map<Class, Collection<ServiceInstance>> serviceList = new ConcurrentHashMap<Class, Collection<ServiceInstance>>();

	@SuppressWarnings({ "unchecked" })
	@Override
	public ServiceInstance<?> discoverService(Class<?> interfaceClass) throws Exception {
		Collection<ServiceInstance> services = serviceList.get(interfaceClass);
		if(services == null || services.size() == 0) {
			String interfaceName = interfaceClass.getName();
			ServiceDiscovery serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceDetail.class).client(client)
					.basePath(interfaceName).build();
			serviceDiscovery.start();
			services = serviceDiscovery.queryForInstances(interfaceClass.getName());
			serviceList.put(interfaceClass, services);
		}
		if (services != null && services.size() > 0) {
			int index = new Random().nextInt(services.size());
			return (ServiceInstance) services.toArray()[index];
		}
		return null;
	}
	
	private String getZkConnStr() {
		return zookeeperAddress + ":" + zookeeperPort;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		client = CuratorFrameworkFactory.newClient(getZkConnStr(), new RetryNTimes(5, 1000));
		client.start();
	}

}
