package io.github.zrdzn.web.chattee.backend.storage;

import net.dzikoysk.cdn.Cdn;

public interface Storage {

    /**
     * An entry point for a data source.
     * It executes in the first place, so it can have some
     * configurations, parsers, default schemas creations etc.
     *
     */
    Storage load(Cdn cdn);

    /**
     * Gets a type for a storage.
     *
     * @return the type for the storage
     */
    StorageType getType();

    /**
     * Used to stop the data source or a connection behind it.
     */
    void stop();

}
