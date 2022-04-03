package com.example.bmproject.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@Entity(name="bmMenu") // 테이블 명을 작성
public class ShopMenuEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    @Column(name = "shopNo", nullable = false, length = 10)
    private String shopNo;

    @Column(name = "menuName", nullable = false, length = 32)
    private String menuName;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "menuNo", nullable = false, length = 10)
    private String menuNo;

    @Column(name = "maxSellable", nullable = false)
    private Integer maxSellable;

    @Column(name = "todayRemain", nullable = false)
    private Integer todayRemain;
    public void setTodayRemain(Integer todayRemain) {
        this.todayRemain = todayRemain;
    }
}
