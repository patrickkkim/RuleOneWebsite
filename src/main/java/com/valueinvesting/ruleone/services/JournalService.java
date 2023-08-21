package com.valueinvesting.ruleone.services;

import com.valueinvesting.ruleone.entities.AppUser;
import com.valueinvesting.ruleone.entities.Journal;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface JournalService {
    Journal createJournal(@NotNull Journal journal);

    Page<Journal> getPaginatedJournals(@NotNull AppUser appUser, int page, int size);

    Map<String, List<Journal>> getAllJournalsForEachStockTicker(@NotNull AppUser appUser);

    Map<String, Double> getTotalStockPercentage(@NotNull AppUser appUser);

    Map<String, Double> getTotalGainForEachStock(@NotNull AppUser appUser);

    void updateJsonBigFiveNumberByJournalId(int journalId, @NotNull Map<String, List<Double>> jsonBigFiveNumber);

    void updateMemoByJournalId(int journalId, @NotNull String memo);
}
