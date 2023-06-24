package com.valueinvesting.ruleone.services;

import com.valueinvesting.ruleone.entities.AppUser;
import com.valueinvesting.ruleone.entities.Authority;

public interface AuthorityService {
    Authority createAuthority(Authority authority);

    Authority getAuthorityById(int id);

    Authority getAuthorityByAppUser(AppUser appUser);

    void updateAuthorityByAppUser(AppUser appUser, String authority);
}
