package com.github.dwade.ndubbo.core.client;

import com.github.dwade.ndubbo.core.service.InvokeInfo;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NpcClientHandler extends ChannelInboundHandlerAdapter {
	
	private InvokeInfo info;
	
	private Object result;

	public NpcClientHandler(InvokeInfo info, Object result) {
		this.info = info;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		ctx.channel().writeAndFlush(info);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		System.out.println(msg);
		result = msg;
	}

}
