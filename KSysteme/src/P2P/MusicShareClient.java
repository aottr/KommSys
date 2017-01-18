package P2P;

import java.net.*;
import java.io.*;
import java.util.*;

/**
 * Peer2Peer Client, der das Netz nach bestehenden Clients durchsucht und dann Informationen mit ihnen austauscht.
 * Er erwartet weitere Clients auf den Ports 50001 bis 50010. Unter Test/ befinden sich 3 Testklassen die einfach
 * die Verbindung von 3 parallel laufenden Instanzen dieser Klasse simulieren. Diese Klasse ist zur Abgabe und
 * Wertung bestimmt!
 *
 * @author Dustin Kröger [3728859]
 * @version 1.3.1
 */
public class MusicShareClient {

    List<MusicModel> musicList = null;
    List<Peer> peerList = null;
    DatagramSocket socket = null;

    private Integer port;

    /**
     * Durchsucht den gegebenen Portbereich via Broadcast und
     * speichert IP-Adresse und Port der antwortenden Peers.
     * Da die Peers mit ihrer Musikliste antworten, wird diese auch aktualisiert.
     * Auslagerung für evtl. spätere Aufrufe
     * @param startport Port, mit dem die Suche beginnt
     * @param endport Port, mit dem die Suche endet.
     */
    public void scan(int startport, int endport) {

        // leert die Peerliste um nur evtl. nicht mehr erreichbare Peers auszuschließen
        peerList.clear();

        for(int p = startport; p <= endport; p++) {

            if(p == this.port)
                continue;

            try {
                //Timeout zurückseten
                socket.setSoTimeout(0);

                // Buffer und Palet für Broadcast vorbereiten
                byte[] buffer = getMusicSet().getBytes("UTF-8");
                DatagramPacket outPacket = new DatagramPacket(buffer,
                        buffer.length, new InetSocketAddress("255.255.255.255", p));

                // Start der Zeitmessung für die Dauer der Antwort
                long start = System.currentTimeMillis() % 1000;

                // Paket via Broadcast an Port p (e.g. 50000) schicken
                socket.send(outPacket);
                System.out.print("Paket an " + p);

                // Vorbereitung des Eingangspaketes
                buffer = new byte[1024];
                DatagramPacket inPacket = new DatagramPacket(buffer, buffer.length);

                // Timeout für das Warten auf Pakete. An meiner Testmaschine dauerte die Antwort 0-1ms, daher scheinen
                // 100ms mehr als genügend.
                socket.setSoTimeout(100);
                socket.receive(inPacket); // Einkommendes Paket wird "in Buffer übertragen"

                // Ende der Zeitmessung für die Dauer der Antwort
                long end = System.currentTimeMillis() % 1000;
                socket.setSoTimeout(0); // Rücksetzen des Timeouts

                System.out.println(" Antwort [" + (end - start) + "ms] von IP-Adresse: " + inPacket.getAddress());

                // checkPeerSet(new Peer(inPacket.getAddress(), p)); unwichtig, da eh geleerte peerList
                peerList.add(new Peer(inPacket.getAddress(), p));
                csvToList(new String(inPacket.getData()).trim()); // Hinzufügen der neu gewonnen Daten.

            }catch (SocketTimeoutException ex) { // Exception für den Timeout

                System.out.println(" keine Antwort [500ms]");
                continue;

            } catch (Exception ex) {

                System.out.println(ex.getMessage());
                continue;
            }
        }

        System.out.print("\nScan beendet, " +  peerList.size() + " Peers gefunden " +
                (peerList.size() > 0 ? "auf den Ports: " : ""));

        // Alle Ports der Peers ausgeben
        if(peerList.size() > 0)
            for(Peer pr: peerList)
                System.out.print(pr.getPort() + " ");

    }

    /**
     * Methode für das Abhören und Reagieren auf eingehende Pakete
     */
    public void listen() {

        System.out.println("\n\nMusikliste:");
        System.out.print(this.toString());

        try {

            // setze Timeout zurück. Oben wird dies bedingt nicht ausgeführt, sollte es zu einem Timeout kommen.
            socket.setSoTimeout(0);
            while(true) {

                int oldMusicSize = musicList.size();

                // Sollte der String irgendwann die Länge von 1024Byte übersteigen könnte man im Paket ein Byte mit
                // Anzahl der Pakete hinterlegen. So oft werden dann Pakete nachgeschickt die Teile des Strings enthalten
                byte[] buffer = new byte[1024];
                DatagramPacket inPacket = new DatagramPacket(buffer, buffer.length);
                // warte auf eingehende Pakete
                socket.receive(inPacket);

                // CSV-String in Liste übertragen
                csvToList(new String(inPacket.getData()).trim());

                // neuen Buffer füllen
                buffer = getMusicSet().getBytes();

                //Paket für Unicast vorbereiten
                DatagramPacket outPacket = new DatagramPacket(buffer, buffer.length, inPacket.getAddress(), inPacket.getPort());
                socket.send(outPacket); // Paket senden
                // Schaue ob empfangener Peer bereits bekannt ist, wenn nicht füge ihn hinzu
                checkPeerSet(new Peer(inPacket.getAddress(), inPacket.getPort()));

                // Falls neue Lieder hinzugekommen sind, gebe nur die neuen auf der Konsole aus
                if(oldMusicSize < musicList.size())
                    for(int i = oldMusicSize; i < musicList.size(); i++)
                        System.out.println("Interpret: " + musicList.get(i).getArtist() +
                                ", Titel: " + musicList.get(i).getTitle());
            }

        }catch (IOException ex) {

            System.out.println(ex);

        }
    }

    /**
     * Konstruktor. Akzeptiert den Dateinamen eines CSV-Files im hiesigen Format: Interpret;Titel; zeilenweise
     * Scannt daraufhin das Netzwerk nach Peers und wartet daraufhin auf neue Peers.
     * @param port Port, auf dem gelauscht werden soll.
     * @param filename  Dateiname zum CSV-File mit Liedern im Format: Interpret;Titel; (zeilenweise)
     */
    public MusicShareClient(int port, String filename) {

        musicList = new ArrayList<>();

        // Datei auslesen und Lieder der musicList beifügen
        try {
            File file = new File(filename);
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()){
                String line = scanner.nextLine();
                if(!line.trim().isEmpty())
                    csvToList(line.trim());
            }
        } catch (FileNotFoundException e) {

            System.out.println(e.getMessage());
        }

        // Socket öffnen und Broadcast erlauben.
        this.port = port;
        try {
            socket = new DatagramSocket(this.port);
            socket.setBroadcast(true);
        } catch (Exception ex) { System.out.println(ex); }

        peerList = new ArrayList<>();

        // nach Peers scannen im Raum 50 001 bis 50 010
        scan(50001, 50010);

        // Auf andere Peers warten
        listen();
    }

    /**
     * Konstruktor. Akzeptiert eine MusicModel-Liste
     * Scannt daraufhin das Netzwerk nach Peers und wartet daraufhin auf neue Peers.
     * @param port Port, auf dem gelauscht werden soll.
     * @param musicList Liste mit MusicModel-Objekten als Datengrundlage
     */
    public MusicShareClient(int port, ArrayList<MusicModel> musicList) {

        // Socket öffnen und Broadcast erlauben.
        this.port = port;
        try {
            socket = new DatagramSocket(this.port);
            socket.setBroadcast(true);
        } catch (Exception ex) { System.out.println(ex); }

        peerList = new ArrayList<>();
        this.musicList = musicList;

        // nach Peers scannen im Raum 50 001 bis 50 010
        scan(50001, 50010);

        // Auf andere Peers warten
        listen();
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
    private void csvToList(String musicModelListString) {

        String[] musicParts = musicModelListString.split(";");

        // Wenn Anzahl der der Teilstücke ungerade, sind es keine Paare.
        if((musicParts.length % 2) != 0) {

            System.out.println("Corrupt MusicString");
            return;
        }

        // Gehe alle Teilstücke durch
        for(int i = 0; i < musicParts.length;) {

            checkMusicList(musicParts[i++], musicParts[i++]);

        }
    }

    /**
     * Überprüft ob ein gegebenes Musikstück bereits in der Musikliste ist.
     * Falls nicht, wird es hinzugefügt.
     * @param artist Name des Interpreten
     * @param title Titel des Liedes
     */
    private void checkMusicList(String artist, String title) {

        MusicModel mm = new MusicModel(artist, title);

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

    /**
     * Hinzufügen von Liedern zur Musikliste
     * @param artist Name des Interpreten
     * @param title Titel des Liedes
     */
    public void appendMusicSet(String artist, String title) {

        if(!artist.trim().isEmpty() && !title.trim().isEmpty())
            checkMusicList(artist, title);
    }

    /**
     * Main-Methode.
     * Fragt den Benutzer nach Port und Musik-CSV zur Demonstration
     * Siehe "Peer1 -3" Nutzung in separaten Klassen.
     * @param args Startparameter
     */
    public static void main( String[] args ) {

        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        System.out.print("Bitte geben Sie einen Port zwischen 50 001 und 50 010 ein: ");

        int port = 0;

        while(!(port > 50000 && port < 50011)) {
            try {
                port = Integer.valueOf(br.readLine());
            } catch (IOException e) {

                System.out.println();
            }
            if(!(port > 50000 && port < 50011))
                System.out.print("Bitte erneut: ");
        }

        System.out.print("Bitte geben Sie den Dateipfad zur gewuenschten CSV-Datei ein (MusicList1 - 3): ");

        String filename = "";
        while(filename.trim().isEmpty()) {
            try {
                filename = br.readLine();
            } catch (IOException e) {

                System.out.println();
            }
            if(filename.trim().isEmpty())
                System.out.print("Bitte erneut: ");
        }

        //Instanziierung und damit Start des Peers
        MusicShareClient msc = new MusicShareClient(port, filename);
    }
}
