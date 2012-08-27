package com.vsiwest.lockpick;

import com.vsiwest.kit.*;
import static com.vsiwest.lockpick.BoxFileModel.*;
import com.vsiwest.util.*;

import javax.swing.tree.*;
import java.nio.*;
import java.text.*;

enum IdTypes {
    isom("ISO_14496-1 Base Media"),
    iso2("ISO 14496-12 Base Media"),
    mp41("ISO 14496-1 vers. 1"),
    mp42("ISO 14496-1 vers. 2"),
    qt("quicktime movie"),
    avc1("JVT AVC"),
    /**
     * + ASCII value
     */
    violates_symbol_spec_3gp("3G MP4 profile"),
    mmp4("3G Mobile MP4"),
    M4A("Apple AAC audio w/ iTunes info"),
    M4P("AES encrypted audio"),
    M4B("Apple audio w/ iTunes position"),
    M4V("Apple video w/ iTunes info"),
    mp71("ISO 14496-12 MPEG-7 meta data");

    private String description;

    IdTypes(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

//x="@"  ?
enum IlstDescription {
    AMP_art("Artist"),
    AMP_alb("Album"),
    aART("Album Artist"),
    AMP_cmt("Comment"),
    AMP_day("Year"),
    AMP_nam("Title"),
    AMP_gen("Genre"),
    trkn("Track number"),
    disk("Disk number"),
    AMP_wrt("Composer"),
    AMP_too("Encoder"),
    tmpo("BPM"),
    cprt("Copyright"),
    cpil("Compilation"),
    covr("Artwork"),
    rtng("Rating/Advisory"),
    AMP_grp("Grouping"),
    stik("?? (stik)"),
    pcst("Podcast"),
    catg("Category"),
    keyw("Keyword"),
    purl("Podcast URL"),
    egid("Episode Global Unique ID"),
    desc("Description"),
    AMP_lyr("Lyrics"),
    tvnn("TV Network Name"),
    tvsh("TV Show Name"),
    tven("TV Episode Number"),
    tvsn("TV Season"),
    tves("TV Episode"),
    purd("Purchase Date"),
    pgap("Gapless Playback"),;
    private final String description;

    IlstDescription(String description) {

        this.description = description;

    }

    static public String getDesc(String key) {
        String s = key.replaceFirst("?", "AMP_");
        return valueOf(s).description;

    }
}

/**
 * It's not cool to violate the slightest nuance of our IP and/or copyrights
 * and we'll pursue such actions with a vengeance befitting an iraqi dictator
 * if you feel it is neccessary to do such things.
 */
enum BoxTypeMeta {
    ftyp(4.3, "file type and compatibility") {

        public Pair<DefaultMutableTreeNode, ByteBuffer> leafNodeView(Pair<DefaultMutableTreeNode, ByteBuffer> chunk) {
            DefaultMutableTreeNode spoke = chunk.first;
            ByteBuffer buf = chunk.second;
//8+ bytes file type box = long unsigned offset + long ASCII text string 'ftyp'
//-> 4 bytes major brand = long ASCII text main type string

            int brand = buf.getInt();

            String brandDesc = cString(brand);
            String description1 = null;
            try {
                final IdTypes types = IdTypes.valueOf(brandDesc.trim());
                description1 = types.getDescription();

            } catch (IllegalArgumentException e) {
                Kit.log("werp!");

            }

            ByteBuffer bb = ByteBuffer.allocateDirect(4);
            bb.put((byte) 'q').put((byte) 't').put((byte) ' ').put((byte) ' ');
            int qtconst = ((ByteBuffer) bb.rewind()).getInt();

//            Kit.log("we might find find major=" + qtconst);


            spoke.add(new DefaultMutableTreeNode("Brand Major: " + brandDesc + (description1 != null ? " - " + description1 : "")));
            Kit.log("qtconst: " + qtconst + " fourcc: " + brandDesc + " numerical: " + brand + "" + (description1 != null ? " - " + description1 : ""));
            isQuicktime = qtconst == brand;

//-> 4 bytes major brand version = long unsigned main type revision value
            description1 = null;
            final String s = cString(buf.getInt());
            try {
                final IdTypes types = IdTypes.valueOf(s.trim());
                description1 = types.getDescription();

            } catch (IllegalArgumentException e) {
                Kit.log("werp!");

            }


            spoke.add(new DefaultMutableTreeNode("Brand Minor: " + s + "" + (description1 != null ? " - " + description1 : "")));
//-> 4+ bytes compatible brands = list of long ASCII text used technology strings
            if (buf.hasRemaining()) {
                DefaultMutableTreeNode child = new DefaultMutableTreeNode("compatible");
                spoke.add(child);
                while (buf.hasRemaining()) {
                    brandDesc = cString(buf.getInt());
                    description1 = null;
                    try {
                        final IdTypes types = IdTypes.valueOf(brandDesc.trim());
                        description1 = types.getDescription();
                    } catch (IllegalArgumentException e) {
                    }
                    final String userObject = brandDesc + (description1 != null ? " - " + description1 : "");
                    child.add(new DefaultMutableTreeNode(userObject));
                }
            }
//- types are ISO 14496-1 Base Media = isom ; ISO 14496-12 Base Media = iso2
//- types are ISO 14496-1 vers. 1 = mp41 ; ISO 14496-1 vers. 2 = mp42
//- types are quicktime movie = 'qt  ' ; JVT AVC = avc1
//- types are 3G MP4 profile = '3gp' + ASCII value ; 3G Mobile MP4 = mmp4
//- types are Apple AAC audio w/ iTunes info = 'M4A ' ; AES encrypted audio = 'M4P '
//- types are Apple audio w/ iTunes position = 'M4B ' ; ISO 14496-12 MPEG-7 meta data = 'mp71'
//- NOTE: All compatible with 'isom', vers. 1 uses no Scene Description Tracks,
//vers. 2 uses the full part one spec, M4A uses custom ISO 14496-12 info,
//qt means the format complies with the original Apple spec, 3gp uses sample
//descriptions in the same style as the original Apple spec.
            return chunk;
        }
    },
    moov(8.1, "container for all the meta-data", true),
    mvhd(8.3, "movie header, overall declarations"),
    trak(8.4, "container for an individual track or stream", true),
    tkhd(8.5, "track header, overall information about the track"),
    tref(8.6, "track reference container"),
    edts(8.25, "edit list container", true),
    elst(8.26, "an edit list"),
    mdia(8.7, "container for the media information in a track", true),
    mdhd(8.8, "media header, overall information about the media"),
//    hdlr(8.9, "handler, declares the media (handler) type"),
    minf(8.10, "media information container", true),
    vmhd(8.11, "video media header, overall information video track only)"),
    smhd(8.11, "sound media header, overall information sound track only)"),
    hmhd(8.11, "hint media header, overall information hint track only)"),
//    dinf(8.12, "data information box, container", true),
//    dref(8.13, "data reference box, declares source(s) of media data in track"),
    stbl(8.14, "sample table box, container for the time/space map", true),
    stsd(8.16, "sample descriptions (codec types, initialization etc.)") {
        public Pair<DefaultMutableTreeNode, ByteBuffer> leafNodeView(Pair<DefaultMutableTreeNode, ByteBuffer> chunk) {
            //version is an integer that specifies the version of this box
            ByteBuffer buf = chunk.second;
            version32(chunk);
            //entry_count is an integer that gives the number of entries in the following table
            DefaultMutableTreeNode entries = entries32(chunk);
            BoxFileModel boxFileModel = new BoxFileModel(buf.slice(), null);
            BoxFileTreeView.populateTreeNode(entries, boxFileModel);
//            return dumpChunk(chunk);
//            buf.position(buf.limit());
            //SampleEntry is the appropriate sample entry.

//                int format = buf.getInt();
//                //data_reference_index is an integer that contains the index of the data reference to use to retrieve
//                //data associated with samples that use this sample description. Data references are stored in Data
//                //Reference Boxes. The index ranges from 1 to the number of data references.
//                //ChannelCount is either 1 (mono) or 2 (stereo)
//                //SampleSize is in bits, and takes the default value of 16
//                //SampleRate is the sampling rate expressed as a 16.16 fixed-point number (hi.lo)
//                //resolution fields give the resolution of the image in pixels-per-inch, as a fixed 16.16 number
//                //frame_count indicates how many frames of compressed video are stored in each sample. The default is
//                //1, for one frame per sample; it may be more than 1 for multiple frames per sample
//                //Compressorname is a name, for informative purposes. It is formatted in a fixed 32-byte field, with the
//                //first byte set to the number of bytes to be displayed, followed by that number of bytes of displayable
//                //data, and then padding to complete 32 bytes total (including the size byte). The field may be set to 0.
//                //depth takes one of the following values
//                //0x0018 ? images are in colour with no alpha
//                //width and height are the maximum visual width and height of the stream described by this sample
//                //description, in pixels
//            }
            return chunk;
        }},
    stts(8.15, "(decoding) time-to-sample") {
        public Pair<DefaultMutableTreeNode, ByteBuffer> leafNodeView(Pair<DefaultMutableTreeNode, ByteBuffer> chunk) {
            DefaultMutableTreeNode spoke = chunk.first;
            ByteBuffer buf = chunk.second;
            version32(chunk);
            DefaultMutableTreeNode child = entries32(chunk);
            while (buf.hasRemaining())
                child.add(new DefaultMutableTreeNode(MessageFormat.format("count, delta: {0}, {1}", buf.getInt(), Integer.toHexString(buf.getInt()))));

            return chunk;
        }},
    ctts(8.15, "(composition) time to sample") {
        public Pair<DefaultMutableTreeNode, ByteBuffer> leafNodeView(Pair<DefaultMutableTreeNode, ByteBuffer> chunk) {
            DefaultMutableTreeNode spoke = chunk.first;
            ByteBuffer buf = chunk.second;
            version32(chunk);
            DefaultMutableTreeNode child = entries32(chunk);
            while (buf.hasRemaining())
                child.add(new DefaultMutableTreeNode(MessageFormat.format("count, offset: {0}, {1}", buf.getInt(), Integer.toHexString(buf.getInt()))));
            return chunk;
        }},
    stsc(8.18, "sample-to-chunk, partial data-offset information") {
        public Pair<DefaultMutableTreeNode, ByteBuffer> leafNodeView(Pair<DefaultMutableTreeNode, ByteBuffer> chunk) {
            ByteBuffer buf = chunk.second;
            version32(chunk);
            DefaultMutableTreeNode child = entries32(chunk);
            while (buf.hasRemaining())
                child.add(new DefaultMutableTreeNode(MessageFormat.format("1st, Size, Index: {0},{1},{2}", Integer.toHexString(buf.getInt()), buf.getInt(), buf.getInt())));
            return chunk;
        }},
    stsz(8.17, "sample sizes (framing)") {
        public Pair<DefaultMutableTreeNode, ByteBuffer> leafNodeView(Pair<DefaultMutableTreeNode, ByteBuffer> chunk) {
            DefaultMutableTreeNode spoke = chunk.first;
            ByteBuffer buf = chunk.second;
            version32(chunk);

            int size = buf.getInt();
            spoke.add(new DefaultMutableTreeNode(MessageFormat.format("Sample Size: {0}", size)));
            DefaultMutableTreeNode child = entries32(chunk);
            while (buf.hasRemaining()) child.add(new DefaultMutableTreeNode(buf.getInt()));
            return chunk;
        }
    },
    stz2(8.17, "compact sample sizes (framing)") {
        public Pair<DefaultMutableTreeNode, ByteBuffer> leafNodeView(Pair<DefaultMutableTreeNode, ByteBuffer> chunk) {
            DefaultMutableTreeNode spoke = chunk.first;
            ByteBuffer buf = chunk.second;

            version32(chunk);
            int samplesize = buf.getInt();
            spoke.add(new DefaultMutableTreeNode(MessageFormat.format("SampleSize: {0}", samplesize)));
            DefaultMutableTreeNode child = entries32(chunk);

            while (buf.hasRemaining()) {

                if (samplesize == 16) {
                    new DefaultMutableTreeNode(buf.getShort());
                } else if (samplesize == 8) {
                    new DefaultMutableTreeNode(buf.get());
                } else if (samplesize == 4) {
                    byte b = buf.get();
                    new DefaultMutableTreeNode(b & 0xf0 >> 4);
                    new DefaultMutableTreeNode(b & 0xf);
                }
                child.add(new DefaultMutableTreeNode(buf.getInt()));
            }
            return chunk;
        }
    },
    stco(8.19, "chunk offset, partial data-offset information") {
        public Pair<DefaultMutableTreeNode, ByteBuffer> leafNodeView(Pair<DefaultMutableTreeNode, ByteBuffer> chunk) {

            DefaultMutableTreeNode spoke = chunk.first;
            ByteBuffer buf = chunk.second;
            version32(chunk);

            DefaultMutableTreeNode child = entries32(chunk);

            while (buf.hasRemaining())
                child.add(new DefaultMutableTreeNode("0 + " + Integer.toHexString(buf.getInt())));
            return chunk;
        }
    },
    co64(8.19, "64-bit chunk offset") {
        public Pair<DefaultMutableTreeNode, ByteBuffer> leafNodeView(Pair<DefaultMutableTreeNode, ByteBuffer> chunk) {

            DefaultMutableTreeNode spoke = chunk.first;
            ByteBuffer buf = chunk.second;
            version32(chunk);

            DefaultMutableTreeNode child = entries32(chunk);

            while (buf.hasRemaining()) child.add(new DefaultMutableTreeNode("0 + " + Long.toHexString(buf.getLong())));
            return chunk;
        }
    },
    stss(8.20, "sync sample table (random access points)") {

        public Pair<DefaultMutableTreeNode, ByteBuffer> leafNodeView(Pair<DefaultMutableTreeNode, ByteBuffer> chunk) {
            ByteBuffer buf = chunk.second;
            version32(chunk);
            DefaultMutableTreeNode child = entries32(chunk);
            while (buf.hasRemaining()) {
                child.add(new DefaultMutableTreeNode(Integer.toBinaryString(buf.get())));
            }

            return chunk;
        }
    },
    stsh(8.21, "shadow sync sample table"),
    padb(8.23, "sample padding bits") {
        public Pair<DefaultMutableTreeNode, ByteBuffer> leafNodeView(Pair<DefaultMutableTreeNode, ByteBuffer> chunk) {
            ByteBuffer buf = chunk.second;

            DefaultMutableTreeNode child = entries32(chunk);

            while (buf.hasRemaining()) child.add(new DefaultMutableTreeNode(Integer.toBinaryString(buf.get())));
            return chunk;
        }
    },
    stdp(8.22, "sample degradation priority"),
    mvex(8.29, "movie extends box", true),
    trex(8.30, "track extends defaults"),
    moof(8.31, "movie fragment", true),
    mfhd(8.32, "movie fragment header"),
    traf(8.33, "track fragment", true),
    tfhd(8.34, "track fragment header"),
    trun(8.35, "track fragment run"),
    mdat(8.2, "media data container", true),
    free(8.24, "free space"),
    skip(8.24, "free space"),
    wide(8.24, "sacrifical annode for 64 bit conversion"),
    udta(8.27, "user-data", true),
    meta(8.44, " metadata", false) {

        public Pair<DefaultMutableTreeNode, ByteBuffer> leafNodeView(Pair<DefaultMutableTreeNode, ByteBuffer> chunk, BoxFileModel parentModel) {
            DefaultMutableTreeNode spoke = chunk.first;
            ByteBuffer buf = chunk.second;

            versionFlags32(spoke, buf);
            BoxFileTreeView.populateTreeNode(spoke, new BoxFileModel(buf, parentModel));
            return chunk;
        }
    },
    hdlr(8.9, "handler, declares the metadata (handler) type") {
        public Pair<DefaultMutableTreeNode, ByteBuffer> leafNodeView(final Pair<DefaultMutableTreeNode, ByteBuffer> chunk) {
            DefaultMutableTreeNode spoke = chunk.first;
            ByteBuffer buf = chunk.second;
            versionFlags32(spoke, buf);
//             -> 4 bytes QUICKTIME type = long ASCII text string (eg. Media Handler = 'mhlr')
            spoke.add(new DefaultMutableTreeNode("Type: " + cString(buf.getInt())));
//             -> 4 bytes subtype/meta data type = long ASCII text string
//                - types are MPEG-7 XML = 'mp7t' ; MPEG-7 binary XML = 'mp7b'
//                - type is APPLE meta data iTunes reader = 'mdir'
            spoke.add(new DefaultMutableTreeNode("SubType: " + cString(buf.getInt())));
//             -> 4 bytes QUICKTIME manufacturer reserved = long ASCII text string
//                  (eg. Apple = 'appl' or 0)
            spoke.add(new DefaultMutableTreeNode("manufacturer: " + cString(buf.getInt())));
//             -> 4 bytes QUICKTIME component reserved flags = long hex flags (none = 0)
            spoke.add(new DefaultMutableTreeNode("flags: " + Integer.toBinaryString(buf.getInt())));
//             -> 4 bytes QUICKTIME component reserved flags mask = long hex mask (none = 0)
            spoke.add(new DefaultMutableTreeNode("mask: " + Integer.toBinaryString(buf.getInt())));

            if (isQuicktime)
                //skip the pascal size
                buf.get();
            spoke.add(new DefaultMutableTreeNode("Meta Data Handler: " + cString(buf).trim()));
//             -> component type name ASCII string
//                 (eg. "Meta Data Handler" - no name = zero length string)

//             -> 1 byte component name string end = byte padding set to zero
//                - note: the quicktime spec uses a Pascal string
//                instead of the above C string*/


            return chunk;
        }},
    dinf(8.12, "data information box, container", true),
    dref(8.13, "data reference box, declares source(s) of metadata items"),
    ipmc(8.45, "IPMP Control Box"),
    iloc(8.44, "item location"),
    ipro(8.44, "item protection", true),
    sinf(8.45, "protection scheme information box", true),
    frma(8.45, "original format box"),
    imif(8.45, "IPMP Information box"),
    schm(8.45, "scheme type box"),
    schi(8.45, "scheme information box"),
    iinf(8.44, "item information"),
    xml(8.44, "XML container"),
    bxml(8.44, "binary XML container"),
    pitm(8.44, "primary item reference"),
    cprt(8.28, "copyright etc. ", true),
    tapt(0, "Undocumented Quicktime Chunk", true),

    ilst(0, "Itunes Metadata list") {
        //
        //* 8+ bytes optional APPLE item list box
        //             = long unsigned offset + long ASCII text string 'ilst'
        public Pair<DefaultMutableTreeNode, ByteBuffer> leafNodeView(Pair<DefaultMutableTreeNode, ByteBuffer> chunk) {
            DefaultMutableTreeNode spoke = chunk.first;
            ByteBuffer buf = chunk.second;

            while (buf.hasRemaining()) {
                int len = buf.getInt();
                String cname = cString(buf.getInt());        // "@som" thing
                ByteBuffer buf1 = (ByteBuffer) buf.slice().limit(len - 8);
                buf.position(buf.position() + len - 8);
                try {
                    String ilstDescription = IlstDescription.getDesc(cname);
                    cname += "- " + ilstDescription;
                } catch (RuntimeException e) {

                }

                DefaultMutableTreeNode child = new DefaultMutableTreeNode(cname);
                spoke.add(child);
                while (buf1.hasRemaining()) {
                    int dlen = buf1.getInt();
                    int infoType = buf1.getInt(); //'data';
                    ByteBuffer buf2 = (ByteBuffer) buf1.slice().limit(dlen - 8);
                    buf1.position(buf1.position() + dlen - 8);
                    byte version = buf2.get();
                    child.add(new DefaultMutableTreeNode(MessageFormat.format("Version: {0}", version)));
                    ByteBuffer flagBytes = ByteBuffer.allocateDirect(4);
                    flagBytes.put((byte) 0);
                    flagBytes.put(buf2.get());
                    flagBytes.put(buf2.get());
                    flagBytes.put(buf2.get());
                    int flags = ((ByteBuffer) flagBytes.rewind()).getInt();
                    if (cString(infoType).equals("data")) {
                        String flagsDesc;
                        final int reserved = buf2.getInt();
                        switch (flags) {
                            case 0x15:
                                flagsDesc = "tmpo/cpil";
                                break;
                            case 0:
                                flagsDesc = "data";
                                break;
                            case 1:
                                flagsDesc = "text";
                                break;

                            case 0xd:
                                flagsDesc = "image data";
                                break;
                            default:
                                flagsDesc = "undocumented";
                                break;
                        }
                        DefaultMutableTreeNode child1 = new DefaultMutableTreeNode(flagsDesc);
                        child.add(child1);
                        child1.add(new DefaultMutableTreeNode("" + cString(buf2)));
                    } else {
                        child.add(new DefaultMutableTreeNode("add'tl info: " + cString(buf2)));
                    }
                }
            }
            return chunk;
        }
    },

    UnKn(0, "unknown") {
        public Pair<DefaultMutableTreeNode, ByteBuffer> leafNodeView(Pair<DefaultMutableTreeNode, ByteBuffer> chunk) {


            return dumpChunk(chunk);

        }},;

    private static Pair<DefaultMutableTreeNode, ByteBuffer> dumpChunk(Pair<DefaultMutableTreeNode, ByteBuffer> chunk) {
        final ByteBuffer b1 = chunk.second;
        final int len = b1.limit();
        final int cap = Math.min(40, len);
        final ByteBuffer b2 = (ByteBuffer) b1.slice().limit(cap).rewind();
        final String desc = cString(b2);
//        b1.position(b1.limit());
        chunk.first.add(new DefaultMutableTreeNode(desc));
        return chunk;
    }

    private static DefaultMutableTreeNode entries32(Pair<DefaultMutableTreeNode, ByteBuffer> p) {
        DefaultMutableTreeNode child = new DefaultMutableTreeNode(MessageFormat.format("Entries: {0}", p.second.getInt()));
        p.first.add(child);
        return child;
    }

//    private static HashMap<String, String> IlstDescription = new HashMap<String, String>();

    private static Pair<Byte, Integer> versionFlags32(DefaultMutableTreeNode spoke, ByteBuffer buf) {
        byte version = buf.get();
        spoke.add(new DefaultMutableTreeNode(MessageFormat.format("Version: {0}", version)));
        ByteBuffer flagBytes = ByteBuffer.allocateDirect(4);
        flagBytes.put((byte) 0);
        flagBytes.put(buf.get());
        flagBytes.put(buf.get());
        flagBytes.put(buf.get());

        int flags = ((ByteBuffer) flagBytes.rewind()).getInt();


        spoke.add(new DefaultMutableTreeNode(MessageFormat.format("Flags: {0}", Integer.toBinaryString(flags))));
        return new Pair<Byte, Integer>(version, flags);
    }

    private static Integer version32(Pair<DefaultMutableTreeNode, ByteBuffer> p) {
        int version = p.second.getInt();

        p.first.add(new DefaultMutableTreeNode(MessageFormat.format("Version: {0}", version)));
        return version;
    }

    public static boolean isQuicktime;
    private double section;
    private String description;
    final boolean parent;

    BoxTypeMeta(final double section, final String description) {
        this.section = section;
        this.description = description;
        parent = false;
    }

    BoxTypeMeta(double section, String description, boolean parent) {
        this.section = section;
        this.description = description;
        this.parent = parent;
    }

    public String getDescription() {
        return description;
    }

    /**
     * the view method describing a known mp4 box
     *
     * @param chunk the node to decorate with metadata and the data blob associated
     * @return
     */
    public Pair<DefaultMutableTreeNode, ByteBuffer> leafNodeView(Pair<DefaultMutableTreeNode, ByteBuffer> chunk) {
        return chunk;
    }


    static {

    }
}
