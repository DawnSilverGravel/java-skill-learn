package com.silvergravel.study;

import com.silvergravel.study.dto.AuthorityDTO;
import com.silvergravel.study.service.PerformanceService;
import com.silvergravel.study.service.impl.OrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.util.List;

/**
 * @author DawnStar
 * @since : 2023/12/19
 */
@SpringBootApplication
@Slf4j
public class AopApplication implements ApplicationRunner {
    public static void main(String[] args) {
        new SpringApplicationBuilder(AopApplication.class)
                .run(args);

    }

    @Resource
    private PerformanceService performanceService;

    @Resource
    private OrderService orderService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("执行完AOP: {},请往上查看日志", orderService.print());
        List<AuthorityDTO> authorityDTOList = performanceService.findAll();
        System.out.println("用户权限相关信息");
        StringBuilder builder = new StringBuilder();
        builder.append("---------------------------------------------------------")
                .append("\n")
                .append("|\t\tID\t\t")
                .append("|\t\t用户\t\t\t")
                .append("|\t\t角色\t\t\t|")
                .append("\n");
        for (AuthorityDTO authorityDTO : authorityDTOList) {

            builder.append(String.format("|\t\t%s\t\t|\t\t%s\t\t|\t%s\t", authorityDTO.getUserId(), authorityDTO.getUsername(), authorityDTO.getRoleNames()));
            if (authorityDTO.getRoleNames().size() < 2) {
                builder.append("\t\t");
            }
            builder.append("|");
            builder.append("\n---------------------------------------------------------\n");
        }
        System.out.println(builder);
    }


}
