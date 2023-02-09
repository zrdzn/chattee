package io.github.zrdzn.web.chattee.backend.user.session;

import java.util.List;
import java.util.Optional;
import io.github.zrdzn.web.chattee.backend.web.HttpResponse;
import panda.std.Blank;
import panda.std.Result;

public class SessionFacade {

    private final SessionService sessionService;

    public SessionFacade(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    public Result<Session, HttpResponse> createSession(Session session) {
        return this.sessionService.createSession(session);
    }

    public Result<List<Session>, HttpResponse> getAllSessions() {
        return this.sessionService.getAllSessions();
    }

    public Result<Optional<Session>, HttpResponse> getSession(String token) {
        return this.sessionService.getSession(token);
    }

    public Result<Blank, HttpResponse> removeSession(String token) {
        return this.sessionService.removeSession(token);
    }

}
