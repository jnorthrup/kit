package com.vsiwest.kit.MetaRef;

import com.vsiwest.kit.MD_KEYS;
import static com.vsiwest.kit.MD_KEYS.*;

import java.util.EnumMap;

/**
 * (c) Copyright 2006 vsiwest, Inc.  All rights reserved.
 * Confidential Information.  Do not copy or distribute without express written permission.
 */
public abstract class MdRefImpl implements MetaDataRef {
    private EnumMap<MD_KEYS, String> metadata = new EnumMap<MD_KEYS, String>(MD_KEYS.class);

    public MdRefImpl(EnumMap<MD_KEYS, String> metadata) {
        this.setMetadata(metadata);
    }

    public MdRefImpl() {
    }

    public String getPCol() {
        return getMetadata().get(PKCOLUMN_NAME);
    }

    public String getPTable() {
        return getMetadata().get(PKTABLE_NAME);
    }

    public String getFCol() {
        return getMetadata().get(FKCOLUMN_NAME);
    }

    public String getFTable() {
        return getMetadata().get(FKTABLE_NAME);
    }

    public EnumMap<MD_KEYS, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(EnumMap<MD_KEYS, String> metadata) {
        this.metadata = metadata;
    }
}
