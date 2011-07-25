package com.vsiwest.util;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.IntBuffer;

/**
 * Property of vsiwest
 * User: jim
 * Date: Jun 20, 2007
 * Time: 10:54:55 PM
 */
class NioUtil {
    static IntBuffer createDirectBuffer(int[] srcs) {
        final IntBuffer buffer = IntBuffer.wrap(srcs);
        return ByteBuffer.allocateDirect(4 * srcs.length).asIntBuffer().put(buffer);
    }

    static CharBuffer createDirectBuffer(char[] srcs) {
        final CharBuffer charBuffer = CharBuffer.wrap(srcs);
        return ByteBuffer.allocateDirect(2 * srcs.length).asCharBuffer().put(charBuffer);
    }

    static ByteBuffer createDirectBuffer(byte[] srcs) {
        return ByteBuffer.allocateDirect(srcs.length).put(ByteBuffer.wrap(srcs));
    }
}
