package com.example.demo.jwt.model;

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
@Table(name = "tokens")
@Entity
public class jwtVo {
    
    @Id
    @Column(name="tid",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int tid;

    @Column(name = "temail",nullable = false,length = 50)
    private String temail;

    @Column(name = "tokenName",nullable = false)
    private String tokenName;
    
    @Column(name = "tuid",nullable = false,length = 20)
    private String tuid;

    @Column(name = "tcreated")
    @CreationTimestamp
    private Timestamp tcreated;

    @Column(name = "texpired")
    private Timestamp texpired;
}
