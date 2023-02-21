package io.github.zrdzn.web.chattee.backend.account.session;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import io.github.zrdzn.web.chattee.backend.web.HttpResponse;
import io.github.zrdzn.web.chattee.backend.web.security.token.AccessTokenService;
import org.postgresql.util.PSQLState;
import org.tinylog.Logger;
import panda.std.Blank;
import panda.std.Result;

import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.badRequest;
import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.internalServerError;

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
                .onError(error -> {
                    if (error instanceof SQLException sqlException) {
                        if (sqlException.getSQLState().equalsIgnoreCase(PSQLState.FOREIGN_KEY_VIOLATION.getState())) {
                            return;
                        }
                    }

                    Logger.error(error, "Could not save the session.");
                })
                .mapErr(error -> {
                    if (error instanceof SQLException sqlException) {
                        if (sqlException.getSQLState().equalsIgnoreCase(PSQLState.FOREIGN_KEY_VIOLATION.getState())) {
                            return badRequest("'userId' does not target an existing record.");
                        }
                    }

                    return internalServerError("Could not create the session.");
                });
    }

    public Result<List<Session>, HttpResponse> getAllSessions() {
        return this.sessionRepository.listAllSessions()
                .onError(error -> Logger.error(error, "Could not list all sessions."))
                .mapErr(error -> internalServerError("Could not retrieve all sessions."));
    }

    public Result<Optional<Session>, HttpResponse> getSession(String token) {
        return this.sessionRepository.findSessionByToken(token)
                .onError(error -> Logger.error(error, "Could not find a session."))
                .mapErr(error -> internalServerError("Could not retrieve a session."));
    }

    public Result<Blank, HttpResponse> removeSession(String token) {
        return this.sessionRepository.deleteSessionByToken(token)
                .onError(error -> Logger.error(error, "Could not delete a session."))
                .mapErr(error -> internalServerError("Could not remove a session."));
    }

}
