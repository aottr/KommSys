package CS;

import java.net.*;
import java.io.*;


public class Server {
	
	public static void main(String[] args) throws IOException {
		
		ServerSocket connection = new ServerSocket();
		connection.setReuseAddress(true);
		connection.bind(new InetSocketAddress(5001));
		
		String clientMsg = "";
		
		while(clientMsg != "shutdown") {
			
			Socket listen = connection.accept();
			InputStreamReader portReader = new InputStreamReader(listen.getInputStream());
			
			BufferedReader input = new BufferedReader(portReader);
			DataOutputStream output = new DataOutputStream(listen.getOutputStream());
			clientMsg = input.readLine();
			System.out.println("Received: " + clientMsg);
			
			output.writeBytes("Received: " + clientMsg);
		}
		
		connection.close();
	}
}
