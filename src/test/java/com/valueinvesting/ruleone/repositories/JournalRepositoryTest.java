package com.valueinvesting.ruleone.repositories;

import com.valueinvesting.ruleone.entities.AppUser;
import com.valueinvesting.ruleone.entities.Journal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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
        journal.setJsonBigFiveNumber("{'roic': {'2023-01-10': '20.2', '2023-01-11': '23.65'}," +
                "'fcf': {'2023-01-10': '2.55', '2023-01-11': '6.45'}" +
                "}");
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
        String newJson = "{'roic': {'2023-01-10': '20.9', '2023-01-11': '28.8'}," +
                "'fcf': {'2023-01-10': '2.123', '2023-01-11': '5.0'}" +
                "}";

        int id = underTest.save(journal).getId();
        underTest.updateJsonBigFiveNumberById(id, newJson);
        testEntityManager.refresh(journal);
        Journal found = underTest.findById(id).get();

        assertThat(found.getJsonBigFiveNumber()).isEqualTo(newJson);
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