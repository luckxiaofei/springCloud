package com.neo.sbrpccorestarter.consumer;


import com.neo.sbrpccorestarter.model.ProviderInfo;
import com.neo.sbrpccorestarter.model.RpcRequest;
import com.neo.sbrpccorestarter.model.RpcResponse;
import com.neo.sbrpccorestarter.registory.DiscoveryService;

import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * @author luckxiaofei@outlook.com
 * @date 2018/10/26 17:11
 * @description
 */
public class RpcProxy {

    private DiscoveryService discoveryService;

    @SuppressWarnings("unchecked")
    public <T> T create(Class<?> interfaceClass, String serviceName) {
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass},
                (proxy, method, args) -> {
                    // 通过netty向Rpc服务发送请求。
                    // 构建一个请求。
                    RpcRequest request = new RpcRequest();
                    request.setRequestId(UUID.randomUUID().toString())
                            .setClassName(method.getDeclaringClass().getName())
                            .setMethodName(method.getName())
                            .setParamTypes(method.getParameterTypes())
                            .setParams(args);
                    // 获取一个服务提供者。
                    ProviderInfo providerInfo = discoveryService.discover(serviceName);
                    // 解析服务提供者的地址信息，数组第一个元素为ip地址，第二个元素为端口号。
                    String[] addrInfo = providerInfo.getServerAddr().split(":");
                    String host = addrInfo[0];
                    int port = Integer.parseInt(addrInfo[1]);
                    RpcClient rpcClient = new RpcClient(host, port);
                    // 发送调用消息。
                    RpcResponse response = rpcClient.send(request);
                    if (response.isError()) {
                        throw response.getError();
                    } else {
                        return response.getResult();
                    }
                });
    }

    public void setServiceDiscovery(DiscoveryService discoveryService) {
        this.discoveryService = discoveryService;
    }
}
