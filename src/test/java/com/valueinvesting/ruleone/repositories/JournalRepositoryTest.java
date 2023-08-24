package com.valueinvesting.ruleone.repositories;

import com.valueinvesting.ruleone.entities.AppUser;
import com.valueinvesting.ruleone.entities.Authority;
import com.valueinvesting.ruleone.entities.BigFiveNumberType;
import com.valueinvesting.ruleone.entities.Journal;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

@DataJpaTest
class JournalRepositoryTest {

    @Autowired private TestEntityManager testEntityManager;
    @Autowired private JournalRepository underTest;
    private List<Journal> journalList = new ArrayList<>();
    private AppUser appUser;

    @BeforeEach
    void setUp() {
        appUser = new AppUser();
        appUser.setUsername("honggilddong");
        appUser.setEmail("a@a.com");
        appUser.setEncryptedPassword("asdfasdfasdfasdf");
        Authority authority = new Authority();
        authority.setAppUser(appUser);
        appUser.setAuthority(new HashSet<>(List.of(authority)));

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
        Map<BigFiveNumberType, List<Double>> bigFiveNumbers = new HashMap<>();
        bigFiveNumbers.put(BigFiveNumberType.ROIC, roic);
        bigFiveNumbers.put(BigFiveNumberType.SALES, sales);
        bigFiveNumbers.put(BigFiveNumberType.EPS, eps);
        bigFiveNumbers.put(BigFiveNumberType.EQUITY, equity);
        bigFiveNumbers.put(BigFiveNumberType.FCF, fcf);

        for (int i = 0; i < 9; ++i) {
            Journal journal = new Journal();
            journal.setAppUser(appUser);
            journal.setMemo("asdf");
            journal.setBought(true);
            journal.setTickerSymbol("BABA");
            journal.setStockPrice(100);
            journal.setStockAmount(1);
            journal.setStockDate(LocalDate.of(2020, 7, 28));
            journal.setJsonBigFiveNumber(bigFiveNumbers);
            journalList.add(journal);
        }

        Journal journal = journalList.get(0);
        journal.setBought(false);
        journal.setTickerSymbol("AAPL");
        journal.setStockPrice(100);
        journal.setStockAmount(2);
        journal.setStockDate(LocalDate.of(2020, 7, 28));

        journal = journalList.get(1);
        journal.setBought(true);
        journal.setTickerSymbol("AAPL");
        journal.setStockPrice(100);
        journal.setStockAmount(5);
        journal.setStockDate(LocalDate.of(2020, 5, 22));

        journal = journalList.get(2);
        journal.setBought(false);
        journal.setTickerSymbol("META");
        journal.setStockPrice(303.28);
        journal.setStockAmount(1);
        journal.setStockDate(LocalDate.of(2023, 8, 18));

        journal = journalList.get(3);
        journal.setBought(true);
        journal.setTickerSymbol("META");
        journal.setStockPrice(204.23);
        journal.setStockAmount(3);
        journal.setStockDate(LocalDate.of(2022, 4, 25));

        journal = journalList.get(4);
        journal.setBought(true);
        journal.setTickerSymbol("META");
        journal.setStockPrice(216.14);
        journal.setStockAmount(5);
        journal.setStockDate(LocalDate.of(2022, 3, 25));

        journal = journalList.get(5);
        journal.setBought(true);
        journal.setTickerSymbol("META");
        journal.setStockPrice(205.26);
        journal.setStockAmount(5);
        journal.setStockDate(LocalDate.of(2022, 3, 22));

        journal = journalList.get(6);
        journal.setBought(true);
        journal.setTickerSymbol("META");
        journal.setStockPrice(212.68);
        journal.setStockAmount(1);
        journal.setStockDate(LocalDate.of(2022, 2, 22));

        journal = journalList.get(7);
        journal.setBought(true);
        journal.setTickerSymbol("META");
        journal.setStockPrice(253.49);
        journal.setStockAmount(4);
        journal.setStockDate(LocalDate.of(2022, 2, 8));

        journal = journalList.get(8);
        journal.setBought(true);
        journal.setTickerSymbol("META");
        journal.setStockPrice(339.98);
        journal.setStockAmount(3);
        journal.setStockDate(LocalDate.of(2021, 1, 5));
    }

    @Test
    void checkIfFindsJournalByAppUserId() {
        underTest.saveAll(journalList);

        Pageable pageable = PageRequest.of(0, 3);
        List<Journal> found = underTest.findJournalByAppUserIdOrderByStockDateDesc(
                appUser.getId(), pageable).getContent();

        Assertions.assertThat(found).containsExactly(
                journalList.get(2), journalList.get(3), journalList.get(4));
    }

    @Test
    void checkIfPaginatesMultipleJournalByAppUserId() {
        underTest.saveAll(journalList);

        Page<Journal> page1 = underTest.findJournalByAppUserIdOrderByStockDateDesc(
                appUser.getId(), PageRequest.of(0, 1));
        Page<Journal> page2 = underTest.findJournalByAppUserIdOrderByStockDateDesc(
                appUser.getId(), PageRequest.of(1, 1));
        assertThat(page1.getContent().get(0)).isEqualTo(journalList.get(2));
        assertThat(page2.getContent().get(0)).isEqualTo(journalList.get(3));
        assertThatExceptionOfType(IndexOutOfBoundsException.class)
                .isThrownBy(() -> page1.getContent().get(1));
    }

    @Test
    void checkIfFindsJournalByAppUserIdAndTickerSymbol() {
        underTest.saveAll(journalList);

        List<Journal> found = underTest.findJournalByAppUserIdAndTickerSymbolOrderByStockDateDesc(
                appUser.getId(), "META", PageRequest.of(1, 2)).getContent();

        Assertions.assertThat(found).containsExactly(journalList.get(4), journalList.get(5));
    }

    @Test
    void checkIfFindsAllByAppUserId() {
        AppUser appUser2 = new AppUser();
        appUser2.setUsername("honggilddong2");
        appUser2.setEmail("b@b.com");
        appUser2.setEncryptedPassword("asdfasdfasdfasdf");
        Authority authority = new Authority();
        authority.setAppUser(appUser2);
        appUser2.setAuthority(new HashSet<>(List.of(authority)));
        journalList.get(1).setAppUser(appUser2);

        underTest.saveAll(journalList);

        List<Journal> found = underTest.findAllByAppUserId(appUser2.getId());
        assertThat(found.size()).isEqualTo(1);
        assertThat(found.get(0).getAppUser()).isEqualTo(appUser2);
    }

    @Test
    void checkIfUpdatedTickerSymbolById() {
        String newSymbol = "GOOGL";

        underTest.saveAll(journalList);
        int id = journalList.get(0).getId();
        underTest.updateTickerSymbolById(id, newSymbol);
        testEntityManager.refresh(journalList.get(0));
        Journal found = underTest.findById(id).get();

        assertThat(found.getTickerSymbol()).isEqualTo(newSymbol);
    }

    @Test
    void checkIfUpdatedBoughtById() {
        boolean newBought = false;

        underTest.saveAll(journalList);
        int id = journalList.get(0).getId();
        underTest.updateBoughtById(id, newBought);
        testEntityManager.refresh(journalList.get(0));
        Journal found = underTest.findById(id).get();

        assertThat(found.isBought()).isEqualTo(newBought);
    }

    @Test
    void checkIfUpdatedStockPriceById() {
        float newPrice = (float) 215.9945;

        underTest.saveAll(journalList);
        int id = journalList.get(0).getId();
        underTest.updateStockPriceById(id, newPrice);
        testEntityManager.refresh(journalList.get(0));
        Journal found = underTest.findById(id).get();

        assertThat(found.getStockPrice()).isEqualTo(newPrice);
    }

    @Test
    void checkIfUpdateStockAmountById() {
        int newAmount = 399;

        underTest.saveAll(journalList);
        int id = journalList.get(0).getId();
        underTest.updateStockAmountById(id, newAmount);
        testEntityManager.refresh(journalList.get(0));
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
        Map<BigFiveNumberType, List<Double>> bigFiveNumbers = new HashMap<>();
        bigFiveNumbers.put(BigFiveNumberType.ROIC, roic);
        bigFiveNumbers.put(BigFiveNumberType.SALES, sales);
        bigFiveNumbers.put(BigFiveNumberType.EPS, eps);
        bigFiveNumbers.put(BigFiveNumberType.EQUITY, equity);
        bigFiveNumbers.put(BigFiveNumberType.FCF, fcf);

        underTest.saveAll(journalList);
        int id = journalList.get(0).getId();
        underTest.updateJsonBigFiveNumberById(id, bigFiveNumbers);
        testEntityManager.refresh(journalList.get(0));
        Journal found = underTest.findById(id).get();

        assertThat(found.getJsonBigFiveNumber().get(BigFiveNumberType.ROIC).get(0))
                .isCloseTo(10.0f, Percentage.withPercentage(5));
    }

    @Test
    void updateMemoById() {
        String newMemo = "New memo!";

        underTest.saveAll(journalList);
        int id = journalList.get(0).getId();
        underTest.updateMemoById(id, newMemo);
        testEntityManager.refresh(journalList.get(0));
        Journal found = underTest.findById(id).get();

        assertThat(found.getMemo()).isEqualTo(newMemo);
    }
}