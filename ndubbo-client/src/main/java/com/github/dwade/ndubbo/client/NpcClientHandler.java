package com.github.dwade.ndubbo.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dwade.ndubbo.core.InvokeContext;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NpcClientHandler extends ChannelInboundHandlerAdapter {
	
	private final static Logger logger = LoggerFactory.getLogger(NpcClientHandler.class);
	
	private InvokeContext context;
	
	public NpcClientHandler(InvokeContext context) {
		this.context = context;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		ctx.channel().writeAndFlush(context.getInfo());
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		logger.debug("Result:" + msg);
		context.setResult(msg);
		ctx.channel().close();
	}

}
