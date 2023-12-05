package com.silvergravel.enable.annotation;

import com.silvergravel.enable.service.DnsServer;
import com.silvergravel.enable.service.HttpServer;
import com.silvergravel.enable.service.Server;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author DawnStar
 * @since : 2023/12/5
 */
public class ServerImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    @SuppressWarnings("all")
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        Map<String, Object> annotationAttributes = importingClassMetadata.getAnnotationAttributes(EnableServer.class.getName());
        Server.Type type = (Server.Type) annotationAttributes.get("type");
        String[] beanNames = switch (type) {
            case DNS -> new String[]{DnsServer.class.getName()};
            case HTTP -> new String[]{HttpServer.class.getName()};
        };
        List<AbstractBeanDefinition> abstractBeanDefinitions = Stream.of(beanNames)
                .map(beanName -> BeanDefinitionBuilder
                        .genericBeanDefinition(beanName)
                        .getBeanDefinition())
                .toList();
        abstractBeanDefinitions.forEach(
                abstractBeanDefinition -> BeanDefinitionReaderUtils.registerWithGeneratedName(abstractBeanDefinition, registry)
        );
    }
}
