package com.example.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description:
 *
 * @author DawnStar
 * Date: 2023/6/26 21:02
 */
@RestController
@RequestMapping("/silver-gravel")
public class SilverGravelController {

    /**
     * http://localhost:8080/silver-gravel/permit
     * @return
     */
    @GetMapping("/permit")
    public String permit() {
        return "permit 路径下该资源无需登录验证即可访问";
    }

    /**
     * http://localhost:8080/silver-gravel/other?username=user&password=password
     * @return
     */
    @GetMapping("/other")
    public String other() {
        return "其他路径下资源需要登录验证才能访问";
    }

    /**
     * http://localhost:8080/silver-gravel/user?username=user&password=password
     * http://localhost:8080/silver-gravel/user?username=admin&password=password
     * @return
     */
    @GetMapping("/user")
    public String user() {
        return "user 路径下资源需要 USER 权限才能访问";
    }

    /**
     * http://localhost:8080/silver-gravel/admin?username=admin&password=password
     * http://localhost:8080/silver-gravel/admin?username=user&password=password
     * @return
     */
    @GetMapping("/admin")
    public String admin() {
        return "admin 路径下资源需要 ADMIN 或 ADMIN1 权限才能访问";
    }


    /**
     * http://localhost:8080/silver-gravel/admin/user?username=admin&password=password
     * http://localhost:8080/silver-gravel/admin/user?username=adminAndUser&password=password
     * @return
     */
    @GetMapping("/admin/user")
    @PreAuthorize("hasAuthority('DAWN_SILVER_GRAVEL_USER')")
    public String adminUser() {
        return "adminUser 资源需要 (ADMIN 或 ADMIN1)且USER 权限才能访问";
    }

    /**
     * http://localhost:8080/silver-gravel/admin/admin1?username=adminAll&password=password
     * http://localhost:8080/silver-gravel/admin/admin1?username=adminAndUser&password=password
     * @return
     */
    @GetMapping("/admin/admin1")
    @PreAuthorize("hasRole('ADMIN1')")
    public String admin1() {
        return "adminUser 资源需要 ADMIN1  权限才能访问";
    }

}
