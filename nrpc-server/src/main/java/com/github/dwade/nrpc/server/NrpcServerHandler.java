package com.github.dwade.nrpc.server;

import java.lang.reflect.Method;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.github.dwade.nrpc.core.InvokeInfo;
import com.github.dwade.nrpc.core.WrapppedResult;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NrpcServerHandler extends ChannelInboundHandlerAdapter {
	
	private final static Logger logger = LoggerFactory.getLogger(NrpcServerHandler.class);

	private ApplicationContext applicationContext;
	
	
	public NrpcServerHandler(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		InvokeInfo info = (InvokeInfo) msg;
		Class interfaceClass = Class.forName(info.getInterfaceName());
		Map<String, Object> beans = applicationContext.getBeansOfType(interfaceClass);
		/**
		 * TODO 使用ref配置代理的bean之后，无法获取代理的FactoryBean了，
		 * 这样每次调用的时候，相当于直接使用了真正的bean，代理失去了意义
		 */
		Object bean = beans.values().toArray()[0];
		for (Method method : interfaceClass.getDeclaredMethods()) {
			if (info.getMethodName().equals(method.getName())) {
				Object result = method.invoke(bean, info.getArgs());
				WrapppedResult wrapppedResult = new WrapppedResult();
				wrapppedResult.setId(info.getId());
				wrapppedResult.setResult(result);
				ctx.channel().writeAndFlush(wrapppedResult);
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
		logger.debug("connected...");
    }
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		logger.debug("disconnected...");
    }

}
