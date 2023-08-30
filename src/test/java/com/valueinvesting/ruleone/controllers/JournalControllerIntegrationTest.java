package com.valueinvesting.ruleone.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.valueinvesting.ruleone.entities.*;
import com.valueinvesting.ruleone.security.JwtUtil;
import com.valueinvesting.ruleone.services.AppUserService;
import com.valueinvesting.ruleone.services.JournalService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
@WebAppConfiguration
@AutoConfigureMockMvc
class JournalControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JournalService journalService;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private EntityManager entityManager;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private AppUserService appUserService;
    private AppUser appUser;
    private Map<String, Object> journalMap;
    private Map<BigFiveNumberType, List<Double>> jsonBigFiveNumber;
    private String jwt;
    private List<Journal> journalList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        appUser = new AppUser();
        appUser.setUsername("honggildong");
        appUser.setEmail("a@a.com");
        appUser.setEncryptedPassword("asdfasdfasdf123!");
        appUserService.createAppUser(appUser);

        journalMap = new HashMap<>();
        journalMap.put("tickerSymbol", "META");
        journalMap.put("stockDate", "2023-08-13");
        journalMap.put("isBought", true);
        journalMap.put("stockAmount", 13);
        journalMap.put("stockPrice", 235.38);
        journalMap.put("memo", "");

        List<Double> roic = new ArrayList<>();
        List<Double> sales = new ArrayList<>();
        List<Double> eps = new ArrayList<>();
        List<Double> equity = new ArrayList<>();
        List<Double> fcf = new ArrayList<>();
        for (int i = 0; i < 10; ++i) {
            roic.add((double) i);
            sales.add((double) i);
            eps.add((double) i);
            equity.add((double) i);
            fcf.add((double) i);
        }
        jsonBigFiveNumber = new HashMap<>();
        jsonBigFiveNumber.put(BigFiveNumberType.ROIC, (roic));
        jsonBigFiveNumber.put(BigFiveNumberType.SALES, (sales));
        jsonBigFiveNumber.put(BigFiveNumberType.EPS, (eps));
        jsonBigFiveNumber.put(BigFiveNumberType.EQUITY, (equity));
        jsonBigFiveNumber.put(BigFiveNumberType.FCF, (fcf));

        journalMap.put("jsonBigFiveNumber", jsonBigFiveNumber);
        jwt = jwtUtil.generateToken(appUser.getUsername());

        for (int i = 0; i < 10; ++i) {
            Journal journal = new Journal();
            journal.setAppUser(appUser);
            journal.setMemo("asdf");
            journal.setBought(true);
            journal.setTickerSymbol("BABA");
            journal.setStockPrice(100);
            journal.setStockAmount(1);
            journal.setStockDate(LocalDate.of(2020, 7, 28));
            journal.setJsonBigFiveNumber(jsonBigFiveNumber);
            journalList.add(journal);
        }

        Journal journal = journalList.get(0);
        journal.setBought(false);
        journal.setTickerSymbol("AAPL");
        journal.setStockPrice(100);
        journal.setStockAmount(2);
        journal.setStockDate(LocalDate.of(2020, 7, 28));

        journal = journalList.get(1);
        journal.setBought(true);
        journal.setTickerSymbol("AAPL");
        journal.setStockPrice(100);
        journal.setStockAmount(5);
        journal.setStockDate(LocalDate.of(2020, 5, 22));

        journal = journalList.get(2);
        journal.setBought(false);
        journal.setTickerSymbol("META");
        journal.setStockPrice(303.28);
        journal.setStockAmount(1);
        journal.setStockDate(LocalDate.of(2023, 8, 18));

        journal = journalList.get(3);
        journal.setBought(true);
        journal.setTickerSymbol("META");
        journal.setStockPrice(204.23);
        journal.setStockAmount(3);
        journal.setStockDate(LocalDate.of(2022, 4, 25));

        journal = journalList.get(4);
        journal.setBought(true);
        journal.setTickerSymbol("META");
        journal.setStockPrice(216.14);
        journal.setStockAmount(5);
        journal.setStockDate(LocalDate.of(2022, 3, 25));

        journal = journalList.get(5);
        journal.setBought(true);
        journal.setTickerSymbol("META");
        journal.setStockPrice(205.26);
        journal.setStockAmount(5);
        journal.setStockDate(LocalDate.of(2022, 3, 22));

        journal = journalList.get(6);
        journal.setBought(true);
        journal.setTickerSymbol("META");
        journal.setStockPrice(212.68);
        journal.setStockAmount(1);
        journal.setStockDate(LocalDate.of(2022, 2, 22));

        journal = journalList.get(7);
        journal.setBought(true);
        journal.setTickerSymbol("META");
        journal.setStockPrice(253.49);
        journal.setStockAmount(4);
        journal.setStockDate(LocalDate.of(2022, 2, 8));

        journal = journalList.get(8);
        journal.setBought(true);
        journal.setTickerSymbol("META");
        journal.setStockPrice(339.98);
        journal.setStockAmount(3);
        journal.setStockDate(LocalDate.of(2021, 1, 5));
    }

    @Test
    void checkIfCreatesJournal() throws Exception {
        String content = objectMapper.writeValueAsString(journalMap);

        mockMvc.perform(MockMvcRequestBuilders.post("/journals")
                .header("Authorization", "Bearer " + jwt)
                .content(content)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        assertThat(journalService.getPaginatedJournals(appUser, 0, 1)
                .getContent().get(0).getStockPrice()).isEqualTo(235.38);
    }

    @Test
    void checkIfCreateJournalThrowsExceptionWhenNotAuthenticated() throws Exception {
        String content = objectMapper.writeValueAsString(journalMap);

        mockMvc.perform(MockMvcRequestBuilders.post("/journals")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    void checkIfGetsPaginatedJournals() throws Exception {
        for (Journal journal : journalList) {
            entityManager.persist(journal);
        }

        String response = mockMvc.perform(MockMvcRequestBuilders.get("/journals/all")
                .header("Authorization", "Bearer " + jwt)
                .param("page", "1")
                .param("size", "2"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        Map<String, Object> responseMap = objectMapper.readValue(response, new TypeReference<>() {});
        List<Map<String, Object>> list = (List<Map<String, Object>>) responseMap.get("content");
        assertThat(list.get(0).get("stockDate")).isEqualTo(journalList.get(4).getStockDate().toString());
        assertThat(list.get(1).get("stockDate")).isEqualTo(journalList.get(5).getStockDate().toString());
    }

    @Test
    void checkIfGetsPaginatedJournalsForSingleTicker() throws Exception {
        for (Journal journal : journalList) {
            entityManager.persist(journal);
        }

        String response = mockMvc.perform(MockMvcRequestBuilders.get(
                "/journals/stocks/AAPL")
                .header("Authorization", "Bearer " + jwt)
                .param("page", "1")
                .param("size", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        Map<String, Object> responseMap = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {});
        List<Map<String, Object>> list = (List<Map<String, Object>>) responseMap.get("content");
        assertThat(list.get(0).get("stockDate")).isEqualTo(journalList.get(1).getStockDate().toString());
    }

    @Test
    void checkIfGetsStockPercentage() throws Exception {
        for (Journal journal : journalList)
            entityManager.persist(journal);

        String response = mockMvc.perform(MockMvcRequestBuilders.get(
                "/journals/percentages")
                .header("Authorization", "Bearer " + jwt))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        Map<String, Double> percentages = objectMapper.readValue(response, new TypeReference<>() {});
        assertThat(percentages.get("AAPL")).isCloseTo(0.125, Percentage.withPercentage(1));
        assertThat(percentages.get("META")).isCloseTo(0.83333, Percentage.withPercentage(1));
        assertThat(percentages.get("BABA")).isCloseTo(0.04166, Percentage.withPercentage(1));
    }

    @Test
    void checkIfGetsTotalGainForEachStock() throws Exception {
        for (Journal journal : journalList)
            entityManager.persist(journal);

        String response = mockMvc.perform(MockMvcRequestBuilders.get(
                                "/journals/gains")
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        Map<String, Double> gains = objectMapper.readValue(response, new TypeReference<>() {});
        assertThat(gains.get("META"))
                .isCloseTo(63, Percentage.withPercentage(10));
    }

    @Test
    void checkIfUpdatesBigFiveNumbers() throws Exception {
        for (Journal journal : journalList)
            entityManager.persist(journal);

        Map<BigFiveNumberType, List<Double>> newBigFiveNumbers = new HashMap<>();
        for (BigFiveNumberType type : BigFiveNumberType.values()) {
            newBigFiveNumbers.put(type, new ArrayList<>());
            for (int i = 10; i < 20; ++i) {
                newBigFiveNumbers.get(type).add((double) i);
            }
        }

        Map<String, Object> contentMap = new HashMap<>();
        contentMap.put("jsonBigFiveNumber", newBigFiveNumbers);
        String content = objectMapper.writeValueAsString(contentMap);

        mockMvc.perform(MockMvcRequestBuilders.put(
                                "/journals/" + journalList.get(4).getId())
                        .header("Authorization", "Bearer " + jwt)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
        entityManager.refresh(journalList.get(4));

        assertThat(journalService.getJournal(journalList.get(4).getId()).get().getJsonBigFiveNumber())
                .isEqualTo(newBigFiveNumbers);
    }

    @Test
    void checkIfUpdatesBoth() throws Exception {
        for (Journal journal : journalList)
            entityManager.persist(journal);

        Map<BigFiveNumberType, List<Double>> newBigFiveNumbers = new HashMap<>();
        for (BigFiveNumberType type : BigFiveNumberType.values()) {
            newBigFiveNumbers.put(type, new ArrayList<>());
            for (int i = 10; i < 20; ++i) {
                newBigFiveNumbers.get(type).add((double) i);
            }
        }
        String newMemo = "This is a new memo.";

        Map<String, Object> contentMap = new HashMap<>();
        contentMap.put("jsonBigFiveNumber", newBigFiveNumbers);
        contentMap.put("memo", newMemo);
        String content = objectMapper.writeValueAsString(contentMap);

        mockMvc.perform(MockMvcRequestBuilders.put(
                                "/journals/" + journalList.get(4).getId())
                        .header("Authorization", "Bearer " + jwt)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
        entityManager.refresh(journalList.get(4));

        assertThat(journalService.getJournal(journalList.get(4).getId()).get().getJsonBigFiveNumber())
                .isEqualTo(newBigFiveNumbers);
        assertThat(journalService.getJournal(journalList.get(4).getId()).get().getMemo())
                .isEqualTo(newMemo);
    }

    @Test
    void checkIfUpdateThrowsExceptionWhenUserIsDifferent() throws Exception {
        AppUser newAppUser = new AppUser();
        newAppUser.setUsername("newUser123");
        newAppUser.setEmail("b@b.com");
        newAppUser.setEncryptedPassword("asdfasdfasdf123!");
        appUserService.createAppUser(newAppUser);
        String newJwt = jwtUtil.generateToken(newAppUser.getUsername());

        for (Journal journal : journalList)
            entityManager.persist(journal);

        String newMemo = "This is a new memo.";

        Map<String, Object> contentMap = new HashMap<>();
        contentMap.put("memo", newMemo);
        String content = objectMapper.writeValueAsString(contentMap);

        mockMvc.perform(MockMvcRequestBuilders.put(
                                "/journals/" + journalList.get(4).getId())
                        .header("Authorization", "Bearer " + newJwt)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError());

        assertThat(journalService.getJournal(journalList.get(4).getId()).get().getMemo())
                .isEqualTo("asdf");
    }
}