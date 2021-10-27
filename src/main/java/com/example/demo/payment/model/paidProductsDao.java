package com.example.demo.payment.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface paidProductsDao extends JpaRepository<paidProductsDto,Integer> {
    
}
