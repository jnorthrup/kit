package com.vsiwest.kit.excel;

import org.apache.poi.hssf.usermodel.HSSFCell;

import java.io.Serializable;

/**
 * (c) Copyright 2006 vsiwest, Inc.  All rights reserved.
 * Confidential Information.  Do not copy or distribute without express written permission.
 */
public class LongCell extends CellImpl<Long> {
    public LongCell(HSSFCell cell) {
        super(cell);
        if (cell.getCellType() != HSSFCell.CELL_TYPE_NUMERIC) {
            throw new IllegalArgumentException(cell.getStringCellValue());
        }
    }

    public Serializable getValue() {
        return cell.getNumericCellValue();
    }
}


