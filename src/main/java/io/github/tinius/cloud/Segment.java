/*
 * Copyright (c) 2017. Paul E. Tinius
 */

package io.github.tinius.cloud;

import java.nio.ByteBuffer;

/**
 * @author ptinius
 */
public class Segment
{
    private final int seqNo;
    private final long start;
    private final long end;
    private final ByteBuffer buffer;

    public Segment( final int seqNo, final long start, final long end, final ByteBuffer buffer )
    {
        this.seqNo = seqNo;
        this.start = start;
        this.end = end;
        this.buffer = buffer;
    }

    /**
     * @return Returns the value represented by seqNo
     */
    public int seqNo( ) { return seqNo; }

    /**
     * @return Returns the value represented by start
     */
    public long start( ) { return start; }

    /**
     * @return Returns the value represented by end
     */
    public long end( ) { return end; }

    /**
     * @return Returns the value represented by buffer
     */
    public ByteBuffer buffer( ) { return buffer; }
}
