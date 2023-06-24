package com.valueinvesting.ruleone.services;

import com.valueinvesting.ruleone.entities.AppUser;
import com.valueinvesting.ruleone.entities.Subscription;
import com.valueinvesting.ruleone.entities.SubscriptionType;
import com.valueinvesting.ruleone.exceptions.SubscriptionAlreadyExistException;
import com.valueinvesting.ruleone.exceptions.SubscriptionNotFoundException;
import com.valueinvesting.ruleone.repositories.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    @Autowired
    public SubscriptionServiceImpl(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public Subscription createSubscription(Subscription subscription) {
        Optional<Subscription> optionalId = subscriptionRepository.findById(subscription.getId());
        Optional<Subscription> optionalAppUser = subscriptionRepository.findByAppUserId(subscription.getAppUser().getId());

        if (optionalId.isPresent() || optionalAppUser.isPresent()) {
            throw new SubscriptionAlreadyExistException(
                    "Subscription already exists");
        }

        return subscriptionRepository.save(subscription);
    }

    @Override
    public Subscription getSubscriptionById(int id) {
        Optional<Subscription> optional = subscriptionRepository.findById(id);

        if (optional.isEmpty()) {
            throw new SubscriptionNotFoundException(
                    "Subscription not found with ID: " + id);
        }

        return optional.get();
    }

    @Override
    public Subscription getSubscriptionByAppUser(AppUser appUser) {
        Optional<Subscription> optional = subscriptionRepository.findByAppUserId(appUser.getId());

        if (optional.isEmpty()) {
            throw new SubscriptionNotFoundException(
                    "Subscription not found with AppUser: " + appUser.getUsername());
        }

        return optional.get();
    }

    @Override
    public void updateSubscriptionTypeByAppUser(AppUser appUser, SubscriptionType subscriptionType) {
        Optional<Subscription> optional = subscriptionRepository.findByAppUserId(appUser.getId());

        if (optional.isEmpty()) {
            throw new SubscriptionNotFoundException(
                    "Subscription not found with AppUser: " + appUser.getUsername());
        }

        subscriptionRepository.updateSubscriptionTypeById(optional.get().getId(), subscriptionType);
    }

    @Override
    public void updateSubscribedDateByAppUser(AppUser appUser, Instant date) {
        Optional<Subscription> optional = subscriptionRepository.findByAppUserId(appUser.getId());

        if (optional.isEmpty()) {
            throw new SubscriptionNotFoundException(
                    "Subscription not found with AppUser: " + appUser.getUsername());
        }

        subscriptionRepository.updateSubscribedDateById(optional.get().getId(), date);
    }

    @Override
    public void updateEndDateByAppUser(AppUser appUser, Instant date) {
        Optional<Subscription> optional = subscriptionRepository.findByAppUserId(appUser.getId());

        if (optional.isEmpty()) {
            throw new SubscriptionNotFoundException(
                    "Subscription not found with AppUser: " + appUser.getUsername());
        }

        subscriptionRepository.updateEndDateById(optional.get().getId(), date);
    }

    @Override
    public void updatePurchaseTypeByAppUser(AppUser appUser, String purchaseType) {
        Optional<Subscription> optional = subscriptionRepository.findByAppUserId(appUser.getId());

        if (optional.isEmpty()) {
            throw new SubscriptionNotFoundException(
                    "Subscription not found with AppUser: " + appUser.getUsername());
        }

        subscriptionRepository.updatePurchaseTypeById(optional.get().getId(), purchaseType);
    }
}
