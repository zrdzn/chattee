package io.github.zrdzn.web.chattee.backend.account;

import at.favre.lib.crypto.bcrypt.BCrypt;
import java.util.List;
import java.util.stream.Collectors;
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

    public Result<AccountDetails, HttpResponse> registerAccount(AccountCreateRequest accountCreateRequest) {
        String password = BCrypt.withDefaults().hashToString(10, accountCreateRequest.getPassword().toCharArray());
        accountCreateRequest.setPassword(password);

        return this.accountRepository.saveAccount(accountCreateRequest)
                .map(account ->
                        new AccountDetails(
                                account.getId(),
                                account.getCreatedAt(),
                                account.getUpdatedAt(),
                                account.getEmail(),
                                account.getUsername(),
                                account.getAvatarUrl()
                        )
                )
                .mapErr(error -> {
                    if (error == AccountError.ALREADY_EXISTS) {
                        return conflict(AccountError.ALREADY_EXISTS.getMessage());
                    }

                    return internalServerError("Could not create account.");
                });
    }

    public Result<List<AccountDetails>, HttpResponse> getAllAccounts() {
        return this.accountRepository.listAllAccounts()
                .map(accounts -> accounts.stream()
                        .map(account ->
                                new AccountDetails(
                                        account.getId(),
                                        account.getCreatedAt(),
                                        account.getUpdatedAt(),
                                        account.getEmail(),
                                        account.getUsername(),
                                        account.getAvatarUrl()
                                )
                        )
                        .collect(Collectors.toList())
                )
                .mapErr(error -> internalServerError("Could not retrieve all accounts."));
    }

    public Result<AccountDetails, HttpResponse> getAccount(long id) {
        return this.accountRepository.findAccountById(id)
                .map(account ->
                        new AccountDetails(
                                account.getId(),
                                account.getCreatedAt(),
                                account.getUpdatedAt(),
                                account.getEmail(),
                                account.getUsername(),
                                account.getAvatarUrl()
                        )
                )
                .mapErr(error -> {
                    if (error == AccountError.NOT_EXISTS) {
                        return notFound(AccountError.NOT_EXISTS.getMessage());
                    }

                    return internalServerError("Could not retrieve account.");
                });
    }

    public Result<AccountDetails, HttpResponse> getAccount(String email) {
        return this.findRawAccount(email)
                .map(account ->
                        new AccountDetails(
                                account.getId(),
                                account.getCreatedAt(),
                                account.getUpdatedAt(),
                                account.getEmail(),
                                account.getUsername(),
                                account.getAvatarUrl()
                        )
                );
    }

    public Result<Account, HttpResponse> findRawAccount(String email) {
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
