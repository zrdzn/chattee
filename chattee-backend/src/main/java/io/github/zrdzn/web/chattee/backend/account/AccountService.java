package io.github.zrdzn.web.chattee.backend.account;

import at.favre.lib.crypto.bcrypt.BCrypt;
import java.util.List;
import io.github.zrdzn.web.chattee.backend.shared.DomainError;
import io.github.zrdzn.web.chattee.backend.web.HttpResponse;
import panda.std.Blank;
import panda.std.Result;

import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.badRequest;
import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.conflict;
import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.internalServerError;
import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.notFound;

public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Result<Account, HttpResponse> registerAccount(AccountRegisterDto accountRegisterDto) {
        String password = BCrypt.withDefaults().hashToString(10, accountRegisterDto.getRawPassword().toCharArray());
        Account account = new Account(accountRegisterDto.getEmail(), password, accountRegisterDto.getUsername());

        return this.accountRepository.saveAccount(account)
                .mapErr(error -> {
                    if (error == DomainError.ACCOUNT_ALREADY_EXISTS) {
                        return conflict("Account already exists.");
                    }

                    return internalServerError("Could not create account.");
                });
    }

    public Result<AccountPrivilege, HttpResponse> createPrivilege(AccountPrivilege privilege) {
        return this.accountRepository.savePrivilege(privilege)
                .mapErr(error -> {
                    if (error == DomainError.ACCOUNT_INVALID_ID) {
                        return badRequest("'accountId' does not target existing record.");
                    }

                    return internalServerError("Could not create the privilege.");
                });
    }

    public Result<List<Account>, HttpResponse> getAllAccounts() {
        return this.accountRepository.listAllAccounts()
                .mapErr(error -> internalServerError("Could not retrieve all accounts."));
    }

    public Result<List<AccountPrivilege>, HttpResponse> getPrivilegesByAccountId(long id) {
        return this.accountRepository.findPrivilegesByAccountId(id)
                .mapErr(error -> internalServerError("Could not retrieve privileges."));
    }

    public Result<Account, HttpResponse> getAccount(long id) {
        return this.accountRepository.findAccountById(id)
                .mapErr(error -> {
                    if (error == DomainError.ACCOUNT_NOT_EXISTS) {
                        notFound("Account does not exist.");
                    }

                    return internalServerError("Could not retrieve account.");
                });
    }

    public Result<Account, HttpResponse> getAccount(String email) {
        return this.accountRepository.findAccountByEmail(email)
                .mapErr(error -> {
                    if (error == DomainError.ACCOUNT_NOT_EXISTS) {
                        notFound("Account does not exist.");
                    }

                    return internalServerError("Could not retrieve account.");
                });
    }

    public Result<Blank, HttpResponse> removeAccount(long id) {
        return this.accountRepository.deleteAccount(id)
                .mapErr(error -> internalServerError("Could not remove account."));
    }

    public Result<Blank, HttpResponse> removePrivilege(long id) {
        return this.accountRepository.deletePrivilege(id)
                .mapErr(error -> internalServerError("Could not remove privilege."));
    }

}
