package com.example.demo.payment.model.card;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface paidCardsDao extends JpaRepository<paidCardsDto,Integer>{


    @Modifying
    @Transactional
    @Query(value = "update temporder a left join paidproducts b on a.pc_mcht_trd_no=b.po_mcht_trd_no left join paidreservations c on a.pc_mcht_trd_no=c.pr_mcht_trd_no set  where a.pc_mcht_trd_no=? ",nativeQuery = true)
    void updateTempProductJoin(String mchtTrdNo);

    int countByPcMchtTrdNo(String mchtTrdNo);
}
