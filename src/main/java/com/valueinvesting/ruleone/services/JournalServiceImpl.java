package com.valueinvesting.ruleone.services;

import com.valueinvesting.ruleone.entities.AppUser;
import com.valueinvesting.ruleone.entities.BigFiveNumberType;
import com.valueinvesting.ruleone.entities.Journal;
import com.valueinvesting.ruleone.exceptions.JournalAlreadyExistException;
import com.valueinvesting.ruleone.exceptions.JournalInvalidException;
import com.valueinvesting.ruleone.exceptions.JournalNotFoundException;
import com.valueinvesting.ruleone.repositories.JournalRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Validated
public class JournalServiceImpl implements JournalService {

    private JournalRepository journalRepository;

    @Autowired
    public JournalServiceImpl(JournalRepository journalRepository) {
        this.journalRepository = journalRepository;
    }

    @Transactional
    @Override
    public Journal createJournal(@NotNull Journal journal) {
        Optional<Journal> optional = journalRepository.findById(journal.getId());
        if (optional.isPresent()) {
            throw new JournalAlreadyExistException("Journal already exists with ID: " + optional.get().getId());
        }
        if (journal.getAppUser() == null) throw new JournalInvalidException("AppUser cannot be null");
        if (journal.getJsonBigFiveNumber() == null) throw new JournalInvalidException("JSON cannot be null");
        if (journal.getTickerSymbol() == null) throw new JournalInvalidException("Stock symbol should not be null");
        if (journal.getStockAmount() <= 0) {
            throw new JournalInvalidException("Stock amount should be bigger than 0");
        }
        return journalRepository.save(journal);
    }

    @Override
    public Page<Journal> getPaginatedJournals(@NotNull AppUser appUser, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return journalRepository.findJournalByAppUserIdOrderByStockDateDesc(
                appUser.getId(), pageable);
    }

    @Override
    public Page<Journal> getPaginatedJournalsForSingleStockTicker(
            @NotNull AppUser appUser, @NotBlank String ticker, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return journalRepository.findJournalByAppUserIdAndTickerSymbolOrderByStockDateDesc(
                appUser.getId(), ticker, pageable);
    }

    @Override
    public Map<String, List<Journal>> getAllJournalsForEachStockTicker(@NotNull AppUser appUser) {
        List<Journal> all = journalRepository.findAllByAppUserId(appUser.getId());
        return all.stream()
                .sorted(Comparator.comparing(Journal::getStockDate))
                .collect(Collectors.groupingBy(Journal::getTickerSymbol));
    }

    @Override
    public Map<String, Double> getTotalStockPercentage(@NotNull AppUser appUser) {
        Map<String, Double> percentages = new HashMap<>();
        Map<String, List<Journal>> journalMap = getAllJournalsForEachStockTicker(appUser);
        int totalCount = 0;

        for (List<Journal> journalList : journalMap.values()) {
            int count = 0;
            for (Journal journal : journalList) {
                if (journal.isBought()) count += journal.getStockAmount();
                else count -= journal.getStockAmount();
            }
            if (count <= 0) count = 0;
            totalCount += count;
            percentages.put(journalList.get(0).getTickerSymbol(), (double) count);
        }

        for (String key : percentages.keySet()) {
            double percentage = percentages.get(key) / totalCount;
            percentages.put(key, percentage);
        }

        return percentages;
    }

    @Override
    public Map<String, Double> getTotalGainForEachStock(@NotNull AppUser appUser) {
        Map<String, Double> totalGainMap = new HashMap<>();
        Map<String, List<Journal>> stockMap = getAllJournalsForEachStockTicker(appUser);

        for (List<Journal> list : stockMap.values()) {
            double totalGain = 0.0;
            double avgBoughtPrice = 0.0;
            int totalStockAmount = 0;
            for (Journal journal : list) {
                if (journal.isBought()) {
                    avgBoughtPrice = (avgBoughtPrice * totalStockAmount +
                            journal.getStockPrice() * journal.getStockAmount()) /
                            (totalStockAmount + journal.getStockAmount());
                    totalStockAmount += journal.getStockAmount();
                }
                else {
                    if (totalStockAmount > 0) {
                        if (journal.getStockAmount() > totalStockAmount)
                            break;
                        totalGain += (journal.getStockPrice() - avgBoughtPrice)
                                * journal.getStockAmount();
                        totalStockAmount -= journal.getStockAmount();
                    }
                    else {
                        // Add total gain anyway even though the stock was sold before bought...
                        totalGain += journal.getStockPrice() * journal.getStockAmount();
                    }
                }
            }
            totalGainMap.put(list.get(0).getTickerSymbol(), totalGain);
        }

        return totalGainMap;
    }

    @Transactional
    @Override
    public void updateJsonBigFiveNumberByJournalId(int journalId, @NotNull Map<BigFiveNumberType, List<Double>> jsonBigFiveNumber) {
        Optional<Journal> optional = journalRepository.findById(journalId);
        if (optional.isEmpty()) {
            throw new JournalNotFoundException("Journal not found with ID: " + journalId);
        }
        journalRepository.updateJsonBigFiveNumberById(journalId, jsonBigFiveNumber);
    }

    @Transactional
    @Override
    public void updateMemoByJournalId(int journalId, @NotNull String memo) {
        Optional<Journal> optional = journalRepository.findById(journalId);
        if (optional.isEmpty()) {
            throw new JournalNotFoundException("Journal not found with ID: " + journalId);
        }
        journalRepository.updateMemoById(journalId, memo);
    }
}
