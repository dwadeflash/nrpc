package com.github.dwade.ndubbo.core.server;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;

public class NpcServer {
	
	private int port;
	
	private NpcServerHandler npcServerHandler;

    public void setPort(int port) {
        this.port = port;
    }
    
    public void setNpcServerHandler(NpcServerHandler npcServerHandler) {
    	this.npcServerHandler = npcServerHandler;
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class) // (3)
             .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                 @Override
						public void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(
									new ObjectDecoder(ClassResolvers.cacheDisabled(this.getClass().getClassLoader())));
							ch.pipeline().addLast(npcServerHandler);
						}
             })
             .option(ChannelOption.SO_BACKLOG, 128)          // (5)
             .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

            // 绑定端口，开始接收进来的连接
            ChannelFuture f = b.bind(port).sync(); // (7)

            // 等待服务器  socket 关闭 。
            // 在这个例子中，这不会发生，但你可以优雅地关闭你的服务器。
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
       server.run();
    }

}
