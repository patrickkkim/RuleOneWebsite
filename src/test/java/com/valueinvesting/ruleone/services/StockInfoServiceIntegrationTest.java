package com.valueinvesting.ruleone.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.valueinvesting.ruleone.entities.BigFiveNumberType;
import com.valueinvesting.ruleone.exceptions.JsonNodeNullException;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class StockInfoServiceIntegrationTest {

    @Autowired private StockInfoService underTest;

    @Test
    void checkIfFetchesFullDatasets() {
        JsonNode fetchedResult = underTest.fetchFullDatasets("META");

        assertThat(fetchedResult.path("data").path("financials").path("annual")
                .isMissingNode()).isFalse();
        assertThat(fetchedResult.path("data").path("financials").path("annual").path("roic")
                .isMissingNode()).isFalse();
        assertThat(fetchedResult.path("data").path("financials").path("annual").path("revenue")
                .isMissingNode()).isFalse();
    }

    @Test
    void checkIfFetchFullDatasetsThrowsExceptionWhenMissing() {
        assertThatExceptionOfType(JsonNodeNullException.class)
                .isThrownBy(() -> {
                    JsonNode jsonNode = underTest.fetchFullDatasets("asdf");
                })
                .withMessageContaining("null or missing");
    }
}