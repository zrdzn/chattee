package io.github.zrdzn.web.chattee.backend.account.infrastructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import io.github.zrdzn.web.chattee.backend.account.Account;
import io.github.zrdzn.web.chattee.backend.account.AccountPrivilege;
import io.github.zrdzn.web.chattee.backend.account.AccountRepository;
import panda.std.Blank;
import panda.std.Result;

public class InMemoryAccountRepository implements AccountRepository {

    private final Map<Long, Account> accounts = new HashMap<>();
    private final Map<Long, AccountPrivilege> privileges = new HashMap<>();

    @Override
    public Result<Account, Exception> saveAccount(Account account) {
        this.accounts.put(account.getId(), account);
        return Result.ok(account);
    }

    public Result<AccountPrivilege, Exception> savePrivilege(AccountPrivilege privilege) {
        this.privileges.put(privilege.getId(), privilege);
        return Result.ok(privilege);
    }

    @Override
    public Result<List<Account>, Exception> listAllAccounts() {
        return Result.ok(new ArrayList<>(this.accounts.values()));
    }

    public Result<List<AccountPrivilege>, Exception> listAllPrivileges() {
        return Result.ok(new ArrayList<>(this.privileges.values()));
    }

    @Override
    public Result<Optional<Account>, Exception> findAccountById(long id) {
        return Result.ok(Optional.ofNullable(this.accounts.get(id)));
    }

    @Override
    public Result<Optional<Account>, Exception> findAccountByEmail(String email) {
        return Result.ok(this.accounts.values().stream()
                .filter(account -> account.getEmail().equalsIgnoreCase(email))
                .findAny());
    }

    @Override
    public Result<Optional<AccountPrivilege>, Exception> findPrivilegeById(long id) {
        return Result.ok(Optional.ofNullable(this.privileges.get(id)));
    }

    @Override
    public Result<List<AccountPrivilege>, Exception> findPrivilegesByAccountId(long id) {
        return Result.ok(this.privileges.values().stream()
                .filter(privilege -> privilege.getAccountId() == id)
                .collect(Collectors.toList()));
    }

    @Override
    public Result<Blank, Exception> deleteAccount(long id) {
        this.accounts.remove(id);
        return Result.ok();
    }

    @Override
    public Result<Blank, Exception> deletePrivilege(long id) {
        this.privileges.remove(id);
        return Result.ok();
    }

}
