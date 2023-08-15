package com.valueinvesting.ruleone.services;

import com.valueinvesting.ruleone.entities.BigFiveNumberType;

import java.util.List;
import java.util.Map;

public interface GrowthRateService {
    double computeROICAverage(List<Double> roicList);

    double computeGrowthRate(double previousValue, double currentValue, int years);

    List<Double> getROICAverageList(List<Double> roicList);

    List<Double> getGrowthRateList(List<Double> numberList);

    Map<BigFiveNumberType, List<Double>> getBigFiveGrowthRates(Map<BigFiveNumberType, List<Double>> bigFiveNumbers);

    double getStickerPrice(Map<BigFiveNumberType, List<Double>> bigFiveNumbers);
}
