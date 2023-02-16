package io.github.zrdzn.web.chattee.backend.account.session;

import java.time.Instant;

public class Session {

    private String token;
    private long accountId;
    private Instant expireAt;
    private Instant createdAt;
    private String ipAddress;

    public Session() {
    }

    public Session(long accountId, Instant expireAt, String ipAddress) {
        this(accountId, expireAt, null, ipAddress);
    }

    public Session(long accountId, Instant expireAt, Instant createdAt, String ipAddress) {
        this(null, accountId, expireAt, createdAt, ipAddress);
    }

    public Session(String token, long accountId, Instant expireAt, Instant createdAt, String ipAddress) {
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
