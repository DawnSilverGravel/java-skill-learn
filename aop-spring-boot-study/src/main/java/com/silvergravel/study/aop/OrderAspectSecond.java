package com.silvergravel.study.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * 实现 Ordered接口使得Second的优先级高于First
 *
 * @author DawnStar
 * @since : 2023/12/19
 */
@Aspect
@Component
@Slf4j
public class OrderAspectSecond implements Ordered {
    @Pointcut("execution(public * com.silvergravel.study.service..*print(..))")
    private void pointcut() {
    }

    /**
     * "pointcut()为@Pointcut标注的方法
     */
    @Before("pointcut()")
    public void before(JoinPoint joinPoint) throws Throwable {
        log.info("Before Second");
    }

    @AfterReturning(value = "pointcut()", returning = "retVal")
    public void afterReturning(JoinPoint joinPoint, Object retVal) throws Throwable {
        log.info("返回值：{}", retVal);
        log.info("AfterReturning Second");
    }

    @AfterThrowing(value = "pointcut()", throwing = "exception")
    public void afterThrowing(JoinPoint joinPoint, Exception exception) throws Throwable {
        log.error("AfterThrowing Second: {}", exception.getMessage());
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Around Before Second");
        Object proceed = joinPoint.proceed();
        log.info("Around After Second");
        return proceed;
    }

    @After("pointcut()")
    public void after(JoinPoint joinPoint) throws Throwable {
        log.info("After Second");
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
