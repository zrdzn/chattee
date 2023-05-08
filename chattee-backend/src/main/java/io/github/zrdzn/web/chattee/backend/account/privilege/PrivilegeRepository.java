package io.github.zrdzn.web.chattee.backend.account.privilege;

import java.util.List;
import io.github.zrdzn.web.chattee.backend.web.security.RoutePrivilege;
import panda.std.Result;

public interface PrivilegeRepository {

    Result<List<Privilege>, PrivilegeError> createPrivileges(long accountId, List<RoutePrivilege> privileges);

    Result<List<Privilege>, PrivilegeError> findPrivilegesByAccountId(long id);

}
