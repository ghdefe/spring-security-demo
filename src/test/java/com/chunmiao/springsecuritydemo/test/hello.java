package com.chunmiao.springsecuritydemo.test;

import com.chunmiao.springsecuritydemo.entity.GrantedAuthorityImpl;
import com.chunmiao.springsecuritydemo.entity.UserDetailImpl;
import com.chunmiao.springsecuritydemo.utils.JWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;

public class hello {

    @Test
    void test() throws Exception {
        GrantedAuthorityImpl admin = new GrantedAuthorityImpl("ADMIN");
        GrantedAuthorityImpl user = new GrantedAuthorityImpl("USER");
        ArrayList<GrantedAuthorityImpl> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(admin);
        grantedAuthorities.add(user);
        UserDetailImpl user1 = new UserDetailImpl("admin", "123", grantedAuthorities);
        ObjectMapper objectMapper = new ObjectMapper();
        String s = objectMapper.writeValueAsString(user);
        JWT jwt = new JWT(s);
        System.out.println(jwt);
    }

    @Test
    void test1() throws Exception {
        System.out.println(new BCryptPasswordEncoder().encode("123"));
    }
}
