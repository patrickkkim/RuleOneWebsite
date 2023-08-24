package com.valueinvesting.ruleone.services;

import com.valueinvesting.ruleone.entities.BigFiveNumberType;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface GrowthRateService {
    double computeROICAverage(List<Double> roicList);

    double computeGrowthRate(List<Double> dataList);

    public Map<Integer, Double> getGrowthRates(
            List<Double> dataList, Function<List<Double>, Double> function);

    Map<Integer, Double> getROICAverageList(List<Double> roicList);

    Map<Integer, Double> getGrowthRateList(List<Double> dataList);

    Map<BigFiveNumberType, Map<Integer, Double>>
    getBigFiveGrowthRates(Map<BigFiveNumberType, List<Double>> bigFiveNumbers);

    double getStickerPrice(Map<BigFiveNumberType, List<Double>> bigFiveNumbers, BigFiveNumberType type);

    double getMOSPrice(double stickerPrice);
}
