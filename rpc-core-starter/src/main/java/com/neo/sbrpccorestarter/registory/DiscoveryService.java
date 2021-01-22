package com.neo.sbrpccorestarter.registory;

import com.neo.sbrpccorestarter.exception.RpcException;
import com.neo.sbrpccorestarter.exception.ZkConnectException;
import com.neo.sbrpccorestarter.model.ProviderInfo;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * 服务发现
 *
 * @author luckxiaofei@outlook.com
 * @date 2018/10/26 17:27
 * @description
 */
public class DiscoveryService {

    private Logger logger = LoggerFactory.getLogger(DiscoveryService.class);

    private volatile List<ProviderInfo> cacheList = new ArrayList<>();

    public DiscoveryService(CuratorFramework zk) throws ZkConnectException {
        watchNode(zk);
    }

    public void watchNode(final CuratorFramework client) {
        try {
            List<ProviderInfo> providerInfos = new ArrayList<>();
            //设置节点的cache
            TreeCache treeCache = new TreeCache(client, "/");
            //设置监听器和处理过程
            treeCache.getListenable().addListener(new TreeCacheListener() {
                @Override
                public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
                    ChildData data = event.getData();
                    if (data != null) {
                        switch (event.getType()) {
                            case NODE_ADDED:
                                byte[] bytes = data.getData();
                                String[] split = new String(bytes).split(",");
                                if (split.length >= 3) {
//                                    logger.info("服务路径：{}", data.getPath());
                                    logger.info("服务上线：{}", new String(bytes));
                                    ProviderInfo providerInfo = new ProviderInfo(split[0], split[1], split[2]);
                                    cacheList.add(providerInfo);
                                }
                                break;
                            case NODE_REMOVED:
                                bytes = data.getData();

                                split = new String(bytes).split(",");
                                if (split.length >= 3) {
//                                    logger.info("服务路径：{}", data.getPath());
                                    logger.info("服务下线：{}", new String(bytes));
                                    ProviderInfo providerInfo = new ProviderInfo(split[0], split[1], split[2]);
                                    cacheList.remove(providerInfo);
                                }
                                break;
                            case NODE_UPDATED:
                                bytes = data.getData();
                                split = new String(bytes).split(",");
                                if (split.length >= 3) {
//                                    logger.info("服务路径：{}", data.getPath());
                                    logger.info("服务更新：{}", new String(bytes));
                                    ProviderInfo providerInfo = new ProviderInfo(split[0], split[1], split[2]);
                                    cacheList.add(providerInfo);
                                }
                                break;
                            default:
                                break;
                        }
                    } else {
                        System.out.println("data is null : " + event.getType());
                    }
                }
            });
            //开始监听
            treeCache.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取一个服务提供者
     *
     * @param serviceName
     * @return
     */
    public ProviderInfo discover(String serviceName) throws RpcException {
        if (cacheList.isEmpty()) {
            return null;
        }
        List<ProviderInfo> providerInfos = cacheList.stream().filter(p -> serviceName.equals(p.getServerName())).collect(Collectors.toList());
        if (providerInfos.isEmpty()) {
            throw new RpcException("服务未发现");
        }
        //todof 负载均衡  暂时简单的随机
        int index = ThreadLocalRandom.current().nextInt(providerInfos.size());
        return providerInfos.get(index);
    }
}
