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
@Table(name = "temporder")
@Entity
public class tempOrderDto {

    @Id
    @Column(name="toid",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int toid;

    @Column(name = "toEmail",nullable = false,length = 50)
    private String toemail;

    @Column(name = "toMchtTrdNo",nullable = false,unique=true)
    private String toMchtTrdNo;

    @Column(name = "buyKind",nullable = false)
    private String buyKind;
    
    @Column(name = "toPrice",nullable = false)
    private int toPrice;
  
    @Column(name = "toDoneFlag",nullable = false)
    private int toDoneFlag;

    @Column(name = "toCash",nullable = false)
    private int toCash;

    @Column(name = "toPoint",nullable = false)
    private int toPoint;


    @Column(name = "toCreated")
    @CreationTimestamp
    private Timestamp toCreated;

    @Column(name = "toDoneDate")
    private Timestamp toDoneDate;



  
}
