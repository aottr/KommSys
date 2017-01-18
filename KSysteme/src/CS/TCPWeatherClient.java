package CS;

import java.net.*;
import java.io.*;

/**
 * Wetterdienst-Client zur für die Verbindung mit zugehörigem Server.
 * Via TCP wird eine Verbindung eines einzelnen Clients mit dem Server hergestellt.
 * Dieser kann dann eine von 5 Städten Deutschlands (Leipzig, Stuttgart, Hamburg, Erfurt, Jena)
 * abfragen und bekommt aktuelle Daten über Temperatur und Wetter der jeweiligen Stadt über
 * die OpenWeatherApp-API. Sollte die Verbindung nicht funktionieren, werden vordefinierte Wetter-
 * Zustände als Daten ausgegeben. Beendet wird die Verbindung durch die Eingabe: logout
 *
 * @author Dustin Kröger [3728859]
 * @version 1.1.2
 */
public class TCPWeatherClient
{
    /**
     * Initialisierung der privaten Variablen für die Datenverbindung, Ein- und Ausgabe
     */
    private Socket              socket    = null;
    private DataInputStream     console   = null;
    private DataInputStream     inStream  = null;
    private DataOutputStream    outStream = null;

    /**
     * Konstruktor und zugleich Träger der Programmlogik. In einer späteren Version kann einfach eine
     * asynchrone Verarbeitung durch Threads hinzugefügt werden. Im rahmen dieser Aufgabe reicht dies jedoch.
     * Fehlerbehebung findet innerhalb der Methode statt durch Ausgabe in der Konsole.
     * @param adress Adresse des Servers, auf den sich verbunden werden soll.
     * @param serverPort Port des Servers, auf den sich verbunden werden soll
     */
    public TCPWeatherClient(String adress, int serverPort)
    {
        try {
            /**
             * Aufbau der Verbindung mit einem Server auf der Adresse "localhost" und dem gegebenen Port.
             * Verbindung zu einem Server mit anderer Adresse möglich, wirft im Fehlerfall UnknownHostException.
             */
            socket = new Socket(adress, serverPort);
            System.out.println("Verbunden: " + socket);

            // Zuweisung der Streams
            console = new DataInputStream(System.in);                       // Eingabe in der Konsole
            outStream = new DataOutputStream(socket.getOutputStream());     // Stream zur Ausgabe am Server
            inStream = new DataInputStream(socket.getInputStream());        // Stream mit Server-Antwort
        }
        catch(IOException ioe)
        {  System.out.println(ioe.getMessage());
        }
        String line = "";

        // While-Schleife mit Abbruchbedingung: Eingabe logout.
        while (!line.equals("logout") && !line.equals(".end"))
        {
            try {
                System.out.print("Stadt oder Befehl: ");
                line = console.readLine();  // Einlesen der Benutzereingabe

                // Senden der Eingabe an den Server über ausgehenden Stream
                outStream.writeUTF(line);
                outStream.flush(); // Alle Bytes im Buffer schicken

                // Annahme der Server-Antwort durch Auslesen des einkommenden Streams
                String serverResponse = inStream.readUTF();

                // Ausgabe wie in Aufgabestellung, sofern nicht ausgeloggt wurde.
                if(!line.equals("logout") && !line.equals(".end"))
                    System.out.println("Client fragt nach Wetter in " + line + ", Server antwortet \"" + serverResponse + "\"");

            } catch(IOException ioe) {

                System.out.println(ioe.getMessage());
            }
        }

        try {

            // Schließen aller Verbindungen
            if (console != null) console.close();
            if (outStream != null) outStream.close();
            if (socket != null) socket.close();

        } catch (IOException ex) {

            System.out.println(ex.getMessage());
        }
    }

    /**
     * Main Methode für die einfache Ausführung des Clients.
     * Aufruf des Servers mit Port 50000.
     * @param args wird nicht benötigt. Port/Serveradresse via Startparameter realisierbar.
     */
    public static void main(String args[])
    {
        TCPWeatherClient client = new TCPWeatherClient("localhost", 50000);
    }
}