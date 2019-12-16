package com.neo.controller;

import com.demo.annotation.RpcAutowired;
import com.demo.service.HelloService;
import com.neo.remote.HelloRemote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConsumerController {

    @Autowired
    HelloRemote HelloRemote;
    @RpcAutowired("helloService")
    HelloService helloService;

    @RequestMapping("/hello/{name}")
    public String index(@PathVariable("name") String name) {
        String info = helloService.getInfo(name);
        String hello = HelloRemote.hello(name);
        return hello;
    }

}