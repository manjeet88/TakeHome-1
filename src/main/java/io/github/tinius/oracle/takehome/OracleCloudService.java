/*
 * Copyright (c) 2017. Paul E. Tinius
 */

package io.github.tinius.oracle.takehome;

import com.google.common.base.Preconditions;
import io.dropwizard.util.Size;
import io.github.tinius.cloud.CloudService;
import io.github.tinius.cloud.Metadata;
import io.github.tinius.cloud.io.Assembler;
import io.github.tinius.cloud.io.Chunker;
import oracle.cloud.storage.CloudStorage;
import oracle.cloud.storage.model.Container;

import java.io.File;
import java.io.FileInputStream;
import java.util.Optional;

/*
 * @author ptinius.
 */
public class OracleCloudService
    extends CloudService
{
    private final CloudStorage backend;

    private Container container;

    /*
     * For simplicity this is a in-memory structure, ideally this would be keeps in a database ( Sql or NoSql )
     * and would be indexed by the bin and file names.
     */
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
     * @param file the {@link File} to be put
     */
    @Override
    public void putObject( final File file )
    {
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
     * @param file the {@link File} to be populated
     *
     * @return Returns the {@link FileInputStream}
     */
    @Override
    public File getObject( final File file )
    {
        if( metadata != null && !metadata.get( ).isEmpty( ) )
        {
            try
            {
                final Assembler assembler = new Assembler( getBin( ), file, backend );
                assembler.assemble( getParallelism( ), metadata( ) );
                logger.trace( "CONTAINER::{}:{}:{}",
                              container.getName( ),
                              Size.megabytes( container.getSize( ) ),
                              container.getCount( ) );
                return file;
            }
            // TODO create a well-known exception
            catch ( Exception e )
            {
                logger.error( "Put failed for {}:{}", getBin( ), file.getName( ), e.getMessage( ) );
                logger.trace( "Stack Trace::", e );
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
