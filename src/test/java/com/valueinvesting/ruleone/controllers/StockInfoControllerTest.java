package com.valueinvesting.ruleone.controllers;

import com.valueinvesting.ruleone.services.StockInfoService;
import jakarta.transaction.Transactional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@WebAppConfiguration
@AutoConfigureMockMvc
class StockInfoControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired StockInfoService stockInfoService;

    @Test
    @WithMockUser(username = "honggildong", authorities = {"SCOPE_PREMIUM"})
    void checkIfGetsAnnualBigFiveNumbers() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(
                "/stock-info/big-five/annual/META"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(
                        Matchers.containsString("ROIC")));
    }
}