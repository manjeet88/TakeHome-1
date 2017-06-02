/*
 * Copyright (c) 2017. Paul E. Tinius
 */

package io.github.tinius.cloud.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/*
 * @author ptinius.
 */
public enum Hasher
{
    MD5( "MD5" ),
    SHA1( "SHA1" ),
    SHA256( "SHA-256" ),
    SHA512( "SHA-512" );

    private String name;

    private static final Logger logger = LoggerFactory.getLogger( Hasher.class );

    Hasher( String name ) { this.name = name; }

    public String getName( ) { return name; }

    /**
     * @param bytes the bytes to create checksum.
     *
     * @return Returns the hex representation of the checksum
     */
    public String checksum( final byte[] bytes )
    {
        if( bytes != null )
        {
            byte[] block = new byte[ bytes.length * 4 ];
            int length;

            try ( final InputStream in = new ByteArrayInputStream( bytes ) )
            {
                MessageDigest digest = MessageDigest.getInstance( getName( ) );
                while ( ( length = in.read( block ) ) > 0 )
                {
                    digest.update( block, 0, length );
                }

                return toHex( digest.digest( ) );
            }
            catch ( NoSuchAlgorithmException | IOException e )
            {
                logger.error( "Checksum failed, reason: {}", e.getMessage( ) );
                logger.trace( "Stack Trace::", e );
            }
        }

        // TODO create a well-known exception, and throw it!
        return null;
    }

    private static String toHex( final byte[] bytes ) { return DatatypeConverter.printHexBinary( bytes ); }
}