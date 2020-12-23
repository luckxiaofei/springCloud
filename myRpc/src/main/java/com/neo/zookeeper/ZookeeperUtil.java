package com.neo.zookeeper;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

/**
 * @author 陈小飞 luckxiaofei@outlook.com
 * @date 12/22/20 6:47 下午
 */
public class ZookeeperUtil {
    private static final Logger logger = LoggerFactory.getLogger(ZookeeperUtil.class);

    public static void main(String[] args) {
        ZooKeeper zooKeeper = zkClient();
    }


    /**
     * 创建Zookeeper连接实例
     * //todof 为毛总是连不上
     *
     * @return
     */
    public static ZooKeeper zkClient() {
        ZooKeeper zooKeeper = null;
        try {
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            //连接成功后，会回调watcher监听，此连接操作是异步的，执行完new语句后，直接调用后续代码
            //  可指定多台服务地址 127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183
            zooKeeper = new ZooKeeper("127.0.0.1:2181", 4000, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (Event.KeeperState.SyncConnected == event.getState()) {
                        //如果收到了服务端的响应事件,连接成功
                        countDownLatch.countDown();
                    }
                }
            });
            logger.info("【初始化ZooKeeper连接状态....】={}", zooKeeper.getState());
            countDownLatch.await();
        } catch (Exception e) {
            logger.error("初始化ZooKeeper连接异常....】={}", e);
        }
        return zooKeeper;
    }
}
