package com.chunmiao.springsecuritydemo.dao;

import com.chunmiao.springsecuritydemo.entity.User;
import com.chunmiao.springsecuritydemo.entity.UserPassword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPasswordRepository extends JpaRepository<UserPassword,Integer> {

    UserPassword findByCreator_Id(Integer id);
}
