package com.github.dwade.ndubbo.server;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.dwade.ndubbo.core.INpcServer;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:nrpc-provider.xml")
public class ServerTest {
	
	@Autowired
	private INpcServer server;
	
	@Test
	public void startServer() throws Exception {
		server.start();
	}

}
