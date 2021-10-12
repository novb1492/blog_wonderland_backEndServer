package com.example.demo.find.model;

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
@Table(name = "requestpwd")
@Entity
public class findPwdVo {
    @Id
    @Column(name="pid",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int pid;

    @Column(name = "pemail",nullable = false,length = 50)
    private String pemail;
    
    @Column(name = "ptokenName",nullable = false,length = 255)
    private String ptokenName;

    @Column(name = "pexpire" ,nullable = false)
    @CreationTimestamp
    private Timestamp pexpire;

    @Column(name = "ucreated")
    @CreationTimestamp
    private Timestamp pcreated;
}
