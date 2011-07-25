package com.vsiwest.birdcage;

/**
 * represent atom-like com.vsiwest.dws.com.vsiwest.dws.com.vsiwest.dws.structures inside
 * the meta atom, designed to work with the byte array
 * of the meta atom (ie, just wraps pointers to the
 * beginning of the atom and its computed size and type)
 */
final class PseudoAtomPointer {
    final int offset;
    final int atomSize;
    final int type;

    public PseudoAtomPointer(int o, int s, int t) {
        offset = o;
        atomSize = s;
        type = t;
    }

}
