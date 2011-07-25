package com.vsiwest.birdcage;

import java.nio.ByteBuffer;
import java.util.HashMap;

public enum QuicktimeTag {
    kUserDataTextAlbum(0xA9616C62,          /*'�alb'*/"Album"),
    kUserDataTextArtist(0xA9415254,         /**/"Artist"),
    kUserDataTextAuthor(0xA9617574,         /*'�aut'*/"Author"),
    kUserDataTextChapter(0xA9636870,        /*'�chp'*/"Chapter"),
    kUserDataTextComment(0xA9636D74,        /*'�cmt'*/"Comment"),
    kUserDataTextComposer(0xA9636F6D,       /*'�com'*/"Composer"),
    kUserDataTextCopyright(0xA9637079,      /*'�cpy'*/"Copyright"),
    kUserDataTextCreationDate(0xA9646179,   /*'�day'*/"Created"),
    kUserDataTextDescription(0xA9646573,    /*'�des'*/"Description"),
    kUserDataTextDirector(0xA9646972,       /*'�dir'*/"Director"),
    kUserDataTextDisclaimer(0xA9646973,     /*'�dis'*/"Disclaimer"),
    kUserDataTextEncodedBy(0xA9656E63,      /*'�enc'*/"Encoder"),
    kUserDataTextFullName(0xA96E616D,       /*'�nam'*/"FullName"),
    kUserDataTextGenre(0xA967656E,          /*'�gen'*/"Genre"),
    kUserDataTextHostComputer(0xA9687374,   /*'�hst'*/"Host"),
    kUserDataTextInformation(0xA9696E66,    /*'�inf'*/"Information"),
    kUserDataTextKeywords(0xA96B6579,       /*'�key'*/"Keywords"),
    kUserDataTextMake(0xA96D616B,           /*'�mak'*/"Make"),
    kUserDataTextModel(0xA96D6F64,          /*'�mod'*/"Model"),
    kUserDataTextOriginalArtist(0xA96F7065, /*'�ope'*/"OriginalArtist"),
    kUserDataTextOriginalFormat(0xA9666D74, /*'�fmt'*/"OriginalFormat"),
    kUserDataTextOriginalSource(0xA9737263, /*'�src'*/"OriginalSource"),
    kUserDataTextPerformers(0xA9707266,     /*'�prf'*/"Performers"),
    kUserDataTextProducer(0xA9707264,       /*'�prd'*/"Producer"),
    kUserDataTextProduct(0xA9505244,        /**/"Product"),
    kUserDataTextSoftware(0xA9737772,       /*'�swr'*/"Software"),
    kUserDataTextSpecialPlaybackRequirements(0xA9726571,/*'�req'*/"SpecialRequirements"),
    kUserDataTextTrack(0xA974726B,          /*'�trk'*/"Track"),
    kUserDataTextWarning(0xA977726E,        /*'�wrn'*/"Warning"),
    kUserDataTextWriter(0xA9777274,         /*'�wrt'*/"Writer"),
    kUserDataTextURLLink(0xA975726C,        /*'�url'*/"URL"),
    kUserDataTextEditDate1(0xA9656431, "EditDate1");

    /**
     * FOUR_CHAR_CODE for 'meta'
     */
    public static final int FCC_meta = 0x6d657461;

    /**
     * FOUR_CHAR_CODE for 'ilst'
     */
    public final static int FCC_ilst = 0x696c7374;

    final String desc;

    static HashMap<Integer, QuicktimeTag> fccMap = new HashMap<Integer, QuicktimeTag>();
    final private ByteBuffer _4ccBuff;


    static {
    }


    QuicktimeTag(int fourcc, String metadesc) {
        desc = metadesc;
        init(fourcc);
        _4ccBuff = ByteBuffer.allocate(4).putInt(fourcc);
    }

    private void init(int fourcc) {
        fccMap.put(getFourcc(), this);
    }

    public int getFourcc() {
        _4ccBuff.rewind();
        return _4ccBuff.getInt();
    }
}

