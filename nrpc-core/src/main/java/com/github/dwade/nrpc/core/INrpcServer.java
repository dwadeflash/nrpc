package com.github.dwade.nrpc.core;

public interface INrpcServer {
	
	public int getPort();
	
	public void start() throws Exception;

}
