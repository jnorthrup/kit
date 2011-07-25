package com.vsiwest.kit.excel;

import org.apache.poi.hssf.usermodel.HSSFCell;

import java.io.Serializable;

/**
 * (c) Copyright 2006 vsiwest, Inc.  All rights reserved.
 * Confidential Information.  Do not copy or distribute without express written permission.
 */
public class ShortCell extends CellImpl<Short> {
    public ShortCell(HSSFCell cell) {
        super(cell);

        if (!(!(null == cell || cell.getCellType() != HSSFCell.CELL_TYPE_NUMERIC))) {
            throw new IllegalArgumentException(String.valueOf(cell));
        }

    }

    public Serializable getValue() {
        return cell.getNumericCellValue();
    }
}

