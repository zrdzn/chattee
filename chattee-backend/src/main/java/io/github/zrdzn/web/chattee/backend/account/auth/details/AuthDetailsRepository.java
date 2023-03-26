package io.github.zrdzn.web.chattee.backend.account.auth.details;

import java.util.List;
import io.github.zrdzn.web.chattee.backend.shared.DomainError;
import panda.std.Blank;
import panda.std.Result;

public interface AuthDetailsRepository {

    Result<AuthDetails, DomainError> saveAuthDetails(AuthDetailsCreateRequest authDetailsCreateRequest, String token);

    Result<List<AuthDetails>, DomainError> listAllAuthDetailsByAccountId(long id);

    Result<AuthDetails, DomainError> findAuthDetailsByToken(String token);

    Result<Blank, DomainError> deleteAuthDetailsByToken(String token);

}
