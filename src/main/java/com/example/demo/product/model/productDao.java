package com.example.demo.product.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface productDao extends JpaRepository<productVo,Integer> {

    @Query(value = "select *,(select count(*) from products where kind=?)totalcount from products where kind=?  order by pid asc limit ?,?",nativeQuery = true)
    Optional<List<getProductInter>> findByKind(String kind,String kind2,int start,int pageSize);
}
