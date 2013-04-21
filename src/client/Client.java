package client;

import client.Main;
import shared.Coder;
import client.browser.Browser;
import client.editor.Editor;
import client.connection_manager.ConnectionManager;
import client.settings.Settings;
import client.help.Help;
import client.about.About;

import nu.xom.*;
import javax.swing.*;     
import java.awt.event.*;
import java.awt.*;
import java.net.*;
import java.io.*;

/**
* Client side application.
* @author Vojtěch Šimša
* @author Ondřej Šlampa
*/
public class Client extends JFrame implements ActionListener
{
	private static JMenuBar menu_bar;
	/**Panel with tabs.*/
	public static JTabbedPane tabbed_pane;
	
	private JMenuItem open_new;
	private JMenuItem open_file;
	private JMenuItem browser;
	private JMenuItem connection_manager;
	private JMenuItem settings;
	private JMenuItem help;
	private JMenuItem about;
	
	/**Communication socket.*/
	public static Socket socket;
	/**Response sender.*/
	public static PrintWriter out;
	/**Request receiver.*/
	public static BufferedReader in;
	
	/**User's name.*/
	public static String username;
	
	private Coder coder;
	
	/**Connection flag.*/
	public boolean connected;
	/**Log in flag.*/
	public boolean loggedin;
	
	/**Font.*/
	public Font font;
	/**Font metrics.*/
	public FontMetrics font_metrics;
	/**Font name.*/
	public String font_name;
	/**Font size.*/
	public int font_size;
	/**Font style.*/
	public int font_style;
	
	/**Place color*/
	public Color place_color;
	/**Active transition color*/
	public Color active_transition_color;
	/**Transition color*/
	public Color transition_color;
	/**Arc color*/
	public Color arc_color;
	/**Line color*/
	public Color line_color;
	/**Font color*/
	public Color font_color;
	
	/**Configuration file.*/
	public File config;
	
	/**Failure flag.*/
	public boolean failed;
	
	/**
	* Creates client side aplication.
	*/
	public Client()
	{
		config=new File("config.xml");
		setConfig();
		createAndShowGUI();
		coder=new Coder();
		connected=false;
		loggedin=false;
		failed=false;
		tabbed_pane.addTab("Connection manager", new ConnectionManager());
	}
	
	/**
	* Sets client configuration according to configuration file.
	* If the file doesn't exist, configuration is set to default.
	*/
	public void setConfig()
	{
		if(config.exists())
		{
			//parse config file
			Document doc=null;
			try
			{
				Builder parser=new Builder(false);
				doc=parser.build(config);
			}
			catch (ValidityException ex)
			{
				doc=ex.getDocument();
			}
			catch (ParsingException ex)
			{
				setDefaultConfig();
				failed=true;
				return;
			}
			catch (IOException ex)
			{
				setDefaultConfig();
				failed=true;
				return;
			}
			
			Element root=doc.getRootElement();
			
			if(!root.getQualifiedName().equals("config"))
			{
				setDefaultConfig();
				failed=true;
				return;
			}
			
			//get new configuration
			place_color=new Color(Integer.parseInt(root.getAttributeValue("place_color")));
			active_transition_color=new Color(Integer.parseInt(root.getAttributeValue("active_transition_color")));
			transition_color=new Color(Integer.parseInt(root.getAttributeValue("transition_color")));
			arc_color=new Color(Integer.parseInt(root.getAttributeValue("arc_color")));
			line_color=new Color(Integer.parseInt(root.getAttributeValue("line_color")));
			font_color=new Color(Integer.parseInt(root.getAttributeValue("font_color")));
			
			font_name=root.getAttributeValue("font_name");
			font_size=Integer.parseInt(root.getAttributeValue("font_size"));
			font_style=Integer.parseInt(root.getAttributeValue("font_style"));
			
			font=new Font(font_name, font_style, font_size);
		}
		else
		{
			setDefaultConfig();
		}
		
		saveConfig();
		failed=false;
	}
	
	/**
	* Sets configuration to default.
	*/
	public void setDefaultConfig()
	{
		font_name="Default";
		font_size=15;
		font_style=0;
		
		font=new Font(font_name, font_style, font_size);
		
		place_color=Color.WHITE;
		active_transition_color=Color.RED;
		transition_color=Color.WHITE;
		arc_color=Color.GRAY;
		line_color=Color.BLACK;
		font_color=Color.BLACK;
		
		failed=false;
	}
	
	/**
	* Saves current configuration to config file.
	*/
	public void saveConfig()
	{
		//create configuration file
		Element root=new Element("config");
		root.addAttribute(new Attribute("place_color", Integer.toString(place_color.getRGB())));
		root.addAttribute(new Attribute("active_transition_color", Integer.toString(active_transition_color.getRGB())));
		root.addAttribute(new Attribute("transition_color", Integer.toString(transition_color.getRGB())));
		root.addAttribute(new Attribute("arc_color", Integer.toString(arc_color.getRGB())));
		root.addAttribute(new Attribute("line_color", Integer.toString(line_color.getRGB())));
		root.addAttribute(new Attribute("font_color", Integer.toString(font_color.getRGB())));
		
		root.addAttribute(new Attribute("font_name", font_name));
		root.addAttribute(new Attribute("font_size", Integer.toString(font_size)));
		root.addAttribute(new Attribute("font_style", Integer.toString(font_style)));
		
		String s=new Document(root).toXML();
		
		//write the file
		try
		{
			FileWriter fw=new FileWriter(config);
			fw.write(s);
			fw.flush();
			fw.close();
			failed=false;
		}
		catch(IOException ex)
		{
			failed=true;
		}
	}
	
	/**
	* Connects client to server.
	* @param host host address
	* @param port port number
	*/
	public void connect(String host, int port)
	{
		if(connected)
		{
			return;
		}
		
		//send request
		try
		{
			socket=new Socket(host, port);
			out=new PrintWriter(new PrintStream(socket.getOutputStream()));
			in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			out.println(coder.getConnectionRequest());
			out.flush();
			
			connected=true;
			failed=false;
		}
		catch(UnknownHostException ex)
		{
			connected=false;
			failed=true;
		}
		catch(IOException ex)
		{
			connected=false;
			failed=true;
		}
	}
	
	/**
	* Disconnects client.
	*/
	public void disconnect()
	{
		if(!connected)
		{
			failed=true;
			return;
		}
		
		//send request
		try
		{
			out.println(coder.getDisconnectionRequest());
			out.flush();
			
			out.close();
			in.close();
			socket.close();
			
			connected=false;
			loggedin=false;
			failed=false;
		}
		catch(UnknownHostException ex)
		{
			failed=true;
		}
		catch(IOException ex)
		{
			failed=true;
		}
	}
	
	/**
	* Registers new user on server.
	* @param username users name
	* @param password password
	*/
	public boolean register(String username, String password)
	{
		if(!connected || !loggedin)
		{
			return false;
		}
		
		out.println(coder.getRegistrationRequest(username, password));
		out.flush();
		
		return true;
	}
	
	/**
	* Logs user to server.
	* @param username users name
	* @param password password
	*/
	public boolean login(String username, String password)
	{
		if(!connected)
		{
			return false;
		}
		
		out.println(coder.getLoginRequest(username, password));
		out.flush();
		this.username=username;
		loggedin=true;
		
		return true;
	}
	
	/**
	* Logs user off server.
	*/
	public boolean logoff()
	{
		if(!connected)
		{
			return false;
		}
		
		out.println(coder.getLogoffRequest(this.username));
		out.flush();
		this.username=null;
		loggedin=false;
		
		return true;
	}
	
	/**
	* @return servers response to clients last request
	*/
	public String getResponse()
	{
		try
		{
			String s=in.readLine();
			
			if(s==null)
			{
				return null;
			}
			
			s+="\n"+in.readLine();
			s+="\n"+in.readLine();
			//System.out.println(s);
			//System.out.flush();
			failed=false;
			return s;
		}
		catch(IOException ex)
		{
			failed=true;
			return null;
		}
	}
	
	/**
	* Sends request to the server.
	* @param request request
	*/
	public void sendRequest(String request)
	{
		out.println(request);
		out.flush();
		failed=false;
	}
	
	/**
	* Creates GUI.
	*/
	private void createAndShowGUI()
	{
		try  
		{  
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());  
		}  
		catch(Exception e)  
		{
		}
		
		JFrame root=new JFrame("Client");
		root.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		root.setSize(750, 600);
		
		////////////////////////////////////////////////////////////////////////
		//create menu
		menu_bar=new JMenuBar();
		
		//create menu File
		JMenu net_menu=new JMenu("Net");
		menu_bar.add(net_menu);
		
		open_new=new JMenuItem("New", KeyEvent.VK_N);
		open_new.addActionListener(this);
		open_new.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		net_menu.add(open_new);
		
		open_file=new JMenuItem("Open...", KeyEvent.VK_O);
		open_file.addActionListener(this);
		open_file.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		net_menu.add(open_file);
		
		net_menu.add(new JSeparator());
		
		browser=new JMenuItem("Browser", KeyEvent.VK_B);
		browser.addActionListener(this);
		browser.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, ActionEvent.CTRL_MASK));
		net_menu.add(browser);
		
		//create menu Tools
		JMenu tools_menu=new JMenu("Tools");
		menu_bar.add(tools_menu);
		
		connection_manager=new JMenuItem("Connection manager", KeyEvent.VK_C);
		connection_manager.addActionListener(this);
		connection_manager.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		tools_menu.add(connection_manager);
		
		settings=new JMenuItem("Settings", KeyEvent.VK_S);
		settings.addActionListener(this);
		settings.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		tools_menu.add(settings);
		
		//create menu Help
		JMenu help_menu=new JMenu("Help");
		menu_bar.add(help_menu);
		
		help=new JMenuItem("Help", KeyEvent.VK_H);
		help.addActionListener(this);
		help.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.CTRL_MASK));
		help_menu.add(help);
		
		about=new JMenuItem("About", KeyEvent.VK_A);
		about.addActionListener(this);
		about.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		help_menu.add(about);
		
		////////////////////////////////////////////////////////////////////////
		//panels
		tabbed_pane=new JTabbedPane();
		
		root.add(tabbed_pane);
		
		root.setJMenuBar(menu_bar);
		//root.pack();
		root.setVisible(true);
	}
	
	/**
	* Handles action event.
	* @param e action event 
	*/
	public void actionPerformed(ActionEvent e)
	{
		//source of event
		Object src=e.getSource();
		//open new net
		if(src==open_new)
		{
			tabbed_pane.addTab("Editor", new Editor());
		}
		//open file
		else if(src==open_file)
		{
			JFileChooser fc=new JFileChooser(System.getProperty("user.dir"));
			
			if(fc.showOpenDialog(menu_bar)==JFileChooser.APPROVE_OPTION)
			{
				tabbed_pane.addTab("Editor", new Editor(fc.getSelectedFile()));
			}
		}
		//open net browser
		else if(src==browser)
		{
			tabbed_pane.addTab("Browser", new Browser());
		}
		//open connection manager
		else if(src==connection_manager)
		{
			tabbed_pane.addTab("Connection manager", new ConnectionManager());
		}
		//open settings
		else if(src==settings)
		{
			tabbed_pane.addTab("Settings", new Settings());
		}
		//open help
		else if(src==help)
		{
			tabbed_pane.addTab("Help", new Help());
		}
		//open about
		else if(src==about)
		{
			tabbed_pane.addTab("About", new About());
		}
	}
}

