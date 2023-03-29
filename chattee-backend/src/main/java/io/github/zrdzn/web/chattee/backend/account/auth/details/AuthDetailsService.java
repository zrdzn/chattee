package io.github.zrdzn.web.chattee.backend.account.auth.details;

import java.util.List;
import io.github.zrdzn.web.chattee.backend.web.HttpResponse;
import io.github.zrdzn.web.chattee.backend.web.security.token.AccessTokenService;
import panda.std.Blank;
import panda.std.Result;

import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.badRequest;
import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.conflict;
import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.internalServerError;
import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.notFound;

public class AuthDetailsService {

    private final AuthDetailsRepository authDetailsRepository;
    private final AccessTokenService accessTokenService;

    public AuthDetailsService(AuthDetailsRepository authDetailsRepository, AccessTokenService accessTokenService) {
        this.authDetailsRepository = authDetailsRepository;
        this.accessTokenService = accessTokenService;
    }

    public Result<AuthDetails, HttpResponse> createAuthDetails(AuthDetailsCreateRequest authDetailsCreateRequest) {
        return this.authDetailsRepository.saveAuthDetails(authDetailsCreateRequest, this.accessTokenService.createToken())
                .mapErr(error -> {
                    if (error == AuthDetailsError.ALREADY_EXIST) {
                        return conflict(AuthDetailsError.ALREADY_EXIST.getMessage());
                    } else if (error == AuthDetailsError.INVALID_ACCOUNT_ID) {
                        return badRequest(AuthDetailsError.INVALID_ACCOUNT_ID.getMessage());
                    }

                    return internalServerError("Could not create auth details.");
                });
    }

    public Result<List<AuthDetails>, HttpResponse> getAuthDetailsByAccountId(long accountId) {
        return this.authDetailsRepository.listAllAuthDetailsByAccountId(accountId)
                .mapErr(error -> internalServerError("Could not retrieve all auth details."));
    }

    public Result<AuthDetails, HttpResponse> getAuthDetailsByToken(String token) {
        return this.authDetailsRepository.findAuthDetailsByToken(token)
                .mapErr(error -> {
                    if (error == AuthDetailsError.NOT_EXIST) {
                        return notFound(AuthDetailsError.NOT_EXIST.getMessage());
                    }

                    return internalServerError("Could not retrieve auth details.");
                });
    }

    public Result<Blank, HttpResponse> removeAuthDetails(String token) {
        return this.authDetailsRepository.deleteAuthDetailsByToken(token)
                .mapErr(error -> internalServerError("Could not remove auth details."));
    }

}
