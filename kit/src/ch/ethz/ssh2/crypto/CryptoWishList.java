package ch.ethz.ssh2.crypto;

import ch.ethz.ssh2.crypto.cipher.CipherEntry;
import ch.ethz.ssh2.crypto.digest.MAC;
import ch.ethz.ssh2.transport.KexManager;

/**
 * CryptoWishList.
 *
 * @author Christian Plattner, plattner@inf.ethz.ch
 * @version $Id: CryptoWishList.java,v 1.3 2005/08/24 17:54:10 cplattne Exp $
 */
final public class CryptoWishList {
    final public String[] kexAlgorithms = KexManager.getDefaultKexAlgorithmList();
    final public String[] serverHostKeyAlgorithms = KexManager.getDefaultServerHostkeyAlgorithmList();
    public CipherEntry[] c2s_enc_algos = CipherEntry.values();
    final public CipherEntry[] s2c_enc_algos = CipherEntry.values();
    final public String[] c2s_mac_algos = MAC.getMacList();
    final public String[] s2c_mac_algos = MAC.getMacList();
}
