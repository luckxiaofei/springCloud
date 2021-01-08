package cn.itweknow.sbrpccorestarter.anno;

import org.springframework.stereotype.Service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author luckxiaofei@outlook.com
 * @date 2018/10/26 14:03
 * @description
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Service//把普通pojo实例化到spring容器中
public @interface RpcService {

    String value() default "";

}
