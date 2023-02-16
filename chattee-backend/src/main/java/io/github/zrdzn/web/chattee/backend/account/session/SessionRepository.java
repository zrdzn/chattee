package io.github.zrdzn.web.chattee.backend.account.session;

import java.util.List;
import java.util.Optional;
import panda.std.Blank;
import panda.std.Result;

public interface SessionRepository {

    Result<Session, Exception> saveSession(Session session);

    Result<List<Session>, Exception> listAllSessions();

    Result<Optional<Session>, Exception> findSessionByToken(String token);

    Result<Blank, Exception> deleteSessionByToken(String token);

}
