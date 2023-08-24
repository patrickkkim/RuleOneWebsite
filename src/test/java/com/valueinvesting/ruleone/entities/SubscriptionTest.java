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
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;


@DataJpaTest
@ExtendWith(MockitoExtension.class)
class SubscriptionTest {

    @Autowired private TestEntityManager testEntityManager;

    AppUser appUser;

    @BeforeEach
    void setUp() {
        appUser = new AppUser();
        appUser.setEmail("a@a.com");
        appUser.setEncryptedPassword("asdfasdfasdfasf");
        appUser.setUsername("honggildong");
        Authority authority = new Authority();
        appUser.setAuthority(new HashSet<>(List.of(authority)));
        authority.setAppUser(appUser);
    }

    @Test
    void checkIfSubscriptionCanBeInserted() {
        Subscription subscription = new Subscription();
        subscription.setSubscriptionType(SubscriptionType.TRIAL);
        subscription.setAppUser(appUser);

        int id = testEntityManager.persist(subscription).getId();
        Subscription newSubscription = testEntityManager.find(Subscription.class, id);

        assertThat(newSubscription).isEqualTo(subscription);
    }

    @Test
    void checkIfSubscriptionCanBeInsertedWithSubscribedDate() {
        Subscription subscription = new Subscription();
        subscription.setSubscriptionType(SubscriptionType.TRIAL);
        subscription.setAppUser(appUser);
        Instant date = Instant.now();
        subscription.setSubscribedDate(date);

        int id = testEntityManager.persist(subscription).getId();
        Instant newDate = testEntityManager.find(Subscription.class, id).getSubscribedDate();

        assertThat(newDate).isEqualTo(date);
    }

    @Test
    void checkIfSubscriptionCanBeInsertedWithEndDate() {
        Subscription subscription = new Subscription();
        subscription.setSubscriptionType(SubscriptionType.TRIAL);
        subscription.setAppUser(appUser);
        Instant date = Instant.now();
        subscription.setEndDate(date);

        int id = testEntityManager.persist(subscription).getId();
        Instant newDate = testEntityManager.find(Subscription.class, id).getEndDate();

        assertThat(newDate).isEqualTo(date);
    }

    @Test
    void checkIfSubscriptionCanBeInsertedWithPurchaseType() {
        Subscription subscription = new Subscription();
        subscription.setSubscriptionType(SubscriptionType.TRIAL);
        subscription.setAppUser(appUser);
        String purchaseType = "paypal";
        subscription.setPurchaseType(purchaseType);

        int id = testEntityManager.persist(subscription).getId();
        String newPurchaseType = testEntityManager.find(Subscription.class, id).getPurchaseType();

        assertThat(newPurchaseType).isEqualTo(purchaseType);
    }

    @Test
    void checkIfSubscriptionCantBeInsertedWithWrongSubscriptionType() {
        Subscription subscription = new Subscription();
        subscription.setAppUser(appUser);
        subscription.setSubscriptionType(null);

        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> {
                    testEntityManager.persist(subscription);
                })
                .withMessageContaining("must not be null");
    }
}