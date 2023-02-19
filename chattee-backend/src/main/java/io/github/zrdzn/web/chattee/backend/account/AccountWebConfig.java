package io.github.zrdzn.web.chattee.backend.account;

import io.github.zrdzn.web.chattee.backend.storage.postgres.PostgresStorage;
import io.github.zrdzn.web.chattee.backend.account.infrastructure.PostgresAccountRepository;
import io.github.zrdzn.web.chattee.backend.web.WebConfig;
import io.javalin.community.routing.annotations.AnnotationsRoutingPlugin;

public class AccountWebConfig implements WebConfig {

    private final PostgresStorage postgresStorage;

    private AccountFacade accountFacade;

    public AccountWebConfig(PostgresStorage postgresStorage) {
        this.postgresStorage = postgresStorage;
    }

    @Override
    public void initialize(AnnotationsRoutingPlugin plugin) {
        AccountRepository accountRepository = new PostgresAccountRepository(this.postgresStorage);
        AccountService accountService = new AccountService(accountRepository);
        this.accountFacade = new AccountFacade(accountService);

        AccountEndpoints accountEndpoints = new AccountEndpoints(this.accountFacade);
        AccountPrivilegeEndpoints accountPrivilegeEndpoints = new AccountPrivilegeEndpoints(this.accountFacade);

        plugin.registerEndpoints(accountEndpoints);
        plugin.registerEndpoints(accountPrivilegeEndpoints);
    }

    public AccountFacade getUserFacade() {
        return this.accountFacade;
    }

}
