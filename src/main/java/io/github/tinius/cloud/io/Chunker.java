package io.github.tinius.cloud.io;

import io.dropwizard.util.Size;
import io.github.tinius.cloud.Metadata;
import io.github.tinius.cloud.util.DiscreteTypeUtil;
import io.github.tinius.cloud.util.Hasher;
import oracle.cloud.storage.CloudStorage;
import oracle.cloud.storage.model.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/*
 * @author ptinius.
 */
public class Chunker
{
    private static final Logger logger = LoggerFactory.getLogger( Chunker.class );

    private final File file;
    private final String bin;
    private final String contentType;
    private final CloudStorage storage;

    private Metadata metadata;

    /**
     * @param bin the bin where file segments are placed.
     * @param file the {@link File} representing the segments ( chunks )
     * @param storage the cloud backend storage api
     */
    public Chunker( final String bin, final File file, final CloudStorage storage )
        throws IOException
    {
        this.bin = bin;
        this.file = file;
        this.contentType = new DiscreteTypeUtil().getMimeType( file );
        this.storage = storage;

        this.metadata = new Metadata( bin,  file, UUID.randomUUID( ) );
    }

    /**
     * @param noOfThreads the number of threads
     * @param chunkSize the chunk size
     *
     * @throws Exception if interrupted or if executor errors occurs
     */
    public void processAll( final int noOfThreads, final Size chunkSize )
        throws Exception
    {
        logger.trace( "# threads {} chunk size {} bytes", noOfThreads, chunkSize );

        int count = ( int ) ( ( file.length( ) + chunkSize.toBytes( ) - 1 ) / chunkSize.toBytes( ) );

        List<Callable<Metadata.Segment>> tasks = new ArrayList<>( count );
        for ( int i = 0; i < count; i++ )
        {
            tasks.add( processPartTask( i, i * chunkSize.toBytes( ),
                                        Math.min( file.length( ), ( i + 1 ) * chunkSize.toBytes( ) ) ) );
        }

        ExecutorService es = Executors.newFixedThreadPool( noOfThreads );

        List<Future<Metadata.Segment>> results = es.invokeAll( tasks );

        es.shutdown( );

        // use the results for something
        for ( Future<Metadata.Segment> result : results )
        {
            if( result != null && result.get( ) != null )
            {
                final Metadata.Segment segment = result.get( );
                if ( segment != null )
                {
                    metadata.put( segment );
                }
            }
        }
    }

    private Metadata.Segment processPart( final int sequence, final long start, final long end )
    {
        Key key = null;
        try( final SeekableByteChannel sbc = Files.newByteChannel( Paths.get( file.toURI( ) ),
                                                                   EnumSet.of( StandardOpenOption.READ ) ) )
        {
            final UUID uuid = UUID.randomUUID( );


            final int newStart = ( int ) ( start == 0L ? start : start + 1L );
            final ByteBuffer buffer = ByteBuffer.allocate( ( int ) ( end - newStart ) );

            logger.trace( "Computing sequence# {} from {} to {}", sequence, newStart, end );

            buffer.clear( );
            sbc.position( newStart );

            if( sbc.read( buffer ) > 0 )
            {
                buffer.flip( );

                key = storage.storeObject( bin,
                                           uuid.toString( ),
                                           contentType,
                                           new ByteArrayInputStream( buffer.array( ) ) );
            }

            logger.trace( "Finished sequence# {} from {} to {}", sequence, newStart, end );

            return new Metadata.Segment( key, sequence, start, end, Hasher.MD5.checksum( buffer.array( ) ) );
        }
        catch ( IOException e )
        {
            logger.error( "Failed processing sequence {} for offset {} and length of {}, reason: {}",
                          sequence, start, end, e.getMessage( ) );
            logger.trace( "Stack Trace::", e );
        }

        return null;
    }

    private Callable<Metadata.Segment> processPartTask( final int sequence, final long start, final long end )
    {
        return ( ) -> processPart( sequence, start, end );
    }
}
