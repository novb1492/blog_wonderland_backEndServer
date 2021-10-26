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
@Table(name = "tempreservations")
@Entity
public class reservationDto {
    
    @Id
    @Column(name="trid",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int trid;
 
    @Column(name = "trMchtTrdNo")
    private String trMchtTrdNo;


    @Column(name = "trcreated")
    @CreationTimestamp
    private Timestamp trcreated;
}
