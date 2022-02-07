package model.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * File handling class
 * Processes file data
 * @author BAMBO_L
 */
public class FileHandler {

	
	private static File f_list = new File("./data/server/fileList.txt");
	/**
	 * Method to retrieve list of the files on the server
	 * @return list
	 */
	public static String getList()
	{
		String s = "";
		//File to get list from
		//File list = new File("./data/server/fileList.txt");
		try {
			//Create a Scanner object
			Scanner reader = new Scanner(f_list);
			//Read file contents
			while(reader.hasNext())
			{
				//Add each list item to string
				String line = reader.nextLine();
				s+=line+"\n";
			}
			//close reader
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s;
	}
	
	
	/**
	 * Method  to retrieve File from list using an ID
	 * @param id
	 * ID of the image
	 */
	public static File getFile(String id)
	{
		File f = null;
		//Get name of the file list
		//File list = new File("./data/server/fileList.txt");
		try {
			
			Scanner reader = new Scanner(f_list);
			//Look for the filename in "list"
			while(reader.hasNext())
			{
				//Search for filename using provided id
				String line = reader.nextLine();
				StringTokenizer tk = new StringTokenizer(line);
				String fID = tk.nextToken();
				String name = tk.nextToken().trim();
				boolean isfound = fID.equals(id);
				if(isfound)
				{
					f = new File("./data/server/"+name);
					break;	
				}
				
			}
			//close reader
			reader.close();
			
		} catch (FileNotFoundException e) {
			//The error caused if "list" is not found
			System.err.println("File: " + f_list.getName() + "\nNot found");
			e.printStackTrace();
		}

		//Alert user that the file has not been found
		if(f == null)
		{
			String msg = "File with id:" + id+" not found..";
			System.err.println(msg);
			
		}
		return f;
	}
	
	/**
	 * Method to add file name to list
	 * @param name
	 * The name of the file to be added
	 * @return
	 * True if ID and file name are added to "list"
	 * False otherwise
	 */
	public static boolean addFile(String name)
	{
		boolean blnAdded = true;
		//File list = new File("./data/server/fileList.txt");
		try {
			
			int f_id = getLastID();
			//Add file to list
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(f_list,true)));
			String d = f_id + " " + name;
			pw.println(d);
			pw.close();
			
			
		} catch (IOException e) {
			System.out.println("Error adding ID and file name to list");
			e.printStackTrace();
			blnAdded = false;
		}
		
		return blnAdded;
	}
	
	private static int getLastID()
	{
		int temp = 0;
		try {
			//Create a Scanner object
			Scanner reader = new Scanner(f_list);
			String s = "";
			//Read file contents
			while(reader.hasNext())
			{
				String line = reader.nextLine();
				s=line;
			}
			//close reader
			reader.close();
			
			StringTokenizer tk = new StringTokenizer(s);
			String id = tk.nextToken();
			System.out.println(id);
			temp = Integer.parseInt(id);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ++temp;
	}
	
}
