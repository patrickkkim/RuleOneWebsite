package com.valueinvesting.ruleone.entities;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name="subscription")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="subscribed_date", columnDefinition = "TIMESTAMP NULL")
    private Instant subscribedDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="end_date", columnDefinition = "TIMESTAMP NULL")
    private Instant endDate;

    @Column(name="purchase_type", columnDefinition = "VARCHAR(50) NULL")
    private String purchaseType;

    public Subscription() {}

    public Subscription(Instant subscribedDate, Instant endDate, String purchaseType) {
        this.subscribedDate = subscribedDate;
        this.endDate = endDate;
        this.purchaseType = purchaseType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "Subscription{" +
                "id=" + id +
                ", subscribedDate=" + subscribedDate +
                ", endDate=" + endDate +
                ", purchaseType='" + purchaseType + '\'' +
                '}';
    }
}
