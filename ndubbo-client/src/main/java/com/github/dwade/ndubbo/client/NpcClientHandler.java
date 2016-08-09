package com.github.dwade.ndubbo.client;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dwade.ndubbo.core.InvokeContext;
import com.github.dwade.ndubbo.core.WrapppedResult;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NpcClientHandler extends ChannelInboundHandlerAdapter {
	
	private final static Logger logger = LoggerFactory.getLogger(NpcClientHandler.class);
	
	private Map<String, InvokeContext> contexts;
	
	public NpcClientHandler(Map<String, InvokeContext> contexts) {
		this.contexts = contexts;
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		logger.debug("Result:" + msg);
		WrapppedResult result = (WrapppedResult) msg;
		InvokeContext context = contexts.get(result.getId());
//		synchronized (context) {
			context.setResult(result.getResult());
//			context.notify();
//		}
		ctx.channel().close();
	}

}
