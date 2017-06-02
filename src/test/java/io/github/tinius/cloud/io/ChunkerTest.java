/*
 * Copyright (c) 2017. Paul E. Tinius
 */

package io.github.tinius.cloud.io;

import io.dropwizard.util.Size;
import io.github.tinius.cloud.util.DiscreteTypeUtil;
import oracle.cloud.storage.CloudStorage;
import oracle.cloud.storage.model.Key;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

/*
 * @author ptinius.
 */
public class ChunkerTest
{
    private static final String BIN = "dummy-bin";

    private CloudStorage cloudStorage = mock( CloudStorage.class );

    // this breaks the rule junit test shouldn't read or write directly
    private File file = Paths.get( "src/test/resources/test-data/6mb-ascii-text-file.txt" )
                             .toFile( );

    private Chunker chunker;

    @Before
    public void setUp( )
        throws Exception
    {
        chunker = new Chunker( BIN, file, cloudStorage );

        final String contentType = new DiscreteTypeUtil().getMimeType( file );

        when( cloudStorage.storeObject( any( String.class ),
                                        any( String.class ),
                                        any( String.class ),
                                        any( ByteArrayInputStream.class ) ) )
            .thenReturn( Key.create( file.getName( ),
                                     contentType,
                                     new Date( Files.getLastModifiedTime( Paths.get( file.toURI( ) ) ).toMillis( ) ),
                                     Files.size( Paths.get( file.toURI( ) ) ),
                                     "ETag-" + file.getName( ) + "-ETag",
                                     new HashMap<>( ) ) );
    }

    @After
    public void tearDown( )
        throws Exception
    {
        reset( cloudStorage );
    }

    @Test
    public void chunk( )
        throws Exception
    {
        chunker.chunk( 5, Size.kilobytes( 1 ) );
    }
}