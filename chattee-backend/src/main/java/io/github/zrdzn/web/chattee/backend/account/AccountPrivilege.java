package io.github.zrdzn.web.chattee.backend.account;

import io.github.zrdzn.web.chattee.backend.web.security.Privilege;

public class AccountPrivilege {

    private long accountId;
    private Privilege privilege;

    public AccountPrivilege(long accountId, Privilege privilege) {
        this.accountId = accountId;
        this.privilege = privilege;
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
