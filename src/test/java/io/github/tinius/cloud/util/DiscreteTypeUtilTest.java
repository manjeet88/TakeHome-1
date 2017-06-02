package io.github.tinius.cloud.util;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.nio.file.Paths;

/**
 * @author ptinius
 */
public class DiscreteTypeUtilTest
{
    private final static File file = Paths.get( "src/test/resources/test-data/6mb-ascii-text-file.txt" )
                                          .toFile( );

    @Test
    public void getMimeTypeFile( )
        throws Exception
    {
        Assert.assertEquals( "text/plain", new DiscreteTypeUtil( ).getMimeType( file ) );
    }

    @Test
    public void getMimeTypeWithFilename( )
        throws Exception
    {
        Assert.assertEquals( "text/plain", new DiscreteTypeUtil( ).getMimeType( file.getName( ) ) );

    }
}