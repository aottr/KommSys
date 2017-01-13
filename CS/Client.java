package CS;

import java.net.*;
import java.io.*;

public class Client {
	
	public static void main(String[] args) throws IOException, UnknownHostException {
		
		Socket connection = new Socket("localhost", 5001);
		BufferedReader userInput = new BufferedReader( new InputStreamReader(System.in));
		
		DataOutputStream output = new DataOutputStream(connection.getOutputStream());
		BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		
		String messageToServer = userInput.readLine();
		output.writeBytes(messageToServer + '\n');
		
		String messageFromServer = input.readLine();		
		
		System.out.println("FROM SERVER: " + messageFromServer);
		
		connection.close();
	}
}
