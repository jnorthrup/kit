package com.vsiwest.kit.excel;

import org.apache.poi.hssf.usermodel.HSSFCell;

import java.io.Serializable;

/**
 * (c) Copyright 2011 vsiwest, Inc.  All rights reserved.
 * Confidential Information.  Do not copy or distribute without express written permission.
 */
public class ByteCell extends CellImpl<Byte> {
    public ByteCell(HSSFCell cell) {
        super(cell);
    }

    public Serializable getValue() {
        return cell.getNumericCellValue();
    }
}
