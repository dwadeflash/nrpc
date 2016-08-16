package com.github.dwade.nrpc.client;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import org.apache.curator.x.discovery.ServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.github.dwade.nrpc.core.INrpcClient;
import com.github.dwade.nrpc.core.InvokeContext;
import com.github.dwade.nrpc.core.discover.IServiceDiscovery;

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

public class NrpcClient implements INrpcClient, InitializingBean, DisposableBean {
	
	private final static Logger logger = LoggerFactory.getLogger(NrpcClient.class);
	
	private IServiceDiscovery serviceDiscovery;
	
	private EventLoopGroup group = new NioEventLoopGroup();

	private volatile Map<String, Bootstrap> bootstraps = new ConcurrentHashMap<String, Bootstrap>();
	
	private volatile Map<String, Channel> channels = new ConcurrentHashMap<String, Channel>();
	
	private Map<String, InvokeContext> contexts = new ConcurrentHashMap<String, InvokeContext>();
	
	private Map<String, CountDownLatch> latches = new ConcurrentHashMap<String, CountDownLatch>();
	
	public void setServiceDiscovery(IServiceDiscovery serviceDiscovery) {
		this.serviceDiscovery = serviceDiscovery;
	}
	
    public void start(InvokeContext context, CountDownLatch latch) throws Exception {
		contexts.put(context.getId(), context);
		latches.put(context.getId(), latch);
		Channel channel = channels.get(context.getHost());
		channel.writeAndFlush(context.getInfo());
	}

	@Override
	public void destroy() throws Exception {
		group.shutdownGracefully();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void afterPropertiesSet() throws Exception {
		logger.debug("initializing rpc client...");
		Map<String, Collection<ServiceInstance>> serviceList = serviceDiscovery.getServices();
		Set<String> addressList = new HashSet<String>();
		Set<ServiceInstance> providerList = new HashSet<ServiceInstance>();
		for (Map.Entry<String, Collection<ServiceInstance>> entry : serviceList.entrySet()) {
			for (ServiceInstance service : entry.getValue()) {
				if (addressList.add(service.getAddress())) {
					providerList.add(service);
				}
			}
		}
		for(ServiceInstance service : providerList) {
			Bootstrap bootstrap = new Bootstrap();
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
			ChannelFuture connectFuture = bootstrap.connect(service.getAddress(), service.getPort()).sync();
			Channel channel = connectFuture.channel();
			bootstraps.put(service.getAddress(), bootstrap);
			channels.put(service.getAddress(), channel);
		}
	}

}
