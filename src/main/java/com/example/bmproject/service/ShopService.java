package com.example.bmproject.service;

import com.example.bmproject.entity.*;
import com.example.bmproject.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopMenuRepository shopMenuRepository;
    private final ShopOrderRepository shopOrderRepository;
    private final ShopOrderStatusRepository ShopOrderStatusRepository;
    private final OrderRiderLogRepository orderRiderLogRepository;
    private final OrderRiderRepository orderRiderRepository;
    private final RiderRepository riderRepository;

    private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @Autowired
    private ObjectMapper objectMapper;

    // 메뉴 정보 검색
    public ShopMenuEntity getMenuInfo(String menoNo) {
        return shopMenuRepository.menuInfo(menoNo);
    }

    // 주문상태로 검색된 개수
    public Long getCount(String orderName) {
        return ShopOrderStatusRepository.countByorderStatus(orderName);
    }

    // 오늘 남은 메뉴 수량 업데이트
    public Integer updateTodayRemain(String menoNo, Integer todayRemain) {
        int ret = 0;
        try {
            ShopMenuEntity shopMenuEntity = shopMenuRepository.menuInfo(menoNo);
            shopMenuEntity.setTodayRemain(todayRemain);
            shopMenuRepository.save(shopMenuEntity);
        } catch (Exception e) {
            logger.error(e.toString());
            ret = -1;
        }
        return ret;
    }

    // 주문 정보 저장
    public Integer saveOrder(String orderNo, String shopNo, String menuNo, Integer quantity) {
        int ret = 0;
        try {
            ShopOrderEntity shopOrderEntity = new ShopOrderEntity();
            shopOrderEntity.setOrderNo(orderNo);
            shopOrderEntity.setShopNo(shopNo);
            shopOrderEntity.setMenuNo(menuNo);
            shopOrderEntity.setQuantity(quantity);
            shopOrderRepository.save(shopOrderEntity);
        } catch (Exception e) {
            logger.error(e.toString());
            ret = -1;
        }
        return ret;
    }

    // 주문 상태 저장
    public Integer saveorderStatus(String orderNo, String shopNo, String accessTime, Integer totalPrice, String orderStatus, Integer distance) {
        int ret = 0;
        try {
            ShopOrderStatusEntity ShopOrderStatusEntity = new ShopOrderStatusEntity();
            ShopOrderStatusEntity.setOrderNo(orderNo);
            ShopOrderStatusEntity.setShopNo(shopNo);
            ShopOrderStatusEntity.setAcceptTime(accessTime);
            ShopOrderStatusEntity.setTotalPrice(totalPrice);
            ShopOrderStatusEntity.setorderStatus(orderStatus);
            ShopOrderStatusEntity.setDistance(distance);
            ShopOrderStatusRepository.save(ShopOrderStatusEntity);
        } catch (Exception e) {
            logger.error(e.toString());
            ret = -1;
        }
        return ret;
    }

    public Integer callRider(String orderNo, String shopNo, String requestDt) {
        int ret = 0;
        try{
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

            Map<String, Object> map = new HashMap<>();
            map.put("orderNo", orderNo);
            map.put("shopNo", shopNo);
            map.put("requestDt", requestDt);
            String param = objectMapper.writeValueAsString(map);

            HttpEntity entity = new HttpEntity(param, httpHeaders);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> responseEntity = restTemplate.exchange("http://localhost:8080/rider", HttpMethod.POST, entity, String.class);
        } catch (Exception e) {
            logger.error(e.toString());
            ret = -1;
        }
        return ret;
    }

    // 주문 상태 업데이트
    public Integer updateOrderStatus(String orderNo, String orderStatus) {
        int ret = 0;
        try {
            ShopOrderStatusEntity ShopOrderStatusEntity = ShopOrderStatusRepository.orderStatusInfo(orderNo);
            ShopOrderStatusEntity.setorderStatus(orderStatus);
            ShopOrderStatusRepository.save(ShopOrderStatusEntity);
        } catch (Exception e) {
            logger.error(e.toString());
            ret = -1;
        }
        return ret;
    }

    // 주문 배달 로그 입력
    public Integer saveOrderRiderLog(String orderNo, String riderNo, String regDt) {
        int ret = 0;
        try {
            ShopOrderStatusEntity ShopOrderStatusEntity = ShopOrderStatusRepository.orderStatusInfo(orderNo);

            OrderRiderLogEntity orderRiderLogEntity = new OrderRiderLogEntity();
            orderRiderLogEntity.setOrderNo(orderNo);
            orderRiderLogEntity.setShopNo(ShopOrderStatusEntity.getShopNo());
            orderRiderLogEntity.setRiderNo(riderNo);
            orderRiderLogEntity.setRemainDistance(ShopOrderStatusEntity.getDistance());
            orderRiderLogEntity.setRegDt(regDt);
            orderRiderLogRepository.save(orderRiderLogEntity);
        } catch (Exception e) {
            logger.error(e.toString());
            ret = -1;
        }
        return ret;
    }

    // 배달 로그 저장
    public Integer updateOrderLog(String currentDt) {
        int ret = 0;
        try {
            List<ShopOrderStatusEntity> ShopOrderStatusEntity = ShopOrderStatusRepository.orderStatusPickupInfo();
            for (ShopOrderStatusEntity entity : ShopOrderStatusEntity) {
                OrderRiderLogEntity orderRiderLogEntity = orderRiderLogRepository.orderLogInfo(entity.getOrderNo());
                RiderEntity riderEntity = riderRepository.getRiderInfo(orderRiderLogEntity.getRiderNo());
                int remain = orderRiderLogEntity.getRemainDistance() - riderEntity.getRiderSpeed();
                // 남은 거리가 0인경우 배달 완료처리한다.
                if (remain <= 0) {
                    remain = 0;
                    // 배달이 완료되어 주문상태를 COMPLETE로 변경한다.
                    this.updateOrderStatus(entity.getOrderNo(), "COMPLETE");
                    // 배달 완료 상태 로그
                    logger.info("[" + riderEntity.getRiderNo() + "][" + riderEntity.getRiderName() + "][" + entity.getOrderNo() + "][배달완료]");
                    // 주문 배달 완료 시간을 업데이트 한다.
                    OrderRiderEntity orderRiderEntity = orderRiderRepository.getOrderRiderInfo(entity.getOrderNo());
                    orderRiderEntity.setCompleteDt(currentDt);
                    orderRiderRepository.save(orderRiderEntity);

                    // 배달 완료된 경우 라이더의 배달여부를 N으로 변경한다.
                    riderEntity.setRiderRun('N');
                    riderRepository.save(riderEntity);

                    // 아직 배달되지 않은 주문 있는 경우 라이더를 배정한다.
                    Long receiptCount = this.getCount("RECEIPT");
                    if (receiptCount > 0) {
                        ShopOrderStatusEntity ShopOrderStatusEntityNew = ShopOrderStatusRepository.orderStatusReceiptInfo();
                        this.callRider(ShopOrderStatusEntityNew.getOrderNo(), ShopOrderStatusEntityNew.getShopNo(), currentDt);
                    }
                }
                // 배달 완료 상태 로그
                else
                {
                    logger.info("[" + riderEntity.getRiderNo() + "][" + riderEntity.getRiderName() + "][" + entity.getOrderNo() + "][" + remain + "]");
                }
                OrderRiderLogEntity saveEntity = new OrderRiderLogEntity();
                saveEntity.setOrderNo(entity.getOrderNo());
                saveEntity.setShopNo(entity.getShopNo());
                saveEntity.setRiderNo(riderEntity.getRiderNo());
                saveEntity.setRemainDistance(remain);
                saveEntity.setRegDt(currentDt);
                orderRiderLogRepository.save(saveEntity);
            }
        } catch (Exception e) {
            logger.error(e.toString());
            ret = -1;
        }
        return ret;
    }

    public List<Object> orderCheck(String orderNo) {
        List<Object> obj = null;
        try {
            obj = ShopOrderStatusRepository.orderJoinInfo(orderNo);
            logger.debug(obj.toString());

        } catch (Exception e) {
            logger.error(e.toString());
        }
        return obj;
    }

    public Integer checkRemainDistance(String orderNo) {
        OrderRiderLogEntity orderRiderLogEntity = orderRiderLogRepository.orderLogInfo(orderNo);
        return orderRiderLogEntity.getRemainDistance();
    }

}
