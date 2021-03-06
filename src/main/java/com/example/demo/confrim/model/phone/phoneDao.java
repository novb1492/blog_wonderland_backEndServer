package com.example.demo.confrim.model.phone;

import java.sql.Timestamp;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface phoneDao extends JpaRepository<phoneVo,Integer> {
    
    Optional<phoneVo> findByPphoneNum(String phone);

    @Query(value = "select *from requestphone where pphone_num=? and detail=?",nativeQuery = true)
    Optional<phoneVo> findByPphoneNative(String phone,String detail);

    void deleteByPphoneNum(String phone);

    @Query(value = "select count(u.id)already, a.* from users u left join requestphone a on a.pphone_num=u.phone_num and a.detail=?  where phone_num=?",nativeQuery=true)
    getRequestAndusersInter findPhoneJoinUsers(String detail,String phone);

    @Query(value = "select count(*)already,(select done_phone from requestphone where pphone_num=? and detail=?)done_phone,(select pphone_num from requestphone where pphone_num=? and detail=?)pphone_num,(select pcount from requestphone where pphone_num=? and detail=?)pcount,(select pcreated from requestphone where pphone_num=? and detail=?)pcreated from users where phone_num=?",nativeQuery=true)
    getRequestAndusersInter findPhoneJoinUsers2(String phone5,String detail5,String phone4,String detail4,String phone,String detail,String phone2,String detail2,String phone3,String detail3);
  
    @Modifying
    @Transactional
    @Query(value = "update requestphone set pcount=?,prand_num=?,pcreated=? where pphone_num=? and detail=?",nativeQuery = true)
    void updatePhoneNative(int count,String randNum,Timestamp now,String phoneNum,String detail);

    @Modifying
    @Transactional
    @Query(value = "delete from requestphone where pphone_num=? and detail=?",nativeQuery = true)
    void deletePhoneNative(String phoneNum,String detail);


}
