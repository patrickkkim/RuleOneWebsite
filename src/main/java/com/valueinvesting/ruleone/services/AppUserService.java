package com.valueinvesting.ruleone.services;

import com.valueinvesting.ruleone.entities.AppUser;
import com.valueinvesting.ruleone.entities.Authority;

import java.util.Set;

public interface AppUserService {
    AppUser createAppUser(AppUser appUser);

    AppUser getAppUserById(int id);

    AppUser getAppUserByUsername(String username);

    void updateUser(int id, String password, String email);

    void updateUserAuthority(int id, Set<Authority> authorities);

    void activateUser(int id);

    void deactivateUser(int id);

    void deleteUser(int id);

    void deleteAuthenticatedUser();

    AppUser getAuthenticatedUser();

    String login(String username, String password);
}
