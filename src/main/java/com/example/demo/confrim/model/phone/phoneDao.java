package com.example.demo.confrim.model.phone;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface phoneDao extends JpaRepository<phoneVo,Integer> {
    
    Optional<phoneVo> findByPhoneNum(String phone);

    void deleteByPhoneNum(String phone);

  
    @Modifying
    @Transactional
    @Query(value = "update requestphone set pcount=? where phone_num=?",nativeQuery = true)
    void updatePhoneNative(int count,String phoneNum);
}
