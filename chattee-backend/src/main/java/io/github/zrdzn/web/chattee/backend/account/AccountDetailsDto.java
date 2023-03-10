package io.github.zrdzn.web.chattee.backend.account;

import java.time.Instant;

public class AccountDetailsDto {

    private long id;
    private String email;
    private String username;
    private Instant createdAt;
    private Instant updatedAt;

    public AccountDetailsDto() {
    }

    public AccountDetailsDto(Account account) {
        this(account.getEmail(), account.getUsername(), account.getCreatedAt(), account.getUpdatedAt());
    }

    public AccountDetailsDto(String email, String username, Instant createdAt, Instant updatedAt) {
        this(0L, email, username, createdAt, updatedAt);
    }

    public AccountDetailsDto(long id, String email, String username, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.email = email;
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
