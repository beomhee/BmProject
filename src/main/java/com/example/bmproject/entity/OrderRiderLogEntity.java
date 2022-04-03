package com.example.bmproject.entity;

import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity(name="bmOrderRiderLog")
public class OrderRiderLogEntity {
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

    @Column(name = "remainDistance", nullable = false)
    private Integer remainDistance;
    public void setRemainDistance(Integer remainDistance) {
        this.remainDistance = remainDistance;
    }

    @Column(name = "regDt", nullable = false, length = 17)
    private String regDt;
    public void setRegDt(String regDt) {
        this.regDt = regDt;
    }
}
