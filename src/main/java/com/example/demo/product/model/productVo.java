package com.example.demo.product.model;

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
@Table(name = "products")
@Entity
public class productVo {
    
    @Id
    @Column(name="pid",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int pid;
 
    @Column(name = "price",nullable = false)
    private int price;

    @Column(name = "count" ,nullable = false)
    private int count;

    @Column(name = "productName" ,nullable = false)
    private String productName;

    @Column(name = "kind" ,nullable = false,length = 100)
    private String kind;

    
    @Column(name = "bigKind" ,nullable = false,length = 50)
    private String bigKind;

        
    @Column(name = "middleKind" ,nullable = false,length = 50)
    private String middleKind;

    @Column(name = "productImg")
    private String productImg;


    @Column(name = "pcreated")
    @CreationTimestamp
    private Timestamp pcreated;
}
