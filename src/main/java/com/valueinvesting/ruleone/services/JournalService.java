package com.valueinvesting.ruleone.services;

import com.valueinvesting.ruleone.entities.AppUser;
import com.valueinvesting.ruleone.entities.Journal;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface JournalService {
    Journal createJournal(@NotNull Journal journal);

    float computeROICAverage(List<Double> roicList);

    float computeGrowthRate(float previousValue, float currentValue, int years);

    Map<String, Object> getBigFiveGrowthRates(@NotNull Map<String, List<Double>> bigFiveNumbers);

    float getStickerPrice(Map<String, Object> bigFiveGrowthNumbers);

    Page<Journal> getPaginatedJournals(@NotNull AppUser appUser, int page, int size);

    Map<String, Object> getTotalStockPercentage(@NotNull AppUser appUser);

    int getTotalGain(AppUser appUser);

    void updateJsonBigFiveNumberByJournalId(int journalId, Map<String, List<Double>> jsonBigFiveNumber);

    void updateMemoByJournalId(int journalId, String memo);
}
