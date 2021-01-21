package com.neo.sbrpccorestarter.config;

import com.neo.sbrpccorestarter.common.Constants;
import com.neo.sbrpccorestarter.consumer.RpcProxy;
import com.neo.sbrpccorestarter.exception.ZkConnectException;
import com.neo.sbrpccorestarter.registory.DiscoveryService;
import lombok.extern.java.Log;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author luckxiaofei@outlook.com
 * @date 2018/10/30 11:06
 * @description
 */
@Configuration
@EnableConfigurationProperties(RpcProperties.class)
@Log
public class RpcAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(RpcAutoConfiguration.class);

    @Autowired
    private RpcProperties rpcProperties;

    @Bean
    public CuratorFramework curatorFramework() {
        initZookeeperRpcNode(rpcProperties);
        //1 重试策略：初试时间为1s 重试10次
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 10);
        //2 通过工厂创建连接
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(rpcProperties.getRegisterAddress())//连接地址
                .connectionTimeoutMs(rpcProperties.getTimeout())//连接超时时间
                .sessionTimeoutMs(30_000)//会话超时时间
                .retryPolicy(retryPolicy)//重试策略
                .namespace("rpc")//命名空间，连接后所有的操作都是在这个/super节点之下
                .build();
        //3 开启连接
        client.start();
        return client;
    }

    @Bean
    //主要实现的是，当你的bean被注册之后，如果而注册相同类型的bean，就不会成功，它会保证你的bean只有一个，即你的实例只有一个，当你注册多个相同的bean时，会出现异常
    @ConditionalOnMissingBean
    public DiscoveryService serviceDiscovery(CuratorFramework zk) {
        DiscoveryService discoveryService = null;
        try {
            discoveryService = new DiscoveryService(zk);
        } catch (ZkConnectException e) {
            logger.error("zk connect failed:", e);
        }
        return discoveryService;
    }

    @Bean
    @ConditionalOnMissingBean
    public RpcProxy rpcProxy(DiscoveryService discoveryService) {
        RpcProxy rpcProxy = new RpcProxy();
        rpcProxy.setServiceDiscovery(discoveryService);
        return rpcProxy;
    }

    /**
     * 初始化rpc根节点
     *
     * @param rpcProperties
     * @return
     */
    public void initZookeeperRpcNode(RpcProperties rpcProperties) {
        //1 重试策略：初试时间为1s 重试10次
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 10);
        //2 通过工厂创建连接
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(rpcProperties.getRegisterAddress())//连接地址
                .connectionTimeoutMs(rpcProperties.getTimeout())//连接超时时间
                .sessionTimeoutMs(30_000)//会话超时时间
                .retryPolicy(retryPolicy)//重试策略
//                .namespace("rpc")//命名空间，连接后所有的操作都是在这个/super节点之下
                .build();
        //3 开启连接
        client.start();
        try {
            Stat stat = client.checkExists().forPath(Constants.RPC_ROOT_NOTE);
            if (stat == null) {
                client.create().forPath(Constants.RPC_ROOT_NOTE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client.close();
        }
    }


}
