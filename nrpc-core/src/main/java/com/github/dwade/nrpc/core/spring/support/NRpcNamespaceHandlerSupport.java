package com.github.dwade.nrpc.core.spring.support;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class NRpcNamespaceHandlerSupport extends NamespaceHandlerSupport {

	@Override
	public void init() {
		registerBeanDefinitionParser("zookeeper", new ZookeeperBeanDefinitionParser());
		registerBeanDefinitionParser("interface", new InterfaceBeanDefinitionParser());
		registerBeanDefinitionParser("service", new ServiceBeanDefinitionParser());
	}

}
