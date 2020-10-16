package com.chunmiao.springsecuritydemo.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class UserPassword {

    @Id
    @Column(nullable = false,unique = true)
    @GeneratedValue
    private Integer id;

    private String password;

    @OneToOne
    private User creator;
}
