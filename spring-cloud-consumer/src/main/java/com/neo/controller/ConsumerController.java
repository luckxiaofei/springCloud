package com.neo.controller;

import com.neo.annotation.RpcServiceResource;
import com.neo.remote.HelloRemote;
import com.neo.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author fei
 */
@RestController
public class ConsumerController {

    @Autowired
    HelloRemote HelloRemote;
    @RpcServiceResource("helloService")
    HelloService helloService;

    @RequestMapping("/hello/{name}")
    public String index(@PathVariable("name") String name) {
        String info = helloService.getInfo(name);
        String hello = HelloRemote.hello(name);
        return hello;
    }

}
