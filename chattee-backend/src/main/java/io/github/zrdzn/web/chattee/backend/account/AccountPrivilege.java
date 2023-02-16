package io.github.zrdzn.web.chattee.backend.account;

import io.github.zrdzn.web.chattee.backend.web.security.Privilege;

public class AccountPrivilege {

    private long id;
    private long accountId;
    private Privilege privilege;

    public AccountPrivilege(long accountId, Privilege privilege) {
        this(0L, accountId, privilege);
    }

    public AccountPrivilege(long id, long accountId, Privilege privilege) {
        this.id = id;
        this.accountId = accountId;
        this.privilege = privilege;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAccountId() {
        return this.accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public Privilege getPrivilege() {
        return this.privilege;
    }

    public void setPrivilege(Privilege privilege) {
        this.privilege = privilege;
    }

}
