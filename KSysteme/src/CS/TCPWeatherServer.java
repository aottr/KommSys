package CS;

import java.io.*;
import java.net.*;

/**
 * Wetterdienst-Server.
 * Via TCP wird eine Verbindung eines einzelnen Clients mit dem Server hergestellt.
 * Dieser kann dann eine von 5 Städten Deutschlands (Leipzig, Stuttgart, Hamburg, Erfurt, Jena)
 * abfragen und bekommt aktuelle Daten über Temperatur und Wetter der jeweiligen Stadt über
 * die OpenWeatherApp-API. Sollte die Verbindung nicht funktionieren, werden vordefinierte Wetter-
 * Zustände als Daten ausgegeben. Beendet wird die Verbindung durch die Eingabe: logout
 *
 * @author Dustin Kröger [3728859]
 * @version 1.1.0
 */
public class TCPWeatherServer
{
    /**
     * Initialisierung der privaten Variablen für die Datenverbindung, Ein- und Ausgabe
     */
    private Socket           socket      = null;
    private ServerSocket     server      = null;
    private DataInputStream  inStream    = null;
    private DataOutputStream outStream   = null;

    /**
     * Konstruktor und zugleich Träger der Programmlogik. In einer späteren Version kann einfach eine
     * asynchrone Verarbeitung durch Threads hinzugefügt werden. Im rahmen dieser Aufgabe reicht dies jedoch.
     * Fehlerbehebung findet innerhalb der Methode statt durch Ausgabe in der Konsole.
     * @param port Port, auf dem der Server "hören" soll
     */
    public TCPWeatherServer(Integer port) {

        try {

            // Information für den Ausführenden, auf welchem Port der Server horcht.
            System.out.println("Binding to " + port);

            /**
             * Initialisieren des Serversockets
             * Dieser wird benötigt um auf dem Port nach Client-Anfragen zu horchen und diese zu akzeptieren
             */
            server = new ServerSocket(port);
            System.out.println("Server started:" + server);

            boolean end = false;

            while(!end) {
                // Akzeptieren einer ankommenden Anfrage
                socket = server.accept();
                System.out.println("Client accepted: " + socket);

                // Über diese beiden Datenströme können geschieht die Ein- und Ausgabe von Nachrichten
                inStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                outStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

                /**
                 *  Hilfsvariable zur Versicherung der Abbruchbedingung unserer while-Schleife, in der
                 *  nacheinander und beliebig oft eingehende Anfragen bearbeitet werden.
                 *  Hätte auch mit simplen break der While-Schleife gelöst werden können, so kann der
                 *  Abbruch später abgefragt werden. Evtl. zur Fehlerbehebung o.ä.
                 */
                boolean logout = false;

                while (!logout) {

                    try {

                        // Lesen der Benutzereingabe aus dem Stream
                        String userInput = inStream.readUTF();
                        System.out.println("Received: " + userInput);
                        String ausgabe = "";

                        /**
                         * Eingabe wird zur Nichtbeachtung der Groß- und Kleinschreibung transformiert und
                         * abgeglichen. Der Aufruf der Methode liest die API simpel aus, auf ein richtiges Parsing
                         * wurde aufgrund der "Simplizität" dieser Anwendung verzichtet.
                         */
                        switch (userInput.toLowerCase()) {

                            case "leipzig":
                                ausgabe = getWeatherFromOWMID(2879139);
                                System.out.println("Get Weatherdata for: " + userInput);
                                break;
                            case "stuttgart":
                                ausgabe = getWeatherFromOWMID(2825297);
                                System.out.println("Get Weatherdata for: " + userInput);
                                break;
                            case "hamburg":
                                ausgabe = getWeatherFromOWMID(2911298);
                                System.out.println("Get Weatherdata for: " + userInput);
                                break;
                            case "erfurt":
                                ausgabe = getWeatherFromOWMID(2929670);
                                System.out.println("Get Weatherdata for: " + userInput);
                                break;
                            case "jena":
                                ausgabe = getWeatherFromOWMID(2895044);
                                System.out.println("Get Weatherdata for: " + userInput);
                                break;
                            case "logout":
                                logout = true;
                                System.out.println("user logged out");
                                break;
                            case ".end":
                                end = true;
                                logout = true;
                                break;
                            default:
                                ausgabe = "could not find city or command";
                                break;
                        }

                        // Senden der Wetterdaten an den Client.
                        outStream.writeUTF(ausgabe);
                        outStream.flush(); // - schickt die Bytes im Buffer ab.

                    } catch (IOException ioe) {
                        logout = true;
                    } // Logout bei einem Fehler
                }
            }
            // Schließen aller Streams und Freigabe der Ports
            if(socket != null) socket.close();
            if(inStream != null) inStream.close();
            if(outStream != null) outStream.close();
            if(server != null) server.close();

        } catch (IOException ioe) {

            System.out.println(ioe.getMessage());
        }
    }

    /**
     * Sehr simpler "Parser" des JSON-Strings von OWM. Es existieren einige 3th-Party Pakete
     * zum sicheren Parsen von JSON bzw. speziell der Wetterinformationen von OWM, jedoch wurde in diesem
     * Programm darauf verzichtet, um es nicht noch weiter zu überladen.
     * Sollte bei der Verbindung/Auswertung ein Fehler auftreten, werden vordefinierte Wetterdaten ausgegeben.
     * @param id OpenWeatherMap City-ID zur Abfrage der jeweiligen Stadt.
     * @return String mit den Wetterinformationen in der Form: "Wetterzustand, xx°C"
     */
    public static String getWeatherFromOWMID(Integer id) {

        StringBuilder sb = new StringBuilder();
        String fullWeather = "";
        try {
            // Zeilenweises abrufen der Wetter-API
            URLConnection connection = new URL("http://api.openweathermap.org/data/2.5/forecast/city?id=" + id.toString() + "&APPID=10f337ba4dd03476cfaf2f35b2bf273d&cnt=1&units=metric").openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            in.close();

            // "Parsen" des Strings um Wetterzustand und aktuelle Temperatur (<2h genau) aus dem String zu filtern.
            String weather = sb.substring(sb.indexOf(",\"weather\""), sb.indexOf(",\"clouds\""));
            fullWeather = weather.substring(weather.indexOf("description") + 14, weather.indexOf("\",\"icon"));
            fullWeather += ", " + String.format("%.2f", Float.parseFloat(sb.substring(sb.indexOf("{\"temp\":") + 8, sb.indexOf(",\"temp_min")))) + "°C";

        } catch (Exception ioe) {

            switch (id) {

                case 2879139:
                    fullWeather = "Sonnig, 20°C";
                    break;
                case 2825297:
                    fullWeather = "Sonnig, 23°C";
                    break;
                case 2911298:
                    fullWeather = "Windig, 18°C";
                    break;
                case 2929670:
                    fullWeather = "Regen, 19°C";
                    break;
                case 2895044:
                    fullWeather = "Neblig, 24°C";
                    break;
            }

            System.out.println(ioe.getMessage());
        }

        return fullWeather;
    }

    /**
     * Main Methode für die einfache Ausführung des Servers.
     * Aufruf des Servers mit Port 50000.
     * @param args wird nicht benötigt. Port via Startparameter realisierbar.
     */
    public static void main(String args[])
    {
        TCPWeatherServer server = new TCPWeatherServer(50000);
    }
}