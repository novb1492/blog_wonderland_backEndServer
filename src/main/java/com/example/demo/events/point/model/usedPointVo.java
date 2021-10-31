package com.example.demo.events.point.model;

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
@Table(name = "usedpoints")
@Entity
public class usedPointVo {
    @Id
    @Column(name="upoid",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int upoid;

    @Column(name = "upoEmail",length = 50,nullable = true)
    private String upoEmail;

    @Column(name = "upoMchtTrdNo",unique = true,nullable = true)
    private String upoMchtTrdNo;

    @Column(name = "upoint",nullable = true)
    private int upoint;

    @Column(name = "upCancelFlag",nullable = true)
    private int upCancelFlag;

    @Column(name = "upoCacncleDate")
    private Timestamp upoCacncleDate;

    @Column(name = "upocreated")
    @CreationTimestamp
    private Timestamp upocreated;

}
