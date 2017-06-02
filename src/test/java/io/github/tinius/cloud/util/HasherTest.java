/*
 * Copyright (c) 2017. Paul E. Tinius
 */

package io.github.tinius.cloud.util;

import org.junit.Assert;
import org.junit.Test;

/*
 * @author ptinius.
 */
public class HasherTest
{
    final byte[] hashBytes = "This is a test String".getBytes( );

    @Test
    public void getNameMD5( )
        throws Exception
    {
        Assert.assertEquals( Hasher.MD5.getName( ), "MD5" );
    }

    @Test
    public void getNameSHA1( )
        throws Exception
    {
        Assert.assertEquals( Hasher.SHA1.getName( ), "SHA1" );
    }

    @Test
    public void getNameSHA256( )
        throws Exception
    {
        Assert.assertEquals( Hasher.SHA256.getName( ), "SHA-256" );
    }

    @Test
    public void getNameSHA512( )
        throws Exception
    {
        Assert.assertEquals( Hasher.SHA512.getName( ), "SHA-512" );
    }

    @Test
    public void checksumMD5( )
        throws Exception
    {
        // TODO come up with a better set of validation test
        Assert.assertNotNull( Hasher.MD5.checksum( hashBytes ) );
    }

    @Test
    public void checksumSHA1( )
        throws Exception
    {
        // TODO come up with a better set of validation test
        Assert.assertNotNull( Hasher.SHA1.checksum( hashBytes ) );
    }

    @Test
    public void checksumSHA256( )
        throws Exception
    {
        // TODO come up with a better set of validation test
        Assert.assertNotNull( Hasher.SHA256.checksum( hashBytes ) );
    }

    @Test
    public void checksumSHA512( )
        throws Exception
    {
        // TODO come up with a better set of validation test
        Assert.assertNotNull( Hasher.SHA512.checksum( hashBytes ) );
    }
}