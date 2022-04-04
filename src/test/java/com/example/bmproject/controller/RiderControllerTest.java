package com.example.bmproject.controller;

import com.example.bmproject.service.RiderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@WebMvcTest(RiderController.class)
class RiderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RiderService riderService;

    @Test
    @DisplayName("배달 조회 테스트")
    void riderCheck() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("orderNo", "ORD_A01");
        String param = objectMapper.writeValueAsString(map);

        mockMvc.perform(MockMvcRequestBuilders.post("/riderCheck/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(param))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }
}