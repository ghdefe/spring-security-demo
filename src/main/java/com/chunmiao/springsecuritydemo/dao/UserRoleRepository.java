package com.chunmiao.springsecuritydemo.dao;

import com.chunmiao.springsecuritydemo.entity.UserInfo;
import com.chunmiao.springsecuritydemo.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole,Integer> {

    List<UserRole> findByCreator_Id(Integer id);
}
