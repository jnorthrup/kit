package com.vsiwest.util;

import java.io.*;
import java.text.*;

/**
 * (c) Copyright 2011 vsiwest, Inc.  All rights reserved.
 * Confidential Information.  Do not copy or distribute without express written permission.
 */
final public class Pair<TFirst, TSecond> implements Serializable {

    public TFirst first;
    public TSecond second;


    public Pair(final TFirst first, final TSecond second) {
        this.first = first;
        this.second = second;
    }

    public TFirst getFirst() {
        return first;
    }

    public TSecond getSecond() {
        return second;
    }

    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Pair)) return false;

        final Pair pair = (Pair) o;

        // "null == first == pair.first" is dissallowed.
        // so... unboxing implcitly provides this as well as natives
        return (first == pair.first || first.equals(pair.first)) && (second == pair.second || second.equals(pair.second));

    }

    public int hashCode() {
        int result;
        result = (first != null ? first.hashCode() : 0);
        result = 29 * result + (second != null ? second.hashCode() : 0);
        return result;
    }

    public String toString() {
        return MessageFormat.format("[Pair first={0}, second={1}]", first, second);
    }

}
