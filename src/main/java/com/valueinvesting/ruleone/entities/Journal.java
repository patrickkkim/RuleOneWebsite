package com.valueinvesting.ruleone.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

@Entity
@Table(name="journal")
public class Journal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @NotBlank
    @Column(name="ticker_symbol", columnDefinition = "VARCHAR(20) NOT NULL")
    private String tickerSymbol;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="last_edit_date", columnDefinition =
            "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Instant lastEditDate = Instant.now();

    @NotNull
    @Column(name="is_bought", columnDefinition = "BOOLEAN NOT NULL")
    private boolean isBought;

    @NotNull
    @Column(name="stock_price", columnDefinition = "FLOAT NOT NULL")
    private float stockPrice;

    @NotNull
    @Column(name="stock_amount", columnDefinition = "INT NOT NULL")
    private int stockAmount;

    @NotBlank
    @Column(name="json_big_five_number", columnDefinition = "JSON NOT NULL")
    private String jsonBigFiveNumber;

    @NotNull
    @Column(name="memo", columnDefinition =
            "VARCHAR(2000) NOT NULL DEFAULT ''")
    private String memo;

    @NotNull
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="app_user_id", columnDefinition =
            "INT NOT NULL")
    private AppUser appUser;

    public Journal() {}

    public Journal(String tickerSymbol, boolean isBought, float stockPrice, int stockAmount, String jsonBigFiveNumber, @NotNull String memo, @NotNull AppUser appUser) {
        this.tickerSymbol = tickerSymbol;
        this.isBought = isBought;
        this.stockPrice = stockPrice;
        this.stockAmount = stockAmount;
        this.jsonBigFiveNumber = jsonBigFiveNumber;
        this.memo = memo;
        this.appUser = appUser;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTickerSymbol() {
        return tickerSymbol;
    }

    public void setTickerSymbol(String tickerSymbol) {
        this.tickerSymbol = tickerSymbol;
    }

    public Instant getLastEditDate() {
        return lastEditDate;
    }

    public void setLastEditDate(Instant lastEditDate) {
        this.lastEditDate = lastEditDate;
    }

    public boolean isBought() {
        return isBought;
    }

    public void setBought(boolean bought) {
        isBought = bought;
    }

    public float getStockPrice() {
        return stockPrice;
    }

    public void setStockPrice(float stockPrice) {
        this.stockPrice = stockPrice;
    }

    public int getStockAmount() {
        return stockAmount;
    }

    public void setStockAmount(int stockAmount) {
        this.stockAmount = stockAmount;
    }

    public String getJsonBigFiveNumber() {
        return jsonBigFiveNumber;
    }

    public void setJsonBigFiveNumber(String jsonBigFiveNumber) {
        this.jsonBigFiveNumber = jsonBigFiveNumber;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public AppUser getAppUser() {
        return appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    @Override
    public String toString() {
        return "Journal{" +
                "id=" + id +
                ", tickerSymbol='" + tickerSymbol + '\'' +
                ", lastEditDate=" + lastEditDate +
                ", isBought=" + isBought +
                ", stockPrice=" + stockPrice +
                ", stockAmount=" + stockAmount +
                ", jsonBigFiveNumber='" + jsonBigFiveNumber + '\'' +
                ", memo='" + memo + '\'' +
                ", appUser=" + appUser +
                '}';
    }
}
