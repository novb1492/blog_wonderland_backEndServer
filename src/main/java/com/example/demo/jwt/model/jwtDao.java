package com.example.demo.jwt.model;

import java.sql.Timestamp;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface jwtDao extends JpaRepository<jwtVo,Integer>{
   Optional<jwtVo> findByTokenName(String tokenName);

   jwtVo findByTemail(String email);

   @Modifying
   @Transactional
   @Query(value = "update tokens set texpired=?,tcreated=? where token_name=?",nativeQuery = true)
   void updateTokenExpire(Timestamp newExpireDate,Timestamp now,String TokenName);
}
