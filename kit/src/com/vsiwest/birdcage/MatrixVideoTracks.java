package com.vsiwest.birdcage;

import quicktime.QTException;
import quicktime.app.view.QTFactory;
import quicktime.io.OpenMovieFile;
import quicktime.io.QTFile;
import quicktime.qd.QDRect;
import quicktime.std.StdQTConstants;
import quicktime.std.image.Matrix;
import quicktime.std.movies.Movie;
import quicktime.std.movies.MovieController;
import quicktime.std.movies.Track;
import quicktime.std.movies.media.DataRef;
import quicktime.std.movies.media.VideoMedia;
import quicktime.util.QTHandle;

import java.awt.*;

public class MatrixVideoTracks extends Frame {

    public static void main(String[] args) {
        try {
            QTSessionCheck.check();
            // get background movie
            QTFile file =
                    QTFile.standardGetFilePreview(QTFile.kStandardQTFileTypes);
            OpenMovieFile omf = OpenMovieFile.asRead(file);
            Movie backMovie = Movie.fromFile(omf);
            // get foreground movie
            file = QTFile.standardGetFilePreview(QTFile.kStandardQTFileTypes);
            omf = OpenMovieFile.asRead(file);
            Movie foreMovie = Movie.fromFile(omf);
            // get frame
            Frame frame = new MatrixVideoTracks(backMovie, foreMovie);
            frame.pack();
            frame.setVisible(true);
        } catch (QTException qte) {
            qte.printStackTrace();
        }
    }

    private MatrixVideoTracks(Movie backMovie, Movie foreMovie)
            throws QTException {
        super("Matrix Video Tracks");
        Movie matrixMovie = new Movie();
        // build tracks
        Track foreTrack = addVideoTrack(foreMovie, matrixMovie);
        Track backTrack = addVideoTrack(backMovie, matrixMovie);
        // set matrix transformation
        Matrix foreMatrix = new Matrix();
        // set matrix to move fore to bottom right 1/4 or back
        QDRect foreFrom =
                new QDRect(0, 0,
                        foreTrack.getSize().getWidth(),
                        foreTrack.getSize().getHeight());
        QDRect foreTo =
                new QDRect(backTrack.getSize().getWidth() >> 1,
                        backTrack.getSize().getHeight() >> 1,
                        backTrack.getSize().getWidth() >> 1,
                        backTrack.getSize().getHeight() >> 1);
        System.out.println("foreTo is = " + foreTo);
        foreMatrix.rect(foreFrom, foreTo);
        foreTrack.setMatrix(foreMatrix);
        // set foreTrack's layer
        foreTrack.setLayer(-1);
        // now get component and add to frame
        MovieController controller = new MovieController(matrixMovie);
        Component c = QTFactory.makeQTComponent(controller).asComponent();
        add(c);
    }

    private static Track addVideoTrack(Movie sourceMovie, Movie targetMovie)
            throws QTException {
        // find first video track
        Track videoTrack =
                sourceMovie.getIndTrackType(1,
                        StdQTConstants.videoMediaType,
                        StdQTConstants.movieTrackMediaType);
        if (videoTrack == null)
            throw new QTException("can't find a video track");
        // add videoTrack to targetMovie
        Track newTrack =
                targetMovie.newTrack(videoTrack.getSize().getWidthF(),
                        videoTrack.getSize().getHeightF(),
                        1.0f);
        VideoMedia newMedia =
                new VideoMedia(newTrack,
                        videoTrack.getMedia().getTimeScale(),
                        new DataRef(new QTHandle()));
        videoTrack.insertSegment(newTrack,
                0,
                videoTrack.getDuration(),
                0);
        return newTrack;
    }
}
