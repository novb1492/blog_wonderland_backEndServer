package com.example.demo.payment.model;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface tempOrderDao extends JpaRepository<tempOrderDto,Integer> {
    
    @Modifying
    @Transactional
    @Query(value = "update temporder a left join temporderproducts b on a.to_mcht_trd_no=b.top_mcht_trd_no set a.to_done_flag=?,b.top_done_flag=?,a.to_done_date=?,b.top_done_date=? where a.to_mcht_trd_no=?",nativeQuery= true)
    void updateTempProducts(int zeroOrOne,int zeroOrOne2,Timestamp now,Timestamp now2,String mchtTrdNo);
    
    @Query(value = "select b.*,a.*,c.* from temporder a left join temporderproducts b on a.to_mcht_trd_no=b.top_mcht_trd_no left join tempreservations c on  a.to_mcht_trd_no=c.tr_mcht_trd_no  where a.to_mcht_trd_no=?",nativeQuery = true)
    List<getJoinProducts> findJoinProducts(String mchtTrdNo);
}
