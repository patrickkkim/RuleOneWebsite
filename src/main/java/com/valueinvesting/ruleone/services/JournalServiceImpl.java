package com.valueinvesting.ruleone.services;

import com.valueinvesting.ruleone.entities.AppUser;
import com.valueinvesting.ruleone.entities.Journal;
import com.valueinvesting.ruleone.exceptions.JournalAlreadyExistException;
import com.valueinvesting.ruleone.exceptions.JournalNotFoundException;
import com.valueinvesting.ruleone.repositories.JournalRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public Journal createJournal(Journal journal) {
        Optional<Journal> optional = journalRepository.findById(journal.getId());
        if (optional.isPresent()) {
            throw new JournalAlreadyExistException("Journal already exists with ID: " + optional.get().getId());
        }
        return journalRepository.save(journal);
    }

    @Override
    public List<Journal> getJournalByAppUser(AppUser appUser) {
        return journalRepository.findJournalByAppUserId(appUser.getId());
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
