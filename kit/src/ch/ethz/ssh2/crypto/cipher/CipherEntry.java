package ch.ethz.ssh2.crypto.cipher;

import com.vsiwest.kit.Kit;

import java.text.MessageFormat;

/**
 * Property of vsiwest
 * User: jim
 * Date: Jul 25, 2007
 * Time: 11:48:44 AM
 */
public enum CipherEntry {
    /* Higher Priority First */
    Caes256_ctr(16, 32, AES.class),
    Caes192_ctr(16, 24, AES.class),
    Caes128_ctr(16, 16, AES.class),
    Cblowfish_ctr(8, 16, BlowFish.class),
    Caes256_cbc(16, 32, AES.class),
    Caes192_cbc(16, 24, AES.class),
    Caes128_cbc(16, 16, AES.class),
    Cblowfish_cbc(8, 16, BlowFish.class),
    C3des_ctr(8, 24, DESede.class),
    C3des_cbc(8, 24, DESede.class);

    public String getType() {
        return name().substring(1).replaceFirst("_", "-");
    }

    private int blocksize;
    private int keysize;
    private Class cipherClass;

    CipherEntry(int blockSize, int keySize, Class cipherClass) {
        this.blocksize = blockSize;
        this.keysize = keySize;
        this.cipherClass = cipherClass;
    }

    /**
     * a kludge to accomodate a symbol "3des-ctr" for instance whicvh beings with a number and contains a dash.
     *
     * @param type
     * @return
     */
    public static CipherEntry getEntry(String type) {
        try {
            String s = new StringBuilder().append("C").append(type.replaceFirst("-", "_")).toString();
            return valueOf(s);
        } catch (IllegalArgumentException e) {
            Kit.getLogger().info(MessageFormat.format("Unsopported Cipher requested - {0}", type));
            return null;
        }
    }

    public int getBlocksize() {
        return blocksize;
    }

    public int getKeysize() {
        return keysize;
    }

    public Class getCipherClass() {
        return cipherClass;
    }
}
