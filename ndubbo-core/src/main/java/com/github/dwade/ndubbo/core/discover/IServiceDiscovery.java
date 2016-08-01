package com.github.dwade.ndubbo.core.discover;

import org.apache.curator.x.discovery.ServiceInstance;

public interface IServiceDiscovery {
	
	public ServiceInstance<?> discoverService(Class<?> interfaceClass) throws Exception;
	
}
