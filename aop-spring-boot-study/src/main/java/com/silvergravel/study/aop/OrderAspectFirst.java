package com.silvergravel.study.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 使用 @Order(100)使得First优先级低于Second，常规First优于Second
 * @author DawnStar
 * @since : 2023/12/19
 */
@Aspect
@Component
@Order(100)
@Slf4j
public class OrderAspectFirst {
    @Pointcut("execution(public * com.silvergravel.study.service..*print(..))")
    private void pointcut() {
    }

    /**
     * "pointcut()为@Pointcut标注的方法
     */
    @Before("pointcut()")
    public void before(JoinPoint joinPoint) throws Throwable {
        log.info("Before First");
    }

    @AfterReturning(value = "pointcut()", returning = "retVal")
    public void afterReturning(JoinPoint joinPoint, Object retVal) throws Throwable {
        log.info("返回值：" + retVal);
        log.info("AfterReturning First");
    }

    @AfterThrowing(value = "pointcut()", throwing = "exception")
    public void afterThrowing(JoinPoint joinPoint, Exception exception) throws Throwable {
        log.info("AfterThrowing First: {}", exception.getMessage());
    }

    @After("pointcut()")
    public void after(JoinPoint joinPoint) throws Throwable {
        log.info("After First");
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Around Before First");
        Object proceed = joinPoint.proceed();
        log.info("Around After First");
        return proceed;
    }

}
