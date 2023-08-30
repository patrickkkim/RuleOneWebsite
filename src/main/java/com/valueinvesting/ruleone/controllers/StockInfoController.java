package com.valueinvesting.ruleone.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.valueinvesting.ruleone.entities.BigFiveNumberType;
import com.valueinvesting.ruleone.services.StockInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/stock-info")
public class StockInfoController {

    private StockInfoService stockInfoService;
    private ObjectMapper objectMapper;

    @Autowired
    public StockInfoController(StockInfoService stockInfoService, ObjectMapper objectMapper) {
        this.stockInfoService = stockInfoService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/big-five/annual/{ticker}")
    public ResponseEntity<?> getAnnualBigFiveNumbers(@PathVariable String ticker) {
        JsonNode jsonNode = stockInfoService.fetchFullDatasets(ticker);
        Map<BigFiveNumberType, Object> bigFiveNumbers =
                stockInfoService.getAnnualBigFiveNumbers(jsonNode);
        return ResponseEntity.ok(bigFiveNumbers);
    }
}
