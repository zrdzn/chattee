package io.github.zrdzn.web.chattee.backend.account.privilege;

import io.github.zrdzn.web.chattee.backend.web.security.RoutePrivilege;

public class Privilege {

    private long accountId;
    private RoutePrivilege privilege;

    public Privilege(long accountId, RoutePrivilege privilege) {
        this.accountId = accountId;
        this.privilege = privilege;
    }

    public long getAccountId() {
        return this.accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public RoutePrivilege getPrivilege() {
        return this.privilege;
    }

    public void setPrivilege(RoutePrivilege routePrivilege) {
        this.privilege = routePrivilege;
    }

}
