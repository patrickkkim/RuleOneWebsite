package com.valueinvesting.ruleone.controllers;

import com.valueinvesting.ruleone.entities.AppUser;
import com.valueinvesting.ruleone.exceptions.ErrorResponse;
import com.valueinvesting.ruleone.services.AppUserService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/user")
public class AppUserController {

    private AppUserService appUserService;

    @Autowired
    public AppUserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    private void validateAppUser(AppUser appUser) {
        if (appUser.getUsername().length() < 6)
            throw new RuntimeException("Username must be longer than 5");
        if (!Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$")
                .matcher(appUser.getEncryptedPassword()).find()) {
            throw new RuntimeException("Password must be longer or equal to 8 and must include digits, alphabets, special character");
        }
    }

    @PostMapping
    public ResponseEntity<?> createAppUser(@RequestBody @NotNull AppUser appUser) {
        validateAppUser(appUser);
        AppUser newAppUser = appUserService.createAppUser(appUser);
        newAppUser.setEncryptedPassword(null);
        return ResponseEntity.ok(newAppUser);
    }

    @PutMapping()
    public ResponseEntity<?> updateAppUser(@RequestBody @NotNull AppUser appUser) {
        validateAppUser(appUser);
        if (!appUser.getEncryptedPassword().contains("{bcrypt}")) {
            appUser.setEncryptedPassword(null);
        }
        appUserService.updateUser(appUser.getId(), appUser.getEncryptedPassword(), appUser.getEmail());
        return ResponseEntity.ok(true);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @NotNull Map<String, String> loginForm) {
        String jwt = appUserService.login(
                loginForm.get("username"), loginForm.get("password"));
        return ResponseEntity.ok(jwt);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteAppUser() {
        appUserService.deleteAuthenticatedUser();
        return ResponseEntity.ok(true);
    }
}
