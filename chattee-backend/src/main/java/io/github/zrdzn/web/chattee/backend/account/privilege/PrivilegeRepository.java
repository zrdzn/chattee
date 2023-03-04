package io.github.zrdzn.web.chattee.backend.account.privilege;

import java.util.List;
import io.github.zrdzn.web.chattee.backend.shared.DomainError;
import panda.std.Result;

public interface PrivilegeRepository {

    Result<List<Privilege>, DomainError> findPrivilegesByAccountId(long id);

}
