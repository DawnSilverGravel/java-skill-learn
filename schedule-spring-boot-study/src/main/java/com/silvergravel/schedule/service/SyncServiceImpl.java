package com.silvergravel.schedule.service;

import com.silvergravel.schedule.event.CronEvent;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * @author DawnStar
 * @since : 2024/10/27
 */
@Service

public class SyncServiceImpl implements SyncService {

    private final Logger log = LoggerFactory.getLogger(SyncServiceImpl.class);

    private CronTrigger cronTrigger;

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public SyncServiceImpl(@Value("${default-cron: 0/5 * * * * *}") String defaultCronExpression) {
        this.cronTrigger = new CronTrigger(defaultCronExpression);
    }

    @Override
    public CronTrigger getCronTrigger() {
        return cronTrigger;
    }

    @Override
    public void executeSync() {
        log.info("执行同步");
    }


    public void updateCron(String cronExpression) {
        Assert.notNull(cronExpression, "表达式不能空");
        boolean equals = ScheduledTaskRegistrar.CRON_DISABLED.equals(cronExpression);
        if (equals) {
            cronTrigger = null;
            applicationEventPublisher.publishEvent(new CronEvent(""));
            return;
        }
        boolean validExpression = CronExpression.isValidExpression(cronExpression);
        Assert.isTrue(validExpression, "表达式不正确");
        this.cronTrigger = new CronTrigger(cronExpression);
        applicationEventPublisher.publishEvent(new CronEvent(this.cronTrigger.getExpression()));
    }

}
