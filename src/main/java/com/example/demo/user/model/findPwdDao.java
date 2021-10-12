package com.example.demo.user.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface findPwdDao extends JpaRepository<findPwdVo,Integer> {
    findPwdVo findByPtokenName(String tokenName);
}
