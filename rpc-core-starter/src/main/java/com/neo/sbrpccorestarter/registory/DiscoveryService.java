package com.neo.sbrpccorestarter.registory;

import com.neo.sbrpccorestarter.exception.RpcException;
import com.neo.sbrpccorestarter.exception.ZkConnectException;
import com.neo.sbrpccorestarter.model.ProviderInfo;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
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
            List<String> moduleNodeList = client.getChildren().forPath("/");//找到服务模块名
            for (String moduleName : moduleNodeList) {
                //模块下的服务
                List<String> moduleChildrenNodeList = client.getChildren().forPath("/" + moduleName);
                for (String serviceNode : moduleChildrenNodeList) {
                    client.getData().usingWatcher((CuratorWatcher) p -> {
                        if (p.getType().equals(Watcher.Event.EventType.NodeCreated)) {//创建节点
                            logger.info("服务上线：{}", serviceNode);
                            String path = p.getPath();
                            byte[] bytes = client.getData().forPath(serviceNode);
                            String s1 = new String(bytes);
                            String[] split = String.valueOf(bytes).split(",");
                            providerInfos.add(new ProviderInfo(split[0], split[1]));
                        } else if (p.getType().equals(Watcher.Event.EventType.NodeDeleted)) {
                            logger.info("服务下线：{}", serviceNode);
                            String path = p.getPath();
                            byte[] bytes = client.getData().forPath(serviceNode);
                            String s1 = new String(bytes);
                            String[] split = String.valueOf(bytes).split(",");
                            providerInfos.remove(new ProviderInfo(split[0], split[1]));
                        }
                    }).forPath("/" + serviceNode);
                }
            }
            this.cacheList = providerInfos;
        } catch (Exception e) {
            logger.error("watch error,", e);
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
        List<ProviderInfo> providerInfos = cacheList.stream().filter(p -> serviceName.equals(p.getName())).collect(Collectors.toList());
        if (providerInfos.isEmpty()) {
            throw new RpcException("服务未发现");
        }
        //todof 负载均衡  暂时简单的随机
        int index = ThreadLocalRandom.current().nextInt(providerInfos.size());
        return providerInfos.get(index);
    }
}
