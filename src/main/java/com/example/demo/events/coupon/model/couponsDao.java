package com.example.demo.events.coupon.model;



import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface couponsDao extends JpaRepository<couponsVo,Integer>{
    Optional<couponsVo> findByCouponName(String couponName);
}
