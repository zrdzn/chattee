package io.github.zrdzn.web.chattee.backend.account;

import io.github.zrdzn.web.chattee.backend.account.auth.AuthService;
import io.github.zrdzn.web.chattee.backend.account.privilege.PrivilegeService;
import io.github.zrdzn.web.chattee.backend.storage.postgres.PostgresStorage;
import io.github.zrdzn.web.chattee.backend.web.WebConfig;
import io.javalin.community.routing.annotations.AnnotationsRoutingPlugin;

public class AccountWebConfig implements WebConfig {

    private final PostgresStorage postgresStorage;
    private final PrivilegeService privilegeService;
    private final AuthService authService;

    private AccountService accountService;

    public AccountWebConfig(PostgresStorage postgresStorage, PrivilegeService privilegeService, AuthService authService) {
        this.postgresStorage = postgresStorage;
        this.privilegeService = privilegeService;
        this.authService = authService;
    }

    @Override
    public void initialize(AnnotationsRoutingPlugin plugin) {
        AccountRepository accountRepository = new PostgresAccountRepository(this.postgresStorage);
        this.accountService = new AccountService(accountRepository, this.privilegeService);

        AccountEndpoints accountEndpoints = new AccountEndpoints(this.accountService, this.authService);

        plugin.registerEndpoints(accountEndpoints);
    }

    public AccountService getAccountService() {
        return this.accountService;
    }

}
