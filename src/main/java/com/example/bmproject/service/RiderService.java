package com.example.bmproject.service;

import com.example.bmproject.entity.OrderRiderEntity;
import com.example.bmproject.entity.RiderEntity;
import com.example.bmproject.entity.ShopMenuEntity;
import com.example.bmproject.entity.ShopOrderStatusEntity;
import com.example.bmproject.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RiderService {

    private final RiderRepository riderRepository;
    private final OrderRiderRepository orderRiderRepository;
    private final OrderRiderLogRepository orderRiderLogRepository;
    private final ShopOrderStatusRepository shopOrderStatusRepository;

    private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @Autowired
    private ObjectMapper objectMapper;

    // rider 검색
    public RiderEntity getRandomRiderInfo() { return riderRepository.randomRiderInfo(); }

    // 주문번호로 검색한 주문 상태
    public ShopOrderStatusEntity getOrderStatusInfo(String orderNo) { return shopOrderStatusRepository.orderStatusInfo(orderNo); }

    // 배달 등록
    public void saveOrderRider(String orderNo, String shopNo, String riderNo, String pickupDt, String completeDt) {
        OrderRiderEntity orderRiderEntity = new OrderRiderEntity();
        orderRiderEntity.setOrderNo(orderNo);
        orderRiderEntity.setShopNo(shopNo);
        orderRiderEntity.setRiderNo(riderNo);
        orderRiderEntity.setPickupDt(pickupDt);
        orderRiderEntity.setCompleteDt(completeDt);
        orderRiderRepository.save(orderRiderEntity);
    }

    // 라이버 배달여부 업데이트
    public void updateRiderStatus(String riderNo, Character riderStatus) {
        RiderEntity riderEntity = riderRepository.getRiderInfo(riderNo);
        riderEntity.setRiderRun(riderStatus);
        riderRepository.save(riderEntity);
    }

    // 주문 상태 업데이트
    // 주문시스템을 호출하여 주문 상태를 업데이트 시킴
    public Integer updateOrderStatus(String orderNo, String orderStatus, String riderNo) {
        Integer ret = 0;
        try{
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

            Map<String, Object> map = new HashMap<>();
            map.put("orderNo", orderNo);
            map.put("orderStatus", orderStatus);
            map.put("riderNo", riderNo);
            String param = objectMapper.writeValueAsString(map);

            HttpEntity entity = new HttpEntity(param, httpHeaders);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> responseEntity = restTemplate.exchange("http://localhost:8080/orderStatusUpdate", HttpMethod.POST, entity, String.class);
            if(responseEntity.getBody().toString().indexOf("SUCCESS") < 0) {
                ret = -1;
                logger.error(responseEntity.getStatusCode().toString());
                logger.error(responseEntity.getBody());
            }
        } catch (Exception e) {
            logger.error(e.toString());
            ret = -1;
        }
        return ret;
    }

    public Object riderCheck(String orderNo) {
        Object obj = null;
        try {
            obj = orderRiderLogRepository.riderJoinInfo(orderNo);
            logger.debug(obj.toString());

        } catch (Exception e) {
            logger.error(e.toString());
        }
        return obj;
    }

}
