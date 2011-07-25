package ch.ethz.ssh2.packets;

/**
 * PacketUserauthInfoResponse.
 *
 * @author Christian Plattner, plattner@inf.ethz.ch
 * @version $Id: PacketUserauthInfoResponse.java,v 1.3 2005/08/24 17:54:09 cplattne Exp $
 */
public class PacketUserauthInfoResponse {
    private byte[] payload;

    private String[] responses;

    public PacketUserauthInfoResponse(String[] responses) {
        this.responses = responses;
    }

    public byte[] getPayload() {
        if (payload == null) {
            TypesWriter tw = new TypesWriter();
            tw.writeByte(Packets.SSH_MSG_USERAUTH_INFO_RESPONSE);
            tw.writeUINT32(responses.length);
            for (String response : responses) tw.writeString(response);

            payload = tw.getBytes();
        }
        return payload;
    }
}
