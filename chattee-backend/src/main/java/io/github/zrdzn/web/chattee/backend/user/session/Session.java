package io.github.zrdzn.web.chattee.backend.user.session;

import java.time.Instant;

public class Session {

    private String token;
    private long userId;
    private Instant expireAt;
    private Instant createdAt;
    private String ipAddress;

    public Session(long userId, Instant expireAt, String ipAddress) {
        this(userId, expireAt, null, ipAddress);
    }

    public Session(long userId, Instant expireAt, Instant createdAt, String ipAddress) {
        this(null, userId, expireAt, createdAt, ipAddress);
    }

    public Session(String token, long userId, Instant expireAt, Instant createdAt, String ipAddress) {
        this.token = token;
        this.userId = userId;
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

    public long getUserId() {
        return this.userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
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
