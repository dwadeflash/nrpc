package com.github.dwade.ndubbo.core.server;

import java.lang.reflect.Method;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.github.dwade.ndubbo.core.service.InvokeInfo;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NpcServerHandler extends ChannelInboundHandlerAdapter implements ApplicationContextAware {

	private static ApplicationContext applicationContext;

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
				final ChannelFuture f = ctx.writeAndFlush(result);
				f.addListener(new ChannelFutureListener() {
					@Override
					public void operationComplete(ChannelFuture future) {
						assert f == future;
						ctx.close();
					}
				});
				break;
			}
		}
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}

}
