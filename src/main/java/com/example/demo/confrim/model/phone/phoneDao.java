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
    @Query(value = "update phone set pcount=? where phone",nativeQuery = true)
    void updatePhoneNative(String newpayment_id,String originpayment_id);
}
