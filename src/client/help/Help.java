package client.help;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
* Project help.
* @author Vojtěch Šimša
* @author Ondřej Šlampa
*/
public class Help extends JPanel implements ActionListener
{
	/**
	* Creates project help.
	*/
	public Help()
	{
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		JPanel client=new JPanel();
		JPanel browser=new JPanel();
		JPanel connection_manager=new JPanel();
		JPanel editor=new JPanel();
		JPanel settings=new JPanel();
		JPanel simulator=new JPanel();
		
		client.setLayout(new BoxLayout(client, BoxLayout.PAGE_AXIS));
		browser.setLayout(new BoxLayout(browser, BoxLayout.PAGE_AXIS));
		connection_manager.setLayout(new BoxLayout(connection_manager, BoxLayout.PAGE_AXIS));
		editor.setLayout(new BoxLayout(editor, BoxLayout.PAGE_AXIS));
		settings.setLayout(new BoxLayout(settings, BoxLayout.PAGE_AXIS));
		simulator.setLayout(new BoxLayout(simulator, BoxLayout.PAGE_AXIS));
		
		//client help
		JLabel client_text=new JLabel("<html>Description:<br>This is client side application of IJA 2012 project: Petri net simulator.<br><br>Creating New Net:<br>To create new net use option \"New\" in \"Net\" menu or keyboard shortcut Ctrl+N.<br><br>Opening Net on Local machine:<br>To open net on local machine use option \"Open...\" in \"Net\" menu or keyboard shortcut Ctrl+O. New window will appear, use it to find and open the net.<br><br>Connecting to server and user accounts management:<br>Use Connection manager. To open Connection manager use option \"Connection manager\" in \"Tools\" menu or keyboard shortcut Ctrl+C.<br><br>Working with nets on server:<br>Open browser. To open Browser use option \"Browser\" in \"Tools\" menu or keyboard shortcut Ctrl+B.</html>");
		client_text.setAlignmentX(Component.LEFT_ALIGNMENT);
		client_text.setHorizontalAlignment(SwingConstants.LEFT);
		client.add(client_text);
		
		//browser help
		JLabel browser_text=new JLabel("<html>Description:<br>Browser allows you to view petri nets saved on server and to start their editation and simulation.<br><br>Net Selection:<br>To choose one net to work with, use combo boxes labeled as \"Net\" and \"Version\". Selected net is automaticaly displayed.<br><br>Loading nets from server:<br>To load current nets on server push \"Refresh\" button.<br><br>Net Filtering:<br>Enter your regular expression into the \"Filter\" field and push \"Refresh\" button to show filtered nets.<br><br>Net Editation:<br>To start editation push \"Edit\" button. Editation is done in a new panel.<br><br>Net Simulation:<br>To start simulation push \"Simulate\" button. Simulation is done in a new panel.</html>");
		browser_text.setAlignmentX(Component.LEFT_ALIGNMENT);
		browser_text.setHorizontalAlignment(SwingConstants.LEFT);
		browser.add(browser_text);
		
		//connection manager help
		JLabel connection_manager_text=new JLabel("<html>Description:<br>Connection manager allows you to connect client to a specified server, log in on server and register new user.<br><br>Connection and Disconnection:<br>To connect client to a sever, type host address and port number into \"Host\" and \"Port\" fields respectively and push \"Connect\" button. To disconnect client push \"Disconnect\" button.<br><br>Registering new user:<br>Type user's name into \"Username\" field and user's password into \"Password\" and \"Password again\" fields. To complete registration push \"Register\" button.<br>NOTE: Only admin can register new users.<br><br>Logging in and off:<br>To log yourself in type your username and password into \"Username\" and \"Password\" fields respectively and push \"Log in\" button. To log yourself off push \"Log off\" button.</html>");
		connection_manager_text.setAlignmentX(Component.LEFT_ALIGNMENT);
		connection_manager_text.setHorizontalAlignment(SwingConstants.LEFT);
		connection_manager.add(connection_manager_text);
		
		//editor help
		JLabel editor_text=new JLabel("<html>Description:<br>Editor allows you to edit petri nets and to save them on server and local machine.<br><br>Saving on local machine:<br>To save on local machine press \"Save locally\" button and enter file name and select path.<br><br>Saving on server:<br>To save on server press \"Save on server\" button.<br><br>Adding Place:<br>Push \"Add Place\" button and click into a net panel to choose location of the place on canvas. New window will appear. Enter tokens into a text area in the window and confirm operation by pressing \"OK\" button.<br><br>Adding Transition:<br>Push \"Add Transition\" button and click into a net panel to choose location of the transition on canvas. New window will appear. Enter guard into a text area in the window and confirm operation by pressing \"OK\" button.<br><br>Adding Arc:<br>Push \"Add Arc\" and click on one place and one transition. New window will appear. Enter varname into a text area in the window and confirm operation by pressing \"OK\" button.<br><br>Deleting Component:<br>Push \"Delete\" button and click on a component to be deleted. In case of arc click on varname.</html>");
		editor_text.setAlignmentX(Component.LEFT_ALIGNMENT);
		editor_text.setHorizontalAlignment(SwingConstants.LEFT);
		editor.add(editor_text);
		
		//settings help
		JLabel settings_text=new JLabel("<html>Description:<br>Setting allows you to set colors of petri net components and font color, size, and style.<br><br>Setting Colors:<br>To set colors use color choosers.<br><br>Setting Font, Font Size, and Font style:<br>Use combo boxes labeled \"Font\", \"Font Size\", and \"Font Style\" respectively.<br><br>Saving setting:<br>To save setting push \"Save\" button.<br><br>Loading settings:<br>Push \"Load\" button and find new settings file.<br><br>Setting Default Settings:<br>Push \"Default\" button.</html>");
		settings_text.setAlignmentX(Component.LEFT_ALIGNMENT);
		settings_text.setHorizontalAlignment(SwingConstants.LEFT);
		settings.add(settings_text);
		
		//simulator help
		JLabel simulator_text=new JLabel("<html>Description:<br>Simulator allows you to simulate petri nets.<br><br>Make One Step:<br>To make one step of simulation, push \"Step\" button.<br><br>Finishing Simulation:<br>To finish simulation press \"Finish\" button.<br><br>Ending simulation:<br>To end simulation push \"End\" button.</html>");
		simulator_text.setAlignmentX(Component.LEFT_ALIGNMENT);
		simulator_text.setHorizontalAlignment(SwingConstants.LEFT);
		simulator.add(simulator_text);
		
		JTabbedPane tabs=new JTabbedPane(JTabbedPane.LEFT);
		tabs.add("Client", client);
		tabs.add("Browser", browser);
		tabs.add("Connection manager", connection_manager);
		tabs.add("Editor", editor);
		tabs.add("Settings", settings);
		tabs.add("Simulator", simulator);
		
		add(tabs);
		
		//close button
		JButton close_button=new JButton("Close");
		close_button.addActionListener(this);
		add(close_button);
	}
	
	/**
	* Handles action event.
	* @param e action event 
	*/
	public void actionPerformed(ActionEvent e)
	{
		client.Main.client.tabbed_pane.remove(this);
	}
}
