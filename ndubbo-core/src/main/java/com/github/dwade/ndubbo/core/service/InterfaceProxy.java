package com.github.dwade.ndubbo.core.service;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class InterfaceProxy implements MethodInterceptor{
	
	private final static Logger logger = LoggerFactory.getLogger(InterfaceProxy.class);
	
	@Override
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		logger.debug(args.toString());
		return null;
	}
	
	public IHelloWorld createInstance() {
		Enhancer enhancer = new Enhancer();
		enhancer.setInterfaces(new Class[] { IHelloWorld.class });
		enhancer.setSuperclass(InterfaceProxy.class);
		enhancer.setCallback(this);
		return (IHelloWorld) enhancer.create();
	}

	public static void main(String[] args) {
		InterfaceProxy proxy = new InterfaceProxy();
		IHelloWorld helloWorld = proxy.createInstance();
		helloWorld.sayHello("hello");
	}

}
