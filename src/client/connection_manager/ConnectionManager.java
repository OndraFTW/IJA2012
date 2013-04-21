package client.connection_manager;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

import shared.*;

/**
* Connection manager.
* @author Vojtěch Šimša
* @author Ondřej Šlampa
*/
public class ConnectionManager extends JPanel implements ActionListener
{
	private JButton connect_button;
	private JButton disconnect_button;
	private JButton register_button;
	private JButton login_button;
	private JButton logoff_button;
	private JButton close_button;
	
	private JTextField host_field;
	private JTextField port_field;
	private JTextField register_username_field;
	private JTextField register_password1_field;
	private JTextField register_password2_field;
	private JTextField login_username_field;
	private JTextField login_password_field;
	
	private JLabel left_error;
	private JLabel middle_error;
	private JLabel right_error;
	
	private Coder coder;
	
	/**
	* Creates connection manager.
	*/
	public ConnectionManager()
	{
		coder=new Coder();
		createGUI();
	}
	
	/**
	* Creates connection manager GUI.
	*/
	private void createGUI()
	{
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		
		//left panel
		JPanel left_panel=new JPanel();
		left_panel.setLayout(new BoxLayout(left_panel, BoxLayout.PAGE_AXIS));
		left_panel.setBorder(BorderFactory.createTitledBorder("Connection"));
		
		connect_button=new JButton("Connect");
		disconnect_button=new JButton("Disconnect");
		close_button=new JButton("Close");
		
		connect_button.addActionListener(this);
		disconnect_button.addActionListener(this);
		close_button.addActionListener(this);
		
		host_field=new JTextField("localhost");
		port_field=new JTextField("10191");
		
		host_field.setMaximumSize(new Dimension(500, 50));
		port_field.setMaximumSize(new Dimension(500, 50));
		
		left_panel.add(Box.createRigidArea(new Dimension(0,5)));
		left_panel.add(new JLabel("Host:"));
		left_panel.add(Box.createRigidArea(new Dimension(0,5)));
		left_panel.add(host_field);
		left_panel.add(Box.createRigidArea(new Dimension(0,5)));
		left_panel.add(new JLabel("Port:"));
		left_panel.add(Box.createRigidArea(new Dimension(0,5)));
		left_panel.add(port_field);
		left_panel.add(Box.createRigidArea(new Dimension(0,5)));
		left_panel.add(connect_button);
		left_panel.add(Box.createRigidArea(new Dimension(0,5)));
		left_panel.add(disconnect_button);
		left_panel.add(Box.createRigidArea(new Dimension(0,5)));
		left_panel.add(close_button);
		left_panel.add(Box.createRigidArea(new Dimension(0,5)));
		left_panel.add(left_error=new JLabel());
		left_panel.add(Box.createVerticalGlue());
		
		left_error.setForeground(Color.RED);
		
		//middle panel
		JPanel middle_panel=new JPanel();
		middle_panel.setLayout(new BoxLayout(middle_panel, BoxLayout.PAGE_AXIS));
		middle_panel.setBorder(BorderFactory.createTitledBorder("Registration"));
		
		register_button=new JButton("Register");
		
		register_button.addActionListener(this);
		
		register_username_field=new JTextField();
		register_password1_field=new JPasswordField();
		register_password2_field=new JPasswordField();
		
		register_username_field.setMaximumSize(new Dimension(500, 50));
		register_password1_field.setMaximumSize(new Dimension(500, 50));
		register_password2_field.setMaximumSize(new Dimension(500, 50));
		
		middle_panel.add(Box.createRigidArea(new Dimension(0,5)));
		middle_panel.add(new JLabel("Username:"));
		middle_panel.add(Box.createRigidArea(new Dimension(0,5)));
		middle_panel.add(register_username_field);
		middle_panel.add(Box.createRigidArea(new Dimension(0,5)));
		middle_panel.add(new JLabel("Password:"));
		middle_panel.add(Box.createRigidArea(new Dimension(0,5)));
		middle_panel.add(register_password1_field);
		middle_panel.add(new JLabel("Password again:"));
		middle_panel.add(Box.createRigidArea(new Dimension(0,5)));
		middle_panel.add(register_password2_field);
		middle_panel.add(Box.createRigidArea(new Dimension(0,5)));
		middle_panel.add(register_button);
		middle_panel.add(Box.createRigidArea(new Dimension(0,5)));
		middle_panel.add(middle_error=new JLabel());
		middle_panel.add(Box.createVerticalGlue());
		
		middle_error.setForeground(Color.RED);
		
		//right panel
		JPanel right_panel=new JPanel();
		right_panel.setLayout(new BoxLayout(right_panel, BoxLayout.PAGE_AXIS));
		right_panel.setBorder(BorderFactory.createTitledBorder("Log in/off"));
		
		login_button=new JButton("Login");
		logoff_button=new JButton("Logoff");
		
		login_button.addActionListener(this);
		logoff_button.addActionListener(this);
		
		login_username_field=new JTextField("admin");
		login_password_field=new JPasswordField("admin");
		
		login_username_field.setMaximumSize(new Dimension(500, 50));
		login_password_field.setMaximumSize(new Dimension(500, 50));
		
		right_panel.add(Box.createRigidArea(new Dimension(0,5)));
		right_panel.add(new JLabel("Username:"));
		right_panel.add(Box.createRigidArea(new Dimension(0,5)));
		right_panel.add(login_username_field);
		right_panel.add(Box.createRigidArea(new Dimension(0,5)));
		right_panel.add(new JLabel("Password:"));
		right_panel.add(Box.createRigidArea(new Dimension(0,5)));
		right_panel.add(login_password_field);
		right_panel.add(Box.createRigidArea(new Dimension(0,5)));
		right_panel.add(login_button);
		right_panel.add(Box.createRigidArea(new Dimension(0,5)));
		right_panel.add(logoff_button);
		right_panel.add(Box.createRigidArea(new Dimension(0,5)));
		right_panel.add(right_error=new JLabel());
		right_panel.add(Box.createVerticalGlue());
		
		right_error.setForeground(Color.RED);
		
		add(left_panel);
		add(middle_panel);
		add(right_panel);
		
		validate();
 		repaint();
	}
	
	/**
	* Handles action event.
	* @param e action event 
	*/
	public void actionPerformed(ActionEvent e)
	{
		//source
		Object src=e.getSource();
		
		//connect
		if(src==connect_button)
		{
			if(client.Main.client.connected)
			{
				return;
			}
			
			int port;
			try
			{
				port=Integer.parseInt(port_field.getText());
			}
			catch(NumberFormatException ex)
			{
				left_error.setText("Incorrect port number.");
				return;
			}
			
			//connect client to server
			client.Main.client.connect(host_field.getText(), port);
			
			if(!client.Main.client.connected || client.Main.client.getResponse().equals(coder.getNotOKResponse()))
			{
				left_error.setText("Connect failed.");
			}
			else
			{
				left_error.setText("");
			}
			middle_error.setText("");
			right_error.setText("");
		}
		//disconnect
		else if(src==disconnect_button)
		{
			client.Main.client.disconnect();
			
			if(client.Main.client.connected)
			{
				left_error.setText("Disconnect failed.");
			}
			else
			{
				left_error.setText("");
			}
			middle_error.setText("");
			right_error.setText("");
		}
		//close connection manager
		else if(src==close_button)
		{
			client.Main.client.tabbed_pane.remove(this);
		}
		//register new user
		else if(src==register_button)
		{
			//are passwords equal
			boolean success=register_password1_field.getText().equals(register_password2_field.getText());
			if(success)
			{
				success=client.Main.client.register(register_username_field.getText(),register_password1_field.getText());
			}
			
			if(!success || client.Main.client.getResponse().equals(coder.getNotOKResponse()))
			{
				middle_error.setText("Registration failed.");
			}
			else
			{
				middle_error.setText("");
			}
			right_error.setText("");
			left_error.setText("");
		}
		//login user to server
		else if(src==login_button)
		{
			boolean success=client.Main.client.login(login_username_field.getText(),login_password_field.getText());
			
			if(!success || client.Main.client.getResponse().equals(coder.getNotOKResponse()))
			{
				right_error.setText("Login failed.");
			}
			else
			{
				right_error.setText("");
			}
			middle_error.setText("");
			left_error.setText("");
		}
		//log off the server
		else if(src==logoff_button)
		{
			boolean success=client.Main.client.logoff();
			
			if(!success || client.Main.client.getResponse().equals(coder.getNotOKResponse()))
			{
				right_error.setText("Logoff failed.");
			}
			else
			{
				right_error.setText("");
			}
			middle_error.setText("");
			left_error.setText("");
		}
	}
}

