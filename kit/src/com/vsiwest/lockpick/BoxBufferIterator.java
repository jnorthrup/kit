package com.vsiwest.lockpick;

import com.vsiwest.kit.Kit;
import com.vsiwest.util.Pair;

import java.nio.ByteBuffer;
import java.text.MessageFormat;
import java.util.Iterator;

/**
 * this is the iterator that does the next() operations.
 */
class BoxBufferIterator implements Iterator<Pair<Integer, ByteBuffer>> {
    private BoxFileModel boxFileModel;

    public BoxBufferIterator(BoxFileModel boxFileModel) {
        this.boxFileModel = boxFileModel;
    }

    public boolean hasNext() {
        final ByteBuffer chunk = boxFileModel.getChunk();
        return (chunk != null) && ((chunk.limit() - chunk.position()) > 7);
    }

    public Pair<Integer, ByteBuffer> next() {
        final ByteBuffer box = boxFileModel.getChunk().slice();
        int chunkLen = box.getInt();
        int ctype = 0;

        switch (chunkLen) {
            /**
             * on 0, mp4 will use a c string and quicktime format will use a pascal string.
             */
            case 0: {
                ctype = box.getInt();
                if (boxFileModel.quicktime) {
                    /** pascal string
                     */
                    chunkLen = box.get();
                } else {
                    final ByteBuffer traversal = box.slice();
                    while (traversal.hasRemaining() && traversal.get() != 0)
                        ++chunkLen;
                }
                break;
            }

            /**
             * indicates a Long 64 bit chunklen
             */
            case 1:
                chunkLen = (int) box.getLong();
            default:
                try {
                    ctype = box.getInt();
                } catch (java.nio.BufferUnderflowException e) {
                    System.out.println(MessageFormat.format("--- mmap underrun and closure at {0}", box.position()));
                }
                break;
        }

        final int pos = boxFileModel.getChunk().position();
        int proposedSeek = pos + chunkLen;
        try {
            final int lim = boxFileModel.getChunk().limit();
            if (proposedSeek < 0 || proposedSeek > lim) {
                Kit.getLogger().severe(MessageFormat.format("Chunk at Position {0} is attempting to seek to {1} against limit of {2}", boxFileModel.getChunk().position(), proposedSeek, lim));
                proposedSeek = lim;
            }

            boxFileModel.getChunk().mark();
            boxFileModel.getChunk().position(proposedSeek).position();
            return new Pair<Integer, ByteBuffer>(ctype, (ByteBuffer) box.slice().limit(chunkLen - box.position()));
        } catch (RuntimeException e) {
            Kit.log("blorp!");
        }
        return null;
    }

    public void remove() {
    }


}
