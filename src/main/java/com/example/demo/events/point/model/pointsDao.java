package com.example.demo.events.point.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface pointsDao extends JpaRepository<pointsVo,Integer> {
    
    pointsVo findByPoEmail(String email);
}
