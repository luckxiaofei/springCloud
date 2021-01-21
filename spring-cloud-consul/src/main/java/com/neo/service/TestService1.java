package com.neo.service;


import com.neo.sbrpccorestarter.anno.RpcService;

/**
 * @author 陈小飞 luckxiaofei@outlook.com
 * @date 12/24/20 5:04 下午
 */
@RpcService
public class TestService1 implements HelloService {

    /**
     * 测试
     *
     * @param name
     * @return
     */
    @Override
    public String hello(String name) {
        return "我是测试";
    }
}
