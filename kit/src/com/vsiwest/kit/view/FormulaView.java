package com.vsiwest.kit.view;

import com.vsiwest.kit.FileFilter.KitFileFilter;
import com.vsiwest.kit.FileFilter.SqlFileFilter;
import com.vsiwest.kit.Kit;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;

/**
 * (c) Copyright 2006 vsiwest, Inc.  All rights reserved.
 * Confidential Information.  Do not copy or distribute without express written permission.
 */
public final class FormulaView {
    final DefaultTableModel formulaModel;
    private static FormulaView instance;
    private final JCheckBox previewCheckbox;
    private JCheckBox pureSqlCheckBox;
    private final JTextPane sqlTextPane;
    private final JTable table;

    public FormulaView(final JDesktopPane desktop) {

        instance = this;
        JPanel panel = new JPanel(new BorderLayout());

        JInternalFrame iframe = new JInternalFrame();
        desktop.add(iframe);
        iframe.setTitle("Formula");
        iframe.setContentPane(panel);
        iframe.setIconifiable(true);
        iframe.setResizable(true);
        iframe.setVisible(true);
        iframe.setBounds(new Rectangle(230, 400, desktop.getWidth() - 230, 300));
        iframe.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
        sqlTextPane = new JTextPane();
        panel.add(new JScrollPane(sqlTextPane), BorderLayout.SOUTH);
        JToolBar tools = new JToolBar();
        panel.add(tools, BorderLayout.NORTH);

        formulaModel = new DefaultTableModel() {
            public Class<?> getColumnClass(int i) {
                return FormulaColumn.values()[i].clazz;
            }
        };
        ArrayList<String> arrayList = new ArrayList<String>();
        for (FormulaColumn formulaColumn : FormulaColumn.values()) {
            arrayList.add(formulaColumn.name());
        }
        FormulaView.getInstance().formulaModel.setColumnIdentifiers(arrayList.toArray());

        table = new JTable(FormulaView.getInstance().formulaModel);
        ListSelectionModel listSelectionModel = new DefaultListSelectionModel();
        listSelectionModel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.setSelectionModel(listSelectionModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        formulaModel.addTableModelListener(
                new TableModelListener() {
                    public void tableChanged(TableModelEvent tableModelEvent) {
                        updateSqlText(updateFormulaSql());
                    }
                });
        tools.add(new AbstractAction("=") {
            public void actionPerformed(ActionEvent actionEvent) {
                DefaultTableModel formulaModel = FormulaView.getInstance().formulaModel;
                if (formulaModel.getRowCount() > 0 || pureSqlCheckBox.isEnabled())
//                    if (formulaModel.getValueAt(0, 0).toString().endsWith(".oid") && Boolean.TRUE.equals(formulaModel.getValueAt(FormulaColumn.visible.ordinal(), 0)))
//                        OidDataView.createDataViewFromFormulaView(desktop);
//                    else
                    DataView.createDataViewFromFormulaView(FormulaView.this, desktop);
            }
        });
        tools.add(new AbstractAction("X") {
            public void actionPerformed(ActionEvent actionEvent) {
                int m;
                ListSelectionModel selectionModel = table.getSelectionModel();
                if (selectionModel.isSelectionEmpty()) {
                    while (FormulaView.getInstance().formulaModel.getRowCount() != 0) {
                        FormulaView.getInstance().formulaModel.removeRow(0);
                    }
                } else
                    while ((m = selectionModel.getMaxSelectionIndex()) != -1) {
                        FormulaView.getInstance().formulaModel.removeRow(m);
                    }
                table.clearSelection();
            }
        });
        tools.add(new AbstractAction("row-up") {
            public void actionPerformed(ActionEvent actionEvent) {
                ListSelectionModel selectionModel = table.getSelectionModel();
                DefaultTableModel defaultTableModel = FormulaView.getInstance().formulaModel;

                while (!selectionModel.isSelectionEmpty()) {
                    int index0 = selectionModel.getAnchorSelectionIndex();
                    int index1 = selectionModel.getLeadSelectionIndex();

                    int ix0 = Math.min(index0, index1), ix1 = Math.max(index0, index1);
                    defaultTableModel.moveRow(ix0, ix1, Math.max(0, ix0 - 1));
//                    selectionModel.removeIndexInterval(ix0,ix1);
//                    ix0 = Math.max(0, --ix0);
//                    ix1 = Math.max(0, --ix1);
                    selectionModel.removeIndexInterval(index0, index1);
//                    selectionModel.removeIndexInterval(Math.max(0, index0 - 1), Math.max(0, index1 - 1));
//                    selectionModel.setAnchorSelectionIndex(ix0);
//                    selectionModel.setAnchorSelectionIndex(ix1);
                }
            }
        });
        tools.add(new AbstractAction("row-dn") {
            public void actionPerformed(ActionEvent actionEvent) {
                ListSelectionModel selectionModel = table.getSelectionModel();
                DefaultTableModel defaultTableModel = FormulaView.getInstance().formulaModel;

                while (!selectionModel.isSelectionEmpty()) {
                    int index0 = selectionModel.getAnchorSelectionIndex();
                    int index1 = selectionModel.getLeadSelectionIndex();
                    int ix0 = Math.min(index0, index1), ix1 = Math.max(index0, index1);

                    int ceiling = defaultTableModel.getRowCount() - 1;
                    defaultTableModel.moveRow(ix0, ix1, Math.min(ceiling, ix0 + 1));
                    selectionModel.removeIndexInterval(index0, index1);
//                    selectionModel.setAnchorSelectionIndex(ix0++);
//                    selectionModel.setLeadSelectionIndex(ix1++);
                }
            }
        });

        tools.add(new AbstractAction("Load") {
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.addChoosableFileFilter(KitFileFilter.createKitFileFilter());
                jFileChooser.addChoosableFileFilter(SqlFileFilter.createSqlFileFilter());
                jFileChooser.setDragEnabled(enabled);
                jFileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
                if (JFileChooser.APPROVE_OPTION == jFileChooser.showOpenDialog(desktop)) {
                    FileReader fileReader;
                    try {
                        fileReader = new FileReader(jFileChooser.getSelectedFile());

                        if (jFileChooser.getFileFilter() == KitFileFilter.getInstance()) {
                            pureSqlCheckBox.setSelected(false);
                            XStream xs = new XStream(new DomDriver());

                            Vector x = (Vector) xs.fromXML(fileReader);
                            Vector<String> v = new Vector<String>();
                            for (FormulaColumn formulaColumn : FormulaColumn.values()) {
                                v.add(formulaColumn.name());
                            }
                            FormulaView.getInstance().formulaModel.setDataVector(x, v);
                        } else {
                            pureSqlCheckBox.setSelected(true);

                            BufferedReader bufferedReader = new BufferedReader(fileReader);
                            StringBuilder b = new StringBuilder();
                            String s = null;
                            do {
                                if (s != null)
                                    b.append(s).append(' ');
                                s = bufferedReader.readLine();
                            } while (s != null);

                            sqlTextPane.setText(b.toString());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            }
        });
        tools.add(new

                AbstractAction("Save") {
                    public void actionPerformed
                            (ActionEvent actionEvent) {
                        JFileChooser jFileChooser = new JFileChooser();
                        jFileChooser.addChoosableFileFilter(KitFileFilter.createKitFileFilter());
                        jFileChooser.addChoosableFileFilter(SqlFileFilter.createSqlFileFilter());
                        jFileChooser.setDragEnabled(enabled);
                        jFileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
                        if (JFileChooser.APPROVE_OPTION == jFileChooser.showSaveDialog(desktop)) {
                            FileFilter filter = jFileChooser.getFileFilter();
                            FileWriter fileWriter = null;
                            try {
                                fileWriter = new FileWriter(jFileChooser.getSelectedFile());
                            } catch (IOException e) {
                                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            }
                            if (filter == KitFileFilter.getInstance()) {

                                XStream xs = new XStream();
                                xs.toXML(FormulaView.getInstance().formulaModel.getDataVector(), fileWriter);

                            }
                            if (filter == SqlFileFilter.getInstance()) {
                                try {
                                    if (fileWriter != null) {
                                        fileWriter.write(sqlTextPane.getText());
                                    }
                                    if (fileWriter != null) {
                                        fileWriter.close();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                                }
                            }
                        }
                    }
                }
        );

        previewCheckbox = new JCheckBox("first 50");
        previewCheckbox.setToolTipText("Selecting this will only fetch the first 50 rows");
        previewCheckbox.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                updateFormulaSql();
            }
        });
        tools.add(previewCheckbox);

        pureSqlCheckBox = new JCheckBox("Sauce-Only");
        pureSqlCheckBox.setSelected(false);
        pureSqlCheckBox.setToolTipText("This bypasses the selection gui and just executes what's pasted into the text box below.");
        tools.add(pureSqlCheckBox);

    }

    public static FormulaView getInstance() {

        return instance;
    }

    public static Collection<Collection<String>> updateFormulaSql() {


        String s;

        ArrayList<String> cols = new ArrayList<String>();
        Set<String> from = new TreeSet<String>();
        ArrayList<String> wher = new ArrayList<String>();

        Vector<?> dataVector = FormulaView.getInstance().formulaModel.getDataVector();
        Collection<String> orby = new ArrayList<String>();
        Collection<String> grby = new ArrayList<String>();
        for (Object s1 : dataVector) {
            Object[] s2 = ((Vector) s1).toArray();
            String col = String.valueOf(s2[FormulaColumn.col.ordinal()]);


            int s2len = s2.length;
            if (!(s2len < FormulaColumn.where.ordinal())) {
                String wclause = (String) s2[FormulaColumn.where.ordinal()];
                if (wclause != null && wclause.length() > 0) {
                    wher.add(new StringBuilder().append(col).append(' ').append(wclause).toString());
                }
            }

            Object visVal = s2[FormulaColumn.visible.ordinal()];
            if (s2len >= FormulaColumn.visible.ordinal() && Boolean.TRUE.equals(visVal)) {

                String ret = col;
                if (!(s2len < FormulaColumn.function.ordinal())) {
                    String fclause = (String) s2[FormulaColumn.function.ordinal()];
                    ret = fclause != null && fclause.trim().length() > 0 ? fclause + '(' + col + ')' : col;
                }
                if (!(s2len < FormulaColumn.as.ordinal())) {
                    String aclause = (String) s2[FormulaColumn.as.ordinal()];
                    if (aclause != null && aclause.trim().length() > 0)
                        ret += " as " + '"' + aclause + '"';

                }

                cols.add(ret);
            }


            if (s2len >= FormulaColumn.order_by.ordinal() && Boolean.TRUE == s2[FormulaColumn.order_by.ordinal()]) {

                if (s2len >= FormulaColumn.ascending.ordinal()) {
                    final boolean asc = Boolean.TRUE == s2[FormulaColumn.ascending.ordinal()];
                    orby.add(MessageFormat.format("{0} {1} ", col, asc ? "ASC" : "DESC"));
                } else
                    orby.add(col);
            }
//            grby = new ArrayList<String>();
//            if (s2len >= FormulaColumn.group_by.ordinal() && Boolean.TRUE.equals(visVal))
//                grby.add(col);

            String[] scol = col.split("\\.");
            from.add(scol[0]);
        }


        Collection<Collection<String>> ret = new ArrayList<Collection<String>>();
        ret.add(cols);
        ret.add(new ArrayList<String>(from));
        ret.add(wher);
        ret.add(orby);
        ret.add(grby);
        return ret;
    }

    String updateSqlText(Collection<Collection<String>> arr) {
        if (!pureSqlCheckBox.isSelected()) {
            StringBuilder query = new StringBuilder();
// Intellij generify slaughters this transaction, copied here.
/*        Object[] objects =  arr.toArray();
        Iterable  cols = (Iterable) objects[0];
        Iterable from = (Iterable) objects[1];
        Collection<String> wher = (Collection<String>) objects[2];
*/
            Object[] objects = arr.toArray();
            Iterable cols = (Iterable) objects[0];
            Iterable from = (Iterable) objects[1];
            Collection<String> wher = (Collection<String>) objects[2];
            Collection<String> orby = (Collection<String>) objects[3];

            query.append(createSELECT(cols)).append(createFROM(from)).append(createWHERE(wher)).append(createORDERBY(orby));

            if (previewCheckbox.isSelected()) {
                query.append(" LIMIT 50 ");

            }
            String finalQuery = query.toString();
            sqlTextPane.setText(finalQuery);

            return finalQuery;
        } else
            return sqlTextPane.getText();
    }

    private static String createORDERBY(Collection<String> orby) {
        String x = "";
        if (!orby.isEmpty()) {
            x += " ORDER BY " +
                    createClause(orby, ", ");
        }
        return x;
    }


    private static String createWHERE(Collection<String> wher) {
        String x = "";
        if (!wher.isEmpty()) {
            x += "  WHERE " +
                    createClause(wher, " AND ");
        }
        return x;
    }

    private static String createFROM(Iterable<String> from) {
        return MessageFormat.format("  FROM {0}", createClause(from, ","));
    }

    private static String createSELECT(Iterable<String> cols) {
        return MessageFormat.format(" SELECT {0}", createClause(cols, ","));
    }

    private static String createClause(Iterable<? extends String> cols, String delim) {
        boolean first = true;

        String x = "";
        for (Object s : cols) {
            if (first)
                first = false;
            else
                x += delim;
            x += s + " ";
        }
        return x;
    }

    static String getSqlRecordCount(Collection<String> from, Collection<String> wher) {
        String x = "SELECT COUNT (*)   ";
        x += createFROM(from);
        x += createWHERE(wher);

        Kit.getLogger().info("SqlCount: " + x);
        return x;
    }

    String getOidRecord(Collection<String> from, Collection<String> wher) {
        String x = "SELECT   " +
                formulaModel.getValueAt(0, 0);

        x += createFROM(from);
        x += createWHERE(wher);

        Kit.getLogger().info("OIDRecrd: " + x);
        return x;
    }


    public JTable getTable() {
        return table;
    }

    public DefaultTableModel getFormulaModel() {
        return formulaModel;
    }
}
