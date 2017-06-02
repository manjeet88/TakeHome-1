/*
 * Copyright (c) 2017. Paul E. Tinius
 */

package io.github.tinius;

import io.dropwizard.util.Size;
import io.github.tinius.oracle.takehome.OracleCloudService;
import oracle.cloud.storage.CloudStorageConfig;
import oracle.cloud.storage.CloudStorageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Paths;
import java.util.Objects;

/*
 * @author ptinius.
 */
public class Main
{
    private static final Logger logger = LoggerFactory.getLogger( Main.class );

    private final static String CONTAINER_NAME = "OracleTakeHomeExercise";

    private final static int DEF_PARALLELISM = 5;
    private final static Size DEF_CHUNK_SIZE = Size.megabytes( 1 );

    private final static String DEF_URL = "https://%s.storage.oraclecloud.com";
    private static final String DEF_SERVICE_NAME = "Storage-%s";

    // /Users/ptinius/workspace/TakeHome/src/test/resources/test-data/6mb-ascii-text-file.txt
    private final static File file = Paths.get( "src/test/resources/test-data/6mb-ascii-text-file.txt" )
                                          .toFile( );

    public static void main( String[] args )
        throws Exception
    {
        final CloudStorageConfig config = new CloudStorageConfig( );

        final String username = Objects.requireNonNull( System.getenv( "CLOUD_USERNAME" ),
                                                        "Missing cloud username." );
        final String passwd = Objects.requireNonNull( System.getenv( "CLOUD_PWD" ),
                                                        "Missing cloud user's password." );
        final String domain = Objects.requireNonNull( System.getenv( "CLOUD_DOMAIN" ),
                                                       "Missing cloud identity domain id." );

        if( !file.exists( ) )
        {
            throw new Exception( "The specified file '" + file.toString( ) + "' wasn't found." );
        }

        config.setUsername( username );
        config.setPassword( passwd.toCharArray( ) );
        config.setServiceName( String.format( DEF_SERVICE_NAME, domain ) );
        config.setServiceUrl( String.format( DEF_URL, domain ) );

        final OracleCloudService service  = new OracleCloudService( CloudStorageFactory.getStorage( config ),
                                                                    CONTAINER_NAME,
                                                                    DEF_PARALLELISM,
                                                                    DEF_CHUNK_SIZE );
        logger.trace( "chunk size {} bytes", DEF_CHUNK_SIZE );
        service.createBin( );
        service.putObject( file );

        // TODO do the reassemble

        service.deleteBin( true );
    }
}
