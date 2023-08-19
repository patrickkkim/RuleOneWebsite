package com.valueinvesting.ruleone.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name="authority")
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name="authority", columnDefinition =
            "ENUM('TRIAL', 'ESSENTIAL', 'PREMIUM', 'ADMIN') NOT NULL DEFAULT 'TRIAL'")
    private AuthorityType authority = AuthorityType.TRIAL;

    @NotNull
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="app_user_id", columnDefinition = "INT NOT NULL")
    private AppUser appUser;

    public Authority() {}
    public Authority(@NotNull AuthorityType authority, @NotNull AppUser appUser) {
        this.authority = authority;
        this.appUser = appUser;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AuthorityType getAuthority() {
        return authority;
    }

    public void setAuthority(AuthorityType authority) {
        this.authority = authority;
    }

    public AppUser getAppUser() {
        return appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    @Override
    public String toString() {
        return "Authority{" +
                "id=" + id +
                ", authority='" + authority + '\'' +
                '}';
    }
}
