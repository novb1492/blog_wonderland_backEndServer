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
@Table(name = "codes")
@Entity
public class codesVo {
    
    @Id
    @Column(name="cdid",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int cdid;

    @Column(name = "codeName",nullable = false,unique = true)
    private String codeName;


    @Column(name = "cdKind",nullable = true)
    private String cdKind;

    @Column(name = "cdNum",nullable = true)
    private int cdNum;

    @Column(name = "cdUsing",nullable = true)
    private int cdUsing;

    @Column(name = "cdcreated")
    @CreationTimestamp
    private Timestamp cdcreated;

    @Column(name = "cdExpired",nullable = false)
    private Timestamp cdExpired;
}
