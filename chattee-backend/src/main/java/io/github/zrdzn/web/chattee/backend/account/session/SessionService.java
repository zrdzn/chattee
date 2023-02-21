package io.github.zrdzn.web.chattee.backend.account.session;

import java.util.List;
import io.github.zrdzn.web.chattee.backend.shared.DomainError;
import io.github.zrdzn.web.chattee.backend.web.HttpResponse;
import io.github.zrdzn.web.chattee.backend.web.security.token.AccessTokenService;
import panda.std.Blank;
import panda.std.Result;

import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.badRequest;
import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.conflict;
import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.internalServerError;
import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.notFound;

public class SessionService {

    private final SessionRepository sessionRepository;
    private final AccessTokenService accessTokenService;

    public SessionService(SessionRepository sessionRepository, AccessTokenService accessTokenService) {
        this.sessionRepository = sessionRepository;
        this.accessTokenService = accessTokenService;
    }

    public Result<Session, HttpResponse> createSession(Session session) {
        if (session.getToken() == null) {
            session.setToken(this.accessTokenService.createToken());
        }

        return this.sessionRepository.saveSession(session)
                .mapErr(error -> {
                    if (error == DomainError.SESSION_ALREADY_EXISTS) {
                        return conflict("Session already exists.");
                    } else if (error == DomainError.ACCOUNT_INVALID_ID) {
                        return badRequest("'accountId' does not target existing record.");
                    }

                    return internalServerError("Could not create session.");
                });
    }

    public Result<List<Session>, HttpResponse> getAllSessions() {
        return this.sessionRepository.listAllSessions()
                .mapErr(error -> internalServerError("Could not retrieve all sessions."));
    }

    public Result<Session, HttpResponse> getSession(String token) {
        return this.sessionRepository.findSessionByToken(token)
                .mapErr(error -> {
                    if (error == DomainError.SESSION_NOT_EXISTS) {
                        return notFound("Session does not exist.");
                    }

                    return internalServerError("Could not retrieve session.");
                });
    }

    public Result<Blank, HttpResponse> removeSession(String token) {
        return this.sessionRepository.deleteSessionByToken(token)
                .mapErr(error -> internalServerError("Could not remove session."));
    }

}
