package com.github.dwade.ndubbo.core.service;

public class HelloWorld implements IHelloWorld {

	@Override
	public String sayHello(String str) {
		return str + ",hahaha";
	}

}
