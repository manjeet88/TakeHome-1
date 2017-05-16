/*
 * Copyright (c) 2017. Paul E. Tinius
 */

package io.github.tinius.cloud;

import java.io.File;
import java.util.UUID;

/*
 * @author ptinius.
 */
public interface CloudServiceApi
{
    /**
     * create a bin
     */
    void createBin( );

    /**
     * @param uuid the unique identifier for the stream being put
     * @param file the {@link File} to be put
     */
    void putObject( final UUID uuid, final File file );

    /**
     * @param uuid the unique identifier for the stream being retrieved
     *
     * @return Returns the {@link File}
     */
    File getObject( final UUID uuid );

    /**
     * delete bin
     */
    default void deleteBin( ) { deleteBin( false ) ; }

    /**
     * @param recursive should sub-objects of teh bin be removed
     */
    void deleteBin( final boolean recursive );
}
