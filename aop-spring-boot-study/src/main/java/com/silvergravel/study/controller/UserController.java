package com.silvergravel.study.controller;

import com.silvergravel.study.aop.Role;
import com.silvergravel.study.service.PerformanceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author DawnStar
 * @since : 2024/4/23
 */
@RestController
public class UserController {

    @Resource
    private PerformanceService performanceService;

    @GetMapping("/admin")
    @Role(roles = "Admin")
    public String admin(@RequestParam String username) {
        return performanceService.findAll().toString();
    }

    /**
     * GET <a href="http://localhost:8080/vip?username=Dawn">...</a>
     * GET <a href="http://localhost:8080/vip?username=Silver">...</a>
     * @param username 用户名称
     */
    @GetMapping("/vip")
    @Role(roles = {"Admin", "VIP"})
    public String vip(@RequestParam String username) {
        return "Admin与VIP用户可以查看";
    }

    @GetMapping("/all")
    public String all() {
        return "所有用户都可以查看";
    }



}
