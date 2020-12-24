package com.neo.zookeeper;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 陈小飞 luckxiaofei@outlook.com
 * @date 12/22/20 6:47 下午
 */
@Slf4j
public class ZookeeperUtil {


    private ZooKeeper zookeeper;
    /**
     * 指定的服务器列表，多个host:port之间用英文逗号分隔
     */
    private static String connStr = "127.0.0.1:2181";

    /**
     * sessionTimeout 会话超时时间。以毫秒为单位。客户端和服务器端之间的连接通过心跳包进行维系，如果心跳包超过这个指定时间则认为会话超时失效。
     */
    private static final int sessionTimeout = 50000;


    /**
     * 传入zk连接信息进行zookeeper初始化可以使用
     *
     * @param connStr
     */
    public ZookeeperUtil(String connStr) {
        try {
            zookeeper = new ZooKeeper(connStr, sessionTimeout, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    // 收到事件通知后的回调函数
                    log.info("success to connect zk cluster:::" + connStr);
                    log.info("watchedEvent::: path=" + watchedEvent.getPath() + ";state=" + watchedEvent.getState() + ";type=" + watchedEvent.getType());
                }
            });
        } catch (Exception ex) {
            log.error("ZookeeperUtil(connStr) failed to connect zk cluster! " + ex);
        }
    }

    /**
     * 创建znode结点
     *
     * @param path 结点路径
     * @param data 结点数据
     * @return true 创建结点成功 false表示结点存在
     */
    private boolean addZnode(String path, String data, CreateMode mode) {
        // znode创建类型(CreateMode)可取以下属性值：
        // CreateMode.PERSISTENT（持久化节点）、
        // CreateMode.PERSISTENT_SEQUENTIAL（顺序自动编号持久化节点，这种节点会根据当前已存在的节点数自动加 1）、
        // CreateMode.EPHEMERAL（临时节点， 客户端session超时这类节点就会被自动删除）、
        // CreateMode.EPHEMERAL_SEQUENTIAL（临时顺序节点）
        try {
            if (zookeeper.exists(path, true) == null) {
                zookeeper.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, mode);
                return true;
            } else {
                log.info("addZnodeData nodePath = " + path + " failed, already exist");
            }
        } catch (KeeperException | InterruptedException e) {
            log.error("addZnodeData nodePath = " + path + " error！", e);
        }
        return false;
    }

    /**
     * 创建永久znode结点
     *
     * @param path 结点路径
     * @param data 结点数据
     * @return true 创建结点成功 false表示结点存在
     */
    public boolean addPZnode(String path, String data) {
        return addZnode(path, data, CreateMode.PERSISTENT);
    }

    /**
     * 创建临时znode结点
     *
     * @param path 结点路径
     * @param data 结点数据
     * @return true 创建结点成功 false表示结点存在
     */
    public boolean addEZnode(String path, String data) {
        return addZnode(path, data, CreateMode.EPHEMERAL_SEQUENTIAL);
    }

    /**
     * zk节点存在性校验
     *
     * @param path
     * @return true 表示结点存在 false表示结点不存在
     */
    public boolean isExists(String path) {
        try {
            if (zookeeper.exists(path, false) != null) {
                return true;
            }
        } catch (KeeperException | InterruptedException e) {
            log.error("isExists nodePath = " + path + " error! " + e);
        }
        return false;
    }

    /**
     * 修改znode
     *
     * @param path 结点路径
     * @param data 结点数据
     * @return 修改结点成功   false表示结点不存在
     */
    public boolean updateZnode(String path, String data) {
        try {
            Stat stat = zookeeper.exists(path, true);
            if (stat != null) {
                zookeeper.setData(path, data.getBytes(), stat.getVersion());
                return true;
            } else {
                log.info("updateZnode nodePath = " + path + " failed, not exist");
            }
        } catch (KeeperException | InterruptedException e) {
            log.error("updateZnode nodePath = " + path + " error！ ", e);
        }
        return false;
    }

    /**
     * 新增或修改znode
     *
     * @param path 结点路径
     * @param data 结点数据
     * @return true 新增或修改成功, false 出现异常
     */
    public boolean addOrUpdateZnode(String path, String data) {
        try {
            Stat stat = zookeeper.exists(path, true);
            if (stat != null) {
                zookeeper.setData(path, data.getBytes(), stat.getVersion());
                return true;
            } else {
                zookeeper.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                return true;
            }
        } catch (KeeperException | InterruptedException e) {
            log.error("addOrUpdateZnode nodePath：" + path + " error！ ", e);
            return false;
        }
    }

    /**
     * 删除结点
     *
     * @param path 结点
     * @return true 删除键结点成功  false表示结点不存在
     */
    public boolean deleteZnode(String path) {
        try {
            if ((zookeeper.exists(path, false)) == null) {
                log.info("deleteZnode nodePath = " + path + " not exist");
                return true;
            } else {
                // 递归删除该节点的所有子节点及其自身
                recurrenceDel(path);
                return true;
            }
        } catch (InterruptedException | KeeperException e) {
            log.error("deleteZnode nodePath = " + path + ",error！", e);
            return false;
        }
    }

    private void recurrenceDel(String path) {
        try {
            List<String> childPathList = zookeeper.getChildren(path, false);
            if (childPathList.isEmpty()) {
                zookeeper.delete(path, -1);
            } else {
                for (String childPath : childPathList) {
                    recurrenceDel(path + "/" + childPath);
                }
                zookeeper.delete(path, -1);
            }

        } catch (InterruptedException | KeeperException e) {
            log.error("recurrenceDel nodePath = " + path + ",error！", e);
        }
    }

    /**
     * 取到结点数据
     *
     * @param path 结点路径
     * @return null表示结点不存在 否则返回结点数据
     */
    public String getZnodeData(String path) {
        String data = null;
        try {
            Stat stat = null;
            if ((stat = zookeeper.exists(path, true)) != null) {
                data = new String(zookeeper.getData(path, true, stat));
            } else {
                log.info("getZnodeData nodePath = " + path + " failed, not exist!");
            }
        } catch (KeeperException | InterruptedException e) {
            log.error("getZnodeData nodePath = " + path + " error！", e);
        }
        return data;
    }

    /**
     * 获取子节点
     *
     * @param path
     * @return 当前路径下的子结点
     */
    public List<String> getChildren(String path) {
        List<String> childrenList = new ArrayList<String>();
        // getChildren()方法参数有两个：节点路径、是否需要监视器，若不需要填写“false”。
        // 监听状态下，当指定的znode被删除或znode下的子节点被创建/删除时，ZooKeeper集合将通知。
        try {
            if ((zookeeper.exists(path, true)) != null) {
                childrenList = zookeeper.getChildren(path, true);
            } else {
                log.info("getChildren nodePath = " + path + " failed, not exist!");
            }
        } catch (KeeperException | InterruptedException ex) {
            log.error("getChildren nodePath = " + path + " error! " + ex);
        }
        return childrenList;
    }

    public static void main(String[] args) {
        ZookeeperUtil zu = new ZookeeperUtil(connStr);
        zu.addPZnode("/t2", null);
        String znodeData = zu.getZnodeData("/t2");
        System.out.println(znodeData);
    }

}
