package com.github.dwade.nrpc.client;

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

import com.github.dwade.nrpc.core.INrpcClient;
import com.github.dwade.nrpc.core.InvokeContext;

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
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;

public class NrpcClient implements INrpcClient, DisposableBean {
	
	private final static Logger logger = LoggerFactory.getLogger(NrpcClient.class);
	
	private EventLoopGroup group = new NioEventLoopGroup();

	private Map<String, Bootstrap> bootstraps = new ConcurrentHashMap<String, Bootstrap>();
	
	private volatile Map<String, Channel> channels = new Hashtable<String, Channel>();
	
	private Map<String, InvokeContext> contexts = new ConcurrentHashMap<String, InvokeContext>();
	
	private Map<String, CountDownLatch> latches = new ConcurrentHashMap<String, CountDownLatch>();
	
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void start(InvokeContext context, CountDownLatch latch) throws Exception {
		logger.debug("start rpc...1");
		boolean init = false;
		contexts.put(context.getId(), context);
		latches.put(context.getId(), latch);
		Bootstrap bootstrap = bootstraps.get(context.getUrl());
		Channel channel = channels.get(context.getUrl());
		if (bootstrap == null && channel == null) {
			logger.debug("start rpc...2");
			synchronized (NrpcClient.class) {
				logger.debug("start rpc...3");
				if (bootstraps.get(context.getUrl()) == null) {
					logger.debug("start rpc...4");
					bootstrap = new Bootstrap();
					bootstraps.put(context.getUrl(), bootstrap);
					init = true;
				}
				if (init) {
					logger.debug("start rpc...5");
					bootstrap.group(group).channel(NioSocketChannel.class);
					bootstrap.handler(new ChannelInitializer<SocketChannel>() {

						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							ChannelPipeline pipeline = ch.pipeline();
							pipeline.addLast("encoder", new ObjectEncoder());
							pipeline.addLast("decoder",
									new ObjectDecoder(ClassResolvers.cacheDisabled(this.getClass().getClassLoader())));
							pipeline.addLast("handler", new NrpcClientHandler(contexts, latches));
						}
					});
					ChannelFuture connectFuture = bootstrap.connect(context.getHost(), context.getPort()).sync();
					channel = connectFuture.channel();
					bootstraps.put(context.getUrl(), bootstrap);
					channels.put(context.getUrl(), channel);
					contexts.put(context.getId(), context);
				}
			}
		}
		bootstrap = bootstraps.get(context.getUrl());
		channel = channels.get(context.getUrl());
		logger.debug("start rpc...6");
		logger.debug(Boolean.valueOf(channel == null).toString());
		channel.writeAndFlush(context.getInfo()).addListener(new FutureListener() {

			@Override
			public void operationComplete(Future future) throws Exception {
				System.out.println("finished send request");
			}
		});
		logger.debug("start rpc...7");
	}

	@Override
	public void destroy() throws Exception {
		group.shutdownGracefully();
	}

}
