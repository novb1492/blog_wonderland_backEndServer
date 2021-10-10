package com.example.demo.confrim.model.phone;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface phoneDao extends JpaRepository<phoneVo,Integer> {

    Optional<phoneVo> findByPhoneNum(String phone);

    void deleteByPhoneNum(String phone);

    @Query(value = "select (select count(*) from users where phone_num=?)already,r.* from requestphone r where r.phone_num=?",nativeQuery = true)
    getRequestAndusersInter findByPhoneAndUsers(String Phone,String Phone2); 
  
    @Modifying
    @Transactional
    @Query(value = "update requestphone set pcount=? where phone_num=?",nativeQuery = true)
    void updatePhoneNative(int count,String phoneNum);
}
