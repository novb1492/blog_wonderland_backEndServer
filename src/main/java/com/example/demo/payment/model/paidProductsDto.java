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
@Table(name = "paidproducts")
@Entity
public class paidProductsDto {
    
    @Id
    @Column(name="poid",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int poid;

    @Column(name = "poEmail",nullable = false,length = 50)
    private String poemail;

    @Column(name = "poMchtTrdNo",nullable = false,unique=true)
    private String poMchtTrdNo;
    
    @Column(name = "poPrice",nullable = false)
    private int poPrice;

    @Column(name = "poName",nullable = false)
    private String poName;

    @Column(name = "poCount",nullable = false)
    private int poCount;

    @Column(name = "poCode")
    private String poCode;

    @Column(name = "poCoupon")
    private String poCoupon;

        
    @Column(name = "poCancleFlag",nullable = false)
    private int poCancleFlag;

    @Column(name = "poCreated")
    @CreationTimestamp
    private Timestamp poCreated;

    @Column(name = "poCancleDate")
    private Timestamp poCancleDate;
}
