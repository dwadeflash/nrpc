package com.github.dwade.ndubbo.core.service;

public class HelloWorld implements IHelloWorld {

	@Override
	public String sayHello1(String str) {
		return str + ",hello1";
	}

	@Override
	public String sayHello2(String str) {
		return str + ",hello2";
	}

}
