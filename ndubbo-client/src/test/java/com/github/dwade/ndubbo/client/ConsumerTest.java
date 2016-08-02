package com.github.dwade.ndubbo.client;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.dwade.ndubbo.core.service.IHelloWorld;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:nrpc-consumer.xml")
public class ConsumerTest {

	@Autowired
	@Qualifier("helloClient")
	private IHelloWorld helloClient;

	@Test
	public void test() throws Exception {
		CountDownLatch latch = new CountDownLatch(1000);
		ExecutorService excutor = Executors.newFixedThreadPool(100);
		long begin = System.currentTimeMillis();
		for(int i=0;i<1000;i++) {
			excutor.submit(new Runnable() {
				
				@Override
				public void run() {
					helloClient.sayHello("test");
					latch.countDown();
				}
			});
		}
		latch.await();
		long end = System.currentTimeMillis();
		System.out.println(end - begin);
		excutor.shutdown();
	}
	
}
