package com.github.dwade.nrpc.core.register;

/**
 * 服务注册接口
 * @author mengwei
 *
 */
public interface IServiceRegistator {
	
	public void registerService(Class<?> interfaceClass, int port) throws Exception;

}
