package com.valueinvesting.ruleone.entities;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.Instant;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

@DataJpaTest
@ExtendWith(MockitoExtension.class)
class JournalTest {

    @Autowired private TestEntityManager testEntityManager;

    @Test
    void checkIfJournalCanBeInserted() {
        String tickerSymbol = "AAPL";
        Instant journalDate = Instant.now();
        boolean isBought = true;
        float stockPrice = (float)280.95;
        int stockAmount = 25;
        String jsonBigFiveNumber = "{'roic': {'2023-01-10': '20.2', '2023-01-11': '23.65'}" +
                "'fcf': {'2023-01-10': '2.55', '2023-01-11': '6.45'}" +
                "}";
        String memo = "This is a memo!";
        AppUser appUser = new AppUser();
        appUser.setEmail("a@a.com");
        appUser.setEncryptedPassword("asdfasdfasdfasf");
        appUser.setUsername("honggildong");

        Journal journal = new Journal(tickerSymbol, journalDate, isBought, stockPrice,
                stockAmount, jsonBigFiveNumber, memo, appUser);

        int id = testEntityManager.persist(journal).getId();
        Journal newJournal = testEntityManager.find(Journal.class, id);

        assertThat(newJournal).isEqualTo(journal);
    }

    @Test
    void checkIfJournalCantBeInsertedWhenJSONIsBlank() {
        String tickerSymbol = "AAPL";
        Instant journalDate = Instant.now();
        boolean isBought = true;
        float stockPrice = (float)280.95;
        int stockAmount = 25;
        String jsonBigFiveNumber = null;
        String memo = "This is a memo!";
        AppUser appUser = new AppUser();
        appUser.setEmail("a@a.com");
        appUser.setEncryptedPassword("asdfasdfasdfasf");
        appUser.setUsername("honggildong");

        Journal journal = new Journal(tickerSymbol, journalDate, isBought, stockPrice,
                stockAmount, jsonBigFiveNumber, memo, appUser);

        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> testEntityManager.persist(journal))
                .withMessageContaining("must not be blank");
    }

    @Test
    void CheckIfMultipleJournalCanBeInsertedWithSingleAppUser() {
        String tickerSymbol = "AAPL";
        Instant journalDate = Instant.now();
        boolean isBought = true;
        float stockPrice = (float)280.95;
        int stockAmount = 25;
        String jsonBigFiveNumber = "{'roic': {'2023-01-10': '20.2', '2023-01-11': '23.65'}" +
                "'fcf': {'2023-01-10': '2.55', '2023-01-11': '6.45'}" +
                "}";
        String memo = "This is a memo!";
        AppUser appUser = new AppUser();
        appUser.setEmail("a@a.com");
        appUser.setEncryptedPassword("asdfasdfasdfasf");
        appUser.setUsername("honggildong");

        Journal journal1 = new Journal(tickerSymbol, journalDate, isBought, stockPrice,
                stockAmount, jsonBigFiveNumber, memo, appUser);
        Journal journal2 = new Journal(tickerSymbol, journalDate, isBought, stockPrice,
                stockAmount, jsonBigFiveNumber, memo, appUser);

        AppUser appUser1 = testEntityManager.persist(journal1).getAppUser();
        AppUser appUser2 = testEntityManager.persist(journal2).getAppUser();

        assertThat(appUser1).isEqualTo(appUser2);
    }

    @Test
    void CheckIfJournalUpdatesLastEditDateWhenItsUpdated() {
        String tickerSymbol = "AAPL";
        Instant journalDate = Instant.now();
        boolean isBought = true;
        float stockPrice = (float)280.95;
        int stockAmount = 25;
        String jsonBigFiveNumber = "{'roic': {'2023-01-10': '20.2', '2023-01-11': '23.65'}" +
                "'fcf': {'2023-01-10': '2.55', '2023-01-11': '6.45'}" +
                "}";
        String memo = "This is a memo!";
        AppUser appUser = new AppUser();
        appUser.setEmail("a@a.com");
        appUser.setEncryptedPassword("asdfasdfasdfasf");
        appUser.setUsername("honggildong");

        Journal journal = new Journal(tickerSymbol, journalDate, isBought, stockPrice,
                stockAmount, jsonBigFiveNumber, memo, appUser);
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