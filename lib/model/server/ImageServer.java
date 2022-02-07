package model.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Class representing ImageServer
 * CLass models image server to handle multiple requests
 * @author BAMBO_L
 *
 */
public class ImageServer {

	//Attributes
	private ServerSocket ss = null;
	private final static int PORT_NUM = 7455;
	
	/**
	 * Method to start the server
	 * Clients connect to server when a request is issued
	 * Upon handling request the connection is closed
	 */
	public void startServer() {
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					ss = new ServerSocket(PORT_NUM);
					System.out.println("Waiting for connection....");
					while(true)
					{
						//Wait for client connection
						Socket clientSocket = ss.accept();
						System.out.println("<Connected>");
						
						//Start client session
						new Thread(new HandleSession(clientSocket)).start();
						
						
					}
					
					
				} catch (IOException e) {
					
					System.err.println("Error with server socket at port number: " + PORT_NUM);
					e.printStackTrace();
				}
				
				finally
				{
					//Close server socket
					if(ss != null)
					{
						try {
							ss.close();
							System.out.println("Server socket closed");
						} catch (IOException e) {
							System.out.println("Error in closing server socket");
							e.printStackTrace();
						}
					}
				}
				
			}
		}).start();
	
	}
}
