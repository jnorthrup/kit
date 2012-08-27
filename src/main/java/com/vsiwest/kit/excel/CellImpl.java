package com.vsiwest.kit.excel;

import org.apache.poi.hssf.usermodel.HSSFCell;
import static org.apache.poi.hssf.usermodel.HSSFCell.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * abstract factory which creates Cell values
 * <p/>
 * <p/>
 * (c) Copyright 2011 vsiwest, Inc.  All rights reserved.
 * Confidential Information.  Do not copy or distribute without express written permission.
 */
public abstract class CellImpl<T> implements ExcelAccessor<T>, Cell {
    final HSSFCell cell;

    protected CellImpl(HSSFCell cell) {
        this.cell = cell;
    }

    /**
     * @param cell spreadsheet cell
     * @return a concrete Cell instance
     */

    public static Cell createMetaCell(HSSFCell cell) {
        int cellType = null == cell ? CELL_TYPE_BLANK : cell.getCellType();

        return
                cellType == CELL_TYPE_STRING ? new StringCell(cell) :
                        cellType == CELL_TYPE_BLANK ? BlankCell.instance() :
                                cellType == CELL_TYPE_NUMERIC ? new NumericCell(cell) :
                                        cellType == CELL_TYPE_BOOLEAN ? new BooleanCell(cell) :
                                                cellType == CELL_TYPE_FORMULA ? new FormulaCell(cell) :
                                                        new ErrorCell(cell);
    }


    /**
     * normally using toString as values is a bad idea but in this case it was promised but not delivered in POI
     *
     * @return a string of the internal cell's value
     */
    @Override
    public String toString() {
        return String.valueOf(getValue());
    }

    public T getTypedValue() {
        return (T) getValue();
    }

//    public String toString() {
//        return new StringBuilder().append("[Cell ").append("cell=").append(cell).append(", value=").append(getValue()).append(']').toString();
//    }

    public static Collection<Collection<Collection<Serializable>>> flattenToObject(Collection<Collection<Collection<Cell>>> rowCells) {
        Collection<Collection<Collection<Serializable>>> flattened = new ArrayList<Collection<Collection<Serializable>>>();
        for (Collection<Collection<Cell>> daxExcelCells : rowCells) {
            ArrayList<Collection<Serializable>> collections = new ArrayList<Collection<Serializable>>();
            for (Collection<Cell> excelCells : daxExcelCells) {
                ArrayList<Serializable> serializables = new ArrayList<Serializable>();
                for (Cell cell : excelCells) {
                    Serializable value = cell.getValue();
                    serializables.add(value);

                }
                collections.add(serializables);
            }
            flattened.add(collections);
        }

        return flattened;
    }

    public static Collection<Collection<Collection<String>>> flattenToString(Collection<Collection<Collection<Cell>>> rowCells) {
        Collection<Collection<Collection<String>>> flattened = new ArrayList<Collection<Collection<String>>>();
        for (Collection<Collection<Cell>> daxExcelCells : rowCells) {
            ArrayList<Collection<String>> collections = new ArrayList<Collection<String>>();
            for (Collection<Cell> excelCells : daxExcelCells) {
                ArrayList<String> Strings = new ArrayList<String>();
                for (Cell cell : excelCells) {
                    String value = cell.toString();
                    Strings.add(value);
                }
                collections.add(Strings);
            }
            flattened.add(collections);
        }

        return flattened;
    }
}
