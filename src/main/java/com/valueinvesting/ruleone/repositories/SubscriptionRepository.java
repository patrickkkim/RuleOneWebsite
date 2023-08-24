package com.valueinvesting.ruleone.repositories;

import com.valueinvesting.ruleone.entities.Subscription;
import com.valueinvesting.ruleone.entities.SubscriptionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {

    Optional<Subscription> findByAppUserId(int app_user_id);

    @Modifying
    @Query("UPDATE Subscription s SET s.subscriptionType = :type WHERE s.id = :id")
    void updateSubscriptionTypeById(@Param("id") int id,
                                  @Param("type") SubscriptionType type);

    @Modifying
    @Query("UPDATE Subscription s SET s.subscribedDate = :subscribedDate WHERE s.id = :id")
    void updateSubscribedDateById(@Param("id") int id,
                                  @Param("subscribedDate")Instant subscribedDate);

    @Modifying
    @Query("UPDATE Subscription s SET s.endDate = :endDate WHERE s.id = :id")
    void updateEndDateById(@Param("id") int id,
                                  @Param("endDate")Instant endDate);

    @Modifying
    @Query("UPDATE Subscription s SET s.purchaseType = :purchaseType WHERE s.id = :id")
    void updatePurchaseTypeById(@Param("id") int id,
                           @Param("purchaseType")String purchaseType);
}
