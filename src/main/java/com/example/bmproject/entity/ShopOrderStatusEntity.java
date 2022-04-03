package com.example.bmproject.entity;
import lombok.*;
import javax.persistence.*;

@Getter
@Entity(name="bmOrderStatus")
public class ShopOrderStatusEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    @Column(name = "orderNo", nullable = false, unique = true, length = 10)
    private String orderNo;
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    @Column(name = "shopNo", nullable = false, length = 10)
    private String shopNo;
    public void setShopNo(String shopNo) {
        this.shopNo = shopNo;
    }

    @Column(name = "acceptTime", nullable = false)
    private String acceptTime;
    public void setAcceptTime(String acceptTime) { this.acceptTime = acceptTime; }

    @Column(name = "totalPrice", nullable = false)
    private int totalPrice;
    public void setTotalPrice(int totalPrice) { this.totalPrice = totalPrice; }

    @Column(name = "orderStatus", nullable = false, length = 8)
    private String orderStatus;
    public void setorderStatus(String orderStatus) { this.orderStatus = orderStatus; }

    @Column(name = "distance", nullable = false)
    private int distance;
    public void setDistance(int distance) { this.distance = distance; }
}
