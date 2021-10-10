package com.example.demo.jwt.model;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface jwtDao extends JpaRepository<jwtVo,Integer>{
   Optional<jwtVo> findByTokenName(String tokenName);

   jwtVo findByTemail(String email);
}
