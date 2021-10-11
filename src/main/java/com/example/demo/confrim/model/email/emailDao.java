package com.example.demo.confrim.model.email;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface emailDao extends JpaRepository<emailVo,Integer> {
    Optional<emailVo> findByEemail(String email);

    @Query(value = "select count(u.id)already,e.* from users u left join requestemail e on e.eemail=u.email where email=?",nativeQuery = true)
    getUserJoinInter findByEemailJoinUsers(String email);

    @Modifying
    @Transactional
    @Query(value = "update requestemail set ecount=?,erand_num=? where eemail=?",nativeQuery = true)
    void updateEmailNative(int count,String randNum,String email);
    
    void deleteByEemail(String email);

}
