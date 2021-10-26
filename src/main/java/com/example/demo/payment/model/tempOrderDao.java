package com.example.demo.payment.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface tempOrderDao extends JpaRepository<tempOrderDto,Integer> {
    
}
