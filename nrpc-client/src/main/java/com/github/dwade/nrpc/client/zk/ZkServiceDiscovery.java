package com.github.dwade.nrpc.client.zk;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.omg.CORBA.ServiceDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final static Logger logger = LoggerFactory.getLogger(ZkServiceDiscovery.class);
	
	@Value("${zookeeper.address}")
	private String zookeeperAddress;
	
	@Value("${zookeeper.port}")
	private int zookeeperPort;
	
	private CuratorFramework client;
	
    private Map<String, Collection<ServiceInstance>> serviceList =
            new ConcurrentHashMap<String, Collection<ServiceInstance>>();

	@Override
	public ServiceInstance<?> discoverService(Class<?> interfaceClass) throws Exception {
        Collection<ServiceInstance> services = serviceList.get(interfaceClass.getName());
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
        discoverServices();
    }

    @SuppressWarnings("unchecked")
    private void discoverServices() throws Exception {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                serviceList.clear();
                ServiceDiscovery serviceDiscovery =
                        ServiceDiscoveryBuilder.builder(ServiceDetail.class).client(client)
                                .basePath("/nrpc/examples").build();
                try {
                    serviceDiscovery.start();
                    Iterator it = serviceDiscovery.queryForNames().iterator();
                    while (it.hasNext()) {
                        String serviceName = (String) it.next();
                        Collection<ServiceInstance> services =
                                serviceDiscovery.queryForInstances(serviceName);
                        serviceList.put(serviceName, services);
                    }
                    serviceDiscovery.close();
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }, 0);
	}

}
