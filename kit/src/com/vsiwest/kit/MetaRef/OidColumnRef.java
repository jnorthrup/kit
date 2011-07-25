package com.vsiwest.kit.MetaRef;

/**
 * (c) Copyright 2006 vsiwest, Inc.  All rights reserved.
 * Confidential Information.  Do not copy or distribute without express written permission.
 */
public final class OidColumnRef extends MdRefImpl {
    private final String table_name;
    private static final String OID = "oid";

    public OidColumnRef(String tableName) {
        this.table_name = tableName;
    }

    private static String getColumn_name() {
        return OID;
    }

    public String toString() {
        return OID;
    }

    public Object[] getFormulaEntry() {
        return new Object[]{table_name + '.' + OID};
    }
}
