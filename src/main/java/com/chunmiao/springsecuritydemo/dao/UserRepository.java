package com.chunmiao.springsecuritydemo.dao;

import com.chunmiao.springsecuritydemo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Integer> {

    User findFirstByUsername(String username);

}
