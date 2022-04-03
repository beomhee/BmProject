package com.example.bmproject.controller;

import com.example.bmproject.entity.OrderRiderLogEntity;
import com.example.bmproject.entity.ShopMenuEntity;
import com.example.bmproject.service.ShopService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequiredArgsConstructor
public class ShopController {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
    private final ShopService shopService;

    private String getCurrentDateTime() {
        Date today = new Date();
        Locale currentLocale = new Locale("KOREAN", "KOREA");
        String pattern = "yyyyMMddHHmmss";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern, currentLocale);
        return formatter.format(today);
    }

    @RequestMapping(value = "/order", method = RequestMethod.POST)
    public Object init(@RequestBody HashMap<String, Object> map) {
        String currentTime = getCurrentDateTime();

        String orderNo = map.get("orderNo").toString();
        String shopNo = map.get("shopNo").toString();
        int distance = Integer.parseInt(map.get("distance").toString());
        // 단일 메뉴 주문이 아닌 경우
        ArrayList<LinkedHashMap> orderMenus = (ArrayList<LinkedHashMap>) map.get("menus");
        if(orderMenus == null) {
            // 단일 메뉴 주문인 경우
            orderMenus = (ArrayList<LinkedHashMap>) map.get("menu");
        }
        int totalPrice = 0;
        String orderStatus = "";
        Map<String, Object> output = new HashMap<>();

        String menuString = "";
        int    totalQuantity = 0;
        for(HashMap<String, Object> menuMap : orderMenus) {
            int r = shopService.saveOrder(orderNo, shopNo, menuMap.get("menuNo").toString(), Integer.parseInt(menuMap.get("quantity").toString()));
            if(r >= 0) {
                // 메뉴 총 수량 검사
                totalQuantity += Integer.parseInt(menuMap.get("quantity").toString());
                if (totalQuantity > 5) {
                    orderStatus = "CANCEL";
                }
                // 중복 메뉴 검사
                if (menuString.contains(menuMap.get("menuNo").toString()) == false) {
                    menuString = menuString.concat(menuMap.get("menuNo").toString());
                    // DB에서 메뉴 정보를 가져옴
                    ShopMenuEntity menuInfo = shopService.getMenuInfo(menuMap.get("menuNo").toString());
                    if(menuInfo != null) {
                        // 판매 가능 수량 확인
                        // 판매 가능 수량보다 많은 양을 주문하는 경우 주문 실패로 리턴한다.
                        if (menuInfo.getTodayRemain() < Integer.parseInt(menuMap.get("quantity").toString())) {
                            orderStatus = "CANCEL";
                        }
                        // 주문 총 금액을 계산한다.
                        totalPrice += Integer.parseInt(menuMap.get("quantity").toString()) * menuInfo.getPrice();
                    }
                    // DB에서 주문 정보를 가져오지 못하는 경우 주문 실패로 리턴한다.
                    else {
                        orderStatus = "CANCEL";
                    }
                }
                // 중복된 메뉴가 있는 경우 주문 실패로 리턴한다.
                else {
                    orderStatus = "CANCEL";
                }
            }
            // 주문 저장에 실패하는 경우 주문 실패로 리턴한다.
            else {
                orderStatus = "CANCEL";
            }
        }
        // 배달 거리가 200 미만 이거나 600초과면 배달취소
        if(distance < 200 || distance > 600) {
            orderStatus = "CANCEL";
        }
        // 주문이 성공한 경우
        if(orderStatus == "") {
            orderStatus = "RECEIPT";
            // 주문 정보 업데이트 및 오늘 판매가능 수량 업데이트
            for (HashMap<String, Object> menuMap : orderMenus) {
                //  todayRemain = todayRemain - quantity
                ShopMenuEntity menuInfo = shopService.getMenuInfo(menuMap.get("menuNo").toString());
                if(menuInfo != null) {
                    int todayRemain = menuInfo.getTodayRemain() - Integer.parseInt(menuMap.get("quantity").toString());
                    int r = shopService.updateTodayRemain(menuMap.get("menuNo").toString(), todayRemain);
                    // 판매가능 수량을 수정하지 못하는 경우 주문 실패로 리턴한다.
                    if(r < 0) {
                        orderStatus = "CANCEL";
                        totalPrice = 0;
                    }
                }
                // 메뉴 정보를 읽어오지 못하는 경우
                else {
                    orderStatus = "CANCEL";
                    totalPrice = 0;
                }
            }
        }
        // 주문이 실패한 경우
        else {
            // 주문이 취소된 경우 주문금액을 0으로 한다.
            totalPrice = 0;
        }

        // 주문 상태 저장
        shopService.saveorderStatus(orderNo, shopNo, currentTime, totalPrice, orderStatus, distance);
        if(totalPrice > 0) {
            // 배달등록
            shopService.callRider(orderNo, shopNo, currentTime);
        }
        output.put("orderNo", orderNo);
        output.put("shopNo", shopNo);
        output.put("acceptTime", currentTime);
        output.put("totalPrice", totalPrice);
        output.put("result", orderStatus);
        return output;
    }

    // 주문 상태 업데이트
    @RequestMapping(value = "/orderStatusUpdate", method = RequestMethod.POST)
    public Object updateOrderStatus(@RequestBody HashMap<String, Object> map) {
        String orderNo = map.get("orderNo").toString();
        String orderStatus = map.get("orderStatus").toString();
        String riderNo = map.get("riderNo").toString();
        Map<String, Object> output = new HashMap<>();
        int r = shopService.updateOrderStatus(orderNo, orderStatus);
        if (r >= 0) {
            r = shopService.saveOrderRiderLog(orderNo, riderNo, getCurrentDateTime());
            if (r >= 0) {
                output.put("result", "SUCCESS");
            } else {
                output.put("result", "FAILURE");
            }
        }
        // 주문 상태를 업데이트 하지 못한 경우 실패
        else {
            output.put("result", "FAILURE");
        }
        return output;
    }

    @RequestMapping(value = "/orderLog", method = RequestMethod.POST)
    public void orderLog(@RequestBody HashMap<String, Object> map) {
        shopService.updateOrderLog(map.get("currentDt").toString());
    }

    @RequestMapping(value = "/orderCheck", method = RequestMethod.POST)
    public Object orderCheck(@RequestBody HashMap<String, Object> map) {
        List<Object> ret = shopService.orderCheck(map.get("orderNo").toString());
        Map<String, Object> output = new HashMap<>();
        List<Map<String, Object>> menus = new ArrayList<>();
        String status = "";
        String orderNo = "";
        if(ret.size() > 0) {
            for(int i = 0; i < ret.size(); i++) {
                Object orderInfo = ret.get(i);
                logger.debug(orderInfo.toString());
                if(i == 0) {
                    orderNo = ((Object[])orderInfo)[0].toString();
                    status = ((Object[])orderInfo)[3].toString();

                    output.put("orderNo", ((Object[])orderInfo)[0]);
                    output.put("shopNO", ((Object[])orderInfo)[1]);
                    output.put("totalPrice", ((Object[])orderInfo)[2]);
                    output.put("status", ((Object[])orderInfo)[3]);
                }
                Map<String, Object> menu = new HashMap<>();
                menu.put("menuName", ((Object[])orderInfo)[5]);
                menu.put("quantity", ((Object[])orderInfo)[6]);
                menu.put("price", ((Object[])orderInfo)[7]);
                menus.add(menu);
            }
            output.put("menus", menus);
            if(status.equals("PICKUP")) {
                output.put("remainDistance", shopService.checkRemainDistance(orderNo));
            }
        }
        // 주문 번호로 확인이 안되는 경우
        else {
            output.put("orderNo", map.get("orderNo").toString());
            output.put("status", "Incorrect order number");
        }
        return output;
    }

}
