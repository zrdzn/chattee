package io.github.zrdzn.web.chattee.backend;

import eu.okaeri.configs.OkaeriConfig;
import io.github.zrdzn.web.chattee.backend.storage.StorageConfig;

public class ChatteeConfig extends OkaeriConfig {

    private int port = 7070;
    private String frontendUrl = "http://localhost:3000";
    private StorageConfig storage = new StorageConfig();

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getFrontendUrl() {
        return this.frontendUrl;
    }

    public void setFrontendUrl(String frontendUrl) {
        this.frontendUrl = frontendUrl;
    }

    public StorageConfig getStorage() {
        return this.storage;
    }

    public void setStorage(StorageConfig storage) {
        this.storage = storage;
    }

}
