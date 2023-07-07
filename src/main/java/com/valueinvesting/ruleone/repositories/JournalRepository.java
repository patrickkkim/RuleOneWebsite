package com.valueinvesting.ruleone.repositories;

import com.valueinvesting.ruleone.entities.Journal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface JournalRepository extends JpaRepository<Journal, Integer> {
    List<Journal> findJournalByAppUserId(int app_user_id);

    @Modifying
    @Query("UPDATE Journal j SET j.tickerSymbol = :tickerSymbol WHERE j.id = :id")
    void updateTickerSymbolById(@Param("id") int id, @Param("tickerSymbol") String tickerSymbol);

    @Modifying
    @Query("UPDATE Journal j SET j.isBought = :isBought WHERE j.id = :id")
    void updateBoughtById(@Param("id") int id, @Param("isBought") boolean isBought);

    @Modifying
    @Query("UPDATE Journal j SET j.stockPrice = :price WHERE j.id = :id")
    void updateStockPriceById(@Param("id") int id, @Param("price") float price);

    @Modifying
    @Query("UPDATE Journal j SET j.stockAmount = :amount WHERE j.id = :id")
    void updateStockAmountById(@Param("id") int id, @Param("amount") int price);

    @Modifying
    @Query("UPDATE Journal j SET j.jsonBigFiveNumber = :json WHERE j.id = :id")
    void updateJsonBigFiveNumberById(@Param("id") int id, @Param("json") Map<String, Object> json);

    @Modifying
    @Query("UPDATE Journal j SET j.memo = :memo WHERE j.id = :id")
    void updateMemoById(@Param("id") int id, @Param("memo") String memo);
}
