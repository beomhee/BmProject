package com.example.bmproject.repository;

import com.example.bmproject.entity.OrderRiderEntity;
import com.example.bmproject.entity.RiderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderRiderRepository extends JpaRepository<OrderRiderEntity, Long> {

    @Query(value = "SELECT * FROM bm_order_rider WHERE order_no = ?1", nativeQuery = true)
    OrderRiderEntity getOrderRiderInfo(String order_no);

}
