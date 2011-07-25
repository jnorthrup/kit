package com.vsiwest.birdcage;

import quicktime.QTException;
import quicktime.io.IOConstants;
import quicktime.io.OpenMovieFile;
import quicktime.io.QTFile;
import quicktime.std.StdQTConstants;
import quicktime.std.movies.Movie;
import quicktime.std.qtcomponents.MovieExporter;
import quicktime.util.QTUtils;

import javax.swing.*;


public final class SimpleMovieExport {

    public static void main(String[] args) {
        new SimpleMovieExport();
    }

    public SimpleMovieExport() {
        // build choices
        ExportChoice[] choices = new ExportChoice[3];
        choices[0] =
                new ExportChoice("QuickTime Movie",
                        StdQTConstants.kQTFileTypeMovie);
        choices[1] =
                new ExportChoice("AVI file",
                        StdQTConstants.kQTFileTypeAVI);
        choices[2] =
                new ExportChoice("MPEG-4 file",
                        QTUtils.toOSType("mpg4"));

        try {
            // query user for a movie to open
            QTSessionCheck.check();
            QTFile file =
                    QTFile.standardGetFilePreview(QTFile.kStandardQTFileTypes);
            OpenMovieFile omFile = OpenMovieFile.asRead(file);
            Movie movie = Movie.fromFile(omFile);

            // offer a choice of movie exporters
            JComboBox exportCombo = new JComboBox(choices);
            JOptionPane.showMessageDialog(null,
                    exportCombo,
                    "Choose exporter",
                    JOptionPane.PLAIN_MESSAGE);
            ExportChoice choice =
                    (ExportChoice) exportCombo.getSelectedItem();

            // create an exporter
            MovieExporter exporter =
                    new MovieExporter(choice.subtype);

            QTFile saveFile =
                    new QTFile(new java.io.File("Untitled"));

            // do the export
            movie.setProgressProc();
            movie.convertToFile(null,
                    saveFile,
                    StdQTConstants.kQTFileTypeMovie,
                    StdQTConstants.kMoviePlayer,
                    IOConstants.smSystemScript,
                    StdQTConstants.showUserSettingsDialog |
                            StdQTConstants.movieToFileOnlyExport |
                            StdQTConstants.movieFileSpecValid,
                    exporter);

            // need to explicitly quit (since awt is running)
            System.exit(0);
        } catch (QTException qte) {
            qte.printStackTrace();
        }

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
