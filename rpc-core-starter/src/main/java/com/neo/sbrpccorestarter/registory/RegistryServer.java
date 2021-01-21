package com.neo.sbrpccorestarter.registory;

import com.neo.sbrpccorestarter.exception.ZkConnectException;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 服务注册
 *
 * @author luckxiaofei@outlook.com
 * @date 2018/10/26 16:04
 * @description
 */
public class RegistryServer {

    private Logger logger = LoggerFactory.getLogger(RegistryServer.class);

    /**
     * zk的地址
     */
    private CuratorFramework zooKeeper;

    /**
     * 超时时间
     */
    private int timeout;

    /**
     * 服务名
     */
    private String serverName;
    /**
     * 模块Name
     */
    private String moduleName;

    private String host;

    private int port;


    public RegistryServer(
            CuratorFramework zooKeeper,
            int timeout,
            String serverName,
            String moduleName,
            String host,
            int port) {
        this.zooKeeper = zooKeeper;
        this.timeout = timeout;
        this.moduleName = moduleName;
        this.serverName = serverName;
        this.host = host;
        this.port = port;
    }

    /**
     * zk注册
     *
     * @throws IOException
     * @throws KeeperException
     * @throws InterruptedException
     */
    public void register() throws ZkConnectException {
        try {
            //注册模块信息
            Stat stat = zooKeeper.checkExists().forPath("/" + moduleName);
            if (stat == null) {
                zooKeeper.create().forPath("/" + moduleName);
            }
            //注册模块下的服务
            String path = "/" + moduleName;
            String data = serverName + "," + host + ":" + port;
            zooKeeper.create().withMode(CreateMode.EPHEMERAL).forPath("/" + moduleName + "/" + serverName, data.getBytes());
            logger.info("provider register success {}", data);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ZkConnectException("register to zk exception : " + e.getMessage());
        }
    }

}
