package com.chunmiao.springsecuritydemo.service;

import com.chunmiao.springsecuritydemo.dao.UserInfoRepository;
import com.chunmiao.springsecuritydemo.dao.UserPasswordRepository;
import com.chunmiao.springsecuritydemo.dao.UserRepository;
import com.chunmiao.springsecuritydemo.dao.UserRoleRepository;
import com.chunmiao.springsecuritydemo.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserPasswordRepository userPasswordRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;

    /**
     * 我的数据库表分为User表、UserInfo用户详细信息表、UserPassword密码表、UserRole用户角色表
     * spring-security会给这个方法提供一个用户名，然后我们实现根据用户名得到这个用户的UserDetail信息（类似于包含用户名、密码、角色的实体类，下一步重写它）
     * 然后返回的就是这个UserDetail，spring-security可以使用该类完成后面的操作
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findFirstByUsername(username);
        Integer id = user.getId();
        if (Objects.nonNull(user) && username.trim().length() <= 0) {
            throw new UsernameNotFoundException("用户名错误");
        }
        // 填充所有角色信息
        List<GrantedAuthorityImpl> grantedAuthorities = new ArrayList<>();
        List<UserRole> roles = userRoleRepository.findByCreator_Id(id);
        for (UserRole role : roles) {
            grantedAuthorities.add(new GrantedAuthorityImpl("ROLE_" + role.getRole()));
        }
        return new UserDetailImpl(
                    username,
                    userPasswordRepository.findByCreator_Id(id).getPassword(),
                    grantedAuthorities
                );
    }
}
