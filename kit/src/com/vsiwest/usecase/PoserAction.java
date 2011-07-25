package com.vsiwest.usecase;


import com.vsiwest.kit.Kit;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.List;

/**
 * (c) Copyright 2011 vsiwest, Inc.  All rights reserved.
 * Confidential Information.  Do not copy or distribute without express written permission.
 */
class PoserAction extends AbstractAction {
    private static final String POSER = "Poser";

    public PoserAction() {
        super(POSER);
    }

    public void actionPerformed(ActionEvent actionEvent) {
        JSplitPane splitter = new JSplitPane();
        try {
            JInternalFrame iframe = new JInternalFrame();
            Kit.getDesktop().add(iframe);
            JScrollPane container = new JScrollPane(splitter);
            iframe.setTitle("Poser");
            iframe.setContentPane(container);
            iframe.setClosable(true);
            iframe.setIconifiable(true);
            iframe.setMaximizable(true);
            iframe.setClosable(true);
            iframe.setResizable(true);
            iframe.setVisible(true);
            iframe.setSelected(true);
            iframe.setBounds(new Rectangle(111, 111, 111, 111));
            iframe.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);

        } catch (PropertyVetoException e) {
            e.printStackTrace();  //TODO: verify for a purpose
        }

        final List<JComponent> widgets = new ArrayList<JComponent>();

        widgets.add(splitter);
        final JButton button = new JButton("report");
        widgets.add(button);
        splitter.setLeftComponent(button);
        final JTextPane jTextPane = new JTextPane();
        widgets.add(jTextPane);
        jTextPane.setEditable(false);
        splitter.setRightComponent(jTextPane);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                String report = "";
                for (JComponent jComponent : widgets) {
                    Rectangle bounds = jComponent.getBounds();
                    report += jComponent.getClass().getName() + ':';
                    report += bounds.toString() + '\n';
                }
                jTextPane.setText(report);
            }
        });
    }
}
