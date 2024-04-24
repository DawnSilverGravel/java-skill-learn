package com.silvergravel.study.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * 性能耗时监测类
 *
 * @author DawnStar
 * @since : 2024/4/21
 */
@Aspect
@Component
public class PerformanceAspect {

    @Pointcut("within(com.silvergravel.study.service..PerformanceService*)")
//    @Pointcut("bean(performanceService*)")
    private void pointcut() {

    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint pjp) {
        Object proceed = null;
        try {
            String format = String.format("执行%s的%s方法耗时...", pjp.getSignature().getDeclaringTypeName(), pjp.getSignature().getName());
            System.out.println(format);
            Instant now = Instant.now();
            proceed = pjp.proceed();
            long times = ChronoUnit.NANOS.between(now, Instant.now());
            System.out.printf("总耗时：%s ns\n",times);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return proceed;
    }
}
