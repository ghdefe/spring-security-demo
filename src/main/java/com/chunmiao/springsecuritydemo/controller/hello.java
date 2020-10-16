package com.chunmiao.springsecuritydemo.controller;

import com.chunmiao.springsecuritydemo.entity.UserDetailImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

@RestController
public class hello {

    // 拥有ADMIN角色才可以访问
    @PreAuthorize("hasAnyRole('ADMIN')")
    @RequestMapping("/hello")
    String test() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof UserDetailImpl) {
            UserDetailImpl user = (UserDetailImpl) authentication.getPrincipal();
            System.out.println(user.getUsername());
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            for (GrantedAuthority authority : authorities) {
                System.out.println(authority.getAuthority());
            }
        }
        return "hello";
    }
}
