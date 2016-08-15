package com.github.dwade.nrpc.core.spring.support;

import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.github.dwade.nrpc.core.service.ProxyService;

public class ServiceBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {
	
	protected Class<?> getBeanClass(Element element) {  
        return ProxyService.class;  
    }
	
	public void doParse(Element element, BeanDefinitionBuilder bean) {
		String interfaceName = element.getAttribute("interface"); 
		if(StringUtils.hasText(interfaceName)) {
			bean.addPropertyValue("interfaceName", interfaceName);
		}
		parseProperties(element.getChildNodes(), bean);
    }

	private static void parseProperties(NodeList nodeList, BeanDefinitionBuilder bean) {
        if (nodeList != null && nodeList.getLength() > 0) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node instanceof Element) {
                    if ("property".equals(node.getNodeName())
                            || "property".equals(node.getLocalName())) {
                        String name = ((Element) node).getAttribute("name");
                        if (name != null && name.length() > 0) {
                            String value = ((Element) node).getAttribute("value");
                            String ref = ((Element) node).getAttribute("ref");
                            if (value != null && value.length() > 0) {
                                bean.addPropertyValue(name, value);
                            } else if (ref != null && ref.length() > 0) {
                                bean.addPropertyValue(name, new RuntimeBeanReference(ref));
                            } else {
                                throw new UnsupportedOperationException("Unsupported <property name=\"" + name + "\"> sub tag, Only supported <property name=\"" + name + "\" ref=\"...\" /> or <property name=\"" + name + "\" value=\"...\" />");
                            }
                        }
                    }
                }
            }
        }
    }
	
}
