package com.valueinvesting.ruleone.entities;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

@DataJpaTest
@ExtendWith(MockitoExtension.class)
class JournalTest {

    @Autowired private TestEntityManager testEntityManager;
    private Journal journal;

    @BeforeEach
    void setUp() {
        AppUser appUser = new AppUser();
        appUser.setEmail("a@a.com");
        appUser.setEncryptedPassword("asdfasdfasdfasf");
        appUser.setUsername("honggildong");

        Map<String, String> roic = new HashMap<>();
        Map<String, String> sales = new HashMap<>();
        Map<String, String> eps = new HashMap<>();
        Map<String, String> equity = new HashMap<>();
        Map<String, String> fcf = new HashMap<>();
        for (int i = 0; i < 10; ++i) {
            String date = "2023-01-0" + i;
            roic.put(date, String.valueOf(i));
            sales.put(date, String.valueOf(i));
            eps.put(date, String.valueOf(i));
            equity.put(date, String.valueOf(i));
            fcf.put(date, String.valueOf(i));
        }
        Map<String, Object> bigFiveNumbers = new HashMap<>();
        bigFiveNumbers.put("roic", roic);
        bigFiveNumbers.put("sales", sales);
        bigFiveNumbers.put("eps", eps);
        bigFiveNumbers.put("equity", equity);
        bigFiveNumbers.put("fcf", fcf);
        journal = new Journal();
        journal.setTickerSymbol("AAPL");
        journal.setBought(true);
        journal.setStockPrice((float)280.95);
        journal.setStockAmount(25);
        journal.setJsonBigFiveNumber(bigFiveNumbers);
        journal.setMemo("This is a memo!");
        journal.setAppUser(appUser);
    }

    @Test
    void checkIfJournalCanBeInserted() {
        int id = testEntityManager.persist(journal).getId();
        Journal newJournal = testEntityManager.find(Journal.class, id);

        assertThat(newJournal).isEqualTo(journal);
    }

    @Test
    void checkIfJsonBigFiveNumberIsInsertedCorrectly() {
        Map<String, String> roic = new HashMap<>();
        Map<String, String> sales = new HashMap<>();
        Map<String, String> eps = new HashMap<>();
        Map<String, String> equity = new HashMap<>();
        Map<String, String> fcf = new HashMap<>();
        for (int i = 0; i < 10; ++i) {
            String date = "2023-01-0" + i;
            roic.put(date, String.valueOf(i));
            sales.put(date, String.valueOf(i));
            eps.put(date, String.valueOf(i));
            equity.put(date, String.valueOf(i));
            fcf.put(date, String.valueOf(i));
        }
        Map<String, Object> bigFiveNumbers = new HashMap<>();
        bigFiveNumbers.put("roic", roic);
        bigFiveNumbers.put("sales", sales);
        bigFiveNumbers.put("eps", eps);
        bigFiveNumbers.put("equity", equity);
        bigFiveNumbers.put("fcf", fcf);

        journal.setJsonBigFiveNumber(bigFiveNumbers);
        Journal newJournal = testEntityManager.persist(journal);

        assertThat(newJournal.getJsonBigFiveNumber()).isEqualTo(bigFiveNumbers);
    }

    @Test
    void checkIfJournalCantBeInsertedWhenJSONIsNull() {
        journal.setJsonBigFiveNumber(null);

        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> testEntityManager.persist(journal))
                .withMessageContaining("must not be null");
    }

    @Test
    void CheckIfMultipleJournalCanBeInsertedWithSingleAppUser() {
        Journal journal1 = journal;
        Journal journal2 = journal;

        AppUser appUser1 = testEntityManager.persist(journal1).getAppUser();
        AppUser appUser2 = testEntityManager.persist(journal2).getAppUser();

        assertThat(appUser1).isEqualTo(appUser2);
    }

    @Test
    void CheckIfJournalUpdatesLastEditDateWhenItsUpdated() {
        Instant previousLastEditDate = Instant.now();
        journal.setLastEditDate(previousLastEditDate);
        int id = testEntityManager.persist(journal).getId();
        Journal found = testEntityManager.find(Journal.class, id);
        found.setBought(false);
        testEntityManager.flush();
        testEntityManager.clear();

        found = testEntityManager.find(Journal.class, id);

        assertThat(found.getLastEditDate()).isNotEqualTo(previousLastEditDate);
    }
}