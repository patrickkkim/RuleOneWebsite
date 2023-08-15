package com.valueinvesting.ruleone.services;

import com.valueinvesting.ruleone.entities.BigFiveNumberType;
import com.valueinvesting.ruleone.exceptions.JournalInvalidException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GrowthRateServiceImpl implements GrowthRateService {
    @Override
    public double computeROICAverage(List<Double> roicList) {
        double sum = 0.0, avg;
        for (Double roic : roicList) {
            sum += roic;
        }
        avg = sum / roicList.size();
        return avg;
    }

    @Override
    public double computeGrowthRate(double previousValue, double currentValue, int years) {
        double growth = Math.pow((currentValue / previousValue), (1.0 / years)) - 1;

        if (previousValue < 0 && currentValue < 0) growth = -growth;
        else if (previousValue < 0 && currentValue > 0) growth = -growth;

        return growth;
    }

    @Override
    public List<Double> getROICAverageList(List<Double> roicList) {
        List<Double> roicAvgList = new ArrayList<>();

        if (roicList.size() >= 10) {
            roicAvgList.add(computeROICAverage(
                    roicList.subList(roicList.size()-10, roicList.size())));
        } else roicAvgList.add(null);
        if (roicList.size() >= 5) {
            roicAvgList.add(computeROICAverage(
                    roicList.subList(roicList.size()-5, roicList.size())));
        } else roicAvgList.add(null);
        if (roicList.size() >= 1) {
            roicAvgList.add(roicList.get(roicList.size()-1));
        } else roicAvgList.add(null);

        return roicAvgList;
    }

    @Override
    public List<Double> getGrowthRateList(List<Double> numberList) {
        List<Double> growthRateList = new ArrayList<>();

        if (numberList.size() >= 10) {
            growthRateList.add(computeGrowthRate(
                    numberList.get(numberList.size()-10), numberList.get(numberList.size()-1), 10
            ));
        } else growthRateList.add(null);
        if (numberList.size() >= 5) {
            growthRateList.add(computeGrowthRate(
                    numberList.get(numberList.size()-5), numberList.get(numberList.size()-1), 5
            ));
        } else growthRateList.add(null);
        if (numberList.size() >= 2) {
            growthRateList.add(computeGrowthRate(
                    numberList.get(numberList.size()-2), numberList.get(numberList.size()-1), 1
            ));
        } else growthRateList.add(null);

        return growthRateList;
    }

    @Override
    public Map<BigFiveNumberType, List<Double>> getBigFiveGrowthRates(Map<BigFiveNumberType, List<Double>> bigFiveNumbers) {
        Map<BigFiveNumberType, List<Double>> bigFiveGrowthRate = new HashMap<>();
        List<Double> roicList = bigFiveNumbers.get(BigFiveNumberType.ROIC);
        List<Double> salesList = bigFiveNumbers.get(BigFiveNumberType.SALES);
        List<Double> epsList = bigFiveNumbers.get(BigFiveNumberType.EPS);
        List<Double> equityList = bigFiveNumbers.get(BigFiveNumberType.EQUITY);
        List<Double> fcfList = bigFiveNumbers.get(BigFiveNumberType.FCF);

        bigFiveGrowthRate.put(BigFiveNumberType.ROIC, getROICAverageList(roicList));
        bigFiveGrowthRate.put(BigFiveNumberType.SALES, getGrowthRateList(salesList));
        bigFiveGrowthRate.put(BigFiveNumberType.EPS, getGrowthRateList(epsList));
        bigFiveGrowthRate.put(BigFiveNumberType.EQUITY, getGrowthRateList(equityList));
        bigFiveGrowthRate.put(BigFiveNumberType.FCF, getGrowthRateList(fcfList));

        return bigFiveGrowthRate;
    }

    @Override
    public double getStickerPrice(Map<BigFiveNumberType, List<Double>> bigFiveNumbers) {
        Map<BigFiveNumberType, List<Double>> bigFiveGrowthRates = getBigFiveGrowthRates(bigFiveNumbers);
        double averagePER = Integer.MAX_VALUE; // temporary value until api is implemented
        double currentEPS = bigFiveNumbers.get(BigFiveNumberType.EPS)
                .get(bigFiveNumbers.get(BigFiveNumberType.EPS).size()-1);
        double estimatedEPSGrowthRate;
        int years;

        int minSize = Math.min(
                bigFiveGrowthRates.get(BigFiveNumberType.ROIC).size(),
                Math.min(bigFiveGrowthRates.get(BigFiveNumberType.SALES).size(),
                Math.min(bigFiveGrowthRates.get(BigFiveNumberType.EPS).size(),
                Math.min(bigFiveGrowthRates.get(BigFiveNumberType.EQUITY).size(),
                bigFiveGrowthRates.get(BigFiveNumberType.FCF).size())))
        );
        if (minSize >= 10) years = 10;
        else if (minSize >= 5) years = 5;
        else if (minSize >= 1) years = 1;
        else throw new JournalInvalidException("provided data is smaller than a year");

        List<Double> equityGrowthRate = bigFiveGrowthRates.get(BigFiveNumberType.EQUITY);
        estimatedEPSGrowthRate = switch (years) {
            case 10 -> equityGrowthRate.get(0);
            case 5 -> equityGrowthRate.get(1);
            case 1 -> equityGrowthRate.get(2);
            default -> throw new JournalInvalidException("years cannot be out of bounds");
        };

        double futurePER = Math.min(2 * estimatedEPSGrowthRate, averagePER);

        if (estimatedEPSGrowthRate < 0) {
            double sum = 0;
            int count = 0;
            for (List<Double> list : bigFiveGrowthRates.values()) {
                switch (years) {
                    case 10 -> {
                        sum += list.get(0);
                        count++;
                    }
                    case 5 -> {
                        sum += list.get(1);
                        count++;
                    }
                    case 1 -> {
                        sum += list.get(2);
                        count++;
                    }
                    default -> throw new JournalInvalidException("years cannot be out of bounds");
                }
            }
            estimatedEPSGrowthRate = sum / count;
            if (estimatedEPSGrowthRate < 0) {
                return Integer.MIN_VALUE;
            }
        }

        double futureEPS = currentEPS * Math.pow((1 + estimatedEPSGrowthRate), years);
        double futureStickerPrice = futureEPS * futurePER;
        double minReturnDivider = switch (years) { // If 15% return rate
            case 10 -> 4.0;
            case 5 -> 2.0;
            case 1 -> 1.0;
            default -> throw new JournalInvalidException("years cannot be out of bounds");
        };

        return futureStickerPrice / minReturnDivider;
    }
}
