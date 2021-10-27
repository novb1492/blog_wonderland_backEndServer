package com.example.demo.payment.model.card;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface paidCardsDao extends JpaRepository<paidCardsDto,Integer>{
    
    @Modifying
    @Transactional
    @Query(value = "delete a.*,b.*,c.* from temporder a left join temporderproducts b on a.to_mcht_trd_no=b.top_mcht_trd_no left join tempreservations c on a.to_mcht_trd_no=c.tr_mcht_trd_no where a.to_mcht_trd_no=? ",nativeQuery = true)
    void deleteTempJoin(String mchtTrdNo);
}