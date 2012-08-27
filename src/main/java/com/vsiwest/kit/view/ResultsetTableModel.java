package com.vsiwest.kit.view;

import javax.swing.event.*;
import javax.swing.table.*;
import java.lang.ref.*;
import java.sql.*;
import java.util.*;

/**
 * this uses ScrollableResultSets, with optional and seeking RowCount logic.
 * <p/>
 * This doesn't help with Postgres as of this writing, but other db's do Scrollable Results with this code.
 */
final class ResultsetTableModel implements TableModel {
    private final ResultSet rs;
    private final ResultSetMetaData metaData;

    class key implements Comparable {
        int r, c;

        public key(final int r, final int c) {

            this.r = r;
            this.c = c;
        }

        public int compareTo(Object o) {
            key k = (key) o;
            int i;
            return ((i = r - k.r) == 0) ? c - k.c : i;
        }
    }

    private Map<key, SoftReference> cache = new TreeMap<key, SoftReference>();
    private long rowcount;

    public ResultsetTableModel(ResultSet rs, ResultSetMetaData metaData) {
        this.rs = rs;
        this.metaData = metaData;
        listeners = new HashSet<TableModelListener>();
    }

    public int getRowCount() {
        if (rowcount == 0) try {
            if (rs.last()) rowcount = ((long) rs.getRow());//-1;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return (int) rowcount;
    }

    public int getColumnCount() {
        try {
            return metaData.getColumnCount();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String getColumnName(int i) {
        try {
            return metaData.getColumnName(1 + i);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Class<?> getColumnClass(int i) {
        try {
            return Class.forName(metaData.getColumnClassName(1 + i));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return String.class;
    }

    public boolean isCellEditable(int i, int i1) {
        return false;
    }

    public Object getValueAt(final int i, final int i1) {
        final key k = new key(i, i1);

        if (cache.containsKey(k)) {
            Object v = cache.get(k).get();
            if (v != null) return v;
        }
        try {
            if (rs.absolute(1 + i)) {
                //                  if (valCount++ % 500 == 0)
                //                    Kit.log(new StringBuilder().append("JdbcFetchCount at ").append(valCount).toString());
                Object val = rs.getObject(1 + i1);
                cache.put(k, new SoftReference(val));
                return val;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cache.get(k).get();
    }

    public void setValueAt(Object object, int i, int i1) {

    }

    private final HashSet<TableModelListener> listeners;

    public void addTableModelListener(TableModelListener tableModelListener) {
        listeners.add(tableModelListener);
    }

    public void removeTableModelListener(TableModelListener tableModelListener) {
        listeners.remove(tableModelListener);
    }
}
