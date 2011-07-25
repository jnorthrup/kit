package com.vsiwest.kit.view;

/**
 * (c) Copyright 2011 vsiwest, Inc.  All rights reserved.
 * Confidential Information.  Do not copy or distribute without express written permission.
 */
enum FormulaColumn {
    col,
    where,
    as,
    function,
    memo,
    visible(Boolean.class),
    order_by(Boolean.class),
    ascending(Boolean.class),
    group_by(Boolean.class);

    final Class clazz;

    FormulaColumn() {
        clazz = String.class;
    }

    FormulaColumn(Class clz) {
        this.clazz = clz;
    }

}
