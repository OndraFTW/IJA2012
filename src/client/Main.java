package client;

import client.Client;  

/**
* Main class.
* @author Vojtěch Šimša
* @author Ondřej Šlampa
*/
public class Main
{
	/**Client application.*/
	static public Client client;
	
	/**Main function*/
	public static void main(String[] args)
	{
		//create new application and run it in separate thread.
		javax.swing.SwingUtilities.invokeLater
		(
			new Runnable()
			{
				public void run()
				{
					client=new Client();
				}
			}
		);
	}
}

