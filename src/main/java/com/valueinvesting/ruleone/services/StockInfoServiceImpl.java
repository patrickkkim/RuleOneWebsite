package com.valueinvesting.ruleone.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.valueinvesting.ruleone.entities.BigFiveNumberType;
import com.valueinvesting.ruleone.entities.JsonNodeToHashMapConverter;
import com.valueinvesting.ruleone.exceptions.JsonNodeNullException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class StockInfoServiceImpl implements StockInfoService {

    private final String API_URL = "https://public-api.quickfs.net/v1";
    @Value("${fs.api.key}")
    private String API_KEY;
    private final RestTemplate restTemplate;
    private JsonNodeToHashMapConverter jsonNodeToHashMapConverter;

    @Autowired
    public StockInfoServiceImpl(RestTemplate restTemplate, JsonNodeToHashMapConverter j) {
        this.restTemplate = restTemplate;
        this.jsonNodeToHashMapConverter = j;
    }

    @Override
    public JsonNode fetchFullDatasets(String ticker) {
        String fetchUrl = API_URL + "/data/all-data/" + ticker + "?api_key=" + API_KEY;
        ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(
                fetchUrl, HttpMethod.GET, null, JsonNode.class);

        JsonNode jsonNode = responseEntity.getBody();
        if (jsonNode == null || jsonNode.isMissingNode() || !jsonNode.path("errors").isMissingNode())
            throw new JsonNodeNullException(
                responseEntity.getStatusCode() + " : fetched json is null or missing");
        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            if (responseEntity.getStatusCode().is4xxClientError())
                throw new HttpServerErrorException(responseEntity.getStatusCode());
            else if (responseEntity.getStatusCode().is5xxServerError())
                throw new HttpServerErrorException(responseEntity.getStatusCode());
            else
                throw new RestClientException(
                        responseEntity.getStatusCode() + " : " + responseEntity.getBody());
        }
        return jsonNode;
    }

    @Override
    public Map<BigFiveNumberType, Object> getAnnualBigFiveNumbers(JsonNode jsonNode) {
        if (jsonNode == null || jsonNode.isMissingNode() || !jsonNode.path("errors").isMissingNode())
            throw new JsonNodeNullException("Json dataset is null or missing");
        JsonNode annualData = jsonNode.path("data").path("financials").path("annual");
        if (annualData.isMissingNode())
            throw new JsonNodeNullException("Annual data is missing");

        Map<String, Object> map =
                jsonNodeToHashMapConverter.convertJsonNodeToHashMap(annualData);
        Map<BigFiveNumberType, Object> bigFiveNumbers = new HashMap<>();
        bigFiveNumbers.put(BigFiveNumberType.SALES, map.get("revenue"));
        bigFiveNumbers.put(BigFiveNumberType.FCF, map.get("fcf"));
        bigFiveNumbers.put(BigFiveNumberType.EPS, map.get("eps_basic"));
        bigFiveNumbers.put(BigFiveNumberType.ROIC, map.get("roic"));
        bigFiveNumbers.put(BigFiveNumberType.EQUITY, map.get("book_value_per_share"));

        return bigFiveNumbers;
    }
}
