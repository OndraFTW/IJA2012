package server;

import nu.xom.*;
import java.io.*;
import java.net.*;

import server.*;

/**
* Main class.
* @author Vojtěch Šimša
* @author Ondřej Šlampa
*/
public class Main
{
	/**Server socket*/
	static public ServerSocket server_socket;
	/**File manager.*/
	static public FileManager fm;
	
	/**
	* Main function.
	* @param args command line arguments
	*/
	public static void main(String[] args)
	{
		//default values
		int port=10191;
		
		//parse command line arguments
		if(args.length==2 && (args[1].equals("-h") || args[1].equals("--help")))
		{
			System.out.println("This is help message.");
		}
		else if(args.length==3 && (args[1].equals("-p") || args[1].equals("--port")))
		{
			try
			{
				port=Integer.parseInt(args[2]);
			}
			catch(NumberFormatException ex)
			{
				System.err.println("Wrong port number.");
				return;
			}
		}
		else if(args.length!=0)
		{
			System.err.println("Wrong number of command line arguments.");
			return;
		}
		
		//create filemanger
		fm=new FileManager();
		
		//create server socket
		try
		{
			server_socket=new ServerSocket(port);
		}
		catch(UnknownHostException ex)
		{
			System.err.println("Unknown host.");
			return;
		}
		catch(IOException ex)
		{
			System.err.println("Server socket: IOException.");
			return;
		}
		
		Socket socket;
		
		//accept connections and create new server threads
		for(;;)
		{
			try
			{
				socket=server_socket.accept();
				ServerThread thread=new ServerThread(socket);
				thread.start();
			}
			catch(IOException ex)
			{
				System.err.println("Socket for new server thread: IOException.");
				break;
			}
		}
		
		//close server socket
		try
		{
			server_socket.close();
		}
		catch(IOException ex)
		{
			System.err.println("Close server socket: IOException.");
			return;
		}
	}
}
