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
@Table(name = "points")
@Entity
public class pointsVo {
    
    @Id
    @Column(name="poid",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int poid;

    @Column(name = "poEmail",length = 50)
    private String poEmail;

    @Column(name = "poHaving",nullable = true)
    private int poHaving;

    @Column(name = "pocreated")
    @CreationTimestamp
    private Timestamp pocreated;

    @Column(name = "poExpired",nullable = false)
    private Timestamp poExpired;
}
