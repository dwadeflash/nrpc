package com.github.dwade.nrpc.client;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dwade.nrpc.core.InvokeContext;
import com.github.dwade.nrpc.core.WrapppedResult;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NrpcClientHandler extends ChannelInboundHandlerAdapter {
	
	private final static Logger logger = LoggerFactory.getLogger(NrpcClientHandler.class);
	
	private Map<String, InvokeContext> contexts;
	
	private Map<String, CountDownLatch> latches;
	
	public NrpcClientHandler(Map<String, InvokeContext> contexts, Map<String, CountDownLatch> latches) {
		this.contexts = contexts;
		this.latches = latches;
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		WrapppedResult result = (WrapppedResult) msg;
		InvokeContext context = contexts.get(result.getId());
		context.setResult(result.getResult());
		latches.get(context.getId()).countDown();
		latches.remove(context.getId());
		contexts.remove(context.getId());
	}

}
