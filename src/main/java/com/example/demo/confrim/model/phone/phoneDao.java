package com.example.demo.confrim.model.phone;

import java.sql.Timestamp;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface phoneDao extends JpaRepository<phoneVo,Integer> {
    
    Optional<phoneVo> findByPhoneNum(String phone);

    void deleteByPhoneNum(String phone);

    @Query(value = "select a.*,count(b.id)already from users b left join requestphone a on b.phone_num=a.phone_num where b.phone_num=?",nativeQuery=true)
    getRequestAndusersInter findPhoneJoinUsers(String phone);
  
    @Modifying
    @Transactional
    @Query(value = "update requestphone set pcount=?,prand_num=?,pcreated=? where phone_num=?",nativeQuery = true)
    void updatePhoneNative(int count,String randNum,Timestamp now,String phoneNum);

    @Modifying
    @Transactional
    @Query(value = "delete from requestphone where phone_num=?",nativeQuery = true)
    void deletePhoneNative(String phoneNum);


}
