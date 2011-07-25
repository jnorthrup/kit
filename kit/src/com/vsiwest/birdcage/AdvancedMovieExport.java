package com.vsiwest.birdcage;

import static com.vsiwest.lockpick.BoxFileModel.*;
import com.vsiwest.util.*;
import quicktime.*;
import quicktime.io.*;
import quicktime.std.*;
import quicktime.std.comp.*;
import quicktime.std.movies.*;
import quicktime.std.movies.media.*;
import quicktime.std.qtcomponents.*;
import quicktime.util.*;

import javax.swing.*;
import java.io.*;
import java.lang.reflect.*;
import java.math.*;
import java.nio.*;
import java.text.*;
import java.util.*;

public final class AdvancedMovieExport {
    private static final Object[] COLUMN_NAMES = new Object[]{"desc", "4cc", "v"};

    public static void main(String[] args) {
        new AdvancedMovieExport();
    }

    public AdvancedMovieExport() {
        try {
            // query user for a movie to open
            QTFile file = requestFile();
            OpenMovieFile omFile = OpenMovieFile.asRead(file);


            Movie movie = null;
            try {
                movie = Movie.fromFile(omFile);
            } catch (QTException e) {
                e.printStackTrace();
                System.exit(1);
            }
            //  movie.showInformation();
            int trackCount = movie.getTrackCount();
            for (int i1 = 0; i1 < trackCount; ++i1) {
                Track track = movie.getIndTrack(i1 + 1);
                boolean b = track.getEnabled();
                if (!b) continue;


                Media media = track.getMedia();
                int sampleDescriptionCount = media.getSampleDescriptionCount();

                for (int j = 0; j < sampleDescriptionCount; j++) {

                    try {
                        SampleDescription sd = media.getSampleDescription(j + 1);
                        Class<? extends SampleDescription> aClass = sd.getClass();
                        Method method1 = aClass.getMethod("getCType");

                        if (method1 != null) {
                            int ctype = (Integer) method1.invoke(sd);

                            ByteBuffer byteBuffer = ByteBuffer.allocate(4);
                            IntBuffer intBuffer = byteBuffer.asIntBuffer();

                            intBuffer.put(ctype);
                            byteBuffer.rewind();
                            ComponentDescription cd2 = new ComponentDescription(QTUtils.toOSType((char) byteBuffer.get(), (char) byteBuffer.get(), (char) byteBuffer.get(), (char) byteBuffer.get()));
                            System.out.println(MessageFormat.format("codec: {0} Description: {1}", cd2, cd2.getInformationString()));
                        }
                    } catch (Exception e) {
                        //                    e.printStackTrace();
                    }
                    try {

                        UserData userData = movie.getUserData();
                        // note: this throws an exception if there's no such
                        // userdata object [um... ow].  The error code appears
                        // to be -2026 (userDataItemNotFound), so you could
                        // check for that with QTException.errorCode() if
                        // you wanted to be really sure
                        QTHandle metaHandle = userData.getData(QuicktimeTag.FCC_meta, 1);
                        System.out.println("got \'meta\' atom");
                        System.out.println("populateFromMetaAtom()");
                        byte[] metaBytes = metaHandle.getBytes();

                        // locate the "ilst" pseudo-atom, ignoring first 4 bytes
                        System.out.println("Looking for ilst, " + QuicktimeTag.FCC_ilst);
                        PseudoAtomPointer ilst = findPseudoAtom(metaBytes, 4, QuicktimeTag.FCC_ilst);
                        System.out.println("Found ilst at " + ilst.offset);

                        // iterate over the pseudo-atoms inside the "ilst"
                        // building lists of tags and values from which we'll
                        // create arrays for the DefaultTableModel constructor
                        int off = ilst.offset + 8;
                        // ArrayList foundTags = new ArrayList(TAG_NAMES.length);
                        // ArrayList foundValues = new ArrayList(TAG_NAMES.length);
                        ArrayList<Pair<QuicktimeTag, ByteBuffer>> foundTags = new ArrayList<Pair<QuicktimeTag, ByteBuffer>>();
                        while (off < metaBytes.length) {
                            PseudoAtomPointer atom = findPseudoAtom(metaBytes, off, -1);
                            System.out.println(MessageFormat.format("Found {0} atom at {1}", Integer.toString(atom.type, 16), Integer.toString(atom.offset, 16)));
                            QuicktimeTag tagName = QuicktimeTag.fccMap.get(atom.type);//valueOf(String) tagMap.get(new Integer(atom.type)));
                            if (tagName != null) {

                                System.out.println(MessageFormat.format("That''s {0}", tagName));
                                // if we match a type, read everything after byte 24
                                // which skips size, type, size, 'data', 8 junk bytes
                                byte[] valueBytes = new byte[atom.atomSize - 24];
                                System.arraycopy(metaBytes, atom.offset + 24, valueBytes, 0, valueBytes.length);

                                // note: this approach is stupid about UTF-8'ed data
                                String value = new String(valueBytes);

                                System.out.println(MessageFormat.format("Value is {0}", value));
                                foundTags.add(new Pair(tagName, value));
                            } // if tagName != null
                            off = atom.offset + atom.atomSize;
                        }
                        // now build up 2-d array so we can set a new table model
                        Object[][] metaModel = new Object[foundTags.size()][3];
                        for (int i = 0; i < metaModel.length; i++) {
                            metaModel[i][0] = foundTags.get(i).first.desc;
                            metaModel[i][1] = Integer.toString(foundTags.get(i).first.getFourcc(), 16);
                            metaModel[i][2] = foundTags.get(i).second;
                        }
                        for (Object[] objects : metaModel) {
                            System.out.println(MessageFormat.format("d{0} k{1} v{2}", objects[0], objects[1], objects[2]));
                        }

                    } catch (QTException e) {
                        System.out.println("poof " + e.errorCodeToString());
                    }
                }
            }

            System.out.println(MessageFormat.format("{0}", movie.getClass().getName()));
            int duration = movie.getDuration();

            float rate = movie.getRate();


            System.out.println(MessageFormat.format("--- dur: {0} rate: {1} ", duration, String.valueOf(rate)));


            UserData userData = movie.getUserData();

            // build up list of suitable components
            Vector<ExportChoice> choices = getMovieChoices(movie);
            ExportChoice choice = getRequestedExportChoice(choices);
            System.out.println(MessageFormat.format("chose {0}", choice.name));

            // create an exporter


            MovieExporter exporter = new MovieExporter(choice.identifier);

            if (exporter != null) {
                if (exporter.getInfo() != null) {

                    ComponentDescription info = exporter.getInfo();

                    int typ = info.getType();
                    System.out.println(MessageFormat.format("info:{0} type:{1} sub:{2} subType:{3} manuf:{4}", info.getInformationString(), pwrint(typ), pwrint(info.getSubType()), pwrint(info.getManufacturer()), pwrint(info.getFlags())));

                }
            }
            boolean success = doExport(movie, exporter);

            // need to explicitly quit (since awt is running)
            if (success) System.exit(0);
        } catch (QTException qte) {
            qte.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String pwrint(int typ) throws IOException {
        return cString((ByteBuffer) bwrint(typ));
    }

    private Buffer bwrint(int typ) {
        return ByteBuffer.allocate(4).putInt(typ).rewind();
    }

    private static boolean doExport(Movie movie, MovieExporter exporter) throws QTException, FileNotFoundException {
        boolean success = true;
        QTFile saveFile = new QTFile(new File(MessageFormat.format("kit.{0}", String.valueOf(System.currentTimeMillis()))));

        // do the export
        /* this works too, assuming you set a file up front
        exporter.toFile (saveFile,
                 movie,
                 null,
                 0,
                 movie.getDuration());
        */

        movie.setProgressProc();
        movie.convertToFile(null, saveFile, StdQTConstants.kQTFileTypeMovie, StdQTConstants.kMoviePlayer, IOConstants.smSystemScript, StdQTConstants.showUserSettingsDialog | StdQTConstants.movieToFileOnlyExport | StdQTConstants.movieFileSpecValid, // flags
                exporter);
        return success;
    }

    private ExportChoice getRequestedExportChoice(Vector<ExportChoice> choices) {
        // offer a choice of movie exporters
        JComboBox exportCombo = new JComboBox(choices);
        JOptionPane.showMessageDialog(null, exportCombo, "Choose exporter", JOptionPane.PLAIN_MESSAGE);
        return (ExportChoice) exportCombo.getSelectedItem();
    }

    private Vector<ExportChoice> getMovieChoices(Movie movie) throws QTException {
        Vector<ExportChoice> choices = new Vector<ExportChoice>();
        ComponentIdentifier ci = null;
        ComponentDescription cd = new ComponentDescription(StdQTConstants.movieExportType);
        while (null != (ci = ComponentIdentifier.find(ci, cd))) {
            // check to see that the movie can be exported
            // with this component (this throws some obnoxious
            // exceptions, maybe a bit expensive?)
            try {
                MovieExporter exporter = new MovieExporter(ci);
                if (exporter.validate(movie, null)) {
                    ExportChoice choice = new ExportChoice(ci.getInfo().getName(), ci);

                    choices.addElement(choice);
                }
            } catch (StdQTException expE) {
                System.out.println("** can't validate " + ci.getInfo().getName() + " **");

            } // ow!
        }
        return choices;
    }

    private static Movie getContentMovie(QTFile file) throws QTException {
        OpenMovieFile omFile = OpenMovieFile.asRead(file);


        Movie movie = Movie.fromFile(omFile);
        //  movie.showInformation();
        int trackCount = movie.getTrackCount();
        for (int i = 0; i < trackCount; ++i) {
            Track track = movie.getIndTrack(i + 1);
            boolean b = track.getEnabled();
            if (!b) continue;


            Media media = track.getMedia();
            int sampleDescriptionCount = media.getSampleDescriptionCount();

            for (int j = 0; j < sampleDescriptionCount; j++) {

                try {
                    SampleDescription sd = media.getSampleDescription(j + 1);
                    Class<? extends SampleDescription> aClass = sd.getClass();
                    Method method1 = aClass.getMethod("getCType");

                    if (method1 != null) {
                        int ctype = (Integer) method1.invoke(sd);

                        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
                        IntBuffer intBuffer = byteBuffer.asIntBuffer();

                        intBuffer.put(ctype);
                        byteBuffer.rewind();
                        ComponentDescription cd2 = new ComponentDescription(QTUtils.toOSType((char) byteBuffer.get(), (char) byteBuffer.get(), (char) byteBuffer.get(), (char) byteBuffer.get()));
                        System.out.println(MessageFormat.format("codec: {0} Description: {1}", cd2, cd2.getInformationString()));
                    }
                } catch (Exception e) {
//                    e.printStackTrace();
                }
            }
        }

        System.out.println(MessageFormat.format("{0}", movie.getClass().getName()));
        int duration = movie.getDuration();

        float rate = movie.getRate();


        System.out.println(MessageFormat.format("--- dur: {0} rate: {1} ", duration, String.valueOf(rate)));

        return movie;
    }

    private static QTFile requestFile() throws QTException {
        QTSessionCheck.check();
        QTFile file = QTFile.standardGetFilePreview(QTFile.kStandardQTFileTypes);

        boolean readable = file.canRead();
        boolean writable = file.canWrite();
        long len = file.length();
        System.out.println(MessageFormat.format("file: {0} r:{1} w:{2} l:{3}", file.toString(), readable, writable, len));

        return file;
    }

    public static final class ExportChoice {
        final String name;
        final ComponentIdentifier identifier;

        public ExportChoice(String n, ComponentIdentifier ci) {
            name = n;
            identifier = ci;
        }

        public String toString() {
            return name;
        }
    }


    /**
     * find the given type in the byte array, starting at
     * the start position.  Returns the offset within the
     * bye array that begins this pseudo-atom.  a helper method
     * to populateFromMetaAtom().
     *
     * @param bytes byte array to search
     * @param start offset to start at
     * @param type  type to search for.  if -1, returns first
     *              atom with a plausible size
     * @return
     */
    private static PseudoAtomPointer findPseudoAtom(byte[] bytes, int start, int type) {
        // read size, then type
        // if size is bogus, forget it, increment offset, and try again
        int off = start;
        while ((off < bytes.length - 8)) {
            System.out.println("findPseudoAtom, off = " + off);
            // read 32 bits of atom size
            // use BigInteger to convert bytes to long
            // (instead of signed int)
            byte sizeBytes[] = new byte[4];
            System.arraycopy(bytes, off, sizeBytes, 0, 4);
            BigInteger atomSizeBI = new BigInteger(sizeBytes);
            long atomSize = atomSizeBI.longValue();

            // don't bother if the size would take us beyond end of
            // array, or is impossibly small
            if ((atomSize > 7L) && ((long) off + atomSize <= (long) bytes.length)) {

                System.out.println("munge type from " + ((int) bytes[off + 4]) + ' ' + ((int) bytes[off + 5]) + ' ' + ((int) bytes[off + 6]) + ' ' + ((int) bytes[off + 7]));


                int aType = (((int) bytes[off + 4]) << 24) | (((int) bytes[off + 5]) << 16) | (((int) bytes[off + 6]) << 8) | ((int) bytes[off + 7]);
                // System.out.println ("Size " + atomSize +
                //                     ", type " + de4CC (aType));
                if ((type == aType) || (type == -1)) return new PseudoAtomPointer(off, (int) atomSize, aType);
                else off += (int) atomSize;

            } else {
                System.out.println("bogus atom size " + atomSize);
                // well, how did this happen?  increment off and try again
                off++;
            }
        } // while
        return null;
    }

}
