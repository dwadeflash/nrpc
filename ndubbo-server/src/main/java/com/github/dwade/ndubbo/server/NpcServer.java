package com.github.dwade.ndubbo.server;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.github.dwade.ndubbo.core.INpcServer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
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

public class NpcServer implements INpcServer, ApplicationContextAware {
	
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
		NpcServer.applicationContext = applicationContext;
	}
    
    public void start() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class) // (3)
             .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                 @Override
						public void initChannel(SocketChannel ch) throws Exception {
                	 		ChannelPipeline pipeline = ch.pipeline();
							pipeline.addLast("encoder", new ObjectEncoder());
							pipeline.addLast("decoder",
									new ObjectDecoder(ClassResolvers.cacheDisabled(this.getClass().getClassLoader())));
							pipeline.addLast("handler", new NpcServerHandler(applicationContext));
						}
             })
             .option(ChannelOption.SO_BACKLOG, 128)          // (5)
             .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

            ChannelFuture f = b.bind(port).sync(); // (7)
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    @SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
       ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("classpath:nrpc-provider.xml");
       NpcServer server = (NpcServer) ac.getBean("server");
       server.start();
    }

}
