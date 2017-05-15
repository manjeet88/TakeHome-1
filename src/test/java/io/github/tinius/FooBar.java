package io.github.tinius;

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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/*
 * @author ptinius.
 */
public class FooBar
{
    private File file;

    public FooBar( File file )
    {
        this.file = file;
    }

    public String processPart( final int sequence, long start, long end )
    {
        try( final SeekableByteChannel sbc = Files.newByteChannel( Paths.get( file.toURI( ) ),
                                                                   EnumSet.of( StandardOpenOption.READ) ) )
        {
            final int newStart = ( int ) ( start == 0L ? start : start + 1L );
            System.out.println( "Computing sequence# " + sequence + " from " + newStart + " to " + end );
            final ByteBuffer buffer = ByteBuffer.allocate( ( int ) ( end - newStart ) );

            buffer.clear( );
            sbc.position( newStart );

            if( sbc.read( buffer ) > 0 )
            {
                buffer.flip( );

//                try
//                {
//                    Thread.sleep( 1000 );
//                }
//                catch ( InterruptedException e )
//                {
//                    e.printStackTrace( );
//                }

                System.out.println( "Finished  sequence# " + sequence + " from " + newStart + " to " + end );
            }
        }
        catch ( IOException e )
        {
            e.printStackTrace( );
        }

        return "Some result";
    }

    public Callable<String> processPartTask( final int sequence, final long start, final long end )
    {
        return ( ) -> processPart( sequence, start, end );
    }

    public void processAll( int noOfThreads, int chunkSize )
        throws Exception
    {
        System.out.printf( "# threads %d chunk size %d bytes%n", noOfThreads, chunkSize );
        int count = ( int ) ( ( file.length( ) + chunkSize - 1 ) / chunkSize );
        java.util.List<Callable<String>> tasks = new ArrayList<>( count );
        for ( int i = 0; i < count; i++ )
        {
            tasks.add( processPartTask( i,i * chunkSize, Math.min( file.length( ), ( i + 1 ) * chunkSize ) ) );
        }

        ExecutorService es = Executors.newFixedThreadPool( noOfThreads );
        List<Future<String>> results = es.invokeAll( tasks );
        es.shutdown( );

        // use the results for something
        for ( Future<String> result : results )
        {
            System.out.println( result.get( ) );
        }
    }

    public static void main( String argv[] )
        throws Exception
    {
        FooBar s = new FooBar( new File( "src/test/resources/test-data/6mb-ascii-text-file.txt" ) );
        s.processAll( 8, 1024 );
    }
}
