package com.vsiwest.birdcage;

import quicktime.QTException;
import quicktime.app.view.QTComponent;
import quicktime.app.view.QTFactory;
import quicktime.io.OpenMovieFile;
import quicktime.io.QTFile;
import quicktime.std.StdQTConstants;
import quicktime.std.clocks.RateCallBack;
import quicktime.std.movies.Movie;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public final class BasicQTButtons extends Frame
        implements ActionListener {

    private Button revButton;
    private Button stopButton;
    private Button startButton;
    private Button fwdButton;

    private final Movie theMovie;

    private BasicQTButtons(Movie m) throws QTException {
        super("Basic QT Player");

//
//And here's the inner class. It has a constructor that takes a Movie argument and an execute( ) method:

        class MyQTCallback extends RateCallBack {
            public MyQTCallback(Movie m) throws QTException {
                super(m.getTimeBase(),
                        0.0F,
                        StdQTConstants.triggerRateChange);
                callMeWhen();
            }

            public void execute() {
                if (rateWhenCalled == (float) 0.0) {
                    startButton.setEnabled(true);
                    stopButton.setEnabled(false);
                } else if (rateWhenCalled == (float) 1.0) {
                    startButton.setEnabled(false);
                    stopButton.setEnabled(true);
                }
                // indicate that we want to be called again
                try {
                    callMeWhen();
                } catch (QTException qte) {
                    qte.printStackTrace();


                }
            }
        }
        theMovie = m;
        QTComponent qc = QTFactory.makeQTComponent(m);
        Component c = qc.asComponent();
        setLayout(new BorderLayout());
        add(c, BorderLayout.CENTER);
        Panel buttons = new Panel();
        revButton = new Button("<");
        revButton.addActionListener(this);
        stopButton = new Button("0");
        stopButton.addActionListener(this);
        startButton = new Button("1");
        startButton.addActionListener(this);
        fwdButton = new Button(">");
        fwdButton.addActionListener(this);
        buttons.add(revButton);
        buttons.add(stopButton);
        buttons.add(startButton);
        buttons.add(fwdButton);
        add(buttons, BorderLayout.SOUTH);
        pack();
        MyQTCallback myCallback = new MyQTCallback(m);

    }

    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == revButton)
                theMovie.setRate(theMovie.getRate() - 0.5f);
            else if (e.getSource() == stopButton)
                theMovie.stop();
            else if (e.getSource() == startButton)
                theMovie.start();
            else if (e.getSource() == fwdButton)
                theMovie.setRate(theMovie.getRate() + 0.5f);
        } catch (QTException qte) {
            qte.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            QTSessionCheck.check();
            QTFile file =
                    QTFile.standardGetFilePreview(
                            QTFile.kStandardQTFileTypes);
            OpenMovieFile omFile = OpenMovieFile.asRead(file);
            Movie m = Movie.fromFile(omFile);
            Frame f = new BasicQTButtons(m);
            f.pack();
            f.setVisible(true);
            m.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
