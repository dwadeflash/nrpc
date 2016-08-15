package com.github.dwade.nrpc.core.spring.support;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import com.github.dwade.nrpc.core.zk.ZookeeperServer;

public class ZookeeperBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {
	
	protected Class<?> getBeanClass(Element element) {
        return ZookeeperServer.class;  
    }
	
	@Override
	public void doParse(Element element, BeanDefinitionBuilder bean) {
		String interfaceName = element.getAttribute("address");
		if(StringUtils.hasText(interfaceName)) {
			bean.addPropertyValue("address", interfaceName);
		}
		String ref = element.getAttribute("port");
		if(StringUtils.hasText(ref)) {
			bean.addPropertyValue("port", ref);
		}
	}
	
	protected boolean shouldGenerateIdAsFallback() {
		return true;
	}

}
