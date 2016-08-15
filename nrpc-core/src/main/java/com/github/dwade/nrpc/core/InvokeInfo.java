package com.github.dwade.nrpc.core;

import java.io.Serializable;
import java.util.UUID;

public class InvokeInfo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String id;

	private String interfaceName;
	
	private String methodName;
	
	private Object[] args;
	
	public InvokeInfo(String interfaceName, String methodName, Object[] args) {
		this.id = UUID.randomUUID().toString();
		this.interfaceName = interfaceName;
		this.methodName = methodName;
		this.args = args;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public String getMethodName() {
		return methodName;
	}
	
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

}
