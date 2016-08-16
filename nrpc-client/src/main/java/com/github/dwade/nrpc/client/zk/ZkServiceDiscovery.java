package com.github.dwade.nrpc.client.zk;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.omg.CORBA.ServiceDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import com.github.dwade.nrpc.core.discover.IServiceDiscovery;

/**
 * zookeeper服务发现实现
 * @author mengwei
 * 
 */
@SuppressWarnings("rawtypes")
public class ZkServiceDiscovery implements IServiceDiscovery, InitializingBean, DisposableBean {

    private final static Logger logger = LoggerFactory.getLogger(ZkServiceDiscovery.class);
	
	@Value("${zookeeper.address}")
	private String zookeeperAddress;
	
	@Value("${zookeeper.port}")
	private int zookeeperPort;
	
	private CuratorFramework client;
	
	private ServiceDiscovery serviceDiscovery;
	
    private Map<String, Collection<ServiceInstance>> serviceList =
            new ConcurrentHashMap<String, Collection<ServiceInstance>>();
    
    private Timer timer = new Timer();
    
    private ReadWriteLock lock = new ReentrantReadWriteLock();

	@Override
	public ServiceInstance<?> discoverService(Class<?> interfaceClass) throws Exception {
        Collection<ServiceInstance> services = getServices().get(interfaceClass.getName());
		if (services != null && services.size() > 0) {
			int index = new Random().nextInt(services.size());
			return (ServiceInstance) services.toArray()[index];
		}
		return null;
	}
	
	@Override
	public Map<String, Collection<ServiceInstance>> getServices() {
		Lock readLock = lock.readLock();
		try {
			readLock.lock();
			return serviceList;
		} finally {
			readLock.unlock();
		}
	}
	
	private String getZkConnStr() {
		return zookeeperAddress + ":" + zookeeperPort;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		client = CuratorFrameworkFactory.newClient(getZkConnStr(), new RetryNTimes(5, 1000));
		client.start();
		serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceDetail.class).client(client)
				.basePath("/nrpc/examples").build();
		serviceDiscovery.start();
		TimerTask task = new DiscoverServiceTask();
		task.run(); // run it in the main thread to cache all the avaliable services before application started
		timer.schedule(task, 500L, 1000L);
    }

	@Override
	public void destroy() throws Exception {
		timer.cancel();
		serviceDiscovery.close();
		client.close();
	}
	
	private class DiscoverServiceTask extends TimerTask {

		@SuppressWarnings("unchecked")
		@Override
		public void run() {
			logger.debug("discovering services...");
			Lock writeLock = lock.writeLock();
			try {
				writeLock.lock();
				serviceList.clear();
				Iterator it = serviceDiscovery.queryForNames().iterator();
				while (it.hasNext()) {
					String serviceName = (String) it.next();
					Collection<ServiceInstance> services = serviceDiscovery.queryForInstances(serviceName);
					serviceList.put(serviceName, services);
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			} finally {
				writeLock.unlock();
			}
		}
	}

}
