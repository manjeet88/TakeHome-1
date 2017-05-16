/*
 * Copyright (c) 2017. Paul E. Tinius
 */

package io.github.tinius.oracle.takehome;

import com.google.common.base.Preconditions;
import io.dropwizard.util.Size;
import io.github.tinius.cloud.CloudService;
import io.github.tinius.cloud.Metadata;
import io.github.tinius.cloud.Metadata.Part;
import io.github.tinius.cloud.io.Chunker;
import oracle.cloud.storage.CloudStorage;
import oracle.cloud.storage.model.Container;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/*
 * @author ptinius.
 */
public class OracleCloudService
    extends CloudService
{
    private final CloudStorage backend;

    private Container container;
    private Metadata metadata;

    /**
     * @param backend the backend cloud storage engine
     * @param bin the name of the bin that will be used to by the storage engine to put/get objects
     * @param parallelism the number of parallel segments to transfer at one time
     * @param chunk the size of the chunks ( maximum object size per segment )
     */
    public OracleCloudService( final CloudStorage backend, final String bin, final int parallelism, final Size chunk )
    {
        super( bin, parallelism, chunk );
        this.backend = backend;
    }

    @Override
    public void createBin( )
    {
        container = backend.createContainer( getBin( ) );
        logger.trace( "Container::{}", container.getName( ) );
    }

    /**
     * @param uuid the unique identifier
     * @param file the {@link File} to be put
     */
    @Override
    public void putObject( final UUID uuid, final File file )
    {
        Preconditions.checkNotNull( uuid, "Must supply valid 'uuid' argument." );
        Preconditions.checkNotNull( file, "Must supply valid 'file' argument." );

        try
        {
            final Chunker chunker = new Chunker( getBin( ), file, backend );
            chunker.chunk( getParallelism( ), getChunk( ) );
            metadata = chunker.metadata( );

            logger.trace( "CONTAINER::{}:{}:{}",
                          container.getName( ),
                          Size.megabytes( container.getSize( ) ),
                          container.getCount( ) );
        }
        // TODO create a well-known exception
        catch ( Exception e )
        {
            logger.error( "Put failed for {}:{}", getBin( ), file.getName( ), e.getMessage( ) );
            logger.trace( "Stack Trace::", e );
        }
    }

    /**
     * @return Returns the metadata
     */
    public Metadata metadata( ) { return metadata; }

    /**
     * @param uuid the unique identifier for the stream being retrieved
     *
     * @return Returns the {@link FileInputStream}
     */
    @Override
    public File getObject( final UUID uuid )
    {
        if( metadata != null && !metadata.get( ).isEmpty( ) )
        {
            final Map<Integer,Part > segment = metadata.get( );
            for( final Map.Entry<Integer,Part > entry :  segment.entrySet( ) )
            {
                System.out.println( backend.retrieveObject( metadata().bin( ), entry.getValue( ).key( ).getKey( ) ) );
            }
        }

        return null;
    }

    @Override
    public void deleteBin( final boolean recursive )
    {
        if( recursive )
        {
            if( container == null )
            {
                final Optional<Container> optional = backend.listContainers( )
                                                            .stream( )
                                                            .filter( c -> c.getName( ).equals( getBin( ) ) )
                                                            .findFirst( );
                optional.ifPresent( c -> container = c );
            }

            if( container != null )
            {
                backend.listObjects( container.getName( ), null )
                       .forEach( o -> backend.deleteObject( container.getName( ), o.getKey( ) ) );
            }
        }

        if( container != null )
        {
            backend.deleteContainer( container.getName( ) );
        }
    }
}
