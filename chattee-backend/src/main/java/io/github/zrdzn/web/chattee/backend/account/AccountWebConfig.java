package io.github.zrdzn.web.chattee.backend.account;

import io.github.zrdzn.web.chattee.backend.storage.postgres.PostgresStorage;
import io.github.zrdzn.web.chattee.backend.account.infrastructure.PostgresAccountRepository;
import io.github.zrdzn.web.chattee.backend.web.WebConfig;
import io.javalin.community.routing.annotations.AnnotationsRoutingPlugin;

public class AccountWebConfig implements WebConfig {

    private final PostgresStorage postgresStorage;

    private AccountService accountService;

    public AccountWebConfig(PostgresStorage postgresStorage) {
        this.postgresStorage = postgresStorage;
    }

    @Override
    public void initialize(AnnotationsRoutingPlugin plugin) {
        AccountRepository accountRepository = new PostgresAccountRepository(this.postgresStorage);
        this.accountService = new AccountService(accountRepository);

        AccountEndpoints accountEndpoints = new AccountEndpoints(this.accountService);
        AccountPrivilegeEndpoints accountPrivilegeEndpoints = new AccountPrivilegeEndpoints(this.accountService);

        plugin.registerEndpoints(accountEndpoints);
        plugin.registerEndpoints(accountPrivilegeEndpoints);
    }

    public AccountService getAccountService() {
        return this.accountService;
    }

}
