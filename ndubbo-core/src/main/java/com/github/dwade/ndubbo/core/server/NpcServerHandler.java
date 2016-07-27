package com.github.dwade.ndubbo.core.server;

import java.lang.reflect.Method;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.github.dwade.ndubbo.core.InvokeInfo;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class NpcServerHandler extends ChannelInboundHandlerAdapter implements ApplicationContextAware {

	private static ApplicationContext applicationContext;
	
	private static ChannelGroup group =  new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		NpcServerHandler.applicationContext = applicationContext;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		InvokeInfo info = (InvokeInfo) msg;
		Class interfaceClass = Class.forName(info.getInterfaceName());
		Object bean = applicationContext.getBean(interfaceClass);
		for (Method method : interfaceClass.getDeclaredMethods()) {
			if (info.getMethodName().equals(method.getName())) {
				Object result = method.invoke(bean, info.getArgs());
				ctx.channel().writeAndFlush(result);
				break;
			}
		}
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("connected...");
		group.add(ctx.channel());
    }
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("disconnected...");
    }

}
