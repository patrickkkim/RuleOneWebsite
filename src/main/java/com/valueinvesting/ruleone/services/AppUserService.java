package com.valueinvesting.ruleone.services;

import com.valueinvesting.ruleone.entities.AppUser;

public interface AppUserService {
    AppUser createAppUser(AppUser appUser);

    AppUser getAppUserById(int id);

    AppUser getAppUserByUsername(String username);

    void updateUser(int id, String password, String email);

    void activateUser(int id);

    void deactivateUser(int id);

    void deleteUser(int id);

    String login(String username, String password);
}
