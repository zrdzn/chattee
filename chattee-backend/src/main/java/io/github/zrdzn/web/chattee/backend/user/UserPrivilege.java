package io.github.zrdzn.web.chattee.backend.user;

import io.github.zrdzn.web.chattee.backend.web.security.Privilege;

public class UserPrivilege {

    private long id;
    private long userId;
    private Privilege privilege;

    public UserPrivilege(long userId, Privilege privilege) {
        this(0L, userId, privilege);
    }

    public UserPrivilege(long id, long userId, Privilege privilege) {
        this.id = id;
        this.userId = userId;
        this.privilege = privilege;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return this.userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Privilege getPrivilege() {
        return this.privilege;
    }

    public void setPrivilege(Privilege privilege) {
        this.privilege = privilege;
    }

}
