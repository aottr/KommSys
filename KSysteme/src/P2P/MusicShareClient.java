package P2P;

import java.net.*;
import java.io.*;
import java.util.*;

/**
 * Created by aervos on 17.01.2017.
 */
public class MusicShareClient {

    List<MusicModel> musicList = null;
    List<Peer> peerList = null;
    DatagramSocket socket = null;
    DatagramPacket packet = null;

    private Integer _port;

    public void scan() {

        peerList.clear();

        for(int p = 50001; p <= 50010; p++) {

            if(p == this._port)
                continue;

            try {
                socket.setSoTimeout(0);
                byte[] buffer = getMusicSet().getBytes("UTF-8");
                packet = new DatagramPacket(buffer,
                        buffer.length, new InetSocketAddress("255.255.255.255", p));
                socket.send(packet);
                System.out.println("Paket an " + p);
                socket.setSoTimeout(1000);
                socket.receive(packet);
                socket.setSoTimeout(0);

                peerList.add(new Peer(packet.getAddress(), p));

                //todo Überprüfung/Übermittlung Fehlerhaft
                /**
                 * Interpret: Hallo, Titel: Welt
                 Interpret: Hallo2, Titel: Welt2
                 Interpret: Hallo, Titel: Welt
                 Interpret: Hallo, Titel: Welt
                 *
                 */
                checkMusicSet(new String(packet.getData()));

            }catch (Exception ex) {

                continue;
            }
        }
    }

    public MusicShareClient(int port, ArrayList<MusicModel> musicList) {

        this._port = port;
        try {
            socket = new DatagramSocket(this._port);
            socket.setBroadcast(true);
        } catch (Exception ex) {}
        peerList = new ArrayList<Peer>();
        this.musicList = musicList;

        scan();

        System.out.println("Scan beendet.");

        System.out.println(peerList.size() + " Peers gefunden:");
        for(Peer p: peerList)
            System.out.println(p.getPort());

        System.out.println(this.toString());


        try {

            byte[] buffer = new byte[1450];
            socket.setSoTimeout(0);
            while(true) {
                packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                System.out.println(new String(packet.getData()));
                socket.send(packet);
            }

        }catch (IOException ex) {System.out.println(ex);}
    }

    private String getMusicSet() {

        StringBuilder sb = new StringBuilder();
        for(MusicModel mm: musicList) {

            sb.append(mm.toString());
        }
        return sb.toString();
    }

    private void checkMusicSet(String musicModelListString) {

        String[] musicParts = musicModelListString.split(";");

        if((musicParts.length % 2) != 0) {

            System.out.println("Corrupt MusicString");
            return;
        }

        for(int i = 0; i < musicParts.length;) {

            MusicModel mm = new MusicModel(musicParts[i++], musicParts[i++]);

            for(MusicModel sm: musicList) {

                if(sm.getArtist().equals(mm.getArtist()) && sm.getTitle().equals(mm.getTitle()))
                    continue;
                else
                    musicList.add(mm);
            }
        }
    }
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        for(MusicModel mm: musicList) {

            sb.append("Interpret: " + mm.getArtist() + ", Titel: " + mm.getTitle() + "\n");
        }

        return sb.toString();
    }
}
