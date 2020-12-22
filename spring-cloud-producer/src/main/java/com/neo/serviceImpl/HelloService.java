package com.neo.serviceImpl;

import com.neo.annotation.RpcService;

@RpcService
public class HelloService implements com.neo.service.HelloService {

    @Override
    public String getInfo(String name) {
        return "我是helloServiceImpl的：" + name;
    }
}
