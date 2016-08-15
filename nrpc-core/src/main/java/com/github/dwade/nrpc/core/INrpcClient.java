package com.github.dwade.nrpc.core;

import java.util.concurrent.CountDownLatch;

public interface INrpcClient {
	
	public void start(InvokeContext context, CountDownLatch latch) throws Exception;
	
}
