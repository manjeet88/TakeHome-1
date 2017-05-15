package io.github.tinius.oracle.takehome;

import com.google.common.base.Preconditions;
import io.dropwizard.util.Size;
import io.github.tinius.cloud.CloudService;
import io.github.tinius.cloud.io.Chunker;
import oracle.cloud.storage.CloudStorage;
import oracle.cloud.storage.model.Container;

import java.io.File;
import java.io.FileInputStream;
import java.util.UUID;

/*
 * @author ptinius.
 */
public class OracleCloudService
    extends CloudService
{
    private final CloudStorage backend;

    private Container container;

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
        logger.trace( "Container::{}", container );
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
            chunker.processAll( getParallelism( ), getChunk( ) );
        }
        // TODO create a well-known exception
        catch ( Exception e )
        {
            logger.error( "Put failed for {}:{}", getBin( ), file.getName( ), e.getMessage( ) );
            logger.trace( "Stack Trace::", e );
        }
    }

    /**
     * @param uuid the unique identifier for the stream being retrieved
     *
     * @return Returns the {@link FileInputStream}
     */
    @Override
    public FileInputStream getObject( final UUID uuid )
    {
        return null;
    }
}
