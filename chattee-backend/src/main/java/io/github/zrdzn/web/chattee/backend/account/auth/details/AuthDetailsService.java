package io.github.zrdzn.web.chattee.backend.account.auth.details;

import java.util.List;
import io.github.zrdzn.web.chattee.backend.shared.DomainError;
import io.github.zrdzn.web.chattee.backend.web.HttpResponse;
import io.github.zrdzn.web.chattee.backend.web.security.token.AccessTokenService;
import panda.std.Blank;
import panda.std.Result;

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

    public Result<AuthDetails, HttpResponse> createAuthDetails(AuthDetailsCreateDto createDto) {
        AuthDetails authDetails = new AuthDetails(this.accessTokenService.createToken(), createDto.getAccountId(),
                createDto.getExpireAt(), createDto.getIpAddress());

        return this.authDetailsRepository.saveAuthDetails(authDetails)
                .mapErr(error -> {
                    if (error == DomainError.AUTH_DETAILS_ALREADY_EXIST) {
                        return conflict("Auth details already exist.");
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
                    if (error == DomainError.AUTH_DETAILS_NOT_EXIST) {
                        return notFound("Auth details do not exist.");
                    }

                    return internalServerError("Could not retrieve auth details.");
                });
    }

    public Result<Blank, HttpResponse> removeAuthDetails(String token) {
        return this.authDetailsRepository.deleteAuthDetailsByToken(token)
                .mapErr(error -> internalServerError("Could not remove auth details."));
    }

}
