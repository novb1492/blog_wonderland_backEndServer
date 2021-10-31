package com.example.demo.events.coupon.model;

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
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@ToString
@Table(name = "coupons")
@Entity
public class couponsVo {
    
     
    @Id
    @Column(name="coid",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int coid;

    @Column(name = "coUsedEmail",length = 50)
    private String coUsedEmail;

    @Column(name = "couponName",nullable = false,unique = true)
    private String couponName;
    
    @Column(name = "usedFlag",nullable = false)
    private int usedFlag;

    @Column(name = "usedDate")
    private Timestamp usedDate;

    @Column(name = "coMchtTrdNo")
    private String coMchtTrdNo;

    @Column(name = "coKind",nullable = true)
    private String coKind;

    @Column(name = "coNum",nullable = true)
    private int coNum;


    @Column(name = "cocreated")
    @CreationTimestamp
    private Timestamp cocreated;

    @Column(name = "coExpired",nullable = false)
    private Timestamp coExpired;
}
