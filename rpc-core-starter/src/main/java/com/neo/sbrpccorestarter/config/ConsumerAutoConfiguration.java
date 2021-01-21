package com.neo.sbrpccorestarter.config;

import com.neo.sbrpccorestarter.anno.RpcServiceResource;
import com.neo.sbrpccorestarter.consumer.RpcProxy;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;

/**
 * @author luckxiaofei@outlook.com
 * @date 2018/10/29 19:53
 * @description
 */
@Configuration
//判断当前classpath下是否存在指定类，若是则将当前的配置装载入spring容器
@ConditionalOnClass(RpcServiceResource.class)
@EnableConfigurationProperties(RpcProperties.class)
public class ConsumerAutoConfiguration {

    @Autowired
    private RpcProxy rpcProxy;


    /**
     * 设置动态代理
     *
     * @return
     */
    @Bean//Spring的@Bean注解用于告诉方法，产生一个Bean对象，然后这个Bean对象交给Spring管理。
    public BeanPostProcessor beanPostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessBeforeInitialization(Object bean, String beanName)
                    throws BeansException {
                Class<?> objClz = bean.getClass();
                for (Field field : objClz.getDeclaredFields()) {
                    RpcServiceResource rpcServiceResource = field.getAnnotation(RpcServiceResource.class);
                    if (null != rpcServiceResource) {
                        Class<?> type = field.getType();
                        String serviceName = type.getSimpleName();
                        if (!StringUtils.isEmpty(rpcServiceResource.name())) {
                            serviceName = rpcServiceResource.name();
                        }
                        field.setAccessible(true);
                        try {
                            field.set(bean, rpcProxy.create(type, serviceName));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } finally {
                            field.setAccessible(false);
                        }
                    }
                }
                return bean;
            }
        };
    }

}
