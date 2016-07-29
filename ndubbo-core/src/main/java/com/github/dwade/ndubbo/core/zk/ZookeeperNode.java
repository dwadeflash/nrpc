package com.github.dwade.ndubbo.core.zk;

public class ZookeeperNode {
	
	private String path;
	
	private Object data;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
	
}
