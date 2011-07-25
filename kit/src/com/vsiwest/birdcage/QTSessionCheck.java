package com.vsiwest.birdcage;

import quicktime.QTException;
import quicktime.QTSession;

class QTSessionCheck {

    private static QTSessionCheck instance;

    private QTSessionCheck() throws QTException {
        super();
        // init
        QTSession.open();
        // create shutdown handler
        Thread shutdownHook = new Thread() {
            public void run() {
                QTSession.close();
            }
        };
        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }

    private static QTSessionCheck getInstance() throws QTException {
        if (instance == null)
            instance = new QTSessionCheck();
        return instance;
    }

    public static void check() throws QTException {
        // gets instance.  if a new one needs to be created,
        // it calls QTSession.open( ) and creates a shutdown hook
        // to call QTSession.close( )
        getInstance();
    }
}

			