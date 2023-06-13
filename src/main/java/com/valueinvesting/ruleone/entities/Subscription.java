package com.valueinvesting.ruleone.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

@Entity
@Table(name="subscription")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @NotNull
    @Column(name="subscription_type",
            columnDefinition = "ENUM('trial', 'essential', 'premium') NOT NULL DEFAULT 'trial'")
    private String subscriptionType;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="subscribed_date", columnDefinition = "TIMESTAMP NULL")
    private Instant subscribedDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="end_date", columnDefinition = "TIMESTAMP NULL")
    private Instant endDate;

    @Column(name="purchase_type", columnDefinition = "VARCHAR(50) NULL")
    private String purchaseType;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="app_user_id", columnDefinition = "INT NOT NULL")
    private AppUser appUser;

    public Subscription() {}

    public Subscription(@NotNull String subscriptionType, Instant subscribedDate, Instant endDate,
                        String purchaseType, @NotNull AppUser appUser) {
        this.subscriptionType = subscriptionType;
        this.subscribedDate = subscribedDate;
        this.endDate = endDate;
        this.purchaseType = purchaseType;
        this.appUser = appUser;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(@NotNull String subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public Instant getSubscribedDate() {
        return subscribedDate;
    }

    public void setSubscribedDate(Instant subscribedDate) {
        this.subscribedDate = subscribedDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public String getPurchaseType() {
        return purchaseType;
    }

    public void setPurchaseType(String purchaseType) {
        this.purchaseType = purchaseType;
    }

    public AppUser getAppUser() {
        return appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    @Override
    public String toString() {
        return "Subscription{" +
                "id=" + id +
                ", subscriptionType='" + subscriptionType + '\'' +
                ", subscribedDate=" + subscribedDate +
                ", endDate=" + endDate +
                ", purchaseType='" + purchaseType + '\'' +
                ", appUser=" + appUser +
                '}';
    }
}
