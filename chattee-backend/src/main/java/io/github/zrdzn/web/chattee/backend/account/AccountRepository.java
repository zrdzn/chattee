package io.github.zrdzn.web.chattee.backend.account;

import java.util.List;
import io.github.zrdzn.web.chattee.backend.shared.DomainError;
import panda.std.Blank;
import panda.std.Result;

public interface AccountRepository {

    Result<Account, DomainError> saveAccount(Account account);

    Result<AccountPrivilege, DomainError> savePrivilege(AccountPrivilege privilege);

    Result<List<Account>, DomainError> listAllAccounts();

    Result<List<AccountPrivilege>, DomainError> listAllPrivileges();

    Result<Account, DomainError> findAccountById(long id);

    Result<Account, DomainError> findAccountByEmail(String email);

    Result<AccountPrivilege, DomainError> findPrivilegeById(long id);

    Result<List<AccountPrivilege>, DomainError> findPrivilegesByAccountId(long id);

    Result<Blank, DomainError> deleteAccount(long id);

    Result<Blank, DomainError> deletePrivilege(long id);

}
