package com.example.demo.confrim.model.phone;

import java.sql.Timestamp;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface phoneDao extends JpaRepository<phoneVo,Integer> {
    
    Optional<phoneVo> findByPphoneNum(String phone);

    void deleteByPphoneNum(String phone);

    @Query(value = "select count(u.id)already,e.* from users u left join requestphone e on e.pphone_num=u.phone_num where phone_num=?",nativeQuery=true)
    getRequestAndusersInter findPhoneJoinUsers(String phone);

    @Query(value = "select count(*)already,(select done_phone from requestphone where pphone_num=?)done_phone,(select pphone_num from requestphone where pphone_num=?)pphone_num,(select pcount from requestphone where pphone_num=?)pcount,(select pcreated from requestphone where pphone_num=?)pcreated from users where phone_num=?",nativeQuery=true)
    getRequestAndusersInter findPhoneJoinUsers2(String phone5,String phone4,String phone,String phone2,String phone3);
  
    @Modifying
    @Transactional
    @Query(value = "update requestphone set pcount=?,prand_num=?,pcreated=? where pphone_num=?",nativeQuery = true)
    void updatePhoneNative(int count,String randNum,Timestamp now,String phoneNum);

    @Modifying
    @Transactional
    @Query(value = "delete from requestphone where pphone_num=?",nativeQuery = true)
    void deletePhoneNative(String phoneNum);


}
