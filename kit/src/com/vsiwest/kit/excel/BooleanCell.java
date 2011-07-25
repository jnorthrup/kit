package com.vsiwest.kit.excel;

import org.apache.poi.hssf.usermodel.HSSFCell;

import java.io.Serializable;

/**
 * (c) Copyright 2006 vsiwest, Inc.  All rights reserved.
 * Confidential Information.  Do not copy or distribute without express written permission.
 */
public class BooleanCell extends CellImpl<Boolean> {
    public static final BooleanCell TRUE = new BooleanCell(null) {
        public Serializable getValue() {
            return Boolean.TRUE;
        }
    };

    public static final BooleanCell FALSE = new BooleanCell(null) {
        public Serializable getValue() {
            return Boolean.FALSE;
        }
    };

    public BooleanCell(HSSFCell cell) {
        super(cell);
    }

    public Serializable getValue() {
        return cell.getBooleanCellValue();
    }
}
