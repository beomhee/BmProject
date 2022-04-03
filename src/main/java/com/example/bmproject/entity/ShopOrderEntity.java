package com.example.bmproject.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity(name="bmOrder")
public class ShopOrderEntity {
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

    @Column(name = "menuNo", nullable = false, length = 10)
    private String menuNo;
    public void setMenuNo(String menuNo) {
        this.menuNo = menuNo;
    }

    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
