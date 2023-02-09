package io.github.zrdzn.web.chattee.backend.web.security.token;

public class AccessTokenFacade {

    private final AccessTokenService accessTokenService;

    public AccessTokenFacade(AccessTokenService accessTokenService) {
        this.accessTokenService = accessTokenService;
    }

    public String createToken() {
        return this.accessTokenService.createToken();
    }

}
