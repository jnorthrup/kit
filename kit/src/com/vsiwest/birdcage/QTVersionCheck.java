package com.vsiwest.birdcage;

import quicktime.QTSession;
import quicktime.util.QTBuild;


public class QTVersionCheck {

    public static void main(String[] args) {
        try {
            QTSession.open();
            System.out.println("QT version: " +
                    QTSession.getMajorVersion() +
                    "." +
                    QTSession.getMinorVersion());
            System.out.println("QTJ version: " +
                    QTBuild.getVersion() +
                    "." +
                    QTBuild.getSubVersion());
            QTSession.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
