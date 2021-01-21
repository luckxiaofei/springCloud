package com.neo.sbrpccorestarter.common;

import com.neo.sbrpccorestarter.config.RpcProperties;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.context.annotation.Configuration;

/**
 * @author 陈小飞 luckxiaofei@outlook.com
 * @date 1/2/21 12:30 上午
 */
@Configuration
public class ZookeeperUtil {

//    @Autowired
//    static RpcProperties rpcProperties;

    public CuratorFramework getZK() {
        RpcProperties rpcProperties = new RpcProperties();
        return getZK(rpcProperties);
    }

    public static CuratorFramework getZK(RpcProperties rpcProperties) {
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
}
