package com.example.demo.find.model;

import java.sql.Timestamp;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface findPwdDao extends JpaRepository<findPwdVo,Integer> {
    
    @Query(value = "select (select count(*) from requestpwd where pemail=?)pcount,(select count(*) from users where email=?)ucount",nativeQuery = true)
    getJoinUsers findJoinUsers(String email,String email2);

    @Query(value = "select u.email,e.doneemail,r.pexpire  from requestpwd r inner join users u on r.pemail=u.email inner join requestemail e on e.eemail=r.pemail where r.ptoken_name=?",nativeQuery = true)
    getJoinRequest findTokenNameJoinRequest(String email);
      
    @Modifying
    @Transactional
    @Query(value = "update requestpwd set pcreated=?,pexpire=?,ptoken_name=? where pemail=?",nativeQuery = true)
    void updateTokenNative(Timestamp created,Timestamp expire,String token,String email);

    @Query(value = "select count(*) from requestpwd where ptoken_name=?",nativeQuery=true)
    int countByPtokenNameNative(String token);

    @Modifying
    @Transactional
    @Query(value = "delete a.*,b.* from requestpwd a inner join requestemail b on a.pemail=b.eemail where a.ptoken_name=?",nativeQuery = true)
    void deleteJoinRequest(String token);

    @Query(value = "select email from users where phone_num=?",nativeQuery = true)
    Optional<String> findEmailNative(String phone);

    @Modifying
    @Transactional
    @Query(value = "delete  from requestphone where pphone_num=?",nativeQuery = true)
    void deleteRequest(String phone);

}
