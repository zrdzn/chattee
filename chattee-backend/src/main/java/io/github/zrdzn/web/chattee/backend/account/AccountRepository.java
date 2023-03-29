package io.github.zrdzn.web.chattee.backend.account;

import java.util.List;
import panda.std.Blank;
import panda.std.Result;

public interface AccountRepository {

    Result<Account, AccountError> saveAccount(AccountCreateRequest accountCreateRequest);

    Result<List<Account>, AccountError> listAllAccounts();

    Result<Account, AccountError> findAccountById(long id);

    Result<Account, AccountError> findAccountByEmail(String email);

    Result<Blank, AccountError> deleteAccount(long id);

}
