package com.example.demo.jwt.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface jwtDao extends JpaRepository<jwtVo,Integer>{
   jwtVo findByTokenName(String tokenName);

   jwtVo findByTemail(String email);
}
