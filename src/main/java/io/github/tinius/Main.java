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
import java.util.UUID;

/*
 * @author ptinius.
 */
public class Main
{
    private static final Logger logger = LoggerFactory.getLogger( Main.class );

    private final static String CONTAINER_NAME = "OracleTakeHomeExercise";

    private final static int DEF_PARALLELISM = 5;
    private final static Size DEF_CHUNK_SIZE = Size.kilobytes( 1 );

    // https://a478936.storage.oraclecloud.com/v1/Storage-a478936 ( REST endpoint )
    // https://a478936.storage.oraclecloud.com/auth/v1.0 ( Auth V1 Endpoint )

    //                                     https://<Identity Domain Name>.storage.oraclecloud.com
    private final static String DEF_URL = "https://%s.storage.oraclecloud.com";
    private static final String DEF_SERVICE_NAME = "Storage-%s";

    private final static File file = Paths.get( "/Users/ptinius/Pictures/f01fc8ca9ffa0c9efd764ed3406d77f8.jpg" )
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

        config.setUsername( username );
        config.setPassword( passwd.toCharArray( ) );
        config.setServiceName( String.format( DEF_SERVICE_NAME, domain ) );
        config.setServiceUrl( String.format( DEF_URL, domain ) );

        final OracleCloudService service  = new OracleCloudService( CloudStorageFactory.getStorage( config ),
                                                                    CONTAINER_NAME,
                                                                    DEF_PARALLELISM,
                                                                    DEF_CHUNK_SIZE );
        logger.trace( "chunk size {} bytes", DEF_CHUNK_SIZE );
        final UUID uuid = UUID.randomUUID();
        service.createBin( );
        service.putObject( uuid, file );
    }
}
