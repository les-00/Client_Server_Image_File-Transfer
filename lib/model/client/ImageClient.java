package model.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;


public class ImageClient {

	//Attributes
	private DataInputStream fromServer = null;
	private DataOutputStream toServer = null;
	private Socket clientSocket = null;
	private static final String HOST_NAME = "localhost";
	private static final int PORT_NUM = 7455;
	
	public ImageClient() {
		//Connect client to server
		connectToServer();
	}
	
	/**
	 * Method to connect client to server
	 * Client send requests DATA, IMGRET and IMGSEND 
	 */
	public void connectToServer()
	{
		//Set up a TCP connection protocol
		try {
			clientSocket = new Socket(HOST_NAME, PORT_NUM);
			fromServer = new DataInputStream(clientSocket.getInputStream());
			toServer = new DataOutputStream(clientSocket.getOutputStream());
			
			
			// Handle connection error
		} catch (IOException e) {
			System.err.println("Error occured connecting to socket with port: " + PORT_NUM);
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Method to request list data stored on server
	 * The list is returned as a readable string
	 * @return list
	 */
	public String requestDATA()
	{
		String res = null;
		try {
			//Send request code to server
			toServer.writeUTF("DATA");
			toServer.flush();
			res = fromServer.readUTF();
			
			//Error handling
		} catch (IOException e) {
			System.err.println("Error occured during DATA..");
			e.printStackTrace();
		}
		return res;
	}
	/**
	 * Method to retrieve image from server
	 * image can be retrieved if the client provides the ID
	 * of the image.
	 * This method will DELETE/OVERRIDE existing files
	 * of the same name stored in default client folder
	 * @param id
	 */
	public String requestIMGRET(String id)
	{
		String path = "NP";
		try {
			String code = "IMGRET " + id;
			String fileName = "";
			
			//Send request code to server
			toServer.writeUTF(code);
			toServer.flush();
			
			//Receive code from server
			//"1" -> File on coming
			//"-1" -> file not found
			int fCode = fromServer.readInt();
			if(fCode == 1)
			{
				//Read in file name
				fileName = fromServer.readUTF();
				long lSize = fromServer.readLong();
				byte[] lData = new byte[1024];
				
				//Read in file data
				//Write file to client memory
				path = "./data/client/"+fileName;
				File f = new File(path);
				FileOutputStream fout = new FileOutputStream(f,false);
				while(fromServer.read(lData) != -1)
					fout.write(lData);
				//Close the output stream
				fout.close();
			}
			else
			{
				//Alert user that file is not found/has some error
				String msg = "File with id: " + id+" not found...";
				System.err.println(msg);
			}
			
		} catch (IOException e) {
			System.err.println("Error occured retrieving image file");
			e.printStackTrace();
		}
		
		return path;
	}
	
	/**
	 * Method to send to the Server a file to save
	 * @param imgFile
	 * The file to be sent
	 */
	public void requestIMGSEND(File imgFile)
	{

		try {
			//Get file name
			String fileName = imgFile.getName();
			//Code to send to server according to protocol
			String code = "IMGSEND "+ fileName;
			//Send request code to server
			toServer.writeUTF(code);
			//Get Server response
			String serverRes = fromServer.readUTF();
			if(serverRes.equals("Success"))
			{
				//Read file data
				byte[] data;
				data = Files.readAllBytes(imgFile.toPath());
				toServer.writeInt(data.length);
				//Send data to server
				toServer.write(data);		
				toServer.flush();
			}
			else
			{
				//Show client response
				String prob = fromServer.readUTF();
				System.err.println(prob);
			}		
			
		} catch (IOException e) {
			System.err.println("Error sending image!!!");
			e.printStackTrace();
		}
	}
	
}
