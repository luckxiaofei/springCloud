package com.neo.sbrpccorestarter.model;

import java.util.Objects;

/**
 * @author luckxiaofei@outlook.com
 * @date 2018/10/26 17:57
 * @description
 */
public class ProviderInfo {

    private String name;

    private String addr;

    public ProviderInfo() {
    }

    public ProviderInfo(String name, String addr) {
        this.name = name;
        this.addr = addr;
    }

    public String getName() {
        return name;
    }

    public ProviderInfo setName(String name) {
        this.name = name;
        return this;
    }

    public String getAddr() {
        return addr;
    }

    public ProviderInfo setAddr(String addr) {
        this.addr = addr;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProviderInfo that = (ProviderInfo) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(addr, that.addr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, addr);
    }

    @Override
    public String toString() {
        return "ProviderInfo{" +
                "name='" + name + '\'' +
                ", addr='" + addr + '\'' +
                '}';
    }
}
