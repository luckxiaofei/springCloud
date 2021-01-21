package com.neo.sbrpccorestarter.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author luckxiaofei@outlook.com
 * @date 2018/10/26 17:08
 * @description
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcServiceResource {

    /**
     * 服务名称
     *
     * @return
     */
    String name() default "";

}
