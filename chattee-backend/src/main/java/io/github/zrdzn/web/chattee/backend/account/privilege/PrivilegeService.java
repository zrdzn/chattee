package io.github.zrdzn.web.chattee.backend.account.privilege;

import java.util.List;
import io.github.zrdzn.web.chattee.backend.web.HttpResponse;
import panda.std.Result;

import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.internalServerError;

public class PrivilegeService {

    private final PrivilegeRepository privilegeRepository;

    public PrivilegeService(PrivilegeRepository privilegeRepository) {
        this.privilegeRepository = privilegeRepository;
    }

    public Result<List<Privilege>, HttpResponse> getPrivilegesByAccountId(long id) {
        return this.privilegeRepository.findPrivilegesByAccountId(id)
                .mapErr(error -> internalServerError("Could not retrieve privileges."));
    }

}
