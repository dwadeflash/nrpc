package com.github.dwade.ndubbo.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

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

public class NpcClient implements INpcClient, DisposableBean {
	
	private final static Logger logger = LoggerFactory.getLogger(NpcClient.class);
	
	private EventLoopGroup group = new NioEventLoopGroup();

	private Map<String, Bootstrap> bootstraps = new ConcurrentHashMap<String, Bootstrap>();
	
	private Map<String, Channel> channels = new ConcurrentHashMap<String, Channel>();
	
	private Map<String, InvokeContext> contexts = new ConcurrentHashMap<String, InvokeContext>();
	
	private Map<String, CountDownLatch> latches = new ConcurrentHashMap<String, CountDownLatch>();
	
	public void start(InvokeContext context, CountDownLatch latch) throws Exception {
		boolean init = false;
		contexts.put(context.getId(), context);
		latches.put(context.getId(), latch);
		Bootstrap bootstrap = bootstraps.get(context.getUrl());
		Channel channel = channels.get(context.getUrl());
		if(bootstrap == null) {
			synchronized (NpcClient.class) {
				if(bootstraps.get(context.getUrl()) == null) {
					bootstrap = new Bootstrap();
					bootstraps.put(context.getUrl(), bootstrap);
					init = true;
				}
			}
			if (init) {
				bootstrap.group(group).channel(NioSocketChannel.class);
				bootstrap.handler(new ChannelInitializer<SocketChannel>() {

					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ChannelPipeline pipeline = ch.pipeline();
						pipeline.addLast("encoder", new ObjectEncoder());
						pipeline.addLast("decoder",
								new ObjectDecoder(ClassResolvers.cacheDisabled(this.getClass().getClassLoader())));
						pipeline.addLast("handler", new NpcClientHandler(contexts, latches));
					}
				});
				ChannelFuture connectFuture = bootstrap.connect(context.getHost(), context.getPort()).sync();
				channel = connectFuture.channel();
				bootstraps.put(context.getUrl(), bootstrap);
				channels.put(context.getUrl(), channel);
				contexts.put(context.getId(), context);
			}
		}
		bootstrap = bootstraps.get(context.getUrl());
		channel = channels.get(context.getUrl());
		channel.writeAndFlush(context.getInfo());
	}

	@Override
	public void destroy() throws Exception {
		group.shutdownGracefully();
	}

}
