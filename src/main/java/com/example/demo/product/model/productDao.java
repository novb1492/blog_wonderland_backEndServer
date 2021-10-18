package com.example.demo.product.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface productDao extends JpaRepository<productVo,Integer> {
    Optional<List<productVo>> findByKind(String kind);
}
