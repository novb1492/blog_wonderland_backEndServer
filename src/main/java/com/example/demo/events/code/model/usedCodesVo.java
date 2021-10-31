package com.example.demo.events.code.model;

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
@Table(name = "usedcodes")
@Entity
public class usedCodesVo {
    
    @Id
    @Column(name="ucdid",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int ucdid;

    @Column(name = "ucodeName",nullable = false,unique = true)
    private String ucodeName;


    @Column(name = "ucdMchtTrdNo",nullable = true)
    private String ucdmchtTrdNo;

    @Column(name = "ucdEmail",nullable = true)
    private String ucdEmail;

    @Column(name = "ucdCancleFlag",nullable = true)
    private int ucdCancleFlag;

    @Column(name = "ucdCancleDate")
    private Timestamp ucdCancleDate;


    @Column(name = "ucdcreated")
    @CreationTimestamp
    private Timestamp ucdcreated;


}
