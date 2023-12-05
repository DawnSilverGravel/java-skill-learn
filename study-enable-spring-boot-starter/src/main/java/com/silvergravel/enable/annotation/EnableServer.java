package com.silvergravel.enable.annotation;

import com.silvergravel.enable.service.Server;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author DawnStar
 * @since : 2023/12/5
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
//@Import(ServerImportSelector.class)
@Import(ServerImportBeanDefinitionRegistrar.class)
public @interface EnableServer {

    Server.Type type() default Server.Type.HTTP;
}
