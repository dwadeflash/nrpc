package com.github.dwade.ndubbo.core.client;

import com.github.dwade.ndubbo.core.InvokeInfo;
import com.github.dwade.ndubbo.core.WrapppedResult;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NpcClientHandler extends ChannelInboundHandlerAdapter {
	
	private InvokeInfo info;
	
	private WrapppedResult result;
	
	public NpcClientHandler(InvokeInfo info, WrapppedResult result) {
		this.info = info;
		this.result = result;
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
		System.out.println("Result:" + msg);
		result.setResult(msg);
		ctx.channel().close();
	}

}
