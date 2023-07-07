package com.valueinvesting.ruleone.services;

import com.valueinvesting.ruleone.entities.AppUser;
import com.valueinvesting.ruleone.entities.Journal;

import java.util.List;
import java.util.Map;

public interface JournalService {
    Journal createJournal(Journal journal);

    List<Journal> getJournalByAppUser(AppUser appUser);

    void updateJsonBigFiveNumberByJournalId(int journalId, Map<String, Object> jsonBigFiveNumber);

    void updateMemoByJournalId(int journalId, String memo);
}
