package io.github.zrdzn.web.chattee.backend.account;

import at.favre.lib.crypto.bcrypt.BCrypt;
import java.util.List;
import io.github.zrdzn.web.chattee.backend.web.HttpResponse;
import panda.std.Blank;
import panda.std.Result;

import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.conflict;
import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.internalServerError;
import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.notFound;

public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Result<Account, HttpResponse> registerAccount(AccountCreateRequest accountCreateRequest) {
        String password = BCrypt.withDefaults().hashToString(10, accountCreateRequest.getPassword().toCharArray());
        accountCreateRequest.setPassword(password);

        return this.accountRepository.saveAccount(accountCreateRequest)
                .mapErr(error -> {
                    if (error == AccountError.ALREADY_EXISTS) {
                        return conflict(AccountError.ALREADY_EXISTS.getMessage());
                    }

                    return internalServerError("Could not create account.");
                });
    }

    public Result<List<Account>, HttpResponse> getAllAccounts() {
        return this.accountRepository.listAllAccounts()
                .mapErr(error -> internalServerError("Could not retrieve all accounts."));
    }

    public Result<Account, HttpResponse> getAccount(long id) {
        return this.accountRepository.findAccountById(id)
                .mapErr(error -> {
                    if (error == AccountError.NOT_EXISTS) {
                        return notFound(AccountError.NOT_EXISTS.getMessage());
                    }

                    return internalServerError("Could not retrieve account.");
                });
    }

    public Result<Account, HttpResponse> getAccount(String email) {
        return this.accountRepository.findAccountByEmail(email)
                .mapErr(error -> {
                    if (error == AccountError.NOT_EXISTS) {
                        return notFound(AccountError.NOT_EXISTS.getMessage());
                    }

                    return internalServerError("Could not retrieve account.");
                });
    }

    public Result<Blank, HttpResponse> removeAccount(long id) {
        return this.accountRepository.deleteAccount(id)
                .mapErr(error -> internalServerError("Could not remove account."));
    }

}
