package cn.itweknow.sbrpccorestarter.registory;

import cn.itweknow.sbrpccorestarter.common.Constants;
import cn.itweknow.sbrpccorestarter.exception.ZkConnectException;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author luckxiaofei@outlook.com
 * @date 2018/10/26 16:04
 * @description
 */
public class RegistryServer {

    private Logger logger = LoggerFactory.getLogger(RegistryServer.class);

    /**
     * zk的地址
     */
    private ZooKeeper zooKeeper;

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


    public RegistryServer(ZooKeeper zooKeeper,
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
            if (zooKeeper.exists(Constants.RPC_ROOT_NOTE, false) == null) {
                zooKeeper.create(Constants.RPC_ROOT_NOTE, Constants.RPC_ROOT_NOTE.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
            String path = Constants.RPC_ROOT_NOTE + "/" + moduleName;
            String data = serverName + "," + host + ":" + port;
            zooKeeper.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            logger.info("provider register success {}", data);
        } catch (Exception e) {
            throw new ZkConnectException("register to zk exception," + e.getMessage(), e.getCause());
        }
    }

}
