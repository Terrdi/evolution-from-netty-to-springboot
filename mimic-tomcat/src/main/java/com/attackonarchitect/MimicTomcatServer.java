package com.attackonarchitect;

import com.attackonarchitect.core.StandardHost;
import com.attackonarchitect.core.WebappClassLoader;
import com.attackonarchitect.logger.FileLogger;
import com.attackonarchitect.servlet.ServletManagerFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import com.attackonarchitect.context.ServletContext;
import com.attackonarchitect.context.ServletContextFactory;
import com.attackonarchitect.handler.DefaultMimicTomcatChannelHandler;
import com.attackonarchitect.handler.MimicHttpInBoundHandler;
import com.attackonarchitect.listener.Notifier;
import com.attackonarchitect.listener.NotifierImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @description: 启动类
 * 使用netty-server 监听接收请求,作为Connector的简单替代实现
 */
public class MimicTomcatServer {
    private final int PORT;

    public MimicTomcatServer(int PORT) {
        this.PORT = PORT;
    }

    /**
     * 组件扫描器servlet,filter,listener
     */
    private final List<WebappClassLoader> webappClassLoaderList = new ArrayList<>();
    /**
     * servletContext
     * 1.配置信息
     * 2.全局数据共享
     */
//    private ServletContext servletContext;

    private StandardHost host = new StandardHost();

    public void addConfig(Class<?> clazz, final String path){
//        this.scanners.add(new WebComponentScanner(clazz));
        WebappClassLoader classLoader = new WebappClassLoader();
        classLoader.setPath(path);
        classLoader.setComponentScanner(new WebComponentScanner(clazz));
        this.webappClassLoaderList.add(classLoader);
    }

    public void addConfig(Class<?> clazz){
        this.addConfig(clazz, "/");
    }

    /**
     * 依据配置文件初始化
     *
     * @param configFile 配置文件路径
     */
    public void addConfig(String configFile, final String path) {
        WebappClassLoader classLoader = new WebappClassLoader();
        classLoader.setPath(path);
        if (configFile.endsWith(".xml")) {
            classLoader.setComponentScanner(new XmlComponentScanner(configFile));
        } else if (configFile.endsWith(".jar") || configFile.endsWith(".war")) {
            classLoader.setComponentScanner(new XmlComponentScanner(configFile));
        } else {
            throw new UnsupportedOperationException("不支持的文件格式:  " + configFile);
        }
        this.webappClassLoaderList.add(classLoader);
    }

    public void addConfig(String configFile){
        this.addConfig(configFile, "/");
    }

    public void addConfig(ClassLoader classLoader0, final String path) {
        WebappClassLoader classLoader = new WebappClassLoader();
        classLoader.setPath(path);
        classLoader.setComponentScanner(new SpiComponentScanner(classLoader0));
        this.webappClassLoaderList.add(classLoader);
    }

    public void addConfig(ClassLoader classLoader){
        this.addConfig(classLoader, "/");
    }

    public void start() {
        host.start(this.webappClassLoaderList);

//        Notifier notifier = new NotifierImpl(Objects.requireNonNull(scanner, "没有找到合适的组件扫描器").getWebListenerComponents());
//        servletContext = ServletContextFactory.getInstance(scanner, notifier, new FileLogger());
//        servletContext.setAttribute("notifier",notifier);

        //初始化servlet一下，主要是preinit
//        ServletManagerFactory.getInstance(scanner,servletContext);

        runNetty();
    }

    private void runNetty(){
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap
                    .group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,128)
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            // 支持 http 协议
                            pipeline.addLast(new HttpServerCodec());
                            // 处理 http 长度较长导致的解析问题
                            pipeline.addLast(new HttpObjectAggregator(65536));
                            // 传递上下文,事件监听注册
                            pipeline.addLast(new MimicHttpInBoundHandler(host));
                            // 模拟servlet处理请求和响应
                            pipeline.addLast(new DefaultMimicTomcatChannelHandler(host));
                        }
                    });

            ChannelFuture cf = serverBootstrap.bind(PORT).sync();
            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
