package com.valueinvesting.ruleone.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.valueinvesting.ruleone.entities.BigFiveNumberType;
import com.valueinvesting.ruleone.entities.JsonNodeToHashMapConverter;
import com.valueinvesting.ruleone.exceptions.JsonNodeNullException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StockInfoServiceImplTest {

    private StockInfoService underTest;
    @Mock private RestTemplate restTemplate;
    @Mock private JsonNodeToHashMapConverter jsonNodeToHashMapConverter;
    @Mock private JsonNode jsonNode;
    private Map<String, List<Double>> bigFiveNumberMap = new HashMap<>();


    @BeforeEach
    void setUp() {
        underTest = new StockInfoServiceImpl(restTemplate, jsonNodeToHashMapConverter);

        List<Double> salesList = new ArrayList<>(), epsList = new ArrayList<>(),
                fcfList = new ArrayList<>(), equityList = new ArrayList<>(),
                roicList = new ArrayList<>();
        Collections.addAll(salesList, 7872.0,12466.0,17928.0,27638.0,40653.0,55838.0,70697.0,85965.0,117929.0,116609.0);
        Collections.addAll(epsList, 0.60,1.10,1.29,3.49,5.39,7.57,6.43,10.09,13.77,8.59);
        Collections.addAll(equityList, 6.15,13.55,15.50,20.24,25.15,28.80,35.14,44.42,43.68,46.53);
        Collections.addAll(fcfList, 2860.0,5495.0,7797.0,11617.0,17483.0,15359.0,21212.0,23632.0,39116.0,19289.0);
        Collections.addAll(roicList, 10.2,11.3,9.1,19.7,23.9,27.8,18.9,23.5,28.6,16.1);
        bigFiveNumberMap.put("revenue", salesList);
        bigFiveNumberMap.put("eps_basic", epsList);
        bigFiveNumberMap.put("book_value_per_share", equityList);
        bigFiveNumberMap.put("fcf", fcfList);
        bigFiveNumberMap.put("roic", roicList);
    }

    @Test
    void checkIfFetchesFullCompaniesList() {
        ResponseEntity<JsonNode> responseEntity = Mockito.mock(ResponseEntity.class);
        given(responseEntity.getStatusCode())
                .willReturn(HttpStatusCode.valueOf(200));
        given(responseEntity.getBody())
                .willReturn(jsonNode);
        given(restTemplate.exchange(anyString(), eq(HttpMethod.GET), eq(null), eq(JsonNode.class)))
                .willReturn(responseEntity);

        assertThat(underTest.fetchFullDatasets("META"))
                .isEqualTo(jsonNode);
    }

    @Test
    void checkIfFetchFullCompaniesListThrowsExceptionWhenJsonNodeIsNull() {
        ResponseEntity<JsonNode> responseEntity = Mockito.mock(ResponseEntity.class);
        given(responseEntity.getStatusCode())
                .willReturn(HttpStatusCode.valueOf(200));
        given(responseEntity.getBody())
                .willReturn(null);
        given(restTemplate.exchange(anyString(), eq(HttpMethod.GET), eq(null), eq(JsonNode.class)))
                .willReturn(responseEntity);

        assertThatExceptionOfType(JsonNodeNullException.class)
                .isThrownBy(() -> underTest.fetchFullDatasets("META"));
    }

    @Test
    void checkIfFetchFullCompaniesListThrowsExceptionWhenJsonNodeIsMissing() {
        ResponseEntity<JsonNode> responseEntity = Mockito.mock(ResponseEntity.class);
        given(jsonNode.isMissingNode()).willReturn(true);
        given(responseEntity.getStatusCode())
                .willReturn(HttpStatusCode.valueOf(200));
        given(responseEntity.getBody())
                .willReturn(jsonNode);
        given(restTemplate.exchange(anyString(), eq(HttpMethod.GET), eq(null), eq(JsonNode.class)))
                .willReturn(responseEntity);

        assertThatExceptionOfType(JsonNodeNullException.class)
                .isThrownBy(() -> underTest.fetchFullDatasets("META"));
    }

    @Test
    void checkIfFetchFullCompaniesListThrowsExceptionWhenStatusCodeIs404() {
        ResponseEntity<JsonNode> responseEntity = Mockito.mock(ResponseEntity.class);
        given(responseEntity.getStatusCode())
                .willReturn(HttpStatusCode.valueOf(404));
        given(responseEntity.getBody())
                .willReturn(jsonNode);
        given(restTemplate.exchange(anyString(), eq(HttpMethod.GET), eq(null), eq(JsonNode.class)))
                .willReturn(responseEntity);

        assertThatExceptionOfType(HttpServerErrorException.class)
                .isThrownBy(() -> underTest.fetchFullDatasets("META"))
                .withMessageContaining("404");
    }

    @Test
    void checkIfFetchFullCompaniesListThrowsExceptionWhenStatusCodeIs500() {
        ResponseEntity<JsonNode> responseEntity = Mockito.mock(ResponseEntity.class);
        given(responseEntity.getStatusCode())
                .willReturn(HttpStatusCode.valueOf(500));
        given(responseEntity.getBody())
                .willReturn(jsonNode);
        given(restTemplate.exchange(anyString(), eq(HttpMethod.GET), eq(null), eq(JsonNode.class)))
                .willReturn(responseEntity);

        assertThatExceptionOfType(HttpServerErrorException.class)
                .isThrownBy(() -> underTest.fetchFullDatasets("META"))
                .withMessageContaining("500");
    }

    @Test
    void checkIfGetsAnnualBigFiveNumbers() {
        given(jsonNode.path(anyString())).willReturn(jsonNode);
        given(jsonNodeToHashMapConverter.convertJsonNodeToHashMap(any()))
                .willAnswer((Answer<Map<String, Object>>) invocation -> {
                    Map<String, Object> objectMap = new HashMap<>();
                    for (Map.Entry<String, List<Double>> entry : bigFiveNumberMap.entrySet()) {
                        objectMap.put(entry.getKey(), new ArrayList<>(entry.getValue()));
                    }
                    return objectMap;
                });

        assertThat(underTest.getAnnualBigFiveNumbers(jsonNode).get(BigFiveNumberType.ROIC))
                .isEqualTo(bigFiveNumberMap.get("roic"));
        assertThat(underTest.getAnnualBigFiveNumbers(jsonNode).get(BigFiveNumberType.EQUITY))
                .isEqualTo(bigFiveNumberMap.get("book_value_per_share"));
    }

    @Test
    void checkIfGetAnnualBigFiveNumbersThrowsExceptionWhenJsonNodeIsMissing() {
        given(jsonNode.isMissingNode()).willReturn(true);

        assertThatExceptionOfType(JsonNodeNullException.class)
                .isThrownBy(() -> underTest.getAnnualBigFiveNumbers(jsonNode));
    }
}