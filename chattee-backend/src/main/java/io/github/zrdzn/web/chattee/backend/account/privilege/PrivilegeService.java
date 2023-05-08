package io.github.zrdzn.web.chattee.backend.account.privilege;

import java.util.List;
import io.github.zrdzn.web.chattee.backend.web.HttpResponse;
import io.github.zrdzn.web.chattee.backend.web.security.RoutePrivilege;
import panda.std.Result;

import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.conflict;
import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.internalServerError;

public class PrivilegeService {

    private final PrivilegeRepository privilegeRepository;

    public PrivilegeService(PrivilegeRepository privilegeRepository) {
        this.privilegeRepository = privilegeRepository;
    }

    public Result<List<Privilege>, HttpResponse> createPrivileges(long accountId, List<RoutePrivilege> privileges) {
        return this.privilegeRepository.createPrivileges(accountId, privileges)
                .mapErr(error -> {
                    if (error == PrivilegeError.ALREADY_EXISTS) {
                        return conflict(PrivilegeError.ALREADY_EXISTS.getMessage());
                    }

                    return internalServerError("Could not create privileges.");
                });
    }

    public Result<List<Privilege>, HttpResponse> getPrivilegesByAccountId(long id) {
        return this.privilegeRepository.findPrivilegesByAccountId(id)
                .mapErr(error -> internalServerError("Could not retrieve privileges."));
    }

}
