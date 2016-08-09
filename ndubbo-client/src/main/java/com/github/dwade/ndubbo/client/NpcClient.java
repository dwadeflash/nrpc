package com.github.dwade.ndubbo.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dwade.ndubbo.core.INpcClient;
import com.github.dwade.ndubbo.core.InvokeContext;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class NpcClient implements INpcClient {
	
	private final static Logger logger = LoggerFactory.getLogger(NpcClient.class);
	
	private Map<String, Bootstrap> bootstraps = new ConcurrentHashMap<String, Bootstrap>();
	
	private Map<String, Channel> channels = new ConcurrentHashMap<String, Channel>();
	
	private EventLoopGroup group = new NioEventLoopGroup();
	
	private Map<String, InvokeContext> contexts = new ConcurrentHashMap<String, InvokeContext>();
	
	public void start(InvokeContext context) throws Exception {
		Bootstrap bootstrap = bootstraps.get(context.getUrl());
		Channel channel = channels.get(context.getUrl());
		if(bootstrap == null) {
			bootstrap = new Bootstrap();
			bootstrap.group(group).channel(NioSocketChannel.class);
			bootstrap.handler(new ChannelInitializer<SocketChannel>() {
				
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ChannelPipeline pipeline = ch.pipeline();
					pipeline.addLast("encoder", new ObjectEncoder());
					pipeline.addLast("decoder",
							new ObjectDecoder(ClassResolvers.cacheDisabled(this.getClass().getClassLoader())));
					pipeline.addLast("handler", new NpcClientHandler(contexts));
				}
			});
			ChannelFuture connectFuture = bootstrap.connect(context.getHost(), context.getPort()).sync();
			channel = connectFuture.channel();
			channel.writeAndFlush(context.getInfo());
			bootstraps.put(context.getUrl(), bootstrap);
			channels.put(context.getUrl(), channel);
			contexts.put(context.getId(), context);
			connectFuture.channel().closeFuture().sync();
		}
	}

}
