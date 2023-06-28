package com.example.filter;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Description:
 *
 * @author DawnStar
 * Date: 2023/6/26
 */
public class MyFilter extends OncePerRequestFilter {
    @Resource
    private UserDetailsService userDetailsService;

    @Resource
    private PasswordEncoder encoder;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // jwt 相关实现差不多
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        if (username != null && password != null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (userDetails != null) {
                boolean matches = encoder.matches(password, userDetails.getPassword());
                if (matches) {
                    // 核心点还是这里,设置当前Authentication
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                            UsernamePasswordAuthenticationToken.authenticated(username, password, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}