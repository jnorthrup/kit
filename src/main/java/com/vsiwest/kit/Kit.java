package com.vsiwest.kit;

import com.vsiwest.kit.view.FormulaView;
import com.vsiwest.kit.view.MetaTreeView;
import com.vsiwest.usecase.UseCaseView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.logging.Logger;


/**
 * (c) Copyright 2011 vsiwest, Inc.  All rights reserved.
 * Confidential Information.  Do not copy or distribute without express written permission.
 */
public final class Kit {
    private static String[] args;

    private static final boolean running = true;
    private static final String[] Mp4Extensions = new String[]{"mp4", "m4v", "mov", "3g2", "3gp", "m4a", "m4b", "cmp", "ipod", "264", "h264", "qt"};
    private static Logger logger = Logger.getAnonymousLogger();

    public static DefaultTableModel getFormulaModel() {
        return (DefaultTableModel) getFormView().getTable().getModel();
    }

    private static final JDesktopPane desktop = new JDesktopPane();
    private static JFrame frame;

    public static void main(String[] arg) throws PropertyVetoException, IOException, InterruptedException, ClassNotFoundException, UnsupportedLookAndFeelException, IllegalAccessException, InstantiationException {
        args = arg;
//        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        initFrame();
        desktop.setDragMode(JDesktopPane.LIVE_DRAG_MODE);
    }

    private static void createUsecaseView() throws PropertyVetoException {
        JInternalFrame iframe = new JInternalFrame();
        UseCaseView useCaseView = new UseCaseView();
        JMenuBar bar = UseCaseView.getJMenuBar();
        getDesktop().add(iframe);
        iframe.setTitle("UseCaseGen");
        iframe.setContentPane(useCaseView);
        iframe.setClosable(true);
        iframe.setIconifiable(true);
        iframe.setMaximizable(true);
        iframe.setClosable(true);
        iframe.setResizable(true);
        iframe.setVisible(true);
        iframe.setSelected(true);
        iframe.setBounds(new Rectangle(222, 222, 222, 222));
        iframe.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
        iframe.setJMenuBar(bar);
    }


    private static void initFrame() throws PropertyVetoException {
        frame = new JFrame();
        getFrame().setBounds(0, 0, 1024, 768);
        getFrame().setVisible(true);
        getFrame().setResizable(true);
        getFrame().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane navTabs = new JTabbedPane();

        MenuBar jmb = new MenuBar();
        getFrame().setMenuBar(jmb);


        Menu jfm = new Menu("File");
        jmb.add(jfm);


        JPanel reportPanel = new JPanel(new BorderLayout());
        navTabs.add("Reports", reportPanel);
        reportPanel.add(getDesktop(), BorderLayout.CENTER);
        JToolBar toolBar = new JToolBar();
        ArrayList<AbstractAction> reportActions = createReportActions(getDesktop());

        for (AbstractAction abstractAction : reportActions) {
            toolBar.add(abstractAction);
            MenuItem menuItem = new MenuItem((String) abstractAction.getValue(Action.NAME));
            menuItem.addActionListener(abstractAction);
            jfm.add(menuItem);
        }
        reportPanel.add(toolBar, BorderLayout.NORTH);

        getFrame().setContentPane(reportPanel);
        getFrame().setMenuBar(jmb);
        new FormulaView(getDesktop());
        try {
            MetaTreeView.createInstanceView(new JInternalFrame(), getDesktop());
        } catch (Exception e) {
            e.printStackTrace();
        }
        createUsecaseView();
    }

    private static ArrayList<AbstractAction> createReportActions(final JDesktopPane desktop) {
        ArrayList<AbstractAction> jfm = new ArrayList<AbstractAction>();//ArrayList<AbstractAction>();

        jfm.add(new AbstractAction("Tables") {
            public void actionPerformed(ActionEvent actionEvent) {
                JInternalFrame iframe = new JInternalFrame();
                try {
                    MetaTreeView.createInstanceView(iframe, desktop);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        jfm.add(new AbstractAction("Formula") {
            public void actionPerformed(ActionEvent actionEvent) {
                new FormulaView(desktop);
            }
        });
        return jfm;
    }

    public static void prepIFrame(JInternalFrame iframe, String title, JComponent container, Rectangle rectangle) throws PropertyVetoException {
        iframe.setTitle(title);
        iframe.setContentPane(container);
        iframe.setClosable(true);
        iframe.setIconifiable(true);
        iframe.setMaximizable(true);
        iframe.setClosable(true);
        iframe.setResizable(true);
        iframe.setVisible(true);
        iframe.setSelected(true);
        iframe.setBounds(rectangle);
        iframe.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
    }

    private static FormulaView getFormView() {
        return FormulaView.getInstance();
    }


    public static void log(String entry) {
        logger.info(entry);

    }

    public static Logger getLogger() {
        return logger;
    }

    public static String[] getArgs() {
        return args;
    }

    public static Driver getDriver() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
    return  (Driver) Class.forName(System.getProperty("kit.sql.driver","org.postgresql.Driver")).newInstance();
    }

    public static boolean isRunning() {
        return running;
    }

    public static String[] getMp4Extensions() {
        return Mp4Extensions;
    }

    public static JDesktopPane getDesktop() {
        return desktop;
    }

    public static JFrame getFrame() {
        return frame;
    }
}
