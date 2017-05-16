/*
 * Copyright (c) 2017. Paul E. Tinius
 */

package io.github.tinius.cloud;

import io.dropwizard.util.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * @author ptinius.
 */
public abstract class CloudService
    implements CloudServiceApi
{
    protected final Logger logger = LoggerFactory.getLogger( getClass() );

    private final String bin;
    private final Size chunk;
    private final int parallelism;

    /**
     * @param bin the name of the bin that will be used to by the storage engine to put/get objects
     * @param parallelism the number of parallel segments to transfer at one time
     * @param chunk the size of the chunks ( maximum object size per segment )
     */
    public CloudService( final String bin, final int parallelism, final Size chunk )
    {
        this.bin = bin;
        this.chunk = chunk;
        this.parallelism = parallelism;
    }

    /**
     * @return Returns teh storage bin
     */
    protected String getBin( ) { return bin; }

    /**
     * @return Returns the size of the chunks
     */
    protected Size getChunk( ) { return chunk; }

    protected int getParallelism( ) { return parallelism; }
}
