package com.silvergravel.schedule.event;

import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * @author DawnStar
 * @since : 2024/11/3
 */
@Component
public class ScheduleTaskApplicationListener implements ApplicationListener<CronEvent> {

    private final Logger log = LoggerFactory.getLogger(ScheduleTaskApplicationListener.class);

    @Resource
    private ScheduleTaskConfigurer taskConfigurer;

    @Override
    public void onApplicationEvent(@NonNull CronEvent event) {
        log.info("任务事件：" + event);
        taskConfigurer.refresh();
    }
}
