package ch.ethz.ssh2.channel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * A StreamForwarder forwards data between two given streams.
 * If two StreamForwarder threads are used (one for each direction)
 * then one can be configured to shutdown the underlying channel/socket
 * if both threads have finished forwarding (EOF).
 *
 * @author Christian Plattner, plattner@inf.ethz.ch
 * @version $Id: StreamForwarder.java,v 1.2 2006/02/13 21:19:25 cplattne Exp $
 */
class StreamForwarder extends Thread {
    private OutputStream os;
    private InputStream is;
    private byte[] buffer = new byte[Channel.CHANNEL_BUFFER_SIZE];
    private Channel c;
    private StreamForwarder sibling;
    private Socket s;
    private String mode;

    StreamForwarder(Channel c, StreamForwarder sibling, Socket s, InputStream is, OutputStream os, String mode) {
        this.is = is;
        this.os = os;
        this.mode = mode;
        this.c = c;
        this.sibling = sibling;
        this.s = s;
    }

    public void run() {
        try {
            while (true) {
                int len = is.read(buffer);
                if (len <= 0)
                    break;
                os.write(buffer, 0, len);
                os.flush();
            }
        }
        catch (IOException ignore) {
            try {
                c.cm.closeChannel(c, "Closed due to exception in StreamForwarder (" + mode + "): "
                        + ignore.getMessage(), true);
            }
            catch (IOException e) {
            }
        }
        finally {
            try {
                os.close();
            }
            catch (IOException e1) {
            }
            try {
                is.close();
            }
            catch (IOException e2) {
            }

            if (sibling != null) {
                while (sibling.isAlive()) {
                    try {
                        sibling.join();
                    }
                    catch (InterruptedException e) {
                    }
                }

                try {
                    c.cm.closeChannel(c, "StreamForwarder (" + mode + ") is cleaning up the connection", true);
                }
                catch (IOException e3) {
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
}