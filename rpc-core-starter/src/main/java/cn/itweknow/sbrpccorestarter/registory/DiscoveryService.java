package cn.itweknow.sbrpccorestarter.registory;

import cn.itweknow.sbrpccorestarter.common.Constants;
import cn.itweknow.sbrpccorestarter.exception.RpcException;
import cn.itweknow.sbrpccorestarter.exception.ZkConnectException;
import cn.itweknow.sbrpccorestarter.model.ProviderInfo;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.BackgroundPathable;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * @author luckxiaofei@outlook.com
 * @date 2018/10/26 17:27
 * @description
 */
public class DiscoveryService {

    private Logger logger = LoggerFactory.getLogger(DiscoveryService.class);

    private volatile List<ProviderInfo> cacheList = new ArrayList<>();

    public DiscoveryService(CuratorFramework curatorFramework) throws ZkConnectException {
        watchNode(curatorFramework);
    }

    public void watchNode(final CuratorFramework client) {
        try {
            List<ProviderInfo> providerInfos = new ArrayList<>();
            List<String> moduleNodeList = client.getChildren().forPath(Constants.RPC_ROOT_NOTE);
            for (String moduleName : moduleNodeList) {
                List<String> moduleChildrenNodeList = client.getChildren().forPath(Constants.RPC_ROOT_NOTE + "/" + moduleName);
                for (String serviceNode : moduleChildrenNodeList) {
                    BackgroundPathable<byte[]> backgroundPathable = client.getData().usingWatcher((CuratorWatcher) p -> {
                        if (p.getType().equals(Watcher.Event.EventType.NodeCreated)) {
                            logger.info("服务上线：{}", serviceNode);
                        }
                    });

                    String[] providerInfo = new String(bytes).split(",");
                    if (providerInfo.length == 2) {
                        providerInfos.add(new ProviderInfo(providerInfo[0], providerInfo[1]));
                    }
                }
            }
            this.cacheList = providerInfos;


            String nodePath = "/super/demo";

            // 为子节点添加watcher
            // PathChildrenCache: 监听数据节点的增删改，会触发事件
            String childNodePathCache = nodePath;

            //新建一个子节点缓存
            PathChildrenCache childrenCache = new PathChildrenCache(client, childNodePathCache, true);//cacheData: 设置缓存节点的数据状态，如果为true，也会将子节点的状态信息缓存下来


            /**
             * StartMode: 初始化方式
             * POST_INITIALIZED_EVENT：异步初始化，初始化之后会触发初始化事件（推荐）
             * NORMAL：异步初始化
             * BUILD_INITIAL_CACHE：同步初始化
             */
            childrenCache.start(StartMode.POST_INITIALIZED_EVENT);

            //获取缓存的子节点数据
            List<ChildData> childDataList = childrenCache.getCurrentData();
            System.out.println("当前数据节点的子节点数据列表：");
            for (ChildData cd : childDataList) {
                String childData = new String(cd.getData());
                System.out.println(childData);
            }

            //为子节点缓存添加监听器，可以对子节点触发的event的类型进行判断
            childrenCache.getListenable().addListener((client, event) -> {
                if (event.getType().equals(PathChildrenCacheEvent.Type.INITIALIZED)) {//初始化事件触发
                    System.out.println("子节点初始化ok...");
                } else if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)) {//增加子节点
                    String path = event.getData().getPath();
                    if (path.equals(ADD_PATH)) {
                        System.out.println("添加子节点:" + event.getData().getPath());
                        System.out.println("子节点数据:" + new String(event.getData().getData()));
                    } else if (path.equals("/super/imooc/e")) {
                        System.out.println("添加不正确...");
                    }
                } else if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_REMOVED)) {//删除子节点
                    System.out.println("删除子节点:" + event.getData().getPath());
                } else if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_UPDATED)) {//子节点数据修改事件
                    System.out.println("修改子节点路径:" + event.getData().getPath());
                    System.out.println("修改子节点数据:" + new String(event.getData().getData()));
                }
            });


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
