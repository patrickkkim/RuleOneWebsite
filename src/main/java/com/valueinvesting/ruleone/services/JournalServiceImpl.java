package com.valueinvesting.ruleone.services;

import com.valueinvesting.ruleone.entities.AppUser;
import com.valueinvesting.ruleone.entities.Journal;
import com.valueinvesting.ruleone.exceptions.JournalAlreadyExistException;
import com.valueinvesting.ruleone.exceptions.JournalInvalidException;
import com.valueinvesting.ruleone.exceptions.JournalNotFoundException;
import com.valueinvesting.ruleone.repositories.JournalRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
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
    public float computeROICGrowthRate(List<Float> roicList, int years) {
        /*
        ???
        Identify the historical net income of the company. This information can be obtained from the company's financial statements, such as the income statement.

        Determine the company's average net fixed assets, which includes property, plant, and equipment (PP&E), and other long-term assets. This information is typically available in the company's balance sheet.

        Calculate the average ROIC over a specific period, usually the past 10 years. The formula for calculating average ROIC is:

        Average ROIC = Average Net Income / Average Net Fixed Assets

        Here, "Average Net Income" refers to the average of the net income values over the selected period, and "Average Net Fixed Assets" refers to the average of the net fixed assets values over the same period.

        Calculate the growth rate of ROIC using the following formula:

        ROIC Growth Rate = (Current ROIC / Average ROIC)^(1/n) - 1

        Here, "Current ROIC" refers to the most recent ROIC value available, "Average ROIC" represents the average ROIC calculated in step 3, and "n" represents the number of years in the selected period.

        The formula calculates the compound annual growth rate (CAGR) of ROIC over the specified period.

         */

        float sum = 0.0f, avg;
        for (Float roic : roicList) {
            sum += roic;
        }
        avg = sum / roicList.size();
        return avg;
    }

    @Override
    public float computeGrowthRate(float previousValue, float currentValue, int years) {
        float growth = 0.0f;

        if (previousValue < 0) previousValue = -previousValue;
        if (currentValue < 0) currentValue = -currentValue;

        float doubles = (float) Math.log(currentValue / previousValue);
        float double_years = years / doubles;
        growth = 72.0f / double_years;
        return growth;
    }

    @Override
    public Map<String, Object> getBigFiveGrowthNumbers(@NotNull Map<String, Object> bigFiveNumbers) {
        return null;
    }

    @Override
    public float getStickerPrice(Map<String, Object> bigFiveGrowthNumbers) {
        return 0;
    }

    @Override
    public Page<Journal> getPaginatedJournals(@NotNull AppUser appUser, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return journalRepository.findJournalByAppUserId(appUser.getId(), pageable);
    }

    @Override
    public Map<String, Object> getTotalStockPercentage(@NotNull AppUser appUser) {
        journalRepository.findAll();
        return null;
    }

    @Override
    public int getTotalGain(AppUser appUser) {
        return 0;
    }

    @Transactional
    @Override
    public void updateJsonBigFiveNumberByJournalId(int journalId, Map<String, Object> jsonBigFiveNumber) {
        Optional<Journal> optional = journalRepository.findById(journalId);
        if (optional.isEmpty()) {
            throw new JournalNotFoundException("Journal not found with ID: " + journalId);
        }
        journalRepository.updateJsonBigFiveNumberById(journalId, jsonBigFiveNumber);
    }

    @Transactional
    @Override
    public void updateMemoByJournalId(int journalId, String memo) {
        Optional<Journal> optional = journalRepository.findById(journalId);
        if (optional.isEmpty()) {
            throw new JournalNotFoundException("Journal not found with ID: " + journalId);
        }
        journalRepository.updateMemoById(journalId, memo);
    }
}
