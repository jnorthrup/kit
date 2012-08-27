/*
 * Property of vsiwest
 * User: jim
 * Date: May 30, 2007
 * Time: 2:03:50 AM
 */

package com.vsiwest.lockpick;

import com.vsiwest.kit.*;
import com.vsiwest.util.*;

import java.io.*;
import static java.lang.System.*;
import java.nio.*;
import static java.nio.ByteOrder.*;
import java.nio.channels.*;
import static java.nio.channels.FileChannel.MapMode.*;
import static java.text.MessageFormat.*;
import java.util.*;

//


/**
 * opens a mp4/quicktime box and provides iterators to the chunk info
 */
public final class BoxFileModel implements Iterable<Pair<Integer, ByteBuffer>> {

    boolean quicktime = false;
    private final ByteBuffer chunk;

    /**
     * parent provides the access to a stack of mark() offsets providing the getter for
     * "offset" to translate absolutes from the parent-most chunk
     */
    private BoxFileModel parent;

    /**
     * convenience ctor
     *
     * @param selectedFile gets mapped to DirectByteBuffer
     * @throws java.io.IOException thrown for bad IO
     */
    public BoxFileModel(final File selectedFile) throws IOException {
        this(new RandomAccessFile(selectedFile, "r").getChannel().map(READ_ONLY, 0, selectedFile.length()), null);
    }

    public BoxFileModel(final ByteBuffer chunk, final BoxFileModel parent) {
        this.chunk = chunk;
        this.setParent(parent);
        chunk.order(BIG_ENDIAN);
    }

    void printChunkHeader(final Pair<Integer, ByteBuffer> blobHandle) {
        final Integer ctype = blobHandle.first;
        final ByteBuffer cdata = blobHandle.second;
        final int len = cdata.limit();
        final String cname = cString((ctype));
        out.println(format("chunk: {0} / {1} / {2} pos: {3} len: {4} ", cname, ctype, Integer.toHexString(ctype), parent == null ? 0 : parent.getOffset(), len));
    }

    public static String cString(final int printme) {
        if (printme == 0) return "0";
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.asIntBuffer().put(printme);
        return cString(buffer);
    }

    public static String cString(final ByteBuffer buffer) {
        final int clen = buffer.limit();
        if (!buffer.hasRemaining()) buffer.rewind();

        final ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            Channels.newChannel(bos).write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        final String x = bos.toString();
        Kit.getLogger().finest(format("{0}:{1}", clen, x));
        return x;
    }


    public Iterator<Pair<Integer, ByteBuffer>> iterator() {
        chunk.mark();
        return new BoxBufferIterator(this);
    }

    public long getOffset() {
        int mark = 0;
        try {
            mark = getChunk().duplicate().reset().position();
        } catch (Exception e) {
        }
        return getParent() != null ? getParent().getOffset() + mark : mark;
    }

    public ByteBuffer getChunk() {
        return chunk;
    }

    public BoxFileModel getParent() {
        return parent;
    }

    public void setParent(BoxFileModel parent) {
        this.parent = parent;
    }
}