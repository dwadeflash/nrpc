package com.github.dwade.ndubbo.core;

import java.util.concurrent.CountDownLatch;

public interface INpcClient {
	
	public void start(InvokeContext context, CountDownLatch latch) throws Exception;
	
}
