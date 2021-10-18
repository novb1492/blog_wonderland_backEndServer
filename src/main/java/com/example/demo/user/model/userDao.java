package com.example.demo.user.model;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface userDao extends JpaRepository<uservo,Integer> {
    Optional<uservo> findByEmail(String email);

    @Query(value = "select (select count(*) from users where phone_num=?)pcount,(select count(*) from users where email=?)ucount,(select done_phone from requestphone where pphone_num=? and detail=?)done",nativeQuery = true)
    inserConfrimInter findByEmailJoinConfrim(String phone,String email,String phone2,String detail);

    @Modifying
    @Transactional
    @Query(value = "update users set pwd=? where email=?",nativeQuery = true)
    void updatePwd(String pwd,String email);

    @Modifying
    @Transactional
    @Query(value = "update users set address=? where email=?",nativeQuery = true)
    void updateAddress(String fullAddress,String email);

    @Modifying
    @Transactional
    @Query(value = "update users set phone_num=? where email=?",nativeQuery = true)
    void updatePhone(String phone,String email);


}
