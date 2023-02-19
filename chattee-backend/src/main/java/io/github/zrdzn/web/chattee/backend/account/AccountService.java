package io.github.zrdzn.web.chattee.backend.account;

import at.favre.lib.crypto.bcrypt.BCrypt;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import io.github.zrdzn.web.chattee.backend.web.HttpResponse;
import org.postgresql.util.PSQLState;
import org.tinylog.Logger;
import panda.std.Blank;
import panda.std.Result;

import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.badRequest;
import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.internalServerError;

public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Result<AccountDetailsDto, HttpResponse> registerAccount(AccountRegisterDto accountRegisterDto) {
        String password = BCrypt.withDefaults().hashToString(10, accountRegisterDto.getRawPassword().toCharArray());
        Account account = new Account(accountRegisterDto.getEmail(), password, accountRegisterDto.getUsername());

        return this.accountRepository.saveAccount(account)
                .map(AccountDetailsDto::new)
                .onError(error -> Logger.error(error, "Could not save the account."))
                .mapErr(error -> internalServerError("Could not create the account."));
    }

    public Result<AccountPrivilege, HttpResponse> createPrivilege(AccountPrivilege privilege) {
        return this.accountRepository.savePrivilege(privilege)
                .onError(error -> {
                    if (error instanceof SQLException sqlException) {
                        if (sqlException.getSQLState().equalsIgnoreCase(PSQLState.FOREIGN_KEY_VIOLATION.getState())) {
                            return;
                        }
                    }

                    Logger.error(error, "Could not save the privilege.");
                })
                .mapErr(error -> {
                    if (error instanceof SQLException sqlException) {
                        if (sqlException.getSQLState().equalsIgnoreCase(PSQLState.FOREIGN_KEY_VIOLATION.getState())) {
                            return badRequest("'accountId' does not target an existing record.");
                        }
                    }

                    return internalServerError("Could not create the privilege.");
                });
    }

    public Result<List<AccountDetailsDto>, HttpResponse> getAllAccounts() {
        return this.accountRepository.listAllAccounts()
                .map(accounts -> accounts.stream().map(AccountDetailsDto::new).collect(Collectors.toList()))
                .onError(error -> Logger.error(error, "Could not list all accounts."))
                .mapErr(error -> internalServerError("Could not retrieve all accounts."));
    }

    public Result<List<AccountPrivilege>, HttpResponse> getAllPrivileges() {
        return this.accountRepository.listAllPrivileges()
                .onError(error -> Logger.error(error, "Could not list all privileges."))
                .mapErr(error -> internalServerError("Could not retrieve all privileges."));
    }

    public Result<Optional<AccountDetailsDto>, HttpResponse> getAccount(long id) {
        return this.accountRepository.findAccountById(id)
                .map(account -> account.map(AccountDetailsDto::new))
                .onError(error -> Logger.error(error, "Could not find an account."))
                .mapErr(error -> internalServerError("Could not retrieve an account."));
    }

    public Result<Optional<AccountDetailsDto>, HttpResponse> getAccount(String email) {
        return this.accountRepository.findAccountByEmail(email)
                .map(account -> account.map(AccountDetailsDto::new))
                .onError(error -> Logger.error(error, "Could not find an account."))
                .mapErr(error -> internalServerError("Could not retrieve an account."));
    }

    public Result<Optional<AccountPrivilege>, HttpResponse> getPrivilege(long id) {
        return this.accountRepository.findPrivilegeById(id)
                .onError(error -> Logger.error(error, "Could not find a privilege."))
                .mapErr(error -> internalServerError("Could not retrieve a privilege."));
    }

    public Result<List<AccountPrivilege>, HttpResponse> getPrivilegesByAccountId(long id) {
        return this.accountRepository.findPrivilegesByAccountId(id)
                .onError(error -> Logger.error(error, "Could not find privileges."))
                .mapErr(error -> internalServerError("Could not retrieve privileges."));
    }

    public Result<Blank, HttpResponse> removeAccount(long id) {
        return this.accountRepository.deleteAccount(id)
                .onError(error -> Logger.error(error, "Could not delete an account."))
                .mapErr(error -> internalServerError("Could not remove an account."));
    }

    public Result<Blank, HttpResponse> removePrivilege(long id) {
        return this.accountRepository.deletePrivilege(id)
                .onError(error -> Logger.error(error, "Could not delete a privilege."))
                .mapErr(error -> internalServerError("Could not remove a privilege."));
    }

}
