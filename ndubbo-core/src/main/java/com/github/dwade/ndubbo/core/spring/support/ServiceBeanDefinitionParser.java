package com.github.dwade.ndubbo.core.spring.support;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import com.github.dwade.ndubbo.core.service.ProxyService;

public class ServiceBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {
	
	protected Class<?> getBeanClass(Element element) {  
        return ProxyService.class;  
    }
	
	public void doParse(Element element, BeanDefinitionBuilder bean) {
		String interfaceName = element.getAttribute("interface"); 
		if(StringUtils.hasText(interfaceName)) {
			bean.addPropertyValue("interfaceName", interfaceName);
		}
    }

}
