import java.net.*;
import java.io.*;

public class TCPWeatherClient
{
    private Socket              socket    = null;
    private DataInputStream     console   = null;
    private DataInputStream     inStream  = null;
    private DataOutputStream    outStream = null;

    public TCPWeatherClient(int serverPort)
    {

        try {
            socket = new Socket("localhost", serverPort);
            System.out.println("Verbunden: " + socket);

            console = new DataInputStream(System.in);
            outStream = new DataOutputStream(socket.getOutputStream());
            inStream = new DataInputStream(socket.getInputStream());
        }
        catch(IOException ioe)
        {  System.out.println(ioe.getMessage());
        }
        String line = "";



        while (!line.equals("logout"))
        {
            try {
                System.out.print("Stadt: ");
                line = console.readLine();

                outStream.writeUTF(line);
                outStream.flush();
                String serverResponse = inStream.readUTF();

                if(!line.equals("logout"))
                    System.out.println("Client fragt nach Wetter in " + line + ", Server antwortet \"" + serverResponse + "\"");

            } catch(IOException ioe) {

                System.out.println(ioe.getMessage());
            }
        }

        try {

            if (console != null) console.close();
            if (outStream != null) outStream.close();
            if (socket != null) socket.close();

        } catch (IOException ex) {

            System.out.println(ex.getMessage());
        }
    }
    public static void main(String args[])
    {
        TCPWeatherClient client = new TCPWeatherClient(50000);
    }
}