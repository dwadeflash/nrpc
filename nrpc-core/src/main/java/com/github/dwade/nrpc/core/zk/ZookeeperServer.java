package com.github.dwade.nrpc.core.zk;

public class ZookeeperServer {
	
	private String address;
	
	private int port;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	public int getPort() {
		return port;
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
	public String getConnectString() {
		return address + ":" + port;
	}

}
