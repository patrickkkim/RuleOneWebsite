package com.valueinvesting.ruleone.entities;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.Instant;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;

@DataJpaTest
@ExtendWith(MockitoExtension.class)
class SubscriptionTest {

    @Autowired private TestEntityManager testEntityManager;

    @Test
    void checkIfSubscriptionCanBeInserted() {
        AppUser appUser = new AppUser();
        appUser.setEmail("a@a.com");
        appUser.setEncryptedPassword("asdfasdfasdfasf");
        appUser.setUsername("honggildong");
        Subscription subscription = new Subscription();
        subscription.setAppUser(appUser);

        int id = testEntityManager.persist(subscription).getId();
        Subscription newSubscription = testEntityManager.find(Subscription.class, id);

        assertThat(newSubscription).isEqualTo(subscription);
    }

    @Test
    void checkIfSubscriptionCanBeInsertedWithSubscribedDate() {
        AppUser appUser = new AppUser();
        appUser.setEmail("a@a.com");
        appUser.setEncryptedPassword("asdfasdfasdfasf");
        appUser.setUsername("honggildong");
        Subscription subscription = new Subscription();
        subscription.setAppUser(appUser);
        Instant date = Instant.now();
        subscription.setSubscribedDate(date);

        int id = testEntityManager.persist(subscription).getId();
        Instant newDate = testEntityManager.find(Subscription.class, id).getSubscribedDate();

        assertThat(newDate).isEqualTo(date);
    }

    @Test
    void checkIfSubscriptionCanBeInsertedWithEndDate() {
        AppUser appUser = new AppUser();
        appUser.setEmail("a@a.com");
        appUser.setEncryptedPassword("asdfasdfasdfasf");
        appUser.setUsername("honggildong");
        Subscription subscription = new Subscription();
        subscription.setAppUser(appUser);
        Instant date = Instant.now();
        subscription.setEndDate(date);

        int id = testEntityManager.persist(subscription).getId();
        Instant newDate = testEntityManager.find(Subscription.class, id).getEndDate();

        assertThat(newDate).isEqualTo(date);
    }

    @Test
    void checkIfSubscriptionCanBeInsertedWithPurchaseType() {
        AppUser appUser = new AppUser();
        appUser.setEmail("a@a.com");
        appUser.setEncryptedPassword("asdfasdfasdfasf");
        appUser.setUsername("honggildong");
        Subscription subscription = new Subscription();
        subscription.setAppUser(appUser);
        String purchaseType = "paypal";
        subscription.setPurchaseType(purchaseType);

        int id = testEntityManager.persist(subscription).getId();
        String newPurchaseType = testEntityManager.find(Subscription.class, id).getPurchaseType();

        assertThat(newPurchaseType).isEqualTo(purchaseType);
    }
}