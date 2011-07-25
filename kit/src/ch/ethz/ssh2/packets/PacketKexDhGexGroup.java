package ch.ethz.ssh2.packets;

import java.io.IOException;
import java.math.BigInteger;

/**
 * PacketKexDhGexGroup.
 *
 * @author Christian Plattner, plattner@inf.ethz.ch
 * @version $Id: PacketKexDhGexGroup.java,v 1.2 2005/08/24 17:54:09 cplattne Exp $
 */
public class PacketKexDhGexGroup {

    private BigInteger p;
    private BigInteger g;

    public PacketKexDhGexGroup(byte payload[], int off, int len) throws IOException {
        byte[] payload1 = new byte[len];
        System.arraycopy(payload, off, payload1, 0, len);

        TypesReader tr = new TypesReader(payload, off, len);

        int packet_type = tr.readByte();

        if (packet_type != Packets.SSH_MSG_KEX_DH_GEX_GROUP)
            throw new IllegalArgumentException(
                    "This is not a SSH_MSG_KEX_DH_GEX_GROUP! (" + packet_type
                            + ')');

        p = tr.readMPINT();
        g = tr.readMPINT();

        if (tr.remain() != 0)
            throw new IOException("PADDING IN SSH_MSG_KEX_DH_GEX_GROUP!");
    }

    public BigInteger getG() {
        return g;
    }

    public BigInteger getP() {
        return p;
    }
}
