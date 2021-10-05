package com.example.demo.user.model;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface userDao extends JpaRepository<uservo,Integer> {
    Optional<uservo> findByEmail(String email);
}
