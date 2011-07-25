package com.vsiwest.kit.view;

import com.vsiwest.kit.Kit;
import com.vsiwest.kit.MetaRef.InboundRef;
import com.vsiwest.kit.MetaRef.MetaDataRef;
import com.vsiwest.kit.MetaRef.OutboundRef;
import com.vsiwest.kit.MetaRef.TableMetaModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;

/**
 * (c) Copyright 2011 vsiwest, Inc.  All rights reserved.
 * Confidential Information.  Do not copy or distribute without express written permission.
 */
class TablePopupView {
    private int windowoffset;
    private JList list;

    private TablePopupView(final String tableName, final JDesktopPane desktop, final boolean oid) {
        try {
//            initColumns(model.metaDataRefs, tableName, oid);
            final TableMetaModel model = TableMetaModel.createTableMetaModel(tableName, oid);
            final JInternalFrame iframe = new JInternalFrame(tableName);
            desktop.add(iframe);
            list = new JList(model.getMetaDataRefs().toArray());
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            addMouseListener(desktop);
            final int i = (++windowoffset % 7) * 15;
            Kit.prepIFrame(iframe, tableName, new JScrollPane(list), new Rectangle(320 + i, 20 + i, 220 + i, 220 + i));

        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
    }

//    private static void initColumns(List<MetaDataRef> metaDataRefs, String tableName, boolean oid) throws SQLException {
//        final TableMetaModel model = new TableMetaModel(tableName);
//    }

    private void addMouseListener(final JDesktopPane desktop) {
        list.addMouseListener(new TablePopupListClickListener(desktop));
    }

    static TablePopupView createTablePopupView(String tableName, final JDesktopPane desktop, final boolean oid) {
        return new TablePopupView(tableName, desktop, oid);
    }


    private static class TablePopupListClickListener extends MouseAdapter {
        private final JDesktopPane desktop;

        public TablePopupListClickListener(JDesktopPane desktop) {
            this.desktop = desktop;
        }

        public void mouseClicked(MouseEvent evt) {
            final JList list = (JList) evt.getSource();

            if (evt.getClickCount() == 2) {          // Double-click

                // Get item index
                final MetaDataRef dbref = (MetaDataRef) list.getModel().getElementAt(list.locationToIndex(evt.getPoint()));
                FormulaView.getInstance().getFormulaModel().addRow(dbref.getFormulaEntry());

                if (dbref instanceof InboundRef) {
                    createTablePopupView(dbref.getPTable(), desktop, false);

                } else if (dbref instanceof OutboundRef) {
                    createTablePopupView(dbref.getFTable(), desktop, false);
                }
            }
        }
    }
}

