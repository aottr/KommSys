import java.io.*;
import java.net.*;

class TCPWeatherServer
{
    private Socket           socket      = null;
    private ServerSocket     server      = null;
    private DataInputStream  inStream    = null;
    private DataOutputStream outStream   = null;

    public TCPWeatherServer(Integer port) {

        try {

            System.out.println("Binding to " + port);

            server = new ServerSocket(port);
            System.out.println("Server started:" + server);

            socket = server.accept();
            System.out.println("Client accepted: " + socket);

            inStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            outStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            boolean logout = false;

            while (!logout) {

                try {

                    String userInput = inStream.readUTF();
                    System.out.println("Received: " + userInput);
                    String ausgabe = "";
                    switch (userInput.toLowerCase()) {

                        case "leipzig":
                            ausgabe = getWeatherFromOWMID(2879139);
                            break;
                        case "stuttgart":
                            ausgabe = getWeatherFromOWMID(2825297);
                            break;
                        case "hamburg":
                            ausgabe = getWeatherFromOWMID(2911298);
                            break;
                        case "erfurt":
                            ausgabe = getWeatherFromOWMID(2929670);
                            break;
                        case "jena":
                            ausgabe = getWeatherFromOWMID(2895044);
                            break;
                        case "logout":
                            logout = true;
                            break;
                        default:
                            ausgabe = "could not find city";
                            break;
                    }

                    outStream.writeUTF(ausgabe);
                    outStream.flush();

                } catch (IOException ioe) { logout = true; }
            }
            if(socket != null) socket.close();
            if(inStream != null) inStream.close();
            if(outStream != null) outStream.close();

        } catch (IOException ioe) {

            System.out.println(ioe.getMessage());
        }
    }

    public static String getWeatherFromOWMID(Integer id) {

        StringBuilder sb = new StringBuilder();
        String fullWeather = "";
        try {
            URLConnection connection = new URL("http://api.openweathermap.org/data/2.5/forecast/city?id=" + id.toString() + "&APPID=10f337ba4dd03476cfaf2f35b2bf273d&cnt=1&units=metric").openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            in.close();

            String weather = sb.substring(sb.indexOf("weather"), sb.indexOf("clouds"));
            fullWeather = weather.substring(weather.indexOf("description") + 14, weather.indexOf("\",\"icon"));
            fullWeather += ", " + String.format("%.2f", Float.parseFloat(sb.substring(sb.indexOf("{\"temp\":") + 8, sb.indexOf(",\"temp_min")))) + "°C";

        } catch (IOException ioe) {

            switch (id) {

                case 2879139:
                    fullWeather = "Sonnig, 20°C";
                    break;
                case 2825297:
                    fullWeather = "Sonnig, 23°C";
                    break;
                case 2911298:
                    fullWeather = "Windig,, 18°C";
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

    public static void main(String argv[]) throws Exception
    {
        TCPWeatherServer server = new TCPWeatherServer(50000);
    }
}