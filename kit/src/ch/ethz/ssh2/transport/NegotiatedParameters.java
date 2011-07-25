package ch.ethz.ssh2.transport;

import ch.ethz.ssh2.crypto.cipher.CipherEntry;

/**
 * NegotiatedParameters.
 *
 * @author Christian Plattner, plattner@inf.ethz.ch
 * @version $Id: NegotiatedParameters.java,v 1.1 2005/05/26 14:53:28 cplattne Exp $
 */
public class NegotiatedParameters {
    public boolean guessOK;
    public String kex_algo;
    public String server_host_key_algo;
    public CipherEntry enc_algo_client_to_server;
    public CipherEntry enc_algo_server_to_client;
    public String mac_algo_client_to_server;
    public String mac_algo_server_to_client;
    public String comp_algo_client_to_server;
    public String comp_algo_server_to_client;
    public String lang_client_to_server;
    public String lang_server_to_client;
}
