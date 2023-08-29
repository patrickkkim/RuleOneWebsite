package com.valueinvesting.ruleone.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.valueinvesting.ruleone.entities.AppUser;
import com.valueinvesting.ruleone.entities.BigFiveNumberType;
import com.valueinvesting.ruleone.entities.Journal;
import com.valueinvesting.ruleone.services.AppUserService;
import com.valueinvesting.ruleone.services.JournalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/journals")
public class JournalController {

    private JournalService journalService;
    private AppUserService appUserService;

    @Autowired
    public JournalController(JournalService journalService, AppUserService appUserService) {
        this.journalService = journalService;
        this.appUserService = appUserService;
    }

    @PreAuthorize("hasAuthority('ROLE_ESSENTIAL')")
    @PostMapping
    public ResponseEntity<?> createJournal(@RequestBody Map<String, Object> requestMap) {
        Journal journal = new Journal();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<BigFiveNumberType, List<Double>> jsonBigFiveNumber;
        try {
            String jsonBigFiveNumberString = objectMapper.writeValueAsString(requestMap.get("jsonBigFiveNumber"));
            jsonBigFiveNumber = objectMapper.readValue(jsonBigFiveNumberString,
                    new TypeReference<>() {});
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        journal.setAppUser(appUserService.getAuthenticatedUser());
        journal.setTickerSymbol((String) requestMap.get("tickerSymbol"));
        journal.setStockDate(LocalDate.parse((String) requestMap.get("stockDate"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        journal.setJsonBigFiveNumber(jsonBigFiveNumber);
        journal.setBought((boolean) requestMap.get("isBought"));
        journal.setStockAmount((int) requestMap.get("stockAmount"));
        journal.setStockPrice((double) requestMap.get("stockPrice"));
        journal.setMemo((String) requestMap.get("memo"));

        Journal newJournal = journalService.createJournal(journal);
        newJournal.getAppUser().setEncryptedPassword("null");
        return ResponseEntity.ok(true);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getPaginatedJournals(
            @RequestParam int page, @RequestParam int size) {
        AppUser appUser = appUserService.getAuthenticatedUser();
        Page<Journal> journalPage = journalService.getPaginatedJournals(appUser, page, size);
        for (Journal journal : journalPage) {
            journal.getAppUser().setEncryptedPassword("null");
        }
        return ResponseEntity.ok(journalPage);
    }

    @GetMapping("/stocks/{ticker}")
    public ResponseEntity<?> getPaginatedJournalsForSingleTicker(
            @PathVariable String ticker, @RequestParam int page, @RequestParam int size) {
        AppUser appUser = appUserService.getAuthenticatedUser();
        Page<Journal> journalPage = journalService.getPaginatedJournalsForSingleStockTicker(
                appUser, ticker, page, size
        );
        for (Journal journal : journalPage) {
            journal.getAppUser().setEncryptedPassword("null");
        }
        return ResponseEntity.ok(journalPage);
    }

    @GetMapping("/percentages")
    public ResponseEntity<?> getStockPercentage() {
        AppUser appUser = appUserService.getAuthenticatedUser();
        Map<String, Double> percentageMap = journalService.getTotalStockPercentage(appUser);
        return ResponseEntity.ok(percentageMap);
    }

    @GetMapping("/gains")
    public ResponseEntity<?> getTotalGainForEachStock() {
        AppUser appUser = appUserService.getAuthenticatedUser();
        Map<String, Double> gainMap = journalService.getTotalGainForEachStock(appUser);
        return ResponseEntity.ok(gainMap);
    }

    @PutMapping("/{journalId}")
    public ResponseEntity<?> updateBigFiveNumbers(@PathVariable int journalId,
            @RequestBody Map<BigFiveNumberType, List<Double>> jsonBigFiveNumber,
            @RequestBody String memo) {
        AppUser appUser = appUserService.getAuthenticatedUser();
        Optional<Journal> optional = journalService.getJournal(journalId);
        if (optional.isPresent()) {
            Journal journal = optional.get();
            if (journal.getAppUser() == appUser) {
                if (jsonBigFiveNumber != null)
                    journalService.updateJsonBigFiveNumberByJournalId(journalId, jsonBigFiveNumber);
                if (memo != null)
                    journalService.updateMemoByJournalId(journalId, memo);
            } else throw new IllegalArgumentException("Current user is not authorized to update this journal");
        } else throw new IllegalArgumentException("Journal does not exist to update");
        return ResponseEntity.ok(true);
    }
}
