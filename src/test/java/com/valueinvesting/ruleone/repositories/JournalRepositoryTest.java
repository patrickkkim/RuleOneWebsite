package com.valueinvesting.ruleone.repositories;

import com.valueinvesting.ruleone.entities.AppUser;
import com.valueinvesting.ruleone.entities.Authority;
import com.valueinvesting.ruleone.entities.Journal;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

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
        Authority authority = new Authority();
        authority.setAppUser(appUser);
        appUser.setAuthority(new HashSet<>(List.of(authority)));

        journal = new Journal();
        journal.setAppUser(appUser);
        journal.setBought(true);
        journal.setTickerSymbol("AAPL");
        journal.setStockPrice((float) 208.85);
        journal.setStockAmount(23);
        List<Double> roic = new ArrayList<>();
        List<Double> sales = new ArrayList<>();
        List<Double> eps = new ArrayList<>();
        List<Double> equity = new ArrayList<>();
        List<Double> fcf = new ArrayList<>();
        for (int i = 0; i < 10; ++i) {
            roic.add((double) i);
            sales.add((double) i);
            eps.add((double) i);
            equity.add((double) i);
            fcf.add((double) i);
        }
        Map<String, List<Double>> bigFiveNumbers = new HashMap<>();
        bigFiveNumbers.put("roic", roic);
        bigFiveNumbers.put("sales", sales);
        bigFiveNumbers.put("eps", eps);
        bigFiveNumbers.put("equity", equity);
        bigFiveNumbers.put("fcf", fcf);

        journal.setJsonBigFiveNumber(bigFiveNumbers);
        journal.setMemo("This is a memo!");
    }

    @Test
    void checkIfFindsJournalByAppUserId() {
        int id = underTest.save(journal).getAppUser().getId();
        Pageable pageable = PageRequest.of(0, 3);
        List<Journal> journalList = underTest.findJournalByAppUserId(id, pageable).getContent();

        assertThat(journalList.get(0)).isEqualTo(journal);
    }

    @Test
    void checkIfFindsMultipleJournalByAppUserId() {
        AppUser appUser = underTest.save(journal).getAppUser();

        Journal journal2 = new Journal();
        journal2.setAppUser(appUser);
        journal2.setBought(true);
        journal2.setTickerSymbol("META");
        journal2.setStockPrice((float) 208.85);
        journal2.setStockAmount(23);
        journal2.setJsonBigFiveNumber(journal.getJsonBigFiveNumber());
        journal2.setMemo("This is a memo!");

        underTest.save(journal2);

        Pageable pageable = PageRequest.of(0, 3);
        List<Journal> journalList = underTest.findJournalByAppUserId(appUser.getId(), pageable).getContent();
        assertThat(journalList.get(0)).isEqualTo(journal);
        assertThat(journalList.get(1)).isEqualTo(journal2);
    }

    @Test
    void checkIfPaginatesMultipleJournalByAppUserId() {
        AppUser appUser = underTest.save(journal).getAppUser();

        Journal journal2 = new Journal();
        journal2.setAppUser(appUser);
        journal2.setBought(true);
        journal2.setTickerSymbol("META");
        journal2.setStockPrice((float) 208.85);
        journal2.setStockAmount(23);
        journal2.setJsonBigFiveNumber(journal.getJsonBigFiveNumber());
        journal2.setMemo("This is a memo!");

        underTest.save(journal2);

        Pageable pageable = PageRequest.of(0, 1);
        Page<Journal> page1 = underTest.findJournalByAppUserId(appUser.getId(), pageable);
        pageable = PageRequest.of(1, 1);
        Page<Journal> page2 = underTest.findJournalByAppUserId(appUser.getId(), pageable);
        assertThat(page1.getContent().get(0)).isEqualTo(journal);
        assertThat(page2.getContent().get(0)).isEqualTo(journal2);
        assertThatExceptionOfType(IndexOutOfBoundsException.class)
                .isThrownBy(() -> {
                    page1.getContent().get(1);
                });
    }

    @Test
    void checkIfFindsAllByAppUserId() {
        underTest.save(journal);

        AppUser appUser2 = new AppUser();
        appUser2.setUsername("honggilddong2");
        appUser2.setEmail("b@b.com");
        appUser2.setEncryptedPassword("asdfasdfasdfasdf");
        Authority authority = new Authority();
        authority.setAppUser(appUser2);
        appUser2.setAuthority(new HashSet<>(List.of(authority)));

        Journal journal2 = new Journal();
        journal2.setAppUser(appUser2);
        journal2.setBought(true);
        journal2.setTickerSymbol("META");
        journal2.setStockPrice((float) 208.85);
        journal2.setStockAmount(23);
        journal2.setJsonBigFiveNumber(journal.getJsonBigFiveNumber());
        journal2.setMemo("This is a memo!");

        underTest.save(journal2);

        List<Journal> journalList = underTest.findAllByAppUserId(appUser2.getId());
        assertThat(journalList.size() == 1).isTrue();
        assertThat(journalList.get(0).getAppUser()).isEqualTo(appUser2);
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
        List<Double> roic = new ArrayList<>();
        List<Double> sales = new ArrayList<>();
        List<Double> eps = new ArrayList<>();
        List<Double> equity = new ArrayList<>();
        List<Double> fcf = new ArrayList<>();
        for (int i = 10; i < 20; ++i) {
            roic.add((double) i);
            sales.add((double) i);
            eps.add((double) i);
            equity.add((double) i);
            fcf.add((double) i);
        }
        Map<String, List<Double>> bigFiveNumbers = new HashMap<>();
        bigFiveNumbers.put("roic", roic);
        bigFiveNumbers.put("sales", sales);
        bigFiveNumbers.put("eps", eps);
        bigFiveNumbers.put("equity", equity);
        bigFiveNumbers.put("fcf", fcf);

        int id = underTest.save(journal).getId();
        underTest.updateJsonBigFiveNumberById(id, bigFiveNumbers);
        testEntityManager.refresh(journal);
        Journal found = underTest.findById(id).get();

        assertThat(found.getJsonBigFiveNumber().get("roic").get(0))
                .isCloseTo(10.0f, Percentage.withPercentage(5));
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