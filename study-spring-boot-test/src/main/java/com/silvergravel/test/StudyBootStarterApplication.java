package com.silvergravel.test;

import com.silvergravel.study.service.DatabaseService;
import com.silvergravel.study.service.ResourceChainService;
import com.silvergravel.study.service.ResourceService;
import com.silvergravel.study.service.WebService;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author DawnStar
 */
@SpringBootApplication
public class StudyBootStarterApplication {
    private static boolean enable;


    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(StudyBootStarterApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
        DatabaseService bean = context.getBean(DatabaseService.class);
        bean.print();
        ResourceService resourceService = context.getBean(ResourceService.class);
        resourceService.resourcePrint();
        WebService webService = context.getBean(WebService.class);
        webService.webPrint();
        ResourceChainService resourceChainService = context.getBean(ResourceChainService.class);
        resourceChainService.resourceChainPrint();

    }
}