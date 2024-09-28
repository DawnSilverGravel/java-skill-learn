package com.silvergravel.study.event.event;

import com.silvergravel.study.event.po.UserPointPO;
import com.silvergravel.study.event.service.UserPointService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author DawnStar
 * @since : 2024/9/22
 */
@Component
@Slf4j
public class UserPointListener {

    @Resource
    private UserPointService userPointService;

    private final ThreadLocalRandom random = ThreadLocalRandom.current();

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
//    @Transactional(propagation = Propagation.NOT_SUPPORTED, rollbackFor = Exception.class)
//    @Async
    public void handleUserPoint(UserPointEvent event) {
        log.info("event execute");
        UserPointPO userPointPO = new UserPointPO();
        userPointPO.setPoint(random.nextInt(800, 16767));
        userPointPO.setId(1);
        userPointPO.setUserId(1);
        userPointService.save(userPointPO);
        userPointService.query(event.getUserId());
    }
}
