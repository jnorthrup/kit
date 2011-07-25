package com.vsiwest.birdcage;

import quicktime.util.QTUtils;

public class FourCharCodeTest {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: FourCharCodeTest <fcc>");
            return;
        }
        System.out.println(args[0]);
        int fcc = QTUtils.toOSType(args[0]);
        System.out.println(fcc);
        System.out.println(Integer.toHexString(fcc));
        String fccString = QTUtils.fromOSType(fcc);
        System.out.println(fccString);
    }

}
