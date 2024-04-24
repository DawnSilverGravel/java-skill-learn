package com.silvergravel.study.aop;

import com.silvergravel.study.exception.BusinessException;
import com.silvergravel.study.service.UserRoleService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 权限校验
 *
 * @author DawnStar
 * @since : 2024/4/21
 */
@Aspect
@Component
@Slf4j
public class AuthorityAspect {

    private final Map<String, Integer> map = new HashMap<>();


    @Resource
    private UserRoleService userRoleService;

    /**
     * 拦截service中的checkUser方法
     */
    @Pointcut("execution(public boolean com.silvergravel.study.service..checkUser(String,String))")
    private void recordLoginFailure() {

    }

    @Pointcut("execution(* com.silvergravel.study.controller.UserController.*(..))")
    public void httpMethod() {

    }

    /**
     * Order注解无效，根据自然顺序执行,before 先于 beforeGetMapping
     * 调用 {@link com.silvergravel.study.controller.UserController#vip(String)} 可知
     */
    @Before("@annotation(getMapping)")
    @Order(-100)
    public void beforeGetMapping(GetMapping getMapping) {
        log.info("只有含有@GetMapping的方法调用：{}", getMapping.name());
    }

    /**
     * Order注解无效，根据自然顺序执行,before 先于 beforeGetMapping
     * 调用 {@link com.silvergravel.study.controller.UserController#vip(String)} 可知
     */
    @Before(value = "httpMethod()  && @annotation(role) &&args(name)", argNames = "role,name")
    @Order(-10)
    public void before(Role role, String name) {

        boolean condition = userRoleService.hasAnyRole(name, Arrays.stream(role.roles()).toList());
        if (!condition) {
            throw new BusinessException("你没有权限访问");
        }
    }


    @AfterReturning(value = "recordLoginFailure()", returning = "condition")
    public void afterReturning(JoinPoint joinPoint, boolean condition) {
//        System.out.println(joinPoint.getTarget().getClass());
//        System.out.println(joinPoint.getThis().getClass());
        String name = joinPoint.getArgs()[0].toString();
        String password = joinPoint.getArgs()[1].toString();
        if (!condition) {
            Integer integer = map.getOrDefault(name, 0) + 1;
            map.put(name, integer);
            log.warn("用户：{} 连续校验失败，本次校验密码：{}，总失败次数：{}", name, password, integer);
            return;
        }
        map.put(name, 0);
    }

    @After("httpMethod() && args()")
    public void after(JoinPoint joinPoint) {
        log.info("只用空参数的方法才可以调用，执行方法为: {}, {}", joinPoint.getSignature().getDeclaringTypeName()
                , joinPoint.getSignature().getName());
    }


}
