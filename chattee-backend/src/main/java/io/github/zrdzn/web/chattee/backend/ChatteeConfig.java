package io.github.zrdzn.web.chattee.backend;

import eu.okaeri.configs.OkaeriConfig;
import io.github.zrdzn.web.chattee.backend.storage.StorageConfig;

public class ChatteeConfig extends OkaeriConfig {

    private int port = 7070;
    private StorageConfig storage = new StorageConfig();

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public StorageConfig getStorage() {
        return this.storage;
    }

    public void setStorage(StorageConfig storage) {
        this.storage = storage;
    }

}
