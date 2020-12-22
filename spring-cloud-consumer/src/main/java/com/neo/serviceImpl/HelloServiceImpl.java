package com.neo.serviceImpl;

import com.neo.annotation.RpcService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@RpcService
@Service
public class HelloServiceImpl {
    @PostConstruct
    public void init(){
        System.out.println("容器启动时执行");


    }
}
