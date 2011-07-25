package ch.ethz.ssh2;

import ch.ethz.ssh2.channel.ChannelManager;
import ch.ethz.ssh2.channel.LocalAcceptThread;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * A <code>LocalPortForwarder</code> forwards TCP/IP connections to a local
 * port via the secure tunnel to another host (which may or may not be identical
 * to the remote SSH-2 server). Checkout {@link Connection#createLocalPortForwarder(int,String,int)}
 * on how to create one.
 *
 * @author Christian Plattner, plattner@inf.ethz.ch
 * @version $Id: LocalPortForwarder.java,v 1.6 2006/10/20 20:50:24 cplattne Exp $
 */
public class LocalPortForwarder {
    private ChannelManager cm;

    private String host_to_connect;

    private int port_to_connect;

    private LocalAcceptThread lat;

    LocalPortForwarder(ChannelManager cm, int local_port, String host_to_connect, int port_to_connect)
            throws IOException {
        this.cm = cm;
        this.host_to_connect = host_to_connect;
        this.port_to_connect = port_to_connect;

        lat = new LocalAcceptThread(cm, local_port, host_to_connect, port_to_connect);
        lat.setDaemon(true);
        lat.start();
    }

    LocalPortForwarder(ChannelManager cm, InetSocketAddress addr, String host_to_connect, int port_to_connect)
            throws IOException {
        this.cm = cm;
        this.host_to_connect = host_to_connect;
        this.port_to_connect = port_to_connect;

        lat = new LocalAcceptThread(cm, addr, host_to_connect, port_to_connect);
        lat.setDaemon(true);
        lat.start();
    }

    /**
     * Stop TCP/IP forwarding of newly arriving connections.
     *
     * @throws IOException
     */
    public void close() {
        lat.stopWorking();
    }
}
