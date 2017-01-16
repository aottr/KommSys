import java.net.*;
import java.io.*;

public class TCPWeatherClient
{  private Socket socket              = null;
    private DataInputStream  console   = null;
    private DataOutputStream streamOut = null;
    private  DataInputStream inStream = null;

    public TCPWeatherClient(String serverName, int serverPort)
    {

        System.out.println("Establishing connection. Please wait ...");

        BufferedReader inFromServer = null;
        try
        {  socket = new Socket(serverName, serverPort);
            System.out.println("Connected: " + socket);
            start();
            inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
        catch(UnknownHostException uhe)
        {  System.out.println("Host unknown: " + uhe.getMessage());
        }
        catch(IOException ioe)
        {  System.out.println("Unexpected exception: " + ioe.getMessage());
        }
        String line = "";



        while (!line.equals("logout"))
        {
            try {
                System.out.print("Stadt: ");
                line = console.readLine();

                streamOut.writeUTF(line);
                streamOut.flush();
                String serverResponse = inStream.readUTF();
                System.out.println("Client fragt nach Wetter in " + line + ", Server antwortet \"" + serverResponse + "\"");

            } catch(IOException ioe) {

                System.out.println("Sending error: " + ioe.getMessage());
            }
        }
    }

    public void start() throws IOException
    {  console   = new DataInputStream(System.in);
        streamOut = new DataOutputStream(socket.getOutputStream());
        inStream = new DataInputStream(socket.getInputStream());
    }
    public void stop()
    {  try
    {  if (console   != null)  console.close();
        if (streamOut != null)  streamOut.close();
        if (socket    != null)  socket.close();
    }
    catch(IOException ioe)
    {  System.out.println("Error closing ...");
    }
    }
    public static void main(String args[])
    {  TCPWeatherClient client = null;

            client = new TCPWeatherClient("localhost", 50000);
    }
}