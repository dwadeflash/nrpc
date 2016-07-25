package com.github.dwade.ndubbo.core.service;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class NRpcNamespaceHandlerSupport extends NamespaceHandlerSupport {

	@Override
	public void init() {
		registerBeanDefinitionParser("interface", new InterfaceBeanDefinitionParser());
		registerBeanDefinitionParser("service", new ServiceBeanDefinitionParser());
	}

}
