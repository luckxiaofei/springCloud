package com.neo.controller;

import cn.itweknow.sbrpccorestarter.anno.RpcServiceResource;
import com.neo.service.HelloService;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 陈小飞 luckxiaofei@outlook.com
 * @date 12/28/20 6:36 下午
 */
@RestController
public class TestController {
    @RpcServiceResource
    private HelloService helloService;
}
