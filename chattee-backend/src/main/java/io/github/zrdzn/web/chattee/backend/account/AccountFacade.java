package io.github.zrdzn.web.chattee.backend.account;

import java.util.List;
import java.util.Optional;
import io.github.zrdzn.web.chattee.backend.web.HttpResponse;
import panda.std.Blank;
import panda.std.Result;

public class AccountFacade {

    private final AccountService accountService;

    public AccountFacade(AccountService accountService) {
        this.accountService = accountService;
    }

    public Result<AccountDetailsDto, HttpResponse> registerAccount(AccountRegisterDto accountRegisterDto) {
        return this.accountService.registerAccount(accountRegisterDto);
    }

    public Result<AccountPrivilege, HttpResponse> createPrivilege(AccountPrivilege privilege) {
        return this.accountService.createPrivilege(privilege);
    }

    public Result<List<AccountDetailsDto>, HttpResponse> getAllAccounts() {
        return this.accountService.getAllAccounts();
    }

    public Result<List<AccountPrivilege>, HttpResponse> getAllPrivileges() {
        return this.accountService.getAllPrivileges();
    }

    public Result<List<AccountPrivilege>, HttpResponse> getPrivilegesByAccountId(long id) {
        return this.accountService.getPrivilegesByAccountId(id);
    }

    public Result<Optional<AccountDetailsDto>, HttpResponse> getAccount(long id) {
        return this.accountService.getAccount(id);
    }

    public Result<Optional<AccountDetailsDto>, HttpResponse> getAccount(String email) {
        return this.accountService.getAccount(email);
    }

    public Result<Optional<AccountPrivilege>, HttpResponse> getPrivilege(long id) {
        return this.accountService.getPrivilege(id);
    }

    public Result<Blank, HttpResponse> removeAccount(long id) {
        return this.accountService.removeAccount(id);
    }

    public Result<Blank, HttpResponse> removePrivilege(long id) {
        return this.accountService.removePrivilege(id);
    }

}
