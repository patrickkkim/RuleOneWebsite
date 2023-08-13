package com.valueinvesting.ruleone.services;

import com.valueinvesting.ruleone.repositories.JournalRepository;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JournalServiceTest {

    @Mock
    JournalRepository journalRepository;
    private JournalService underTest;

    @BeforeEach
    void setUp() {
        underTest = new JournalServiceImpl(journalRepository);
    }

    @Disabled
    @Test
    void checkIfComputesROICGrowthRate() {
        List<Double> roicList = new ArrayList<>();
        int years = 10;
        for (int i = 1; i < years; ++i) {
            roicList.add((double) i);
        }

        assertThat(underTest.computeROICAverage(roicList)).isCloseTo(0.9f, Percentage.withPercentage(99));
    }
}