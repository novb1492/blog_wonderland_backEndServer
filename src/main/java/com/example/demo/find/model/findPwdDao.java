package com.example.demo.find.model;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface findPwdDao extends JpaRepository<findPwdVo,Integer> {
    
    @Query(value = "select (select count(*) from requestpwd where pemail=?)pcount,(select count(*) from users where email=?)ucount",nativeQuery = true)
    getJoinUsers findJoinUsers(String email,String email2);

      
    @Modifying
    @Transactional
    @Query(value = "update requestpwd set pcreated=?,pexpire=?,ptoken_name=? where pemail=?",nativeQuery = true)
    void updateTokenNative(Timestamp created,Timestamp expire,String token,String email);
}
