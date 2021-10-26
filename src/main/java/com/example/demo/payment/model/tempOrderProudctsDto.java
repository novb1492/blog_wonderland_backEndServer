package com.example.demo.payment.model;

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
@Table(name = "tempOrderProudcts")
@Entity
public class tempOrderProudctsDto {
    
    @Id
    @Column(name="topid",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int topid;

    @Column(name = "topEmail",nullable = false,length = 50)
    private String topemail;

    @Column(name = "topMchtTrdNo",nullable = false)
    private String topMchtTrdNo;
    
    @Column(name = "topPrice",nullable = false)
    private int topPrice;

    @Column(name = "topName",nullable = false)
    private String topName;

    @Column(name = "topCount",nullable = false)
    private int topCount;

    @Column(name = "topCreated")
    @CreationTimestamp
    private Timestamp topCreated;

}
