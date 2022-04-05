package com.example.bmproject.controller;

import com.example.bmproject.entity.RiderEntity;
import com.example.bmproject.entity.ShopOrderStatusEntity;
import com.example.bmproject.service.RiderService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class RiderController {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
    private final RiderService riderService;

    @ApiOperation(value = "배달등록", notes = "주문시스템에서 자동으로 호출되는 API로 주문이 완료되었을 때 호출된다.")
    @RequestMapping(value = "/rider", method = RequestMethod.POST)
    public void init(@RequestBody HashMap<String, Object> map) {
        logger.debug(map.toString());

        RiderEntity riderEntity = riderService.getRandomRiderInfo();
        if(riderEntity == null) {
            logger.warn("No rider available.");
        } else {
            // 배달 가능한 라이더가 있는 경우 픽업 처리
            // bm_order_rider 테이블 입력
            String orderNo = map.get("orderNo").toString();
            String shopNo = map.get("shopNo").toString();
            String riderNo = riderEntity.getRiderNo();
            String pickupDt = map.get("requestDt").toString();
            riderService.saveOrderRider(orderNo, shopNo, riderNo, pickupDt, "0");
            // bm_order_status 테이블 order_status를 PICKUP으로 업데이트
            // 주문시스템을 호출하여 주문 상태를 업데이트 함.
            int r = riderService.updateOrderStatus(orderNo, "PICKUP", riderNo);
            if(r >= 0) {
                // bm_rider 테이블 rider_run을 Y로 업데이트
                riderService.updateRiderStatus(riderNo, 'Y');
            }
        }
    }

    @ApiOperation(value = "배달조회", notes = "주문번호로 현재 배달 상태를 조회한다.")
    @RequestMapping(value = "/riderCheck", method = RequestMethod.POST)
    public Object riderCheck(@RequestBody HashMap<String, Object> map) {
        Object ret = riderService.riderCheck(map.get("orderNo").toString());
        Map<String, Object> output = new HashMap<>();
        if(ret != null) {
            output.put("orderNo", map.get("orderNo").toString());
            output.put("shopNo", ((Object[])ret)[4]);
            output.put("riderName", ((Object[])ret)[0]);
            output.put("riderMobility", ((Object[])ret)[1]);
            if(Integer.parseInt(((Object[])ret)[2].toString()) > 0) {
                output.put("status", "PICKUP");
                output.put("expectedArrival", Integer.parseInt(((Object[])ret)[3].toString()) + "sec");
            } else {
                output.put("status", "COMPLETE");
            }
        }
        // 주문번호로 배달 확인 안되는 경우
        else {
            ShopOrderStatusEntity shopOrderStatusEntity = riderService.getOrderStatusInfo(map.get("orderNo").toString());
            if(shopOrderStatusEntity != null) {
                output.put("orderNo", map.get("orderNo").toString());
                output.put("shopNo", shopOrderStatusEntity.getShopNo());
                output.put("status", "RECEIPT");
            }
            // 주문번호로 주문상태가 확인 안되는 경우
            else {
                output.put("orderNo", map.get("orderNo").toString());
                output.put("status", "Incorrect order number");
            }
        }
        return output;
    }
}
