package com.github.dwade.ndubbo.core;

public class InvokeContext {
	
	private String host;
	
	private int port;
	
	private InvokeInfo info;
	
	private WrapppedResult result = new WrapppedResult();

	public InvokeContext(String host, int port, InvokeInfo info) {
		this.host = host;
		this.port = port;
		this.info = info;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public InvokeInfo getInfo() {
		return info;
	}

	public void setInfo(InvokeInfo info) {
		this.info = info;
	}

	public Object getResult() {
		return result.getResult();
	}

	public void setResult(Object result) {
		this.result.setResult(result);
	}
	
	public String getUrl() {
		return host + ":" + port;
	}
	
	public String getId() {
		return info.getId();
	}
}
