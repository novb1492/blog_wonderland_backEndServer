package com.example.demo.confrim.model.phone;

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
@Table(name = "requestphone")
@Entity
public class phoneVo {
    
    @Id
    @Column(name="pid",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int pid;

    @Column(name = "pcount",nullable = false)
    private int pcount;

    @Column(name = "phoneNum",nullable = false,length = 20)
    private String phoneNum;

    @Column(name = "prandNum",nullable = false,length = 20)
    private String randNum;

    @Column(name = "donePhone",nullable = false)
    private int donePhone;

    @Column(name = "pcreated")
    @CreationTimestamp
    private Timestamp pcreated;
    
}
