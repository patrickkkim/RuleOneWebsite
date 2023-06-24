package com.valueinvesting.ruleone.services;

import com.valueinvesting.ruleone.entities.AppUser;
import com.valueinvesting.ruleone.entities.Subscription;
import com.valueinvesting.ruleone.entities.SubscriptionType;

import java.time.Instant;

public interface SubscriptionService {
    Subscription createSubscription(Subscription subscription);

    Subscription getSubscriptionById(int id);

    Subscription getSubscriptionByAppUser(AppUser appUser);

    void updateSubscriptionTypeByAppUser(AppUser appUser, SubscriptionType subscriptionType);

    void updateSubscribedDateByAppUser(AppUser appUser, Instant date);

    void updateEndDateByAppUser(AppUser appUser, Instant date);

    void updatePurchaseTypeByAppUser(AppUser appUser, String purchaseType);
}
