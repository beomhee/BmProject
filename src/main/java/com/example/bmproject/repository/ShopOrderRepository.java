package com.example.bmproject.repository;

import com.example.bmproject.entity.ShopMenuEntity;
import com.example.bmproject.entity.ShopOrderEntity;
import com.example.bmproject.entity.ShopOrderStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopOrderRepository extends JpaRepository<ShopOrderEntity, Long> {

    @Query(value = "SELECT * FROM bm_order WHERE order_no = ?1", nativeQuery = true)
    ShopOrderEntity orderInfo(String orderNo);

}
