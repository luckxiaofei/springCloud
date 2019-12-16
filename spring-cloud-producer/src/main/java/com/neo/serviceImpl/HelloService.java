package com.neo.serviceImpl;

import com.demo.annotation.RpcService;

@RpcService
public class HelloService implements com.demo.service.HelloService {

    @Override
    public String getInfo(String name) {
        return "我是helloServiceImpl的："+name;
    }
}
