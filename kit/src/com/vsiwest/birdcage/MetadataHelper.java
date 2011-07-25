package com.vsiwest.birdcage;
//
//import quicktime.QTException;
//import quicktime.io.IOConstants;
//import quicktime.std.movies.Movie;
//import quicktime.std.movies.media.UserData;
//import quicktime.util.QTHandle;
//
//import javax.swing.table.DefaultTableModel;
//import java.math.BigInteger;
//import java.util.ArrayList;

//
class MetadataHelper {
//
//    /**
//     * FOUR_CHAR_CODE for 'meta'
//     */
//    public static final int FCC_meta = 0x6d657461;
//
//    /**
//     * FOUR_CHAR_CODE for 'ilst'
//     */
//    public final static int FCC_ilst = 0x696c7374;
//
//    public static final String[] TABLE_HEADERS = {
//            "Tag", "Value"
//    };
//
////    public static final String[][] EMPTY_TABLE_CONTENTS = {};
////
////    /* technote on using QT to get ID3 tags:
////       http://developer.apple.com/qa/qa2001/qa1135.html
////     */
////
////    /* these values are straight out of Movies.h
////       (want them in StdQtConstants? file a feature request
////       at http://bugreport.apple.com/  )
////    */
////    final static int  kUserDataTextAlbum            = 0xA9616C62; /*'�alb' */
////    final static int  kUserDataTextArtist           = 0xA9415254;
////    final static int  kUserDataTextAuthor           = 0xA9617574; /*'�aut' */
////    final static int  kUserDataTextChapter          = 0xA9636870; /*'�chp' */
////    final static int  kUserDataTextComment          = 0xA9636D74; /*'�cmt' */
////    final static int  kUserDataTextComposer         = 0xA9636F6D; /*'�com' */
////    final static int  kUserDataTextCopyright        = 0xA9637079; /*'�cpy' */
////    final static int  kUserDataTextCreationDate     = 0xA9646179; /*'�day' */
////    final static int  kUserDataTextDescription      = 0xA9646573; /*'�des' */
////    final static int  kUserDataTextDirector         = 0xA9646972; /*'�dir' */
////    final static int  kUserDataTextDisclaimer       = 0xA9646973; /*'�dis' */
////    final static int  kUserDataTextEncodedBy        = 0xA9656E63; /*'�enc' */
////    final static int  kUserDataTextFullName         = 0xA96E616D; /*'�nam' */
////    final static int  kUserDataTextGenre            = 0xA967656E; /*'�gen' */
////    final static int  kUserDataTextHostComputer     = 0xA9687374; /*'�hst' */
////    final static int  kUserDataTextInformation      = 0xA9696E66; /*'�inf' */
////    final static int  kUserDataTextKeywords         = 0xA96B6579; /*'�key' */
////    final static int  kUserDataTextMake             = 0xA96D616B; /*'�mak' */
////    final static int  kUserDataTextModel            = 0xA96D6F64; /*'�mod' */
////    final static int  kUserDataTextOriginalArtist   = 0xA96F7065; /*'�ope' */
////    final static int  kUserDataTextOriginalFormat   = 0xA9666D74; /*'�fmt' */
////    final static int  kUserDataTextOriginalSource   = 0xA9737263; /*'�src' */
////    final static int  kUserDataTextPerformers       = 0xA9707266; /*'�prf' */
////    final static int  kUserDataTextProducer         = 0xA9707264; /*'�prd' */
////    final static int  kUserDataTextProduct          = 0xA9505244;
////    final static int  kUserDataTextSoftware         = 0xA9737772; /*'�swr' */
////    final static int  kUserDataTextSpecialPlaybackRequirements = 0xA9726571; /*'�req' */
////    final static int  kUserDataTextTrack            = 0xA974726B; /*'�trk' */
////    final static int  kUserDataTextWarning          = 0xA977726E; /*'�wrn' */
////    final static int  kUserDataTextWriter           = 0xA9777274; /*'�wrt' */
////    final static int  kUserDataTextURLLink          = 0xA975726C; /*'�url' */
////    final static int  kUserDataTextEditDate1        = 0xA9656431; /*'�ed1' */
////
////    /* This array maps all the tag constants to human-readable
////       strings (I18N note: could be a localized properties file!)
////     */
////    private static final Object[][] TAG_NAMES = {
////        {kUserDataTextAlbum, "Album"},
////        {kUserDataTextArtist,"Artist" },
////        {kUserDataTextAuthor, "Author"},
////        {kUserDataTextChapter, "Chapter"},
////        {kUserDataTextComment, "Comment"},
////        {kUserDataTextComposer, "Composer"},
////        {kUserDataTextCopyright, "Copyright"},
////        {kUserDataTextCreationDate, "Created"},
////        {kUserDataTextDescription, "Description"},
////        {kUserDataTextDirector, "Director"},
////        {kUserDataTextDisclaimer, "Disclaimer"},
////        {kUserDataTextEncodedBy, "Encoder"},
////        {kUserDataTextFullName, "Full Name"},
////        {kUserDataTextGenre, "Genre"},
////        {kUserDataTextHostComputer, "Host"},
////        {kUserDataTextInformation, "Information"},
////        {kUserDataTextKeywords, "Keywords"},
////        {kUserDataTextMake, "Make"},
////        {kUserDataTextModel, "Model"},
////        {kUserDataTextOriginalArtist, "Original Artist"},
////        {kUserDataTextOriginalFormat, "Original Format"},
////        {kUserDataTextOriginalSource, "Original Source"},
////        {kUserDataTextPerformers, "Performers"},
////        {kUserDataTextProducer, "Producer"},
////        {kUserDataTextProduct, "Product"},
////        {kUserDataTextSoftware, "Software"},
////        {kUserDataTextSpecialPlaybackRequirements, "Special Requirements"},
////        {kUserDataTextTrack, "Track"},
////        {kUserDataTextWarning, "Warning"},
////        {kUserDataTextWriter, "Writer"},
////        {kUserDataTextURLLink, "URL"},
////        {kUserDataTextEditDate1, "Edit Date 1" }
////    };
////
////    /** HashMap version of TAG_NAMES
////     */
////    private HashMap tagMap;
////
////    /** Builds an empty table
////     */
////    public MetadataJTable() {
////        super (new DefaultTableModel(EMPTY_TABLE_CONTENTS,
////                                      TABLE_HEADERS));
////        makeTagMap();
////    }
////
////    /** Builds a table and sets metadata from movie m
////     */
////    public MetadataJTable (Movie m) throws QTException {
////        this();
////        setMovie (m);
////    }
////
////    private void makeTagMap() {
////        tagMap = new HashMap (TAG_NAMES.length);
////        for (int i=0; i<TAG_NAMES.length; i++) {
////            tagMap.put (TAG_NAMES[i][0],
////                        TAG_NAMES[i][1]);
////        }
////    }
//
//    /**
//     * parses out metadata and resets the table model
//     */
//    public void setMovie(Movie movie) throws QTException {
//        if (movie == null) {
////            setModel(new DefaultTableModel(EMPTY_TABLE_CONTENTS,
////                    TABLE_HEADERS));
//            return;
//        }
//
//        // does this movie use ID3 tags or a big "meta" tag?
//        UserData userData = null;
//        try {
//            userData = movie.getUserData();
//        } catch (QTException qte) {
//            System.out.println("Movie has no user data");
////            setModel(new DefaultTableModel(EMPTY_TABLE_CONTENTS,
////                    TABLE_HEADERS));
//            return;
//        }
//        // do we have "meta" (ie, is this an iTunes AAC)?
//        try {
//            // note: this throws an exception if there's no such
//            // userdata object [um... ow].  The error code appears
//            // to be -2026 (userDataItemNotFound), so you could
//            // check for that with QTException.errorCode() if
//            // you wanted to be really sure
//            QTHandle metaHandle = userData.getData(FCC_meta, 1);
//            // System.out.println ("got \'meta\' atom");
//            populateFromMetaAtom(metaHandle);
//        } catch (QTException qte) {
//            System.out.println("QTException, code=" +
//                    qte.errorCode());
//            // System.out.println ("no \'meta\', assuming ID3 tags");
//            populateLikeID3(userData);
//        }
//    }
//
//    /**
//     * Look for the Apple atoms in this userdata and
//     * construct an appropriate table model.
//     */
//    private void populateLikeID3(UserData userData) {
//        System.out.println("populateLikeID3()");
//        // loop through all known tags, building lists of
//        // tags and values from which we'll create arrays
//        // for the DefaultTableModel constructor
//        ArrayList foundTags = new ArrayList(QuicktimeTag.values().length);
//        ArrayList foundValues = new ArrayList(QuicktimeTag.values().length);
//
//        userData.
//
//        for (int i = 0; i < QuicktimeTag.values().length; i++) {
//            try {
//                int type = QuicktimeTag.values()[i].fourcc;
//                String value =
//                        userData.getTextAsString(type,
//                                1,
//                                IOConstants.langUnspecified);
//                if (value != null) {
//                    foundTags.add(TAG_NAMES[i][1]);
//                    foundValues.add(value);
//                }
//            } catch (QTException qte) {
//            } // didn't have this tag
//        } // for
//        // now build up 2-d array so we can set a new table model
//        Object[][] metaModel = new Object[foundTags.size()][2];
//        for (int i = 0; i < metaModel.length; i++) {
//            metaModel[i][0] = foundTags.get(i);
//            metaModel[i][1] = foundValues.get(i);
//        }
//        // reset table model!
//        setModel(new DefaultTableModel(metaModel, TABLE_HEADERS));
//    } // populateLikeID3
//
//
//    /**
//     * Dig through meta atom of an iTunes AAC (.m4a or .m4p)
//     * to get values to the Apple tags, build an appropriate
//     * table model.
//     */
//    private void populateFromMetaAtom(QTHandle metaHandle) {
//        System.out.println("populateFromMetaAtom()");
//        byte[] metaBytes = metaHandle.getBytes();
//
//        // locate the "ilst" pseudo-atom, ignoring first 4 bytes
//        // System.out.println ("Looking for ilst, " + FCC_ilst);
//        PseudoAtomPointer ilst = findPseudoAtom(metaBytes, 4, FCC_ilst);
//        // System.out.println ("Found ilst at " + ilst.offset);
//
//        // iterate over the pseudo-atoms inside the "ilst"
//        // building lists of tags and values from which we'll
//        // create arrays for the DefaultTableModel constructor
//        int off = ilst.offset + 8;
//        ArrayList foundTags = new ArrayList(TAG_NAMES.length);
//        ArrayList foundValues = new ArrayList(TAG_NAMES.length);
//        while (off < metaBytes.length) {
//            PseudoAtomPointer atom = findPseudoAtom(metaBytes, off, -1);
//            // System.out.println ("Found " + de4CC (atom.type) +
//            //                     " atom at " + atom.offset);
//            String tagName = (String) tagMap.get(new Integer(atom.type));
//            if (tagName != null) {
//                // System.out.println ("That's " + tagName);
//                // if we match a type, read everything after byte 24
//                // which skips size, type, size, 'data', 8 junk bytes
//                byte[] valueBytes = new byte[atom.atomSize - 24];
//                System.arraycopy(metaBytes,
//                        atom.offset + 24,
//                        valueBytes,
//                        0,
//                        valueBytes.length);
//                // note: this approach is stupid about UTF-8'ed data
//                String value = new String(valueBytes);
//                // System.out.println ("Value is " + value);
//                foundTags.add(tagName);
//                foundValues.add(value);
//            } // if tagName != null
//            off = atom.offset + atom.atomSize;
//        }
//        // now build up 2-d array so we can set a new table model
//        Object[][] metaModel = new Object[foundTags.size()][2];
//        for (int i = 0; i < metaModel.length; i++) {
//            metaModel[i][0] = foundTags.get(i);
//            metaModel[i][1] = foundValues.get(i);
//        }
//        // reset table model!
//        setModel(new DefaultTableModel(metaModel, TABLE_HEADERS));
//    }
//
//
//    /**
//     * find the given type in the byte array, starting at
//     * the start position.  Returns the offset within the
//     * bye array that begins this pseudo-atom.  a helper method
//     * to populateFromMetaAtom().
//     *
//     * @param bytes byte array to search
//     * @param start offset to start at
//     * @param type  type to search for.  if -1, returns first
//     *              atom with a plausible size
//     */
//    private PseudoAtomPointer findPseudoAtom(byte[] bytes,
//                                             int start,
//                                             int type) {
//        // read size, then type
//        // if size is bogus, forget it, increment offset, and try again
//        int off = start;
//        boolean found = false;
//        while ((!found) &&
//                (off < bytes.length - 8)) {
//            System.out.println("findPseudoAtom, off = " + off);
//            // read 32 bits of atom size
//            // use BigInteger to convert bytes to long
//            // (instead of signed int)
//            byte sizeBytes[] = new byte[4];
//            System.arraycopy(bytes, off, sizeBytes, 0, 4);
//            BigInteger atomSizeBI = new BigInteger(sizeBytes);
//            long atomSize = atomSizeBI.longValue();
//
//            // don't bother if the size would take us beyond end of
//            // array, or is impossibly small
//            if ((atomSize > 7) &&
//                    (off + atomSize <= bytes.length)) {
//
//                System.out.println("munge type from " +
//                        ((int) bytes[off + 4]) + " " +
//                        ((int) bytes[off + 5]) + " " +
//                        ((int) bytes[off + 6]) + " " +
//                        ((int) bytes[off + 7]));
//
//
//                int aType =
//                        (((int) bytes[off + 4]) << 24) |
//                                (((int) bytes[off + 5]) << 16) |
//                                (((int) bytes[off + 6]) << 8) |
//                                ((int) bytes[off + 7]);
//                // System.out.println ("Size " + atomSize +
//                //                     ", type " + de4CC (aType));
//                if ((type == aType) ||
//                        (type == -1))
//                    return new PseudoAtomPointer(off, (int) atomSize, aType);
//                else
//                    off += atomSize;
//
//            } else {
//                System.out.println("bogus atom size " + atomSize);
//                // well, how did this happen?  increment off and try again
//                off++;
//            }
//        } // while
//        return null;
//    }
//
//    /**
//     * Inner class to represent atom-like com.vsiwest.dws.com.vsiwest.dws.com.vsiwest.dws.structures inside
//     * the meta atom, designed to work with the byte array
//     * of the meta atom (ie, just wraps pointers to the
//     * beginning of the atom and its computed size and type)
//     */
//    class PseudoAtomPointer {
//        int offset;
//        int atomSize;
//        int type;
//
//        public PseudoAtomPointer(int o, int s, int t) {
//            offset = o;
//            atomSize = s;
//            type = t;
//        }
//
//    }
//
}
