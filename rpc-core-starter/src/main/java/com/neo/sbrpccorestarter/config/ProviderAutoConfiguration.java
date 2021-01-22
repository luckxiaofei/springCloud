package com.neo.sbrpccorestarter.config;

import com.neo.sbrpccorestarter.anno.RpcService;
import com.neo.sbrpccorestarter.common.RpcDecoder;
import com.neo.sbrpccorestarter.common.RpcEncoder;
import com.neo.sbrpccorestarter.exception.RpcException;
import com.neo.sbrpccorestarter.exception.ZkConnectException;
import com.neo.sbrpccorestarter.model.RpcRequest;
import com.neo.sbrpccorestarter.model.RpcResponse;
import com.neo.sbrpccorestarter.provider.BeanFactory;
import com.neo.sbrpccorestarter.provider.ServerHandler;
import com.neo.sbrpccorestarter.registory.RegistryServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * @author luckxiaofei@outlook.com
 * @date 2018/10/26 14:07
 * @description
 */
@Configuration
@ConditionalOnClass(RpcService.class)
public class ProviderAutoConfiguration {

    private Logger logger = LoggerFactory.getLogger(ProviderAutoConfiguration.class);

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private RpcProperties rpcProperties;
    @Autowired
    private CuratorFramework zk;


    /**
     * 这里注入bean
     */

    //@PostConstruct该注解被用来修饰一个非静态的void（）方法。
    // 被@PostConstruct修饰的方法会在服务器加载Servlet的时候运行，并且只会被服务器执行一次。
    // PostConstruct在构造函数之后执行，init（）方法之前执行。
    @PostConstruct
    public void init() {
        logger.info("rpc server start scanning provider service...");
        Map<String, Object> beanMap = this.applicationContext.getBeansWithAnnotation(RpcService.class);
        logger.info("rpc server scan over...");
        // 如果有服务的话才启动netty server
        if (!beanMap.isEmpty()) {
            int port = rpcProperties.getPort();
            startNetty(port);
            // netty服务端启动成功后，向zk注册这个服务
            beanMap.entrySet().forEach(one -> {
                try {
                    Object serviceBean = one.getValue();
                    String RpcServiceName = serviceBean.getClass().getAnnotation(RpcService.class).value();
                    if (StringUtils.isEmpty(RpcServiceName)) {
                        Class<?>[] interfaces = serviceBean.getClass().getInterfaces();
                        if (interfaces == null || interfaces.length <= 0) {
                            throw new RpcException("rpc异常:service请实现接口");
                        }
                        for (Class<?> anInterface : interfaces) {
                            RpcServiceName = anInterface.getSimpleName();
                            int timeout = rpcProperties.getTimeout();
                            String moduleName = rpcProperties.getModuleName();
                            String host = rpcProperties.getHost();//todof 自动获取ip 待完善
                            new RegistryServer(zk, timeout, RpcServiceName, moduleName, host, port).register();
                        }
                    }
                } catch (ZkConnectException | RpcException e) {
                    e.printStackTrace();
                }
            });

        }
    }

    /**
     * 将服务类交由BeanFactory管理
     *
     * @param beanName
     * @param bean
     */
    private void initProviderBean(String beanName, Object bean) {
        BeanFactory.addBean(bean.getClass(), bean);
    }

    /**
     * 启动netty server
     *
     * @param port netty启动的端口
     */
    public void startNetty(int port) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new RpcDecoder(RpcRequest.class))
                                    .addLast(new RpcEncoder(RpcResponse.class))
                                    .addLast(new ServerHandler());
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture f = b.bind(port).sync();
            logger.info("server started on port : {}", port);
//            f.channel().closeFuture().sync();  //todof 会阻塞主线程,不能要这个
        } catch (Exception e) {
            e.printStackTrace();
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }


}
