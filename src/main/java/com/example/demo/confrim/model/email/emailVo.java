package com.example.demo.confrim.model.email;

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
@Table(name = "requestemail")
@Entity
public class emailVo {

    @Id
    @Column(name="eid",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int eid;

    @Column(name = "ecount",nullable = false)
    private int ecount;

    @Column(name = "eemail",nullable = false,length = 50)
    private String eemail;

    @Column(name = "erandNum",nullable = false,length = 20)
    private String erandNum;

    @Column(name = "doneemail",nullable = false)
    private int doneemail;

    @Column(name = "ecreated")
    @CreationTimestamp
    private Timestamp ecreated;
}
