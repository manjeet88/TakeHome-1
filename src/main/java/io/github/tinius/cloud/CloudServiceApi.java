package io.github.tinius.cloud;

import java.io.File;
import java.io.FileInputStream;
import java.util.UUID;

/*
 * @author ptinius.
 */
public interface CloudServiceApi
{
    void createBin( );

    /**
     * @param uuid the unique identifier for the stream being put
     * @param file the {@link File} to be put
     */
    void putObject( final UUID uuid, final File file );

    /**
     * @param uuid the unique identifier for the stream being retrieved
     *
     * @return Returns the {@link FileInputStream}
     */
    FileInputStream getObject( final UUID uuid );
}
