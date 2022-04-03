package com.example.bmproject.repository;

import com.example.bmproject.entity.ShopOrderStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ShopOrderStatusRepository extends JpaRepository<ShopOrderStatusEntity, Long> {

    Long countByorderStatus(String orderStatus);

    @Query(value = "SELECT * FROM bm_order_status WHERE order_no = ?1", nativeQuery = true)
    ShopOrderStatusEntity orderStatusInfo(String orderNo);

    @Query(value = "SELECT * FROM bm_order_status WHERE order_status = 'PICKUP'", nativeQuery = true)
    List<ShopOrderStatusEntity> orderStatusPickupInfo();

    @Query(value = "SELECT * FROM bm_order_status WHERE order_status = 'RECEIPT' LIMIT 1", nativeQuery = true)
    ShopOrderStatusEntity orderStatusReceiptInfo();

    @Query(value = "SELECT t1.order_no, t1.shop_no, t1.total_price, t1.order_status, t1.distance, t3.menu_name, t2.quantity, t3.price * t2.quantity" +
            " FROM bm_order_status AS t1, bm_order AS t2, bm_menu AS t3" +
            " WHERE t1.order_no = t2.order_no" +
            " AND t1.shop_no = t2.shop_no" +
            " AND t2.shop_no = t3.shop_no" +
            " AND t2.menu_no = t3.menu_no" +
            " AND t1.order_no = ?1", nativeQuery = true)
    List<Object> orderJoinInfo(String orderNo);
}
