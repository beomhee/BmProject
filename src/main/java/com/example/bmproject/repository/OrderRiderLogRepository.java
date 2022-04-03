package com.example.bmproject.repository;

import com.example.bmproject.entity.OrderRiderLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRiderLogRepository extends JpaRepository<OrderRiderLogEntity, Long> {

    @Query(value = "SELECT * FROM bm_order_rider_log WHERE order_no = ?1 ORDER BY idx DESC LIMIT 1", nativeQuery = true)
    OrderRiderLogEntity orderLogInfo(String orderNo);

    @Query(value = "SELECT t1.rider_name, t1.rider_mobility, t2.remain_distance, ROUND((t2.remain_distance / rider_speed) * 10), t2.shop_no" +
            " FROM bm_rider AS t1," +
            " (SELECT order_no, shop_no, rider_no, remain_distance FROM bm_order_rider_log" +
            "  WHERE order_no = ?1" +
            "  ORDER BY idx DESC LIMIT 1) AS t2" +
            " WHERE t1.rider_no = t2.rider_no", nativeQuery = true)
    Object riderJoinInfo(String orderNo);
}
