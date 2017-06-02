/*
 * Copyright (c) 2017. Paul E. Tinius
 */

package io.github.tinius.cloud;

import java.io.File;

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
     * @param file the {@link File} to be put
     */
    void putObject( final File file );

    /**
     * @param file the {@link File} to be populated
     *
     * @return Returns the {@link File}
     */
    File getObject( final File file );

    /**
     * delete bin
     */
    default void deleteBin( ) { deleteBin( false ) ; }

    /**
     * @param recursive should sub-objects of teh bin be removed
     */
    void deleteBin( final boolean recursive );
}
