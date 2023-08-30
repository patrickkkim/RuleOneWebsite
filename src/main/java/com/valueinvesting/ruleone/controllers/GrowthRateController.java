package com.valueinvesting.ruleone.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.valueinvesting.ruleone.entities.BigFiveNumberType;
import com.valueinvesting.ruleone.services.GrowthRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/growth")
public class GrowthRateController {

    private GrowthRateService growthRateService;
    private ObjectMapper objectMapper;

    @Autowired
    public GrowthRateController(GrowthRateService growthRateService, ObjectMapper objectMapper) {
        this.growthRateService = growthRateService;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public ResponseEntity<?> getBigFiveGrowthRates(
            @RequestBody Map<BigFiveNumberType, List<Double>> bigFiveNumbers) {
        Map<BigFiveNumberType, Map<Integer, Double>> result =
                growthRateService.getBigFiveGrowthRates(bigFiveNumbers);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/sticker-price")
    public ResponseEntity<?> getStickerPrice(
            @RequestBody Map<String, Object> request) throws Exception {
        Map<BigFiveNumberType, List<Double>> bigFiveNumbers = objectMapper.readValue(
                objectMapper.writeValueAsString(request.get("bigFiveNumbers")), new TypeReference<>() {});
        BigFiveNumberType type = objectMapper.readValue(
                objectMapper.writeValueAsString(request.get("type")), new TypeReference<>() {});

        double result = growthRateService.getStickerPrice(bigFiveNumbers, type);
        return ResponseEntity.ok(result);
    }
}
