package com.example.demo.user.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Table(name = "users")
@Entity
public class uservo {
    @Id
    @Column(name="id",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int uid;

    @Column(name = "email",nullable = false,length = 50)
    private String email;
    
    @Column(name = "name",nullable = false,length = 20)
    private String name;

    @Column(name = "pwd",nullable = false,length = 255)
    private String pwd;

    @Column(name = "phoneNum",nullable = false,length = 30)
    private String phoneNum;

    @Column(name = "address",nullable = false,length = 50)
    private String address;
    @Column(name = "role",nullable = false,length = 20)
    private String role;

    @Column(name = "ucreated")
    @CreationTimestamp
    private Timestamp ucreated;


    
}
