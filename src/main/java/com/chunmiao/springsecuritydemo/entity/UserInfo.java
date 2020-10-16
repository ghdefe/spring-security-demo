package com.chunmiao.springsecuritydemo.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class UserInfo {

    @Id
    @Column(nullable = false,unique = true)
    @GeneratedValue
    private Integer id;

    @Column(columnDefinition = "varchar(11)")
    private String phone;

    private String email;

    private String others;


    private Integer roleId;

    @OneToOne
    private User creator;
}
