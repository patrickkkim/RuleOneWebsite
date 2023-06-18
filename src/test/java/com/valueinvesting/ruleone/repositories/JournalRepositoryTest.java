package com.valueinvesting.ruleone.repositories;

import com.valueinvesting.ruleone.entities.AppUser;
import com.valueinvesting.ruleone.entities.Journal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class JournalRepositoryTest {

    @Autowired private TestEntityManager testEntityManager;
    @Autowired private JournalRepository underTest;
    private Journal journal;

    @BeforeEach
    void setUp() {
        AppUser appUser = new AppUser();
        appUser.setUsername("honggilddong");
        appUser.setEmail("a@a.com");
        appUser.setEncryptedPassword("asdfasdfasdfasdf");

        journal = new Journal();
        journal.setAppUser(appUser);
        journal.setBought(true);
        journal.setTickerSymbol("AAPL");
        journal.setStockPrice((float) 208.85);
        journal.setStockAmount(23);
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
        journal.setMemo("This is a memo!");
    }

    @Test
    void checkIfFoundJournalByAppUserId() {
        int id = underTest.save(journal).getAppUser().getId();
        Optional<Journal> journalOptional = underTest.findJournalByAppUserId(id);

        assertThat(journalOptional.isPresent()).isTrue();
        Journal found = journalOptional.get();
        assertThat(found).isEqualTo(journal);
    }

    @Test
    void checkIfUpdatedTickerSymbolById() {
        String newSymbol = "GOOGL";

        int id = underTest.save(journal).getId();
        underTest.updateTickerSymbolById(id, newSymbol);
        testEntityManager.refresh(journal);
        Journal found = underTest.findById(id).get();

        assertThat(found.getTickerSymbol()).isEqualTo(newSymbol);
    }

    @Test
    void checkIfUpdatedBoughtById() {
        boolean newBought = false;

        int id = underTest.save(journal).getId();
        underTest.updateBoughtById(id, newBought);
        testEntityManager.refresh(journal);
        Journal found = underTest.findById(id).get();

        assertThat(found.isBought()).isEqualTo(newBought);
    }

    @Test
    void checkIfUpdatedStockPriceById() {
        float newPrice = (float) 215.9945;

        int id = underTest.save(journal).getId();
        underTest.updateStockPriceById(id, newPrice);
        testEntityManager.refresh(journal);
        Journal found = underTest.findById(id).get();

        assertThat(found.getStockPrice()).isEqualTo(newPrice);
    }

    @Test
    void checkIfUpdateStockAmountById() {
        int newAmount = 399;

        int id = underTest.save(journal).getId();
        underTest.updateStockAmountById(id, newAmount);
        testEntityManager.refresh(journal);
        Journal found = underTest.findById(id).get();

        assertThat(found.getStockAmount()).isEqualTo(newAmount);
    }

    @Test
    void checkIfUpdatedJsonBigFiveNumberById() {
        Map<String, String> roic = new HashMap<>();
        Map<String, String> sales = new HashMap<>();
        Map<String, String> eps = new HashMap<>();
        Map<String, String> equity = new HashMap<>();
        Map<String, String> fcf = new HashMap<>();
        for (int i = 0; i < 10; ++i) {
            String date = "2023-01-1" + i;
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

        int id = underTest.save(journal).getId();
        underTest.updateJsonBigFiveNumberById(id, bigFiveNumbers);
        testEntityManager.refresh(journal);
        Journal found = underTest.findById(id).get();

        assertThat(found.getJsonBigFiveNumber()).isEqualTo(bigFiveNumbers);
    }

    @Test
    void updateMemoById() {
        String newMemo = "New memo!";

        int id = underTest.save(journal).getId();
        underTest.updateMemoById(id, newMemo);
        testEntityManager.refresh(journal);
        Journal found = underTest.findById(id).get();

        assertThat(found.getMemo()).isEqualTo(newMemo);
    }
}