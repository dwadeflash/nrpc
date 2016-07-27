package com.github.dwade.ndubbo.core.client;

import com.github.dwade.ndubbo.core.InvokeInfo;
import com.github.dwade.ndubbo.core.WrapppedResult;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class NpcClient {
	
	private final String host;
	private final int port;
	
	private InvokeInfo info;
	
	private WrapppedResult result;
	
	public NpcClient(String host, int port, InvokeInfo info, WrapppedResult result) {
		this.host = host;
		this.port = port;
		this.info = info;
		this.result = result;
	}
	
	public void start() throws Exception {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class).remoteAddress(host, port)
					.handler(new ChannelInitializer<SocketChannel>() {

						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							ChannelPipeline pipeline = ch.pipeline();
							pipeline.addLast("encoder", new ObjectEncoder());
							pipeline.addLast("decoder",
									new ObjectDecoder(ClassResolvers.cacheDisabled(this.getClass().getClassLoader())));
					        pipeline.addLast("handler", new NpcClientHandler(info, result));
						}
					});
			ChannelFuture connectFuture = b.connect().sync();
			connectFuture.channel().closeFuture().sync();
			connectFuture.addListener(new ChannelFutureListener() {
				
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					System.out.println("connection closed");
				}
			});
		} finally {
			group.shutdownGracefully();
		}
	}
	
}
