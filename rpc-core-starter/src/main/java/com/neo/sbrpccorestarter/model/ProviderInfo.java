package com.neo.sbrpccorestarter.model;

import lombok.Data;

import java.util.Objects;

/**
 * @author luckxiaofei@outlook.com
 * @date 2018/10/26 17:57
 * @description
 */
@Data
public class ProviderInfo {

    /**
     * 模块Name
     */
    private String moduleName;

    /**
     * 服务名
     */
    private String serverName;

    /**
     * 服务地址
     */
    private String serverAddr;


    public ProviderInfo(String moduleName, String serverName, String serverAddr) {
        this.moduleName = moduleName;
        this.serverName = serverName;
        this.serverAddr = serverAddr;
    }
}
