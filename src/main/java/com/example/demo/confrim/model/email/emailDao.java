package com.example.demo.confrim.model.email;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface emailDao extends JpaRepository<emailVo,Integer> {
    Optional<emailVo> findByEemail(String email);
}
