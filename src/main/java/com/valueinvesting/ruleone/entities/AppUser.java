package com.valueinvesting.ruleone.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

@Entity
@Table(name="app_user")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @NotNull
    @Size(min=5)
    @Column(name="username", columnDefinition =
            "VARCHAR(50) NOT NULL UNIQUE CHECK (LENGTH() >= 5)")
    private String username;

    @NotBlank
    @Column(name="encrypted_password",
            columnDefinition = "VARCHAR(100) NOT NULL CHECK (LENGTH() >= 10)")
    private String encryptedPassword;

    @NotNull
    @Size(min=4)
    @Email
    @Column(name="email", columnDefinition =
            "VARCHAR(255) NOT NULL CHECK (LENGTH() >= 4)")
    private String email;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="created_date", columnDefinition =
            "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Instant createdDate = Instant.now();

    @Column(name="is_active", columnDefinition = "BOOLEAN NOT NULL DEFAULT 1")
    private boolean isActive;

    public AppUser() {}
    public AppUser(@NotNull String username, String password, @NotNull String email) {
        this.username = username;
        this.encryptedPassword = password;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "AppUser{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", encryptedPassword='" + encryptedPassword + '\'' +
                ", email='" + email + '\'' +
                ", createdDate=" + createdDate +
                ", isActive=" + isActive +
                '}';
    }
}
