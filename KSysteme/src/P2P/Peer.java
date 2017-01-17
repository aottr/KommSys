package P2P;

import java.net.*;

public class Peer
{
    private InetAddress _ip;
    private Integer _port;

    public Peer(InetAddress ip, int port)
    {
        _ip = ip;
        _port = port;
    }

    public Integer getPort() {

        return _port;
    }
}
