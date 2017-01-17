package P2P;

import java.net.*;
import java.util.ArrayList;

/**
 * Created by aervos on 17.01.2017.
 */
public class Peer1 {

    public static void main( String[] args ) {

        /*try {
            System.out.println("Socket binden");
            DatagramSocket s = new DatagramSocket(50004);
            s.setBroadcast(true);
            while(true) {
                DatagramPacket dp = new DatagramPacket("insert data here".getBytes(),
                        "insert data here".length(), new InetSocketAddress("255.255.255.255", 50001));
                System.out.println("Paket senden");
                s.send(dp);
                System.out.println("Paket gesendet");
            }
        } catch (Exception e) {System.out.println(e);} */

        ArrayList<MusicModel> ml = new ArrayList<>();
        ml.add(new MusicModel("Hallo3", "Welt3"));
        ml.add(new MusicModel("Hallo4", "Welt4"));

        MusicShareClient msc = new MusicShareClient(50004, ml);

    }
}