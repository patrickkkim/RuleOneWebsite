package com.valueinvesting.ruleone.services;

import com.valueinvesting.ruleone.entities.AppUser;
import com.valueinvesting.ruleone.entities.Subscription;
import com.valueinvesting.ruleone.entities.SubscriptionType;
import com.valueinvesting.ruleone.exceptions.SubscriptionAlreadyExistException;
import com.valueinvesting.ruleone.exceptions.SubscriptionNotFoundException;
import com.valueinvesting.ruleone.repositories.SubscriptionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class SubscriptionServiceIntegrationTest {

    @Autowired private SubscriptionService underTest;
    @Autowired private SubscriptionRepository subscriptionRepository;
    @PersistenceContext private EntityManager entityManager;
    private Subscription subscription;
    private AppUser appUser;

    @BeforeEach
    void setUp() {
        appUser = new AppUser();
        appUser.setUsername("honggildong");
        appUser.setEmail("a@a.com");
        appUser.setEncryptedPassword("asdfasdfasdfasdf");
        subscription = new Subscription();
        subscription.setAppUser(appUser);
        subscription.setSubscribedDate(Instant.now());
        subscription.setEndDate(Instant.now());
        subscription.setPurchaseType("paypal");
    }

    @Test
    void checkIfCreatesSubscription() {
        underTest.createSubscription(subscription);

        Optional<Subscription> optional = subscriptionRepository.findById(subscription.getId());

        assertThat(optional.isPresent()).isTrue();
        assertThat(optional.get()).isEqualTo(subscription);
    }

    @Test
    void checkIfCreateSubscriptionThrowsExceptionWhenSameIdExists() {
        underTest.createSubscription(subscription);

        assertThatExceptionOfType(SubscriptionAlreadyExistException.class)
                .isThrownBy(() -> {
                    underTest.createSubscription(subscription);
                }).withMessageContaining("Subscription already exists");
    }

    @Test
    void checkIfCreateSubscriptionThrowsExceptionWhenSameAppUserExists() {
        Subscription subscription2 = new Subscription();
        subscription2.setAppUser(appUser);
        subscription2.setPurchaseType(subscription.getPurchaseType());
        subscription2.setSubscribedDate(subscription.getSubscribedDate());
        subscription2.setEndDate(subscription.getEndDate());
        underTest.createSubscription(subscription);

        assertThatExceptionOfType(SubscriptionAlreadyExistException.class)
                .isThrownBy(() -> {
                    underTest.createSubscription(subscription2);
                }).withMessageContaining("Subscription already exists");
    }

    @Test
    void checkIfGetsSubscriptionById() {
        subscriptionRepository.save(subscription);

        Subscription newSubscription = underTest.getSubscriptionById(subscription.getId());

        assertThat(newSubscription).isEqualTo(subscription);
    }

    @Test
    void checkIfGetSubscriptionByIdThrowsExceptionWhenNotFound() {
        subscriptionRepository.save(subscription);

        assertThatExceptionOfType(SubscriptionNotFoundException.class)
                .isThrownBy(() -> {
                    underTest.getSubscriptionById(subscription.getId()+1);
                }).withMessageContaining("Subscription not found with ID");
    }

    @Test
    void checkIfGetsSubscriptionByAppUser() {
        subscriptionRepository.save(subscription);

        appUser.setUsername("another");
        Subscription newSubscription = underTest.getSubscriptionByAppUser(appUser);

        assertThat(newSubscription).isEqualTo(subscription);
    }

    @Test
    void checkIfGetSubscriptionByAppUserThrowsExceptionWhenNotFound() {
        AppUser appUser2 = new AppUser();
        appUser2.setUsername("another");
        appUser2.setEmail("b@b.com");
        appUser2.setEncryptedPassword("asdfasdfasdf");
        subscriptionRepository.save(subscription);

        assertThatExceptionOfType(SubscriptionNotFoundException.class)
                .isThrownBy(() -> {
                    underTest.getSubscriptionByAppUser(appUser2);
                }).withMessageContaining("Subscription not found with AppUser:");
    }

    @Test
    void checkIfUpdatesSubscriptionTypeByAppUser() {
        subscriptionRepository.save(subscription);

        underTest.updateSubscriptionTypeByAppUser(appUser, SubscriptionType.PREMIUM);
        entityManager.refresh(subscription);

        assertThat(subscriptionRepository.findById(subscription.getId()).get().getSubscriptionType())
                .isEqualTo(SubscriptionType.PREMIUM);
    }

    @Test
    void checkIfUpdateSubscriptionTypeByAppUserThrowsExceptionWhenNotFound() {
        AppUser appUser2 = new AppUser();
        appUser2.setUsername("another");
        appUser2.setEmail("b@b.com");
        appUser2.setEncryptedPassword("asdfasdfasdf");
        subscriptionRepository.save(subscription);

        assertThatExceptionOfType(SubscriptionNotFoundException.class)
                .isThrownBy(() -> {
                    underTest.updateSubscriptionTypeByAppUser(appUser2, SubscriptionType.PREMIUM);
                }).withMessageContaining("Subscription not found with AppUser:");
    }

    @Test
    void checkIfUpdatesSubscribedDateByAppUser() {
        Instant newInstant = Instant.ofEpochSecond(Instant.now().getEpochSecond() + 100000);
        subscriptionRepository.save(subscription);

        underTest.updateSubscribedDateByAppUser(appUser, newInstant);
        entityManager.refresh(subscription);

        assertThat(subscriptionRepository.findById(subscription.getId()).get().getSubscribedDate())
                .isEqualTo(newInstant);
    }

    @Test
    void checkIfUpdateSubscribedDateByAppUserThrowsExceptionWhenNotFound() {
        AppUser appUser2 = new AppUser();
        appUser2.setUsername("another");
        appUser2.setEmail("b@b.com");
        appUser2.setEncryptedPassword("asdfasdfasdf");
        Instant newInstant = Instant.ofEpochSecond(Instant.now().getEpochSecond() + 100000);
        subscriptionRepository.save(subscription);

        assertThatExceptionOfType(SubscriptionNotFoundException.class)
                .isThrownBy(() -> {
                    underTest.updateSubscribedDateByAppUser(appUser2, newInstant);
                }).withMessageContaining("Subscription not found with AppUser:");
    }

    @Test
    void checkIfUpdatesEndDateByAppUser() {
        Instant newInstant = Instant.ofEpochSecond(Instant.now().getEpochSecond() + 100000);
        subscriptionRepository.save(subscription);

        underTest.updateEndDateByAppUser(appUser, newInstant);
        entityManager.refresh(subscription);

        assertThat(subscriptionRepository.findById(subscription.getId()).get().getEndDate())
                .isEqualTo(newInstant);
    }

    @Test
    void checkIfUpdateEndDateByAppUserThrowsExceptionWhenNotFound() {
        AppUser appUser2 = new AppUser();
        appUser2.setUsername("another");
        appUser2.setEmail("b@b.com");
        appUser2.setEncryptedPassword("asdfasdfasdf");
        Instant newInstant = Instant.ofEpochSecond(Instant.now().getEpochSecond() + 100000);
        subscriptionRepository.save(subscription);

        assertThatExceptionOfType(SubscriptionNotFoundException.class)
                .isThrownBy(() -> {
                    underTest.updateEndDateByAppUser(appUser2, newInstant);
                }).withMessageContaining("Subscription not found with AppUser:");
    }

    @Test
    void checkIfUpdatesPurchaseTypeByAppUser() {
        subscriptionRepository.save(subscription);

        underTest.updatePurchaseTypeByAppUser(appUser, "other");
        entityManager.refresh(subscription);

        assertThat(subscriptionRepository.findById(subscription.getId()).get().getPurchaseType())
                .isEqualTo("other");
    }

    @Test
    void checkIfUpdatePurchaseTypeByAppUserThrowsExceptionWhenNotFound() {
        AppUser appUser2 = new AppUser();
        appUser2.setUsername("another");
        appUser2.setEmail("b@b.com");
        appUser2.setEncryptedPassword("asdfasdfasdf");
        subscriptionRepository.save(subscription);

        assertThatExceptionOfType(SubscriptionNotFoundException.class)
                .isThrownBy(() -> {
                    underTest.updatePurchaseTypeByAppUser(appUser2, "other");
                }).withMessageContaining("Subscription not found with AppUser:");
    }
}