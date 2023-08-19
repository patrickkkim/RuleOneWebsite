package com.valueinvesting.ruleone.repositories;

import com.valueinvesting.ruleone.entities.AppUser;
import com.valueinvesting.ruleone.entities.Authority;
import com.valueinvesting.ruleone.entities.Subscription;
import com.valueinvesting.ruleone.entities.SubscriptionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class SubscriptionRepositoryTest {

    @Autowired private TestEntityManager testEntityManager;
    @Autowired private SubscriptionRepository underTest;
    AppUser appUser;

    @BeforeEach
    void setUp() {
        appUser = new AppUser();
        appUser.setUsername("honggilddong");
        appUser.setEmail("a@a.com");
        appUser.setEncryptedPassword("asdfasdfasdfasdf");
        Authority authority = new Authority();
        authority.setAppUser(appUser);
        appUser.setAuthority(new HashSet<>(List.of(authority)));
    }

    @Test
    void checkIfFoundByAppUserId() {
        Subscription subscription = new Subscription();
        subscription.setSubscriptionType(SubscriptionType.TRIAL);
        subscription.setAppUser(appUser);

        underTest.save(subscription);
        Optional<Subscription> optionalSubscription = underTest.findByAppUserId(appUser.getId());
        assertThat(optionalSubscription.isPresent()).isTrue();
        Subscription found = optionalSubscription.get();
        assertThat(found).isEqualTo(subscription);
    }

    @Test
    void checkIfNotFoundByAppUserId() {
        Subscription subscription = new Subscription();
        subscription.setSubscriptionType(SubscriptionType.TRIAL);
        subscription.setAppUser(appUser);

        underTest.save(subscription);
        Optional<Subscription> optionalSubscription = underTest.findByAppUserId(appUser.getId() + 1);
        assertThat(optionalSubscription.isPresent()).isFalse();
    }

    @Test
    void updateSubscriptionTypeById() {
        Subscription subscription = new Subscription();
        subscription.setSubscriptionType(SubscriptionType.TRIAL);
        subscription.setAppUser(appUser);

        int id = underTest.save(subscription).getId();
        underTest.updateSubscriptionTypeById(id, SubscriptionType.ESSENTIAL);
        testEntityManager.refresh(subscription);

        Subscription found = underTest.findById(id).get();
        assertThat(found.getSubscriptionType()).isEqualTo(SubscriptionType.ESSENTIAL);
    }

    @Test
    void updateSubscribedDateById() {
        Subscription subscription = new Subscription();
        subscription.setSubscriptionType(SubscriptionType.TRIAL);
        subscription.setAppUser(appUser);
        subscription.setSubscribedDate(Instant.now());
        Instant newSubscribedDate = Instant.ofEpochSecond(Instant.now().getEpochSecond() + 100000);

        int id = underTest.save(subscription).getId();
        underTest.updateSubscribedDateById(id, newSubscribedDate);
        testEntityManager.refresh(subscription);

        Subscription found = underTest.findById(id).get();
        assertThat(found.getSubscribedDate()).isEqualTo(newSubscribedDate);
    }

    @Test
    void updateEndDateById() {
        Subscription subscription = new Subscription();
        subscription.setSubscriptionType(SubscriptionType.TRIAL);
        subscription.setAppUser(appUser);
        subscription.setEndDate(Instant.now());
        Instant newEndDate = Instant.ofEpochSecond(Instant.now().getEpochSecond() + 100000);

        int id = underTest.save(subscription).getId();
        underTest.updateEndDateById(id, newEndDate);
        testEntityManager.refresh(subscription);

        Subscription found = underTest.findById(id).get();
        assertThat(found.getEndDate()).isEqualTo(newEndDate);
    }

    @Test
    void updatePurchaseTypeById() {
        Subscription subscription = new Subscription();
        subscription.setSubscriptionType(SubscriptionType.TRIAL);
        subscription.setAppUser(appUser);
        String newPurchaseType = "paypal";

        int id = underTest.save(subscription).getId();
        underTest.updatePurchaseTypeById(id, newPurchaseType);
        testEntityManager.refresh(subscription);

        Subscription found = underTest.findById(id).get();
        assertThat(found.getPurchaseType()).isEqualTo(newPurchaseType);
    }
}