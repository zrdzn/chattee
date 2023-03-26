package io.github.zrdzn.web.chattee.backend.account;

import java.util.List;
import io.github.zrdzn.web.chattee.backend.account.privilege.Privilege;
import io.github.zrdzn.web.chattee.backend.shared.DomainError;
import panda.std.Blank;
import panda.std.Result;

public interface AccountRepository {

    Result<Account, DomainError> saveAccount(AccountCreateRequest accountCreateRequest);

    Result<Privilege, DomainError> savePrivilege(Privilege privilege);

    Result<List<Account>, DomainError> listAllAccounts();

    Result<Account, DomainError> findAccountById(long id);

    Result<Account, DomainError> findAccountByEmail(String email);

    Result<Blank, DomainError> deleteAccount(long id);

    Result<Blank, DomainError> deletePrivilege(long id);

}
