package io.github.zrdzn.web.chattee.backend.storage;

import eu.okaeri.configs.OkaeriConfig;

public final class StorageConfig extends OkaeriConfig {

    private String host = "localhost";
    private String port = "5432";
    private String db = "chattee";
    private String user = "chattee";
    private String pass = "chattee";
    private boolean ssl = false;
    private int maxPoolSize = 10;
    private int connectionTimeoutMs = 5000;

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return this.port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getDb() {
        return this.db;
    }

    public void setDb(String db) {
        this.db = db;
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return this.pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public boolean getSsl() {
        return this.ssl;
    }

    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }

    public int getMaxPoolSize() {
        return this.maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public int getConnectionTimeoutMs() {
        return this.connectionTimeoutMs;
    }

    public void setConnectionTimeoutMs(int connectionTimeoutMs) {
        this.connectionTimeoutMs = connectionTimeoutMs;
    }

}
