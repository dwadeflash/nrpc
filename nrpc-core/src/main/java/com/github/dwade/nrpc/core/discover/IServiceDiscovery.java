package com.github.dwade.nrpc.core.discover;

import java.util.Collection;
import java.util.Map;

import org.apache.curator.x.discovery.ServiceInstance;

/**
 * 服务发现接口
 * @author mengwei
 *
 */
public interface IServiceDiscovery {
	
	public ServiceInstance<?> discoverService(Class<?> interfaceClass) throws Exception;
	
	@SuppressWarnings("rawtypes")
	public Map<String, Collection<ServiceInstance>> getServices();
	
}
