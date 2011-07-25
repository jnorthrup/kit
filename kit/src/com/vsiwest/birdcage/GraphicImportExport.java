package com.vsiwest.birdcage;

import quicktime.QTException;
import quicktime.app.view.QTComponent;
import quicktime.app.view.QTFactory;
import quicktime.io.QTFile;
import quicktime.std.StdQTConstants;
import quicktime.std.comp.ComponentDescription;
import quicktime.std.comp.ComponentIdentifier;
import quicktime.std.image.GraphicsExporter;
import quicktime.std.image.GraphicsImporter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Vector;

public final class GraphicImportExport {

    private Frame frame;
    private GraphicsImporter importer;

    private static final int[] imagetypes =
            {StdQTConstants.kQTFileTypeQuickTimeImage};
    /* other interesting values:
        StdQTConstants.kQTFileTypeGIF,
        StdQTConstants.kQTFileTypeJPEG,
        StdQTConstants4.kQTFileTypePNG,
        StdQTConstants4.kQTFileTypeTIFF
        StdQTConstants.kQTFileTypeMacPaint,
        StdQTConstants.kQTFileTypePhotoShop,
        StdQTConstants.kQTFileTypePICS,
        StdQTConstants.kQTFileTypePicture,
    */

    public static void main(String[] args) {
        new GraphicImportExport();
    }

    public GraphicImportExport() {
        try {
            QTSessionCheck.check();
            QTFile inFile = QTFile.standardGetFilePreview(imagetypes);
            importer = new GraphicsImporter(inFile);
            // put image onscreen
            QTComponent qtc = QTFactory.makeQTComponent(importer);
            Component c = qtc.asComponent();
            frame = new Frame("Imported image");
            frame.setLayout(new BorderLayout());
            frame.add(c, BorderLayout.CENTER);
            Button exportButton = new Button("Export");
            exportButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    try {
                        doExport();
                    } catch (QTException qte) {
                        qte.printStackTrace();
                    }
                }
            });
            frame.add(exportButton, BorderLayout.SOUTH);
            frame.pack();
            frame.setVisible(true);
        } catch (QTException qte) {
            qte.printStackTrace();
        }
    }

    private void doExport() throws QTException {
        // build list of GraphicExporters
        Vector<ExportChoice> choices;
        choices = new Vector<ExportChoice>();
        ComponentDescription cd =
                new ComponentDescription(
                        StdQTConstants.graphicsExporterComponentType);
        ComponentIdentifier ci = null;
        while ((ci = ComponentIdentifier.find(ci, cd)) != null) {
            choices.add(new ExportChoice(ci.getInfo().getName(),
                    ci.getInfo().getSubType()));
        }

        // offer a choice of movie exporters
        JComboBox exportCombo = new JComboBox(choices);
        JOptionPane.showMessageDialog(frame,
                exportCombo,
                "Choose exporter",
                JOptionPane.PLAIN_MESSAGE);
        ExportChoice choice = (ExportChoice) exportCombo.getSelectedItem();
        System.out.println("chose " + choice.name);

        // build a GE, wire up to the GraphicsImporter
        GraphicsExporter exporter =
                new GraphicsExporter(choice.subtype);
        exporter.setInputGraphicsImporter(importer);

        // ask for destination, settings
        FileDialog fd =
                new FileDialog(frame, "Save As",
                        FileDialog.SAVE);
        fd.setVisible(true);
        String filename = fd.getFile();
        if (filename.indexOf((int) '.') == -1)
            filename = filename + '.' +
                    exporter.getDefaultFileNameExtension();
        File file = new File(fd.getDirectory(), filename);
        exporter.setOutputFile(new QTFile(file));
        exporter.requestSettings();

        // export
        exporter.doExport();

        // need to explicitly quit (since awt is running)
        System.exit(0);
    }

    public static final class ExportChoice {
        final String name;
        final int subtype;

        public ExportChoice(String n, int st) {
            name = n;
            subtype = st;
        }

        public String toString() {
            return name;
        }
    }

}
