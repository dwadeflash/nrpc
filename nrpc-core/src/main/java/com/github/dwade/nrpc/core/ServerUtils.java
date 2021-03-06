package com.github.dwade.nrpc.core;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ServerUtils implements ApplicationContextAware {
	
	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ServerUtils.applicationContext = applicationContext;
	}
	
	public static int getServicePort() {
		INrpcServer server = applicationContext.getBean(INrpcServer.class);
		return server.getPort();
	}

}
