package com.example.bmproject.service;

import com.example.bmproject.entity.ShopOrderStatusEntity;
import com.example.bmproject.repository.ShopOrderStatusRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class RiderServiceTest {

    @Autowired
    private RiderService riderService;
    private ShopOrderStatusRepository ShopOrderStatusRepository;

    @Test
    public void test1() throws Exception {
        ShopOrderStatusEntity ShopOrderStatusEntity = ShopOrderStatusRepository.orderStatusInfo("ORD_A01");
        ShopOrderStatusEntity.setorderStatus("PICKUP");
        ShopOrderStatusRepository.save(ShopOrderStatusEntity);
    }
}
