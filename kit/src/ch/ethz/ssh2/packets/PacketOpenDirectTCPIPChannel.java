package ch.ethz.ssh2.packets;


/**
 * PacketOpenDirectTCPIPChannel.
 *
 * @author Christian Plattner, plattner@inf.ethz.ch
 * @version $Id: PacketOpenDirectTCPIPChannel.java,v 1.2 2005/08/24 17:54:09 cplattne Exp $
 */
public class PacketOpenDirectTCPIPChannel {
    private byte[] payload;

    private int channelID;
    private int initialWindowSize;
    private int maxPacketSize;

    private String host_to_connect;
    private int port_to_connect;
    private String originator_IP_address;
    private int originator_port;

    public PacketOpenDirectTCPIPChannel(int channelID, int initialWindowSize, int maxPacketSize,
                                        String host_to_connect, int port_to_connect, String originator_IP_address,
                                        int originator_port) {
        this.channelID = channelID;
        this.initialWindowSize = initialWindowSize;
        this.maxPacketSize = maxPacketSize;
        this.host_to_connect = host_to_connect;
        this.port_to_connect = port_to_connect;
        this.originator_IP_address = originator_IP_address;
        this.originator_port = originator_port;
    }

    public byte[] getPayload() {
        if (payload == null) {
            TypesWriter tw = new TypesWriter();

            tw.writeByte(Packets.SSH_MSG_CHANNEL_OPEN);
            tw.writeString("direct-tcpip");
            tw.writeUINT32(channelID);
            tw.writeUINT32(initialWindowSize);
            tw.writeUINT32(maxPacketSize);
            tw.writeString(host_to_connect);
            tw.writeUINT32(port_to_connect);
            tw.writeString(originator_IP_address);
            tw.writeUINT32(originator_port);

            payload = tw.getBytes();
        }
        return payload;
    }
}
