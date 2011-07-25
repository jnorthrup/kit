package com.vsiwest.usecase;

import com.thoughtworks.xstream.XStream;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * (c) Copyright 2011 vsiwest, Inc.  All rights reserved.
 * Confidential Information.  Do not copy or distribute without express written permission.
 */
class LoadXMIAction extends AbstractAction {
    public LoadXMIAction() {
        super("Load");
    }

    public void actionPerformed(ActionEvent actionEvent) {
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setDialogTitle("Select a usecase xmi file");
        jFileChooser.setFileFilter(new FileNameExtensionFilter("XMI Usecase Diagram", "xmi"));
        int res = jFileChooser.showOpenDialog(UseCaseView.getFrame());

        if (res != JFileChooser.APPROVE_OPTION)
            return;

        File selectedFile = jFileChooser.getSelectedFile();
        UseCaseView.getUseCaseView().inject(selectedFile);
        XStream xs = new XStream();

        String actors_xml = xs.toXML(new Object[]{UseCaseView.useCaseView.actors, UseCaseView.useCaseView.usecases});
        try {
            FileWriter fileWriter = new FileWriter(UseCaseView.UCIMPORT_ACTORS_XML);
            fileWriter.write(actors_xml);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();  //TODO: verify for a purpose
        }
    }

}
