package com.valueinvesting.ruleone.services;

import com.valueinvesting.ruleone.entities.BigFiveNumberType;
import com.valueinvesting.ruleone.exceptions.JournalInvalidException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class GrowthRateServiceImpl implements GrowthRateService {
    @Override
    public double computeROICAverage(List<Double> roicList) {
        if (roicList.size() == 0) throw new IllegalArgumentException("ROIC list is empty");

        roicList = roicList.subList(1, roicList.size());
        double sum = 0.0, avg;
        int nullCount = 0;
        for (Double roic : roicList) {
            if (roic == null) nullCount++;
            else sum += roic;
        }
        avg = sum / (roicList.size() - nullCount);
        return avg;
    }

    @Override // Can return NaN
    public double computeGrowthRate(List<Double> dataList) {
        if (dataList.size() <= 1) throw new IllegalArgumentException("Data list is smaller than 2");

        int years = dataList.size()-1;
        Double currentValue = dataList.get(years);
        Double previousValue = dataList.get(0);
        if (currentValue == null || previousValue == null) return Double.NaN;

        double growth = Math.pow((currentValue / previousValue), (1.0 / years)) - 1;
        if (currentValue < 0 && previousValue < 0) {
            growth = -growth;
        }
        return growth;
    }

    public Map<Integer, Double> getGrowthRates(
            List<Double> dataList, Function<List<Double>, Double> function) {
        if (dataList == null) return null;

        Map<Integer, Double> growthRates = new HashMap<>();

        if (dataList.size() >= 10) {
            dataList.subList(0, 10);
        }
        if (dataList.size() >= 2) {
            for (int i = dataList.size()-1; i >= 1; --i) {
                growthRates.put(i, function.apply(dataList.subList(dataList.size()-(i+1), dataList.size())));
            }
        }
        else throw new IllegalArgumentException("No data was provided enough for growth rate calculation");

        return growthRates;
    }

    @Override
    public Map<Integer, Double> getROICAverageList(List<Double> roicList) {
        return getGrowthRates(roicList, this::computeROICAverage);
    }

    @Override
    public Map<Integer, Double> getGrowthRateList(List<Double> dataList) {
        return getGrowthRates(dataList, this::computeGrowthRate);
    }

    @Override
    public Map<BigFiveNumberType, Map<Integer, Double>>
    getBigFiveGrowthRates(Map<BigFiveNumberType, List<Double>> bigFiveNumbers) {
        Map<BigFiveNumberType, Map<Integer, Double>> bigFiveGrowthRates = new HashMap<>();

        for (BigFiveNumberType type : BigFiveNumberType.values()) {
            List<Double> dataList = bigFiveNumbers.get(type);
            if (type == BigFiveNumberType.ROIC) {
                bigFiveGrowthRates.put(type, getROICAverageList(dataList));
            }
            else {
                bigFiveGrowthRates.put(type, getGrowthRateList(dataList));
            }
        }

        return bigFiveGrowthRates;
    }

    @Override
    public double getStickerPrice(
        Map<BigFiveNumberType, List<Double>> bigFiveNumbers, BigFiveNumberType type
    ) {
        double averagePER = Integer.MAX_VALUE; // temporary value until api is implemented
        double currentEPS = bigFiveNumbers.get(BigFiveNumberType.EPS)
                .get(bigFiveNumbers.get(BigFiveNumberType.EPS).size()-1);
        double estimatedEPSGrowthRate;
        int years = 0;

        Map<Integer, Double> growthRate
                = getGrowthRateList(bigFiveNumbers.get(type));
        for (int key : growthRate.keySet()) {
            years = Math.max(years, key);
        }
        estimatedEPSGrowthRate = growthRate.get(years);

        double futurePER = Math.min(2 * estimatedEPSGrowthRate, averagePER) * 100;

        double futureEPS = currentEPS * Math.pow((1 + estimatedEPSGrowthRate), years);
        double futureStickerPrice = futureEPS * futurePER;

        return futureStickerPrice / Math.pow(1+0.15, years);
    }

    @Override
    public double getMOSPrice(double stickerPrice) {
        return stickerPrice / 2.0;
    }
}
