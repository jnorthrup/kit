package com.vsiwest.kit.MetaRef;

import com.vsiwest.kit.MD_KEYS;

import java.util.EnumMap;

/**
 * (c) Copyright 2006 vsiwest, Inc.  All rights reserved.
 * Confidential Information.  Do not copy or distribute without express written permission.
 */
public final class OutboundRef extends MdRefImpl {
    public OutboundRef(EnumMap<MD_KEYS, String> metadata) {
        super(metadata);
    }

    public Object[] getFormulaEntry() {
        return new String[]{getFTable() + '.' + getFCol(), '=' + getJoinTarget()};
    }


    public String toString() {
        return getPCol() + "<-" + getFTable() + '.' + getFCol();
    }

    private String getJoinTarget() {
        return getPTable() + '.' + getPCol();
    }

}
