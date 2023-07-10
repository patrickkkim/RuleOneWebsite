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
        return 0;
    }

    @Override
    public float computeGrowthRate(float previousValue, float currentValue, int years) {
        return 0;
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
