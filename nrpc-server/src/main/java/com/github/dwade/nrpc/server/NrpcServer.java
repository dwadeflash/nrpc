package com.github.dwade.nrpc.server;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.github.dwade.nrpc.core.INrpcServer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class NrpcServer implements INrpcServer, ApplicationContextAware {
	
	private static ApplicationContext applicationContext;
	
	private int port;
	
    public void setPort(int port) {
        this.port = port;
    }
    
    public int getPort() {
    	return port;
    }
    
    @Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		NrpcServer.applicationContext = applicationContext;
	}
    
    public void start() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup();
		ServerBootstrap b = new ServerBootstrap(); // (2)
		b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class) // (3)
				.childHandler(new ChannelInitializer<SocketChannel>() { // (4)
					@Override
					public void initChannel(SocketChannel ch) throws Exception {
						ChannelPipeline pipeline = ch.pipeline();
						pipeline.addLast("encoder", new ObjectEncoder());
						pipeline.addLast("decoder",
								new ObjectDecoder(ClassResolvers.cacheDisabled(this.getClass().getClassLoader())));
						pipeline.addLast("handler", new NrpcServerHandler(applicationContext));
					}
				}).option(ChannelOption.SO_BACKLOG, 128) // (5)
				.childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

		b.bind(port);
    }

}
