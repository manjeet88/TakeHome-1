/*
 * Copyright (c) 2017. Paul E. Tinius
 */

package io.github.tinius.cloud.io;

import io.github.tinius.cloud.Metadata;
import io.github.tinius.cloud.Metadata.Part;
import io.github.tinius.cloud.Segment;
import oracle.cloud.storage.CloudStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author ptinius
 */
public class Assembler
{
    private static final Logger logger = LoggerFactory.getLogger( Chunker.class );

    private final File file;
    private final String bin;
    private final CloudStorage storage;

    /**
     * @param bin the bin where file segments are placed.
     * @param file the {@link File} representing the segments ( chunks )
     * @param storage the cloud backend storage api
     */
    public Assembler( final String bin, final File file, final CloudStorage storage )
        throws IOException
    {
        this.bin = bin;
        this.file = file;
        this.storage = storage;

        if( file.exists( ) )
        {
            throw new FileAlreadyExistsException( "The specified file " + file.toURI( ) + " already exists." );
        }
    }

    /**
     * @param noOfThreads the number of threads
     * @param metadata the metadata of the object
     *
     * @throws Exception if interrupted or if executor errors occurs
     */
    public void assemble( final int noOfThreads, final Metadata metadata )
            throws Exception
    {
        logger.trace( "# threads {} metadata part # {}", noOfThreads, metadata.get( ).size( ) );

        List<Callable<Segment>> tasks = new ArrayList<>( metadata.get( ).size( ) );
        for ( final Map.Entry<Integer, Part > segments : metadata.get( ).entrySet( ) )
        {
            tasks.add( task( segments.getKey( ), segments.getValue( ) ) );
        }

        ExecutorService es = Executors.newFixedThreadPool( noOfThreads );

        List<Future<Segment> > results = es.invokeAll( tasks );

        es.shutdown( );

        // use the results for something
        for ( Future<Segment> result : results )
        {
            if( result != null && result.get( ) != null )
            {
                // TODO reassemble
            }
        }
    }

    private Segment doAssemble( final Integer seqNo, final Part segment )
    {
        return null;
    }

    private Callable<Segment> task( final Integer seqNo, final Part segment )
    {
        return ( ) -> doAssemble( seqNo, segment );
    }
}
