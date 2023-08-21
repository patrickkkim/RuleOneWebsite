package com.valueinvesting.ruleone.services;

import com.valueinvesting.ruleone.entities.AppUser;
import com.valueinvesting.ruleone.entities.Authority;
import com.valueinvesting.ruleone.entities.BigFiveNumberType;
import com.valueinvesting.ruleone.entities.Journal;
import com.valueinvesting.ruleone.exceptions.JournalInvalidException;
import com.valueinvesting.ruleone.exceptions.JournalNotFoundException;
import com.valueinvesting.ruleone.repositories.JournalRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

@SpringBootTest
@Transactional
class JournalServiceIntegrationTest {

    @Autowired private JournalService underTest;
    @Autowired private JournalRepository journalRepository;
    @Autowired private EntityManager entityManager;
    AppUser appUser;
    List<Journal> journalList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        appUser = new AppUser();
        appUser.setUsername("honggildong");
        appUser.setEmail("a@a.com");
        appUser.setEncryptedPassword("adsfasdfasdf");
        Authority authority = new Authority();
        authority.setAppUser(appUser);
        appUser.setAuthority(new HashSet<>(List.of(authority)));

        Map<BigFiveNumberType, List<Double>> bigFiveNumbers = new HashMap<>();

        for (int i = 0; i < 10; ++i) {
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
    void checkIfCreatesJournal() {
        underTest.createJournal(journalList.get(0));

        Journal journal = journalRepository.findAllByAppUserId(appUser.getId()).get(0);

        assertThat(journal).isEqualTo(journalList.get(0));
    }

    @Test
    void checkIfCreateJournalThrowsExceptionWhenAppUserIsNull() {
        Journal journal = new Journal();

        assertThatExceptionOfType(JournalInvalidException.class)
                .isThrownBy(() -> underTest.createJournal(journal))
                .withMessageContaining("AppUser");
    }

    @Test
    void checkIfCreateJournalThrowsExceptionWhenStockAmountIsZero() {
        journalList.get(0).setStockAmount(0);

        assertThatExceptionOfType(JournalInvalidException.class)
                .isThrownBy(() -> underTest.createJournal(journalList.get(0)))
                .withMessageContaining("amount");
    }

    @Test
    void checkIfGetsPaginatedJournals() {
        journalRepository.saveAll(journalList);

        List<Journal> list1 = underTest.getPaginatedJournals(appUser, 0, 3).getContent();
        List<Journal> list2 = underTest.getPaginatedJournals(appUser, 1, 3).getContent();

        Assertions.assertThat(list1).containsExactly(
                journalList.get(2), journalList.get(3), journalList.get(4));
        Assertions.assertThat(list2).containsExactly(
                journalList.get(5), journalList.get(6), journalList.get(7));
    }

    @Test
    void checkIfGetPaginatedJournalsIsEmptyWhenOutOfBounds() {
        journalRepository.saveAll(journalList);

        Page<Journal> page = underTest.getPaginatedJournals(appUser, 4, 3);

        assertThat(page.hasContent()).isFalse();
    }

    @Test
    void checkIfGetsPaginatedJournalsForSingleStockTicker(){
        journalRepository.saveAll(journalList);

        Page<Journal> page = underTest.getPaginatedJournalsForSingleStockTicker(
                appUser, "META", 2, 2);

        Assertions.assertThat(page.getContent()).containsExactly(
                journalList.get(6), journalList.get(7));
    }

    @Test
    void checkIfGetPaginatedJournalsForSingleStockTickerThrowsExceptionWhenBlank(){
        journalRepository.saveAll(journalList);

        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> {
                    underTest.getPaginatedJournalsForSingleStockTicker(
                            appUser, "", 2, 2);
                }).withMessageContaining("must not be blank");
    }

    @Test
    void checkIfGetsTotalStockPercentage() {
        journalRepository.saveAll(journalList);

        Map<String, Double> percentages = underTest.getTotalStockPercentage(appUser);

        assertThat(percentages.get("AAPL")).isCloseTo(0.125, Percentage.withPercentage(1));
        assertThat(percentages.get("META")).isCloseTo(0.83333, Percentage.withPercentage(1));
        assertThat(percentages.get("BABA")).isCloseTo(0.04166, Percentage.withPercentage(1));
    }

    @Test
    void checkIfGetsTotalGain() {
        journalRepository.saveAll(journalList);

        assertThat(underTest.getTotalGainForEachStock(appUser).get("META"))
                .isCloseTo(63, Percentage.withPercentage(10));
    }

    @Test
    void checkIfUpdatesJsonBigFiveNumberByJournalId() {
        journalRepository.saveAll(journalList);
        Map<BigFiveNumberType, List<Double>> jsonBigFiveNumber = new HashMap<>();
        jsonBigFiveNumber.put(BigFiveNumberType.SALES, new ArrayList<>());

        underTest.updateJsonBigFiveNumberByJournalId(journalList.get(3).getId(),
                jsonBigFiveNumber);
        entityManager.refresh(journalList.get(3));

        assertThat(journalRepository.findJournalByAppUserIdOrderByStockDateDesc(
                appUser.getId(), PageRequest.of(1, 1))
                .getContent().get(0).getJsonBigFiveNumber()).isEqualTo(jsonBigFiveNumber);
    }

    @Test
    void checkIfUpdateJsonBigFiveNumberByJournalIdThrowsExceptionWhenJournalIsNotFound() {
        Map<BigFiveNumberType, List<Double>> jsonBigFiveNumber = new HashMap<>();

        assertThatExceptionOfType(JournalNotFoundException.class)
                .isThrownBy(() -> underTest.updateJsonBigFiveNumberByJournalId(
                        1, jsonBigFiveNumber))
                .withMessageContaining("Journal not found");
    }

    @Test
    void checkIfUpdatesMemoByJournalId() {
        journalRepository.saveAll(journalList);
        String memo = "New memo!!";

        underTest.updateMemoByJournalId(journalList.get(3).getId(), memo);
        entityManager.refresh(journalList.get(3));

        assertThat(journalRepository.findJournalByAppUserIdOrderByStockDateDesc(
                appUser.getId(), PageRequest.of(1, 1))
                .getContent().get(0).getMemo()).isEqualTo(memo);
    }

    @Test
    void checkIfupdateMemoByJournalIdThrowsExceptionWhenJournalIsNotFound() {
        Map<String, List<Double>> jsonBigFiveNumber = new HashMap<>();

        assertThatExceptionOfType(JournalNotFoundException.class)
                .isThrownBy(() -> underTest.updateMemoByJournalId(1, "Hello"))
                .withMessageContaining("Journal not found");
    }
}