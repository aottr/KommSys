package P2P;

import java.net.*;

/**
 *  Datenmodell eines Peers zum Abspeichern von IP-Adresse und Port
 *
 *  @author Dustin Kröger [3728859]
 *  @version 1.0
 */
public class Peer
{
    private InetAddress ip;
    private Integer port;

    /**
     * Konstruktor der Klasse.
     * @param ip IP-Adresse des Peers
     * @param port Portnummer des Peers
     */
    public Peer(InetAddress ip, int port)
    {
        this.ip = ip;
        this.port = port;
    }

    /**
     * Getter des Ports
     * @return Port des Peers
     */
    public Integer getPort() {

        return port;
    }

    /**
     * Getter der IP-Adresse
     * @return IP-Adresse des Peers
     */
    public InetAddress getIPAddress() {

        return ip;
    }
}
