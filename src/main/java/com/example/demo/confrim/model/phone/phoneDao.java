package com.example.demo.confrim.model.phone;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface phoneDao extends JpaRepository<phoneVo,Integer> {
    Optional<phoneVo> findByPhoneNum(String phone);

    void deleteByPhoneNum(String phone);
}
