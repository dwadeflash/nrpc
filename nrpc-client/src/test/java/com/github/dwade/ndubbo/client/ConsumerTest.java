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

import com.github.dwade.nrpc.core.service.IHelloWorld;
import com.github.dwade.nrpc.core.service.IWorldHello;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:nrpc-consumer.xml")
public class ConsumerTest {
	
    private final int COUNT = 5000;

	@Autowired
	@Qualifier("helloClient1")
	private IHelloWorld helloClient1;
	
	@Autowired
    @Qualifier("helloClient2")
    private IWorldHello helloClient2;

	@Test
	public void testMultiThread() throws Exception {
		ExecutorService executor = Executors.newFixedThreadPool(COUNT/1000);
		CountDownLatch latch = new CountDownLatch(COUNT);
		long begin = System.currentTimeMillis();
		for (int i = 0; i < COUNT; i++) {
			executor.submit(new Runnable() {
				public void run() {
					try {
						helloClient1.sayHello1("test");
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						latch.countDown();
					}
				}
			});
		}
		latch.await();
		long end = System.currentTimeMillis();
		System.out.println(end - begin);
	}
	
    public void testSingleThread() throws Exception {
    	Thread.sleep(1000);
        CountDownLatch latch = new CountDownLatch(COUNT);
        long begin = System.currentTimeMillis();
        for (int i = 0; i < COUNT; i++) {
            try {
                helloClient1.sayHello1("test");
                // System.out.println(helloClient2.sayHi("test"));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        }
        latch.await();
        long end = System.currentTimeMillis();
        System.out.println(end - begin);
    }

}
