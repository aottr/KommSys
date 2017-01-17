package P2P;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class P2PClient {

    private static byte QUERY_PACKET = 127;

    private static byte RESPONSE_PACKET = -127;

    private DatagramSocket      bcastSocket  = null;
    private InetSocketAddress   bcastAddress = null;

    private Boolean    endSearch   = false;
    private List<Peer> responseList = null;

    private Integer destLastResponse = null;

    private Thread bcastListen = new Thread(P2PClient.class.getSimpleName() + " broadcast listener") {

        @Override
        public void run() {

            try {

                byte[] buffer = new byte[1];
                DatagramPacket dp = new DatagramPacket(buffer, buffer.length);

                while(!endSearch) {

                    try {

                        buffer[0] = 0;
                        bcastSocket.receive(dp);

                        if(buffer[0] == QUERY_PACKET) {

                            byte[] data = new byte[] {RESPONSE_PACKET};

                            DatagramPacket rdp = new DatagramPacket(data, data.length, dp.getAddress(), dp.getPort());
                            destLastResponse = dp.getPort();

                            bcastSocket.send(rdp);

                        }
                        else if(buffer[0] == RESPONSE_PACKET) {

                            if(responseList != null && dp.getPort() != destLastResponse) {

                                synchronized (responseList) {

                                    responseList.add(new Peer(dp.getAddress(), dp.getPort()));
                                }
                            }
                        }

                    } catch (IOException ioe) {

                        //todo
                    }
                }

                bcastSocket.disconnect();
                bcastSocket.close();

            } catch (Exception e) {
                //todo
            }
        }
    };


    DatagramSocket p2pSocket = null;
    DatagramPacket p2pPacket = null;
    List<MusicModel> musicList = null;

    public P2PClient(int port) {

        musicList = new ArrayList<MusicModel>();
        try {

            bcastSocket = new DatagramSocket(port);
            bcastAddress = new InetSocketAddress("255.255.255.255", port);
            bcastSocket.setBroadcast(true);

            bcastListen.setDaemon(true);
            bcastListen.start();
        } catch (SocketException se) {

            //todo
        }

    }

    public void disconnect()
    {
        endSearch = true;

        bcastSocket.close();
        bcastSocket.disconnect();

        try
        {
            bcastListen.join();
        }
        catch( InterruptedException e )
        {
            e.printStackTrace();
        }
    }

    public Peer[] getPeers( int timeout) throws IOException
    {
        responseList = new ArrayList<Peer>();

        // send query byte, appended with the group id
        byte[] data = new byte[] {QUERY_PACKET};

        DatagramPacket dp = new DatagramPacket( data, data.length, bcastAddress );

        bcastSocket.send( dp );

        // wait for the listen thread to do its thing
        try
        {
            Thread.sleep( timeout );
        }
        catch( InterruptedException e )
        {
        }

        Peer[] peers;
        synchronized( responseList )
        {
            peers = responseList.toArray( new Peer[ responseList.size() ] );
        }

        responseList = null;

        return peers;
    }

    public static void main( String[] args )
    {
        try
        {

            P2PClient mp = new P2PClient(50001);

            boolean stop = false;

            BufferedReader br = new BufferedReader( new InputStreamReader( System.in ) );

            while( !stop )
            {
                System.out.println( "enter \"q\" to quit, or anything else to query peers" );
                String s = br.readLine();

                if( s.equals( "q" ) )
                {
                    System.out.print( "Closing down..." );
                    mp.disconnect();
                    System.out.println( " done" );
                    stop = true;
                }
                else
                {
                    System.out.println( "Querying" );

                    Peer[] peers = mp.getPeers( 100);

                    System.out.println( peers.length + " peers found" );
                    for( Peer p : peers )
                    {
                        System.out.println( "\t" + p );
                    }
                }
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }
}
