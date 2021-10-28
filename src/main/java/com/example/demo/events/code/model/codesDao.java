package com.example.demo.events.code.model;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface codesDao extends JpaRepository<codesVo,Integer> {
    
    Optional<codesVo> findByCodeName(String codeName);
}
