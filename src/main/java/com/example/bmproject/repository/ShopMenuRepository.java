package com.example.bmproject.repository;

import com.example.bmproject.entity.ShopMenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopMenuRepository extends JpaRepository<ShopMenuEntity, Long> {
    @Query(value = "SELECT * FROM bm_menu WHERE menu_no = ?1", nativeQuery = true)
    ShopMenuEntity menuInfo(String menuNo);

}