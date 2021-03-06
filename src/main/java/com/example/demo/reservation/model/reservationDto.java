package com.example.demo.reservation.model;

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
@Table(name = "paidreservations")
@Entity
public class reservationDto {
    
    @Id
    @Column(name="prid",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int prid;
 
    @Column(name = "prMchtTrdNo")
    private String prMchtTrdNo;


    @Column(name = "prcreated")
    @CreationTimestamp
    private Timestamp prcreated;
}
