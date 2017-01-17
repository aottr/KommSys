package P2P;

import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aervos on 17.01.2017.
 */
public class Peer2 {

    public static void main( String[] args ) {

        /*try {
            System.out.println("Socket binden");
            DatagramSocket s = new DatagramSocket(50001);
            s.setBroadcast(true);

            while(true) {
                DatagramPacket dp = new DatagramPacket(new byte[1024], 1024);
                System.out.println("Paket empfangen");
                s.receive(dp);
                System.out.println("Paket empfangen2");
                System.out.println(dp.getAddress() + ":" + dp.getPort());

            }
        } catch (Exception e) {System.out.println(e);} */

        ArrayList<MusicModel> ml = new ArrayList<>();
        ml.add(new MusicModel("Hallo", "Welt"));
        ml.add(new MusicModel("Hallo2", "Welt2"));

        MusicShareClient msc = new MusicShareClient(50001, ml);

    }
}
