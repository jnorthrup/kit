package com.vsiwest.kit.view;


import com.vsiwest.kit.*;

import javax.jnlp.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.sql.Statement;
import java.text.*;
import java.util.*;

/**
 * (c) Copyright 2011 vsiwest, Inc.  All rights reserved.
 * Confidential Information.  Do not copy or distribute without express written permission.
 */
public final class DataView {
    private String sql;

    public static void create(final String sql, final JDesktopPane desktop) throws PropertyVetoException, SQLException, IOException, InterruptedException {

        ResultSet rs = PSqlChannel.executeSql(sql);

        new DataView(rs, desktop, sql);
    }

    private DataView(FormulaView formulaView, JDesktopPane desktop) {

        Collection<Collection<String>> formColl = FormulaView.updateFormulaSql();

        try {

            Statement statement = PSqlChannel.getSqlConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            String x = formulaView.updateSqlText(formColl);
            Kit.getLogger().info("Sql: " + x);

            statement.setFetchSize(1);
            final ResultSet rs = statement.executeQuery(x);
            final ResultSetMetaData metaData = rs.getMetaData();
            prepIframe(desktop, rs, metaData, x);

        } catch (SQLException e) {
            PSqlChannel.doSqlErrorMessage(e);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }

    }


    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    private DataView(final ResultSet rs, JDesktopPane desktop, String title) throws SQLException, PropertyVetoException {
        setSql(title);
        final ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        String[] colNames;
        colNames = new String[columnCount];

        for (int i = 0; i < colNames.length; i++) {
            colNames[i] = metaData.getColumnName(i + 1);
        }

        while (rs.next()) {
            for (int i = 0; i < columnCount; i++) {
                colNames[i] = rs.getString(i + 1);
            }
        }

        prepIframe(desktop, rs, metaData, title);
    }

    private void prepIframe(final JDesktopPane desktop, ResultSet rs, ResultSetMetaData metaData, final String title) throws PropertyVetoException {
        final JInternalFrame dataView = new JInternalFrame();
        desktop.add(dataView);

        ResultsetTableModel resultsetTableModel = new ResultsetTableModel(rs, metaData);
//        TableSorter tableSorter = new TableSorter(resultsetTableModel);
        final JTable table = new JTable(resultsetTableModel);
        RowSorter<TableModel> sorter = new TableRowSorter<TableModel>(resultsetTableModel);
        table.setRowSorter(sorter);

//        tableSorter.setTableHeader(table.getTableHeader());
        JScrollPane pane = new JScrollPane(table);
        JPanel jPanel = new JPanel(new BorderLayout());
        jPanel.add(pane, BorderLayout.CENTER);
        JToolBar toolBar = new JToolBar();
        jPanel.add(toolBar, BorderLayout.NORTH);
        toolBar.add(new AbstractAction("<table/>") {
            public void actionPerformed(ActionEvent e) {
                StringBuilder tbod = createHtmlTable(table, true);
                openBrowser(tbod);

            }
        });
        toolBar.add(new AbstractAction(".xls") {
            public void actionPerformed(ActionEvent e) {
                StringBuilder tbod = createHtmlTable(table, false);
                openXls(tbod);
            }
        });
        Kit.prepIFrame(dataView, "[live] " + title, jPanel, new Rectangle(210, 0, 700, (int) desktop.getBounds().getHeight()));
    }

    private static void openBrowser(StringBuilder tbod) {
        URL turl = null;
        try {
            File tempFile = File.createTempFile("kit", ".html");
            tempFile.createNewFile();
            FileWriter fileWriter = new FileWriter(tempFile);
            fileWriter.write(tbod.toString());
            fileWriter.flush();
            fileWriter.close();
            turl = tempFile.getAbsoluteFile().toURI().toURL();
            javax.jnlp.BasicService svc = (javax.jnlp.BasicService) javax.jnlp.ServiceManager.lookup("javax.jnlp.BasicService");

            svc.showDocument(turl);
        } catch (IOException e1) {
            e1.printStackTrace();  //is this informative enough?
        } catch (UnavailableServiceException e1) {
            JTextField field = new JTextField();
            field.setText(turl.toExternalForm());
            JOptionPane.showInternalConfirmDialog(Kit.getFrame().getContentPane(), field);
        }
    }

    private static void openXls(StringBuilder tbod) {
        try {
            File tempFile = File.createTempFile("kit", ".xls");
            tempFile.createNewFile();
            FileWriter fileWriter = new FileWriter(tempFile);
            fileWriter.write(tbod.toString());
            fileWriter.flush();
            fileWriter.close();
            javax.jnlp.BasicService svc = (javax.jnlp.BasicService) javax.jnlp.ServiceManager.lookup("javax.jnlp.BasicService");
            svc.showDocument(tempFile.getAbsoluteFile().toURI().toURL());
        } catch (IOException e1) {
            e1.printStackTrace();  //is this informative enough?
        } catch (UnavailableServiceException e1) {
            e1.printStackTrace();  //is this informative enough?
        }
    }

    private StringBuilder createHtmlTable(JTable table, boolean textFormatting) {
        StringBuilder tbod = new StringBuilder("<html><head><meta name='query' content='" + getSql() + "' /></head><body><table><tr>");
        int ccount = table.getColumnCount();
        TableModel tmodel = table.getModel();

        for (int i = 0; i < ccount; i++) {
            String id = String.valueOf(table.getColumnName(i));
            tbod.append("<th>").append(id).append("</th>");
        }


        tbod.append("</tr>\n");

        int rcount = tmodel.getRowCount();
        for (int rowidx = 0; rowidx < rcount; rowidx++) {
            for (int colidx = 0; colidx < ccount; colidx++) {
                String val = String.valueOf(table.getValueAt(rowidx, colidx));
                tbod.append(MessageFormat.format((textFormatting ? val.trim().lastIndexOf((int) '\n') != -1 ? "<td><pre>{0}</pre></td>" : "<td>{0}</td>" : "<td>{0}</td>"), val));
            }
            tbod.append("</tr>\n");
        }

        tbod.append("</table></body></html>\n");
        return tbod;
    }

    static DataView createDataViewFromFormulaView(FormulaView formulaView, JDesktopPane desktop) {
        return new DataView(formulaView, desktop);
    }

    static DataView createDataViewFromResultset(final ResultSet rs, JDesktopPane desktop, String title) throws SQLException, PropertyVetoException {
        return new DataView(rs, desktop, title);
    }

    {

    }
}
