package com.chunmiao.springsecuritydemo.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class User {
    @Id
    @Column(nullable = false,unique = true)
    @GeneratedValue
    private Integer id;

    @Column(nullable = false)
    private String username;
}
