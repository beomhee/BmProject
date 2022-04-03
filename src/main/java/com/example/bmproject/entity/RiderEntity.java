package com.example.bmproject.entity;

import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity(name="bmRider")
public class RiderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    @Column(name = "riderNo", nullable = false, length = 10)
    private String riderNo;

    @Column(name = "riderName", nullable = false, length = 32)
    private String riderName;

    @Column(name = "riderMobility", nullable = false, length = 8)
    private String riderMobility;

    @Column(name = "riderSpeed", nullable = false)
    private Integer riderSpeed;

    @Column(name = "riderRun", nullable = false)
    private Character riderRun;
    public void setRiderRun(Character riderRun) {
        this.riderRun = riderRun;
    }
}
