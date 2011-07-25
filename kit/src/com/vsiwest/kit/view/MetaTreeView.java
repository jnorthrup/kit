package com.vsiwest.kit.view;

import com.vsiwest.kit.Kit;
import com.vsiwest.kit.PSqlChannel;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * (c) Copyright 2006 vsiwest, Inc.  All rights reserved.
 * Confidential Information.  Do not copy or distribute without express written permission.
 */
public class MetaTreeView {
    public static void createInstanceView(JInternalFrame iframe, final JDesktopPane desktop) throws PropertyVetoException, IOException, InterruptedException {
        desktop.add(iframe);

        final JTree tree = new JTree();

        Kit.prepIFrame(iframe, "InstanceView", new JScrollPane(tree), new Rectangle(0, 0, 200, desktop.getHeight()));
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Dashboard");
        DefaultTreeModel defaultTreeModel = new DefaultTreeModel(rootNode);
        tree.setModel(defaultTreeModel);

        try {

            DatabaseMetaData metaData;
            {
                metaData = PSqlChannel.getSqlConnection().getMetaData();
            }
            ResultSet tables = metaData.getTables("public", null, null, new String[]{"TABLE"});

            while (tables.next()) {
                String nm = tables.getString("table_name");
                rootNode.add(new DefaultMutableTreeNode(nm));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //Where the tree is initialized:
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        //Listen for when the selection changes.
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
                TreeNode lastSelectedPathComponent = (TreeNode) tree.getLastSelectedPathComponent();

                if (tree.getModel().getRoot() != lastSelectedPathComponent)
                    TablePopupView.createTablePopupView(lastSelectedPathComponent.toString(), desktop, true);
            }
        });
    }
}
