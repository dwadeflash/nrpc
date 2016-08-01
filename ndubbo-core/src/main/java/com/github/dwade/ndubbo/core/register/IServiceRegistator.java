package com.github.dwade.ndubbo.core.register;

public interface IServiceRegistator {
	
	public void registerService(Class<?> interfaceClass, int port) throws Exception;

}
