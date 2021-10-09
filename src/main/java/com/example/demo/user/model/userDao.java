package com.example.demo.user.model;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface userDao extends JpaRepository<uservo,Integer> {
    Optional<uservo> findByEmail(String email);

    @Query(value = "select (select count(*) from users where email=?)ucount,(select done_phone from requestphone where phone_num=?)done",nativeQuery = true)
    inserConfrimInter findByEmailJoinConfrim(String email,String phone);
}
