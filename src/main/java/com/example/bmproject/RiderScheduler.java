package com.example.bmproject;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Component
public class RiderScheduler {

    @Autowired
    private ObjectMapper objectMapper;

    private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
    @Scheduled(fixedDelay = 10000)
    public void updateRiderLog() {
        logger.debug("time : {}", new Date());
        Date today = new Date();
        Locale currentLocale = new Locale("KOREAN", "KOREA");
        String pattern = "yyyyMMddHHmmss";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern, currentLocale);
        try{
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

            Map<String, Object> map = new HashMap<>();
            map.put("currentDt", formatter.format(today));
            String param = objectMapper.writeValueAsString(map);

            HttpEntity entity = new HttpEntity(param, httpHeaders);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> responseEntity = restTemplate.exchange("http://localhost:8080/orderLog", HttpMethod.POST, entity, String.class);
            logger.debug(responseEntity.getStatusCode().toString());
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }
}
