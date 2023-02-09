package io.github.zrdzn.web.chattee.backend.web.security.token;

import java.security.SecureRandom;
import java.util.Base64;

public class AccessTokenService {

    private final SecureRandom secureRandom = new SecureRandom();
    private final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

    public String createToken() {
        byte[] randomBytes = new byte[24];
        this.secureRandom.nextBytes(randomBytes);

        return this.base64Encoder.encodeToString(randomBytes);
    }

}
