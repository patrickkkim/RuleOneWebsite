package com.valueinvesting.ruleone.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.valueinvesting.ruleone.entities.BigFiveNumberType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.*;

@SpringBootTest
@Transactional
@WebAppConfiguration
@AutoConfigureMockMvc
class GrowthRateControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    private Map<String, List<Double>> bigFiveNumberMap = new HashMap<>();

    @BeforeEach
    void setUp() {
        List<Double> salesList = new ArrayList<>(), epsList = new ArrayList<>(),
                fcfList = new ArrayList<>(), equityList = new ArrayList<>(),
                roicList = new ArrayList<>();
        Collections.addAll(salesList, 7872.0,12466.0,17928.0,27638.0,40653.0,55838.0,70697.0,85965.0,117929.0,116609.0);
        Collections.addAll(epsList, 0.60,1.10,1.29,3.49,5.39,7.57,6.43,10.09,13.77,8.59);
        Collections.addAll(equityList, 6.15,13.55,15.50,20.24,25.15,28.80,35.14,44.42,43.68,46.53);
        Collections.addAll(fcfList, 2860.0,5495.0,7797.0,11617.0,17483.0,15359.0,21212.0,23632.0,39116.0,19289.0);
        Collections.addAll(roicList, 10.2,11.3,9.1,19.7,23.9,27.8,18.9,23.5,28.6,16.1);
        bigFiveNumberMap.put("SALES", salesList);
        bigFiveNumberMap.put("EPS", epsList);
        bigFiveNumberMap.put("EQUITY", equityList);
        bigFiveNumberMap.put("FCF", fcfList);
        bigFiveNumberMap.put("ROIC", roicList);
    }

    @Test
    @WithMockUser(username = "honggildong", authorities = {"SCOPE_ESSENTIAL"})
    void checkIfGetsBigFiveGrowthRates() throws Exception {
        String content = objectMapper.writeValueAsString(bigFiveNumberMap);

        mockMvc.perform(MockMvcRequestBuilders.get("/growth")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(username = "honggildong", authorities = {"SCOPE_ESSENTIAL"})
    void checkIfGetsStickerPrice() throws Exception {
        Map<String, Object> contentMap = new HashMap<>();
        contentMap.put("bigFiveNumbers", bigFiveNumberMap);
        contentMap.put("type", "EQUITY");
        String content = objectMapper.writeValueAsString(contentMap);

        mockMvc.perform(MockMvcRequestBuilders.get("/growth/sticker-price")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}