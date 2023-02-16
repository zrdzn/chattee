package io.github.zrdzn.web.chattee.backend.account;

import java.time.Instant;

public class AccountDetailsDto {

    private long id;
    private String email;
    private String password;
    private String username;
    private Instant createdAt;
    private Instant updatedAt;

    public AccountDetailsDto() {
    }

    public AccountDetailsDto(Account account) {
        this.id = account.getId();
        this.email = account.getEmail();
        this.password = account.getPassword();
        this.username = account.getUsername();
        this.createdAt = account.getCreatedAt();
        this.updatedAt = account.getUpdatedAt();
    }

    public AccountDetailsDto(String email, String password, String username, Instant createdAt, Instant updatedAt) {
        this(0L, email, password, username, createdAt, updatedAt);
    }

    public AccountDetailsDto(long id, String email, String password, String username, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.username = username;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

}
