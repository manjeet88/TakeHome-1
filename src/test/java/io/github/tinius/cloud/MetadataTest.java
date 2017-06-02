package io.github.tinius.cloud;

import io.dropwizard.util.Size;
import io.github.tinius.cloud.Metadata.Part;
import oracle.cloud.storage.model.Key;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author ptinius
 */
public class MetadataTest
{
    // this breaks the rule junit test shouldn't read or write directly
    private File file = Paths.get( "src/test/resources/test-data/6mb-ascii-text-file.txt" )
                             .toFile( );
    final UUID uuid = UUID.randomUUID( );
    final String binName = "BinName";

    final Metadata metadata = new Metadata( binName, file, uuid );

    @Test
    public void file( )
        throws Exception
    {
        Assert.assertEquals( file, metadata.file( ) );
    }

    @Test
    public void bin( )
        throws Exception
    {
        Assert.assertEquals( binName, metadata.bin( ) );
    }

    @Test
    public void uuid( )
        throws Exception
    {
        Assert.assertEquals( uuid, metadata.uuid( ) );
    }

    @Test
    public void put_get( )
        throws Exception
    {
        Key key = Key.create( "name",
                              "ctype",
                              new Date( Instant.now( ).getEpochSecond( ) ),
                              Long.MAX_VALUE,
                              "eTag",
                              new HashMap<>( ) );

        // final Key key, final int seqNo, final long start, final long end, final String checksum
        metadata.put( new Part( key,
                                1024,
                                Size.megabytes( 500 ).toBytes( ),
                                Size.gigabytes( 1 ).toBytes( ),
                                "checksum" ) );

        final Map<Integer, Part> parts = metadata.get( );
        Assert.assertTrue( parts.size( ) == 1 );
        parts.forEach( ( segment, part ) ->
                       {
                           Assert.assertEquals( key, part.key( ) );
                           Assert.assertEquals( 1024, part.seqNo( ) );
                           Assert.assertEquals( Size.megabytes( 500 ).toBytes( ), part.start( ) );
                           Assert.assertEquals( Size.gigabytes( 1 ).toBytes( ), part.end( ) );
                           Assert.assertEquals( "checksum", part.checksum( ) );
                       } );
    }
}