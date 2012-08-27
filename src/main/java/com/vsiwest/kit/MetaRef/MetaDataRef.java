package com.vsiwest.kit.MetaRef;

import com.vsiwest.kit.MD_KEYS;

import java.util.EnumMap;

/**
 * Property of vsiwest
 * User: jim
 * Date: Jul 24, 2007
 * Time: 9:34:32 AM
 */
public interface MetaDataRef {
    String getPCol();

    String getPTable();

    String getFCol();

    String getFTable();

    Object[] getFormulaEntry();

    EnumMap<MD_KEYS, String> getMetadata();

    void setMetadata(EnumMap<MD_KEYS, String> metadata);
}
