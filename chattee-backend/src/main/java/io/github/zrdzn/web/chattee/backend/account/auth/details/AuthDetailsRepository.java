package io.github.zrdzn.web.chattee.backend.account.auth.details;

import java.util.List;
import panda.std.Blank;
import panda.std.Result;

public interface AuthDetailsRepository {

    Result<AuthDetails, AuthDetailsError> saveAuthDetails(AuthDetailsCreateRequest authDetailsCreateRequest, String token);

    Result<List<AuthDetails>, AuthDetailsError> listAllAuthDetailsByAccountId(long id);

    Result<AuthDetails, AuthDetailsError> findAuthDetailsByToken(String token);

    Result<Blank, AuthDetailsError> deleteAuthDetailsByToken(String token);

}
