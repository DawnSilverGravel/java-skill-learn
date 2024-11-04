package com.silvergravel.schedule.service;

import org.springframework.scheduling.support.CronTrigger;

/**
 * @author DawnStar
 * @since : 2024/11/3
 */
public interface SyncService {

    /**
     * 执行同步
     */
    void executeSync();

    /**
     * 返回指定触发器
     * @return 返回指定的触发器
     */
    CronTrigger getCronTrigger();

}
