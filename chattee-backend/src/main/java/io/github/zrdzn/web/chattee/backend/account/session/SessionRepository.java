package io.github.zrdzn.web.chattee.backend.account.session;

import java.util.List;
import io.github.zrdzn.web.chattee.backend.shared.DomainError;
import panda.std.Blank;
import panda.std.Result;

public interface SessionRepository {

    Result<Session, DomainError> saveSession(Session session);

    Result<List<Session>, DomainError> listAllSessions();

    Result<Session, DomainError> findSessionByToken(String token);

    Result<Blank, DomainError> deleteSessionByToken(String token);

}
