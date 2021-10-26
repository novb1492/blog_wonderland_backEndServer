package com.example.demo.payment.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface tempOrderDao extends JpaRepository<tempOrderDto,Integer> {
    
    @Query(value = "select b.*,a.*,c.* from temporder a left join temporderproudcts b on a.to_mcht_trd_no=b.top_mcht_trd_no left join tempreservations c on  a.to_mcht_trd_no=c.tr_mcht_trd_no  where a.to_mcht_trd_no=?",nativeQuery = true)
    getJoinProducts findJoinProducts(String mchtTrdNo);
}
