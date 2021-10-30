package com.example.demo.payment.model.card;

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
@Table(name = "paidcards")
@Entity
public class paidCardsDto {
    
    @Id
    @Column(name="pcid",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int pcid;

    @Column(name = "pcFn_nm",nullable = false,length = 50)
    private String pcFn_nm;

    @Column(name = "pcMcht_id",nullable = false)
    private String pcMcht_id;

    
    @Column(name = "pcMchtTrdNo",nullable = false,unique=true)
    private String pcMchtTrdNo;
        
    @Column(name = "pcMethod",nullable = false)
    private String pcMethod;
    
    @Column(name = "pcTrd_amt",nullable = false)
    private int pcTrd_amt;

    @Column(name = "pcPoint",nullable = false)
    private int pcPoint;

    @Column(name = "pcTrd_no",nullable = false)
    private String pcTrd_no;

    @Column(name = "pcCncl_ord",nullable = false)
    private int pcCncl_ord;

    @Column(name = "pcEmail",nullable = false)
    private String pcEmail;
    
    @Column(name = "pcCancleFlag",nullable = false)
    private int pcCancleFlag;

    @Column(name = "pcCreated")
    @CreationTimestamp
    private Timestamp poCreated;

    @Column(name = "pcCancleDate")
    private Timestamp poCancleDate;
}
