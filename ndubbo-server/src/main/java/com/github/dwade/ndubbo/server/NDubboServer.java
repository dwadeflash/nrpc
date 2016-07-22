package com.github.dwade.ndubbo.server;

public class NDubboServer {
	
	private final int port;

	public NDubboServer(int port) {
		this.port = port;
	}

	public void run() throws Exception {
		
	}

	public static void main(String[] args) throws Exception {
		int port;
		if (args.length > 0) {
			port = Integer.parseInt(args[0]);
		} else {
			port = 8080;
		}
		new NDubboServer(port).run();
	}

}
