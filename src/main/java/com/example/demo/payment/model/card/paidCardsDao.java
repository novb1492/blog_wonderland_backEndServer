package com.example.demo.payment.model.card;

import org.springframework.data.jpa.repository.JpaRepository;

public interface paidCardsDao extends JpaRepository<paidCardsDto,Integer>{
    
}
