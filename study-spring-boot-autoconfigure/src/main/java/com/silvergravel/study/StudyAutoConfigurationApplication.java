package com.silvergravel.study;

import com.silvergravel.study.config.DatabaseProperties;
import com.silvergravel.study.service.*;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.autoconfigure.web.ConditionalOnEnabledResourceChain;
import org.springframework.boot.cloud.CloudPlatform;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.system.JavaVersion;
import org.springframework.context.annotation.Bean;


/**
 * @author DawnStar
 */
@AutoConfiguration
@EnableConfigurationProperties(DatabaseProperties.class)
public class StudyAutoConfigurationApplication {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "study.datasource", name = "enable", havingValue = "true", matchIfMissing = true)
    @ConditionalOnJava(value = JavaVersion.SEVENTEEN)
    public DatabaseService databaseService(DatabaseProperties databaseProperties) {
        return new DatabaseService(databaseProperties);
    }

    @Bean
    @ConditionalOnResource(resources = "file:P:\\IDEA\\Learn\\java-skill-learn\\study-spring-boot-test\\src\\main\\resources\\bootstrap.yml")
    public ResourceService resourceService() {
        String resource = "classpath:bootstrap.yml";
        return new ResourceService(resource);
    }

    @Bean
    @ConditionalOnNotWebApplication
    @ConditionalOnNotWarDeployment
    @ConditionalOnCloudPlatform(CloudPlatform.NONE)
    public WebService notWeService() {
        return new WebService(WebApplicationType.NONE);
    }

    @Bean
    @ConditionalOnCloudPlatform(CloudPlatform.NONE)
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    public WebService servletService() {
        return new WebService(WebApplicationType.SERVLET);
    }

    @Bean
    @ConditionalOnEnabledResourceChain
    public ResourceChainService resourceChainService() {
        return new ResourceChainService();
    }


    @Bean
//    @ConditionalOnBean(type = "com.silvergravel.study.service.SortServiceB")
    @ConditionalOnBean(name = "serviceB")
//    @ConditionalOnClass(value = SortServiceB.class)
//    @ConditionalOnClass(name = "com.silvergravel.study.service.SortServiceB")
    public SortServiceA serviceA() {
        return new SortServiceA();
    }


}