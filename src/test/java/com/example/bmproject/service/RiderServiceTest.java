package com.example.bmproject.service;

import com.example.bmproject.repository.OrderRiderLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.DependsOn;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataJpaTest
@DependsOn("flyway")
public class RiderServiceTest {

    RiderService riderService;

    private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @Autowired private OrderRiderLogRepository orderRiderLogRepository;

//    @Test
//    public void test1() throws Exception {
//        Object obj = riderService.riderCheck("ORD_A01");
//        logger.debug(obj.toString());
//    }
}
