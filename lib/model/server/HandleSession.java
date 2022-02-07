package model.server;

import model.file.FileHandler;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.util.StringTokenizer;


/**
 * Class representing HandleSession
 * Task to handle a single request
 * @author BAMBO_L
 *
 */

public class HandleSession implements Runnable{

	//Attributes
	private Socket s = null;
	private DataInputStream fromClient = null;
	private DataOutputStream toClient = null;
	private static int requestCount = 0;
	/**
	 * Constructor taking in client socket
	 * @param s1
	 * client socket
	 */
	public HandleSession(Socket s1) {
		s = s1;
		requestCount++;
	}
	
	public void run(){
		
		try {
			toClient = new DataOutputStream(s.getOutputStream());
			fromClient = new DataInputStream(s.getInputStream());
			//Read request
			String code = fromClient.readUTF();
			System.out.println(requestCount + ". Request code: " + code);
			//Handle Data request
			if(code.startsWith("DATA"))
			{
				//Get list
				String s = FileHandler.getList();
				System.out.println(s);
				//Sending the list
				toClient.writeUTF(s);
				toClient.flush();
			}
			//Handle IMGRET request 
			else if(code.startsWith("IMGRET"))
			{
				//Get id from code
				StringTokenizer tk = new StringTokenizer(code);
				String s1 = tk.nextToken();
				String s2 = tk.nextToken();
				//Get file from id
				File f = FileHandler.getFile(s2);
				//Send code to client if file exists 
				//1 -> file is on the way
				//-1 -> file has not been found
				if(f != null)
				{
					toClient.writeInt(1);;
					//send file name
					toClient.writeUTF(f.getName());
					toClient.flush();
					//Send file data
					byte[] data = Files.readAllBytes(f.toPath());
					System.out.println(data.length);
					toClient.writeLong(data.length);
					toClient.write(data);
					toClient.flush();
				}
				else
				{
					//File not found
					toClient.writeInt(-1);
					toClient.flush();
				}
			}
			//Handle IMGSEND request
			else
			{
				
				//Break up code into segments
				StringTokenizer tk = new StringTokenizer(code);
				String s1 = tk.nextToken();
				String name = tk.nextToken();
				//Add ID and name to list
				boolean isfileSaved = FileHandler.addFile(name);
				System.out.println("File saved: " + isfileSaved);
				if(isfileSaved)
				{
					//send response
					toClient.writeUTF("Success");
					//Create a buffer to store file on server
					File f = new File("./data/server/"+name);
					
					int fsize1 = fromClient.readInt();
					System.out.println("length2: " + fsize1);
					byte[] buffer = new byte[fsize1];
					FileOutputStream fout  = new FileOutputStream(f);
					int n = 0, count = 0;
					int nlast = 0;
					while(true) {
						
						n = fromClient.read(buffer);
						
						if(count == 0)
							nlast = n;
						
						fout.write(buffer,0,n);
						System.out.println("N last sizd: " + nlast);
						System.out.println("N size: " + n);
						
						if(n != nlast)
							break;
						 
						++count;
						
						if(fromClient.available() <= 0)
							break;
					}
					
					System.out.println("file saved!");
					System.out.println(FileHandler.getList());
					//close output stream
					fout.close();
				}
				else
				{
					//File failed to save
					toClient.writeUTF("Failure");
					//send possible problem to client
					toClient.writeUTF("Error in saving file, file might be corrupted");
				}
				
				
				
								
			}
			
		} catch (IOException e) {
			System.err.println("Error receiving client request");
			e.printStackTrace();
		} 
		finally
		{
			//Close client socket and data streams
			if(s != null)
			{
				try {
					s.close();
					toClient.close();
					fromClient.close();
					
					System.out.println("Client socket closed");
				} catch (IOException e) {
					System.out.println("Error in closing client socket");
					e.printStackTrace();
				}
			}
		}
		
	}
	

}
