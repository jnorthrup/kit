package ch.ethz.ssh2.crypto.digest;

/**
 * HMAC.
 *
 * @author Christian Plattner, plattner@inf.ethz.ch
 * @version $Id: HMAC.java,v 1.3 2006/11/01 14:20:24 cplattne Exp $
 */
public final class HMAC implements Digest {
    private Digest md;
    private byte[] k_xor_ipad;
    private byte[] k_xor_opad;

    private byte[] tmp;

    private int size;

    public HMAC(Digest md, byte[] key, int size) {
        this.md = md;
        this.size = size;

        tmp = new byte[md.getDigestLength()];

        final int BLOCKSIZE = 64;

        k_xor_ipad = new byte[BLOCKSIZE];
        k_xor_opad = new byte[BLOCKSIZE];

        if (key.length > BLOCKSIZE) {
            md.reset();
            md.update(key);
            md.digest(tmp);
            key = tmp;
        }

        System.arraycopy(key, 0, k_xor_ipad, 0, key.length);
        System.arraycopy(key, 0, k_xor_opad, 0, key.length);

        for (int i = 0; i < BLOCKSIZE; i++) {
            k_xor_ipad[i] ^= 0x36;
            k_xor_opad[i] ^= 0x5C;
        }
        md.update(k_xor_ipad);
    }

    public final int getDigestLength() {
        return size;
    }

    public final void update(byte b) {
        md.update(b);
    }

    public final void update(byte[] b) {
        md.update(b);
    }

    public final void update(byte[] b, int off, int len) {
        md.update(b, off, len);
    }

    public final void reset() {
        md.reset();
        md.update(k_xor_ipad);
    }

    public final void digest(byte[] out) {
        digest(out, 0);
    }

    public final void digest(byte[] out, int off) {
        md.digest(tmp);

        md.update(k_xor_opad);
        md.update(tmp);

        md.digest(tmp);

        System.arraycopy(tmp, 0, out, off, size);

        md.update(k_xor_ipad);
    }
}
