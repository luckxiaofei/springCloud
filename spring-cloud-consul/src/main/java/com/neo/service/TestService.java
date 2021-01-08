package com.neo.service;

import com.neo.annotation.RpcService;

/**
 * @author 陈小飞 luckxiaofei@outlook.com
 * @date 12/24/20 5:04 下午
 */
@RpcService
public class TestService {

    public String getTest() {
        return "我是测试";
    }

}
