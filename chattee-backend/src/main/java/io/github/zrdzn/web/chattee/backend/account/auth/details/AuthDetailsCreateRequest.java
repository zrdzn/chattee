package io.github.zrdzn.web.chattee.backend.account.auth.details;

import java.time.Instant;

public class AuthDetailsCreateRequest {

    private long accountId;
    private Instant expireAt;
    private String ipAddress;

    public AuthDetailsCreateRequest() {
    }

    public AuthDetailsCreateRequest(long accountId, Instant expireAt, String ipAddress) {
        this.accountId = accountId;
        this.expireAt = expireAt;
        this.ipAddress = ipAddress;
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

    public String getIpAddress() {
        return this.ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

}
