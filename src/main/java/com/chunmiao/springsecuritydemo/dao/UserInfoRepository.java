package com.chunmiao.springsecuritydemo.dao;

import com.chunmiao.springsecuritydemo.entity.UserInfo;
import com.chunmiao.springsecuritydemo.entity.UserPassword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInfoRepository extends JpaRepository<UserInfo,Integer> {

    UserInfo findByCreator_Id(Integer id);
}
