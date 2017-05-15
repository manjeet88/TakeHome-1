package io.github.tinius.cloud;

import oracle.cloud.storage.model.Key;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/*
 * @author ptinius.
 */
public class Metadata
{
    private final String bin;
    private final File file;
    private final UUID uuid;
    private final Map<Integer, Segment> segments;

    /**
     * @param bin the bin where file segments are placed.
     * @param file the {@link File} representing the segments ( chunks )
     * @param uuid the {@link UUID} used to look up {@link File} {@link Metadata}
     */
    public Metadata( final String bin, final File file, final UUID uuid )
    {
        this.bin = bin;
        this.file = file;
        this.uuid = uuid;

        this.segments = new HashMap<>( );
    }

    /**
     * @return Returns the {@link File}
     */
    public File file( ) { return this.file; }

    /**
     * @return Returns the bin where file segments are placed.
     */
    public String bin( ) { return this.bin; }

    /**
     * @return Returns the {@link UUID} of the metadata representing {@link #file()}
     */
    public UUID uuid( ) { return this.uuid; }

    /**
     * @param segment the segment
     */
    public void put( final Segment segment ) { segments.put( segment.seqNo( ), segment ); }

    public static final class Segment
    {
        private final Key key;
        private final String checksum;
        private final int seqNo;
        private final long start;
        private final long end;

        public Segment( final Key key, final int seqNo, final long start, final long end, final String checksum )
        {
            this.key = key;
            this.seqNo = seqNo;
            this.start = start;
            this.end = end;
            this.checksum = checksum;
        }

        public String checksum( ) { return this.checksum; }
        public Key key( ) { return key; }
        public int seqNo( ) { return seqNo; }
        public long start( ) { return start; }
        private long end( ) { return end; }
    }
}
