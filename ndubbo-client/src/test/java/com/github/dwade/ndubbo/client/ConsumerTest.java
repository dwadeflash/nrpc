package com.github.dwade.ndubbo.client;

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
		long begin = System.currentTimeMillis();
//		for(int i=0;i<13;i++) {
			System.out.println(helloClient.sayHello("test"));
//		}
		long end = System.currentTimeMillis();
		System.out.println(end - begin);
	}
	
}
