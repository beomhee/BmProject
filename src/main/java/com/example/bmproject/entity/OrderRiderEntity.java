package com.example.bmproject.entity;

import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity(name="bmOrderRider")
public class OrderRiderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    @Column(name = "orderNo", nullable = false, length = 10)
    private String orderNo;
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    @Column(name = "shopNo", nullable = false, length = 10)
    private String shopNo;
    public void setShopNo(String shopNo) {
        this.shopNo = shopNo;
    }

    @Column(name = "riderNo", nullable = false, length = 10)
    private String riderNo;
    public void setRiderNo(String riderNo) {
        this.riderNo = riderNo;
    }

    @Column(name = "pickupDt", nullable = false, length = 17)
    private String pickupDt;
    public void setPickupDt(String pickupDt) {
        this.pickupDt = pickupDt;
    }

    @Column(name = "completeDt", nullable = false, length = 17)
    private String completeDt;
    public void setCompleteDt(String completeDt) {
        this.completeDt = completeDt;
    }
}
