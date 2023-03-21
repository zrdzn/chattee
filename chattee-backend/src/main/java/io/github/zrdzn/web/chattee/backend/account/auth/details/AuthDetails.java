package io.github.zrdzn.web.chattee.backend.account.auth.details;

import java.time.Instant;

public class AuthDetails {

    private String token;
    private long accountId;
    private Instant expireAt;
    private Instant createdAt;
    private String ipAddress;

    public AuthDetails() {
    }

    public AuthDetails(String token, long accountId, Instant expireAt, String ipAddress) {
        this(token, accountId, expireAt, null, ipAddress);
    }

    public AuthDetails(String token, long accountId, Instant expireAt, Instant createdAt, String ipAddress) {
        this.token = token;
        this.accountId = accountId;
        this.expireAt = expireAt;
        this.createdAt = createdAt;
        this.ipAddress = ipAddress;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getAccountId() {
        return this.accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public Instant getExpireAt() {
        return this.expireAt;
    }

    public void setExpireAt(Instant expireAt) {
        this.expireAt = expireAt;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getIpAddress() {
        return this.ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

}
