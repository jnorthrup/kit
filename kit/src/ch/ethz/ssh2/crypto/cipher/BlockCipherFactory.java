package ch.ethz.ssh2.crypto.cipher;

import java.text.MessageFormat;

/**
 * BlockCipherFactory.
 *
 * @author Christian Plattner, plattner@inf.ethz.ch
 * @version $Id: BlockCipherFactory.java,v 1.4 2005/12/05 17:13:27 cplattne Exp $
 */
public class BlockCipherFactory {
    private BlockCipherFactory() {}


    public static CipherEntry getEntry(String type) {
        return CipherEntry.getEntry(type);
    }

    public static BlockCipher createCipher(CipherEntry cipherEntry, boolean encrypt, byte[] key, byte[] iv) {
        try {
            Class cc = cipherEntry.getCipherClass();
            BlockCipher bc = (BlockCipher) cc.newInstance();

            if (cipherEntry.name().endsWith("_cbc")) {
                bc.init(encrypt, key);
                return new CBCMode(bc, iv, encrypt);
            } else if (cipherEntry.name().endsWith("_ctr")) {
                bc.init(true, key);
                return new CTRMode(bc, iv, encrypt);
            }
            throw new IllegalArgumentException(MessageFormat.format("Cannot instantiate {0}", cipherEntry.name()));
        }
        catch (Exception e) {
            throw new IllegalArgumentException(MessageFormat.format("Cannot instantiate {0}", cipherEntry.name()));
        }
    }


}
