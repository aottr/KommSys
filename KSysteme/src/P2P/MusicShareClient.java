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

    private Integer port;

    public void scan() {

        peerList.clear();

        for(int p = 50001; p <= 50010; p++) {

            if(p == this.port)
                continue;

            try {
                socket.setSoTimeout(0);
                byte[] buffer = getMusicSet().getBytes("UTF-8");

                DatagramPacket outPacket = new DatagramPacket(buffer,
                        buffer.length, new InetSocketAddress("255.255.255.255", p));
                long start = System.currentTimeMillis() % 1000;
                socket.send(outPacket);
                System.out.print("Paket an " + p);

                buffer = new byte[1024];
                DatagramPacket inPacket = new DatagramPacket(buffer, buffer.length);

                socket.setSoTimeout(100);
                socket.receive(inPacket);
                long end = System.currentTimeMillis() % 1000;
                socket.setSoTimeout(0);

                System.out.println(" Antwort [" + (end - start) + "ms] von IP-Adresse: " + inPacket.getAddress());

                checkPeerSet(new Peer(inPacket.getAddress(), p));
                checkMusicSet(new String(inPacket.getData()).trim());

            }catch (SocketTimeoutException ex) {

                System.out.println(" keine Antwort [500ms]");
                continue;

            } catch (Exception ex) {

                System.out.println(ex.getMessage());
                continue;
            }

        }
    }

    public MusicShareClient(int port, ArrayList<MusicModel> musicList) {

        this.port = port;
        try {
            socket = new DatagramSocket(this.port);
            socket.setBroadcast(true);
        } catch (Exception ex) {}

        peerList = new ArrayList<>();
        this.musicList = musicList;

        scan();

        System.out.print("\nScan beendet, " +  peerList.size() + " Peers gefunden " +
                (peerList.size() > 0 ? "auf den Ports: " : ""));

        if(peerList.size() > 0)
            for(Peer p: peerList)
                System.out.print(p.getPort() + " ");

        System.out.println("\n\nMusikliste:");
        System.out.print(this.toString());


        try {

            socket.setSoTimeout(0);
            while(true) {

                int oldMusicSize = musicList.size();

                byte[] buffer = new byte[1024];
                DatagramPacket inPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(inPacket);

                checkMusicSet(new String(inPacket.getData()).trim());
                buffer = getMusicSet().getBytes();

                DatagramPacket outPacket = new DatagramPacket(buffer, buffer.length, inPacket.getAddress(), inPacket.getPort());
                socket.send(outPacket);
                checkPeerSet(new Peer(inPacket.getAddress(), inPacket.getPort()));

                if(oldMusicSize < musicList.size())
                    for(int i = oldMusicSize; i < musicList.size(); i++)
                        System.out.println("Interpret: " + musicList.get(i).getArtist() +
                                ", Titel: " + musicList.get(i).getTitle());
            }

        }catch (IOException ex) {System.out.println(ex);}
    }

    /**
     * Baut einen String im MusicModel-'C'SV (Separation durch ';') aus der kompletten hinterlegten Musikliste.
     * @return CSV-String mit allen Liedern aus der Musikliste
     */
    private String getMusicSet() {

        StringBuilder sb = new StringBuilder();
        for(MusicModel mm: musicList) {

            sb.append(mm.toString());
        }
        return sb.toString();
    }

    /**
     * Überprüft ob die Musikstücke aus einem MusicModel-'C'SV (Separation durch ';') bereits in der
     * Musikliste sind. Falls nicht, werden sie hinzugefügt.
     * @param musicModelListString Zu überprüfendes MusicModel-CSV
     */
    private void checkMusicSet(String musicModelListString) {

        String[] musicParts = musicModelListString.split(";");

        // Wenn Anzahl der der Teilstücke ungerade, sind es keine Paare.
        if((musicParts.length % 2) != 0) {

            System.out.println("Corrupt MusicString");
            return;
        }

        // Gehe alle Teilstücke durch
        for(int i = 0; i < musicParts.length;) {

            MusicModel mm = new MusicModel(musicParts[i++], musicParts[i++]);

            //Kontrollboolean ob ein Musikstück bereits in der Liste zu finden ist
            boolean archived = false;
            for(MusicModel sm: musicList) {

                // Gefunden -> boolean wird wahr gesetzt
                if(sm.getArtist().equals(mm.getArtist()) && sm.getTitle().equals(mm.getTitle()))
                    archived = true;
            }

            // nur wenn nicht gefunden, wird es hinzugefügt
            if(!archived)
                musicList.add(mm);
        }
    }

    /**
     * Prüft ob ein Peer (Kombination aus IP-Adresse und Port) bereits in der Liste ist.
     * Falls nein, wird er hinzugefügt.
     * @param peer der zu überprüfende Peer
     */
    private void checkPeerSet(Peer peer) {

        //Kontrollboolean ob ein Peer bereits in der Liste zu finden ist
        boolean archived = false;
        for (Peer p : peerList) {

            // Gefunden -> boolean wird wahr gesetzt
            if (p.getPort() == peer.getPort() && p.getIPAddress() == peer.getIPAddress())
                archived = true;
        }

        // nur wenn nicht gefunden, wird es hinzugefügt
        if (!archived)
            peerList.add(peer);
    }

    /**
     * Ausgabe aller hinterlegten Musikstücke untereinander und
     * Mit Bezeichnung in der Form: Interpret: xxxx, Titel: xxx
     * @return String mit Liste aller Musikstücke und NewLine am Ende.
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        for(MusicModel mm: musicList) {

            sb.append("Interpret: " + mm.getArtist() + ", Titel: " + mm.getTitle() + "\n");
        }

        return sb.toString();
    }
}
