package com.valueinvesting.ruleone.services;

import com.valueinvesting.ruleone.entities.BigFiveNumberType;
import org.assertj.core.data.Percentage;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

class GrowthRateServiceTest {

    private GrowthRateService underTest;

    @BeforeEach
    void setUp() {
        underTest = new GrowthRateServiceImpl();
    }

    @Test
    void checkIfComputesROICAverageForTenYears() {
        List<Double> roicList = new ArrayList<>();
        Collections.addAll(roicList, 10.2, 11.3, 9.1, 19.7, 23.9, 27.8, 18.9, 23.5, 28.6, 16.1);

        assertThat(underTest.computeROICAverage(roicList))
                .isCloseTo(19.3, Percentage.withPercentage(10));
    }

    @Test
    void checkIfComputesROICAverageForSevenYears() {
        List<Double> roicList = new ArrayList<>();
        Collections.addAll(roicList, 10.2, 11.3, 9.1, 19.7, 23.9, 27.8, 18.9);

        assertThat(underTest.computeROICAverage(roicList))
                .isCloseTo(17.2, Percentage.withPercentage(10));
    }

    @Test
    void checkIfComputesROICAverageForEmptyList() {
        List<Double> roicList = new ArrayList<>();

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> {
                    underTest.computeROICAverage(roicList);
                });
    }

    @Test
    void checkIfComputesGrowthRateForSalesWhenRateIsPositive() {
        List<Double> salesList = new ArrayList<>();
        Collections.addAll(salesList, 102.0, 136.0, 160.0, 169.0, 233.0, 345.0, 369.0, 465.0, 573.0, 763.0);

        assertThat(underTest.computeGrowthRate(salesList))
                .isCloseTo(0.24, Percentage.withPercentage(10));
    }

    @Test
    void checkIfComputesGrowthRateForEPSWhenRateIsNegative() {
        List<Double> epsList = new ArrayList<>();
        Collections.addAll(epsList, 7.28, 6.07, 8.7, 4.18, 8.53, 6.68, 1.77, 3.35, 5.03, 4.96);

        assertThat(underTest.computeGrowthRate(epsList))
                .isCloseTo(-0.04, Percentage.withPercentage(10));
    }

    @Test
    void checkIfComputesGrowthRateForSalesForOneYear() {
        List<Double> salesList = new ArrayList<>();
        Collections.addAll(salesList, 164.0, 158.0);

        assertThat(underTest.computeGrowthRate(salesList))
                .isCloseTo(-0.04, Percentage.withPercentage(10));
    }

    @Test
    void checkIfComputesGrowthRateForEPSWhenNotANumber() {
        List<Double> epsList = new ArrayList<>();
        Collections.addAll(epsList, 0.49, -0.14, -0.24);

        assertThat(underTest.computeGrowthRate(epsList)).isNaN();
    }

    @Test
    void checkIfComputesGrowthRateForEPSWhenValueIsNegative() {
        List<Double> epsList = new ArrayList<>();
        Collections.addAll(epsList, 0.46, -0.14);

        assertThat(underTest.computeGrowthRate(epsList))
                .isCloseTo(-1.304, Percentage.withPercentage(10));
    }

    @Test
    void checkIfComputesGrowthRateForEPSWhenValueIsBothNegative() {
        List<Double> epsList = new ArrayList<>();
        Collections.addAll(epsList, -3.24, -0.22);

        assertThat(underTest.computeGrowthRate(epsList))
                .isCloseTo(0.932, Percentage.withPercentage(10));
    }

    @Test
    void checkIfGetsROICAverageListForTenYears() {
        List<Double> roicList = new ArrayList<>();
        Collections.addAll(roicList, 10.2, 11.3, 9.1, 19.7, 23.9, 27.8, 18.9, 23.5, 28.6, 16.1);

        Map<Integer, Double> roicAvgMap = underTest.getROICAverageList(roicList);

        assertThat(roicAvgMap.get(9)).isCloseTo(19.3, Percentage.withPercentage(10));
        assertThat(roicAvgMap.get(5)).isCloseTo(22.98, Percentage.withPercentage(10));
        assertThat(roicAvgMap.get(3)).isCloseTo(22.7, Percentage.withPercentage(10));
        assertThat(roicAvgMap.get(1)).isCloseTo(16.1, Percentage.withPercentage(10));
    }

    @Test
    void checkIfGetsROICAverageListForSevenYears() {
        List<Double> roicList = new ArrayList<>();
        Collections.addAll(roicList, 19.7, 23.9, 27.8, 18.9, 23.5, 28.6, 16.1);

        Map<Integer, Double> roicAvgMap = underTest.getROICAverageList(roicList);

        assertThat(roicAvgMap.get(6)).isNotNull();
        assertThat(roicAvgMap.get(3)).isCloseTo(22.98, Percentage.withPercentage(10));
        assertThat(roicAvgMap.get(1)).isCloseTo(16.1, Percentage.withPercentage(10));
    }

    @Test
    void checkIfGetsROICAverageListForOneYears() {
        List<Double> roicList = new ArrayList<>();
        Collections.addAll(roicList, 16.1);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> {
                    underTest.getROICAverageList(roicList);
                }).withMessageContaining("No data");
    }

    @Test
    void checkIfGetsGrowthRateListOfEPSForTenYears() {
        List<Double> epsList = new ArrayList<>();
        Collections.addAll(epsList, 7.28, 6.07, 8.7, 4.18, 8.53, 6.68, 1.77, 3.35, 5.03, 4.96);

        Map<Integer, Double> epsGrowthMap = underTest.getGrowthRateList(epsList);

        assertThat(epsGrowthMap.get(9)).isCloseTo(-0.04, Percentage.withPercentage(10));
        assertThat(epsGrowthMap.get(5)).isCloseTo(-0.1, Percentage.withPercentage(10));
        assertThat(epsGrowthMap.get(3)).isCloseTo(0.4, Percentage.withPercentage(10));
        assertThat(epsGrowthMap.get(1)).isCloseTo(-0.013, Percentage.withPercentage(10));
    }

    @Test
    void checkIfGetsGrowthRateListOfEPSForSixYears() {
        List<Double> epsList = new ArrayList<>();
        Collections.addAll(epsList, 8.53, 6.68, 1.77, 3.35, 5.03, 4.96);

        Map<Integer, Double> epsGrowthMap = underTest.getGrowthRateList(epsList);

        assertThat(epsGrowthMap.get(5)).isCloseTo(-0.1, Percentage.withPercentage(10));
        assertThat(epsGrowthMap.get(3)).isCloseTo(0.4, Percentage.withPercentage(10));
        assertThat(epsGrowthMap.get(1)).isCloseTo(-0.013, Percentage.withPercentage(10));
    }

    @Test
    void checkIfGetsGrowthRateListOfEquityForTenYears() {
        List<Double> equityList = new ArrayList<>();
        Collections.addAll(equityList, 23.0, 23.0, 17.0, 15.0, 21.0, 30.0, 20.0, 7.0, 25.0, 27.0);

        Map<Integer, Double> equityGrowthMap = underTest.getGrowthRateList(equityList);

        assertThat(equityGrowthMap.get(9)).isCloseTo(0.017, Percentage.withPercentage(10));
        assertThat(equityGrowthMap.get(5)).isCloseTo(0.05, Percentage.withPercentage(10));
        assertThat(equityGrowthMap.get(3)).isCloseTo(0.11, Percentage.withPercentage(10));
        assertThat(equityGrowthMap.get(1)).isCloseTo(0.08, Percentage.withPercentage(10));
    }

    @Test
    void checkIfGetsBigFiveGrowthRatesForEightYears() {
        List<Double> salesList = new ArrayList<>(), epsList = new ArrayList<>(), fcfList = new ArrayList<>();
        Map<BigFiveNumberType, List<Double>> bigFiveNumberMap = new HashMap<>();
        Collections.addAll(salesList, 117.2, 160.3, 208.6, 265.2, 347.5, 438.3, 539.1, 652.0);
        Collections.addAll(epsList, 0.16, 0.1, 0.17, 0.2, 0.31, 0.43, 0.53, 0.64);
        Collections.addAll(fcfList, null, null, null, 15.0, 617.0, 15990.0, 101.0, 6833.0);

        bigFiveNumberMap.put(BigFiveNumberType.ROIC, null);
        bigFiveNumberMap.put(BigFiveNumberType.EQUITY, null);
        bigFiveNumberMap.put(BigFiveNumberType.EPS, epsList);
        bigFiveNumberMap.put(BigFiveNumberType.SALES, salesList);
        bigFiveNumberMap.put(BigFiveNumberType.FCF, fcfList);
        Map<BigFiveNumberType, Map<Integer, Double>> result = underTest.getBigFiveGrowthRates(bigFiveNumberMap);

        assertThat(result.get(BigFiveNumberType.SALES).get(7)).isCloseTo(0.28, Percentage.withPercentage(10));
        assertThat(result.get(BigFiveNumberType.EPS).get(7)).isCloseTo(0.22, Percentage.withPercentage(10));
        assertThat(result.get(BigFiveNumberType.EQUITY)).isNull();
        assertThat(result.get(BigFiveNumberType.ROIC)).isNull();
    }

    @Test
    void checkIfGetsStickerPriceForMETA() {
        List<Double> salesList = new ArrayList<>(), epsList = new ArrayList<>(),
                fcfList = new ArrayList<>(), equityList = new ArrayList<>(),
                roicList = new ArrayList<>();
        Collections.addAll(salesList, 7872.0,12466.0,17928.0,27638.0,40653.0,55838.0,70697.0,85965.0,117929.0,116609.0);
        Collections.addAll(epsList, 0.60,1.10,1.29,3.49,5.39,7.57,6.43,10.09,13.77,8.59);
        Collections.addAll(equityList, 6.15,13.55,15.50,20.24,25.15,28.80,35.14,44.42,43.68,46.53);
        Collections.addAll(fcfList, 2860.0,5495.0,7797.0,11617.0,17483.0,15359.0,21212.0,23632.0,39116.0,19289.0);
        Collections.addAll(roicList, 10.2,11.3,9.1,19.7,23.9,27.8,18.9,23.5,28.6,16.1);
        Map<BigFiveNumberType, List<Double>> bigFiveNumberMap = new HashMap<>();
        bigFiveNumberMap.put(BigFiveNumberType.SALES, salesList);
        bigFiveNumberMap.put(BigFiveNumberType.EPS, epsList);
        bigFiveNumberMap.put(BigFiveNumberType.EQUITY, equityList);
        bigFiveNumberMap.put(BigFiveNumberType.FCF, fcfList);
        bigFiveNumberMap.put(BigFiveNumberType.ROIC, roicList);

        assertThat(underTest.getStickerPrice(bigFiveNumberMap, BigFiveNumberType.EQUITY))
                .isCloseTo(1013, Percentage.withPercentage(10));
    }
}