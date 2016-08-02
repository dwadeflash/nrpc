package com.github.dwade.ndubbo.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dwade.ndubbo.core.INpcClient;
import com.github.dwade.ndubbo.core.InvokeContext;

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

public class NpcClient implements INpcClient{
	
	private final static Logger logger = LoggerFactory.getLogger(NpcClient.class);
	
	public void start(InvokeContext context) throws Exception {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class).remoteAddress(context.getHost(), context.getPort())
					.handler(new ChannelInitializer<SocketChannel>() {

						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							ChannelPipeline pipeline = ch.pipeline();
							pipeline.addLast("encoder", new ObjectEncoder());
							pipeline.addLast("decoder",
									new ObjectDecoder(ClassResolvers.cacheDisabled(this.getClass().getClassLoader())));
					        pipeline.addLast("handler", new NpcClientHandler(context));
						}
					});
			ChannelFuture connectFuture = b.connect().sync();
			connectFuture.channel().closeFuture().sync();
			connectFuture.addListener(new ChannelFutureListener() {
				
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					logger.debug("connection closed");
				}
			});
		} finally {
			group.shutdownGracefully();
		}
	}

}
