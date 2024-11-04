package com.silvergravel.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author DawnStar
 * @since : 2024/11/2
 */
@EnableScheduling
@SpringBootApplication
public class ScheduleApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder()
                .sources(ScheduleApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
        Map<String, TaskScheduler> type = context.getBeansOfType(TaskScheduler.class);
        type.forEach((s, taskScheduler) -> System.out.println(s));
    }

    @Component
    static class ScheduleTaskClass {

        protected final Log logger = LogFactory.getLog(getClass());

        @Schedules({@Scheduled(fixedDelay = 5000,scheduler = "aaa"), @Scheduled(fixedDelay = 7000)})
        public void delayTwoSeconds() {
            logger.info("上个任务完成之后的等待指定间隔执行下一个任务...");
        }

        @Scheduled(fixedRate = 2000)
        public void rateTwoSeconds() {
            logger.info("固定频率执行任务....");
        }

        @Scheduled(initialDelay = 1, timeUnit = TimeUnit.MINUTES)
        public void initialDelay() {
            logger.info("一分钟之后执行一次任务...");
        }

        @Scheduled(cron = "0/9 * * * * *")
        public void cron() {
            logger.info("每九秒执行一次任务...");
        }
    }

    @Bean
    public TaskScheduler aaa() {
        return new ThreadPoolTaskScheduler();
    }

    @Bean
    public TaskScheduler bbb() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setThreadNamePrefix("bbb");
        return threadPoolTaskScheduler;

    }

    /**
     * 注意：这个没有被使用，要使用如下
     * @param taskScheduler 指定的线程池
     * @return ScheduledTaskRegistrar
     */
    @Bean
    public ScheduledTaskRegistrar scheduledTaskRegistrar(@Qualifier("bbb") TaskScheduler taskScheduler) {
        ScheduledTaskRegistrar taskRegistrar = new ScheduledTaskRegistrar();
        taskRegistrar.setScheduler(taskScheduler);
        return taskRegistrar;
    }

    /**
     * 这个不会覆盖原始的：ScheduledAnnotationBeanPostProcessor
     * @param scheduledTaskRegistrar taskScheduler
     * @return ScheduledAnnotationBeanPostProcessor
     */
//    @Bean(TaskManagementConfigUtils.SCHEDULED_ANNOTATION_PROCESSOR_BEAN_NAME)
    @Bean
    public ScheduledAnnotationBeanPostProcessor scheduledAnnotationBeanPostProcessor(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        return new ScheduledAnnotationBeanPostProcessor(scheduledTaskRegistrar);
    }
}
