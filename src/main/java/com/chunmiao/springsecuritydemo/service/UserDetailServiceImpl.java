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
