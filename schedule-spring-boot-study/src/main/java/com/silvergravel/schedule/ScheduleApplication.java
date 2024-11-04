package com.silvergravel.schedule;

import com.silvergravel.schedule.service.SyncServiceImpl;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.TimeUnit;

/**
 * @author SilverGravel
 */
@SpringBootApplication
@EnableScheduling
public class ScheduleApplication {
    public static void main(String[] args) throws InterruptedException {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(ScheduleApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
        // 延迟15秒之后指定
        TimeUnit.SECONDS.sleep(15);
        SyncServiceImpl bean = context.getBean(SyncServiceImpl.class);
        // 改成每10秒执行一次
        bean.updateCron("0/10 * * * * *");
    }
}