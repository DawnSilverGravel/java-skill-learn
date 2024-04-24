package com.example.config;

import com.example.filter.MyFilter;
import com.example.handler.MyAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Description:
 *
 * @author DawnStar
 * Date: 2023/6/23
 */
@Configuration
public class SecurityConfiguration {

    public final static String ROLE_PREFIX = "DAWN_SILVER_GRAVEL_";

    @Bean
    public SecurityFilterChain dawnStarFilterChain(HttpSecurity httpSecurity)
            throws Exception {
        httpSecurity.formLogin()
                .and()
                // 匹配指定路径
                .regexMatcher("^/(dawn-star|login).*")
                // 使用FilterSecurityInterceptor过滤器
                .authorizeRequests()
                .antMatchers("/dawn-star/**")
                .authenticated();
        httpSecurity.exceptionHandling()
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("该账号没有权限访问资源！" + accessDeniedException.getMessage());
                    response.getWriter().close();
                });
        return httpSecurity.build();
    }

    @Bean
    public SecurityFilterChain silverGravelFilterChain(HttpSecurity httpSecurity)
            throws Exception {

        httpSecurity.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // 匹配指定路径
                .regexMatcher("^/silver-gravel/.*")
                // 使用 AuthenticationFilter
                .authorizeHttpRequests()
                // 允许该路径下的资源直接访问
                .antMatchers("/silver-gravel/permit/**").permitAll()
                // 该路径下的资源需要 USER 角色才能访问
                .antMatchers("/silver-gravel/user/**").hasAuthority(ROLE_PREFIX + "USER")
                // 该路径下的资源需要 ADMIN角色或 ADMIN1 角色才能访问
                .antMatchers("/silver-gravel/admin/**")
                .hasAnyAuthority(ROLE_PREFIX + "ADMIN", ROLE_PREFIX + "ADMIN1")
                // 其他资源需要验证即可访问
                .anyRequest().authenticated();

        httpSecurity.addFilterBefore(myFilter(), UsernamePasswordAuthenticationFilter.class);
        httpSecurity.exceptionHandling().accessDeniedHandler((request, response, accessDeniedException) -> {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("该账号没有权限访问资源！" + accessDeniedException.getMessage());
            response.getWriter().close();
        }).authenticationEntryPoint(new MyAuthenticationEntryPoint());
        return httpSecurity.build();
    }


    /**
     * 注入MyFilter
     */
    @Bean
    public MyFilter myFilter() {
        return new MyFilter();
    }


    /**
     * 注入指定编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 指定前缀
     * \@PreAuthorize("hasRole('USER')")
     */
    @Bean
    static GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults(ROLE_PREFIX);
    }

    /**
     * 注入用户信息
     */
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder()
                .authorities(ROLE_PREFIX + "USER")
                .password(passwordEncoder().encode("password"))
                .username("user")
                .build();

        UserDetails admin = User.builder()
                .authorities(ROLE_PREFIX + "ADMIN")
                .password(passwordEncoder().encode("password"))
                .username("admin")
                .build();

        UserDetails adminAll = User.builder()
                .authorities(ROLE_PREFIX + "ADMIN", ROLE_PREFIX + "ADMIN1")
                .password(passwordEncoder().encode("password"))
                .username("adminAll")
                .build();

        UserDetails adminAndUser = User.builder()
                .authorities(ROLE_PREFIX + "ADMIN", ROLE_PREFIX + "USER")
                .password(passwordEncoder().encode("password"))
                .username("adminAndUser")
                .build();

        return new InMemoryUserDetailsManager(user, admin, adminAll, adminAndUser);
    }

}
