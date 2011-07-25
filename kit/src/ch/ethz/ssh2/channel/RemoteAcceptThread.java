package ch.ethz.ssh2.channel;

import java.io.IOException;
import java.net.Socket;

/**
 * RemoteAcceptThread.
 *
 * @author Christian Plattner, plattner@inf.ethz.ch
 * @version $Id: RemoteAcceptThread.java,v 1.4 2006/02/13 21:19:25 cplattne Exp $
 */
class RemoteAcceptThread extends Thread {

    private Channel c;

    private String targetAddress;
    private int targetPort;

    private Socket s;

    public RemoteAcceptThread(Channel c, String remoteConnectedAddress, int remoteConnectedPort,
                              String remoteOriginatorAddress, int remoteOriginatorPort, String targetAddress, int targetPort) {
        this.c = c;
        String remoteConnectedAddress1 = remoteConnectedAddress;
        int remoteConnectedPort1 = remoteConnectedPort;
        String remoteOriginatorAddress1 = remoteOriginatorAddress;
        int remoteOriginatorPort1 = remoteOriginatorPort;
        this.targetAddress = targetAddress;
        this.targetPort = targetPort;

        if (false) {
        }
    }

    public void run() {
        try {
            c.cm.sendOpenConfirmation(c);

            s = new Socket(targetAddress, targetPort);

            StreamForwarder r2l = new StreamForwarder(c, null, null, c.getStdoutStream(), s.getOutputStream(),
                    "RemoteToLocal");
            StreamForwarder l2r = new StreamForwarder(c, null, null, s.getInputStream(), c.getStdinStream(),
                    "LocalToRemote");

            /* No need to start two threads, one can be executed in the current thread */

            r2l.setDaemon(true);
            r2l.start();
            l2r.run();

            while (r2l.isAlive()) {
                try {
                    r2l.join();
                }
                catch (InterruptedException e) {
                }
            }

            /* If the channel is already closed, then this is a no-op */

            c.cm.closeChannel(c, "EOF on both streams reached.", true);
            s.close();
        }
        catch (IOException e) {

            try {
                c.cm.closeChannel(c, "IOException in proxy code (" + e.getMessage() + ')', true);
            }
            catch (IOException e1) {
            }
            try {
                if (s != null)
                    s.close();
            }
            catch (IOException e1) {
            }
        }
    }
}
