package io.github.zrdzn.web.chattee.backend.account.privilege;

import java.util.List;
import panda.std.Result;

public interface PrivilegeRepository {

    Result<List<Privilege>, PrivilegeError> findPrivilegesByAccountId(long id);

}
