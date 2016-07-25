package com.github.dwade.ndubbo.core.client;

import com.github.dwade.ndubbo.core.service.InvokeInfo;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class NpcClient {
	
	private final String host;
	private final int port;
	
	private InvokeInfo info;
	
	private Object result;
	
	public NpcClient(String host, int port, InvokeInfo info) {
		this.host = host;
		this.port = port;
		this.info = info;
	}
	
	public Object start() throws Exception {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class).remoteAddress(host, port)
					.handler(new ChannelInitializer<SocketChannel>() {

						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(new ObjectEncoder());
							ch.pipeline().addLast(new NpcClientHandler(info, result));
						}
					});
			ChannelFuture future = b.connect().sync();
			future.channel().closeFuture().await();
			return result;
		} finally {
			try {
				group.shutdownGracefully().sync();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
