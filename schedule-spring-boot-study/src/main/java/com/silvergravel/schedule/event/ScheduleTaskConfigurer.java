package com.silvergravel.schedule.event;

import com.silvergravel.schedule.service.SyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;

/**
 * @author DawnStar
 * @since : 2024/11/3
 */
@Component
public class ScheduleTaskConfigurer implements SchedulingConfigurer, ApplicationContextAware {

    private final Logger log = LoggerFactory.getLogger(ScheduleTaskConfigurer.class);

    /**
     * 容器上下文，用于获取指定的服务
     */
    private ApplicationContext appCxt;

    private ScheduledTaskRegistrar taskRegistrar;


    private final HashMap<String, String> beanNameExpressionMap = new HashMap<>(16);

    private final HashMap<String, ScheduledFuture<?>> beanNameScheduledTaskMap = new HashMap<>(16);


    @Override
    public void setApplicationContext(@Nullable ApplicationContext applicationContext) throws BeansException {
        this.appCxt = applicationContext;
    }

    @Override
    public void configureTasks(@Nullable ScheduledTaskRegistrar taskRegistrar) {
        this.taskRegistrar = taskRegistrar;
        refresh();
    }

    synchronized void refresh() {
        Map<String, SyncService> syncServiceMap = appCxt.getBeansOfType(SyncService.class);
        syncServiceMap.forEach(this::refreshTaskCron);
    }

    private void refreshTaskCron(String beanName, SyncService syncService) {
        CronTrigger cronTrigger = syncService.getCronTrigger();
        if (cronTrigger == null) {
            log.warn("{} 没有 cron ", beanName);
            cancelTask(beanName);
            return;
        }
        String currentExpression = beanNameExpressionMap.get(beanName);
        if (cronTrigger.getExpression().equals(currentExpression)) {
            log.info("{} 当前cron表达式未变更", beanName);
            return;
        }
        cancelTask(beanName);
        scheduleNewTask(beanName, cronTrigger, syncService::executeSync);
    }

    private void scheduleNewTask(String taskName, CronTrigger cronTrigger, Runnable executeSync) {
        CronTask cronTask = new CronTask(executeSync, cronTrigger);
        ScheduledFuture<?> schedule = Objects.requireNonNull(taskRegistrar.getScheduler()).schedule(cronTask.getRunnable(), cronTask.getTrigger());
        beanNameExpressionMap.put(taskName, cronTrigger.getExpression());
        beanNameScheduledTaskMap.put(taskName, schedule);
        log.info("创建新定时任务 {}", taskName);
    }

    private void cancelTask(String beanName) {
        ScheduledFuture<?> scheduledFuture = beanNameScheduledTaskMap.get(beanName);
        if (Objects.isNull(scheduledFuture)) {
            return;
        }
        // false 表示允许任务完成后取消
        scheduledFuture.cancel(false);
        log.info("取消定时任务 {}", beanName);
    }


}
