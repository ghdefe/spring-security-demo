package com.chunmiao.springsecuritydemo.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class UserRole {

    @Id
    @Column(nullable = false,unique = true)
    @GeneratedValue
    private Integer id;

    private String role;

    @OneToOne
    private User creator;
}
