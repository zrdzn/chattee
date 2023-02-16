package io.github.zrdzn.web.chattee.backend.account;

import java.util.List;
import java.util.Optional;
import panda.std.Blank;
import panda.std.Result;

public interface AccountRepository {

    Result<Account, Exception> saveAccount(Account account);

    Result<AccountPrivilege, Exception> savePrivilege(AccountPrivilege privilege);

    Result<List<Account>, Exception> listAllAccounts();

    Result<List<AccountPrivilege>, Exception> listAllPrivileges();

    Result<Optional<Account>, Exception> findAccountById(long id);

    Result<Optional<Account>, Exception> findAccountByEmail(String email);

    Result<Optional<AccountPrivilege>, Exception> findPrivilegeById(long id);

    Result<List<AccountPrivilege>, Exception> findPrivilegesByAccountId(long id);

    Result<Blank, Exception> deleteAccount(long id);

    Result<Blank, Exception> deletePrivilege(long id);

}
