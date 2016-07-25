package com.github.dwade.ndubbo.core.service;

import java.io.Serializable;

public class InvokeInfo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String interfaceName;
	
	private String methodName;
	
	private Object[] args;

	public InvokeInfo(String interfaceName, String methodName, Object[] args) {
		this.interfaceName = interfaceName;
		this.methodName = methodName;
		this.args = args;
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
