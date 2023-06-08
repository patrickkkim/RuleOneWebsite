package com.valueinvesting.ruleone.entities;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.Instant;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class SubscriptionTest {
    @Autowired private TestEntityManager testEntityManager;

    @Test
    void checkIfSubscriptionCanBeInserted() {
        Subscription subscription = new Subscription();

        int id = testEntityManager.persist(subscription).getId();
        Subscription newSubscription = testEntityManager.find(Subscription.class, id);

        assertThat(newSubscription).isEqualTo(subscription);
    }

    @Test
    void checkIfSubscriptionCanBeInsertedWithSubscribedDate() {
        Subscription subscription = new Subscription();
        Instant date = Instant.now();
        subscription.setSubscribedDate(date);

        int id = testEntityManager.persist(subscription).getId();
        Instant newDate = testEntityManager.find(Subscription.class, id).getSubscribedDate();

        assertThat(newDate).isEqualTo(date);
    }

    @Test
    void checkIfSubscriptionCanBeInsertedWithEndDate() {
        Subscription subscription = new Subscription();
        Instant date = Instant.now();
        subscription.setEndDate(date);

        int id = testEntityManager.persist(subscription).getId();
        Instant newDate = testEntityManager.find(Subscription.class, id).getEndDate();

        assertThat(newDate).isEqualTo(date);
    }

    @Test
    void checkIfSubscriptionCanBeInsertedWithPurchaseType() {
        Subscription subscription = new Subscription();
        String purchaseType = "paypal";
        subscription.setPurchaseType(purchaseType);

        int id = testEntityManager.persist(subscription).getId();
        String newPurchaseType = testEntityManager.find(Subscription.class, id).getPurchaseType();

        assertThat(newPurchaseType).isEqualTo(purchaseType);
    }
}