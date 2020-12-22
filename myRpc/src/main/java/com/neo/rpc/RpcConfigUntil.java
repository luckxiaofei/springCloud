package com.neo.rpc;


import com.neo.annotation.RpcServiceResource;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.AbstractApplicationContext;

import java.lang.reflect.Field;

/**
 * @author fei
 */
@Configuration
public class RpcConfigUntil implements ApplicationListener<ApplicationEvent> {

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        // 这里的生命周期不做介绍了，大家自己搜搜即可。
        if ((event instanceof ContextRefreshedEvent)) {
            try {
                AbstractApplicationContext context = (AbstractApplicationContext) event.getSource();
                // 在合适的生命周期下（ContextRefreshedEvent），进行远程对象的注入
                injectionRemoteService(context);
            } catch (Exception e) {
            }
        }

    }

    /**
     * 注入的原理就是，扫描所有的注册到Spring中的对象中的属性，凡是使用了@MyRpc注解的属性，都手动实例化一个远程对象并且set给这个属性
     *
     * @param context
     */
    public void injectionRemoteService(AbstractApplicationContext context) {
        String[] beannames = context.getBeanDefinitionNames();
        for (String name : beannames) {
            Object service = context.getBean(name);
            Object target = null;
            try {
                target = getRealTarget(service);
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }

            for (Field field : target.getClass().getDeclaredFields()) {
                RpcServiceResource rpc = field.getAnnotation(RpcServiceResource.class);
                if (null != rpc) {
                    field.setAccessible(true);

                    String serviceName = rpc.value();

                    try {

                        if (!context.getBeanFactory().containsSingleton(serviceName)) {
                            System.err.println("injectionRemoteService serviceName " + serviceName);
                            System.err.println("injectionRemoteService field " + field.getType());
                            ClientStub consumerBean = createConsumerBean(field.getType(), serviceName); // 实例化远程对象
                            context.getBeanFactory().registerSingleton(serviceName, consumerBean); // 注册到Spring
                        } else {
                        }
                        Object remoteBean = context.getBean(serviceName);

                        field.set(target, remoteBean);

                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        break;
                    } catch (Exception e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        }
    }

    /**
     * 实例化远程对象，就是new了一个ClientStub对象。
     *
     * @param clzz
     * @param serviceName
     * @return
     */
    private ClientStub createConsumerBean(Class<?> clzz, String serviceName) {
        ClientStub consumerBean = new ClientStub(clzz);
        consumerBean.setServiceName(serviceName);
        return consumerBean;
    }

    /**
     * 递归获取原本的对象
     *
     * @param bean
     * @return
     * @throws Exception
     */
    public static Object getRealTarget(Object bean) throws Exception {
        if ((AopUtils.isAopProxy(bean)) && ((bean instanceof Advised))) {
            Advised advised = (Advised) bean;
            bean = advised.getTargetSource().getTarget();
            return getRealTarget(bean);
        }
        return bean;
    }

}
