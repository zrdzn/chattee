package io.github.zrdzn.web.chattee.backend.storage;

import com.zaxxer.hikari.HikariDataSource;

public interface Poolable {

    HikariDataSource getHikariDataSource();

}
