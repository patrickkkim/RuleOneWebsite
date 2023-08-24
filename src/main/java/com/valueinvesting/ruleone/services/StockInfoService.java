package com.valueinvesting.ruleone.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.valueinvesting.ruleone.entities.BigFiveNumberType;

import java.util.Map;

public interface StockInfoService {

    JsonNode fetchFullDatasets(String ticker);

    public Map<BigFiveNumberType, Object> getAnnualBigFiveNumbers(JsonNode jsonNode);

}
