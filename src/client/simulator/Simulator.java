package client.simulator;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.List;
import java.util.Iterator;
import java.io.*;

import nu.xom.*;

import shared.*;
import client.editor.Editor;
import client.NetCanvas;

/**
* Simulator (client side).
* @author Vojtěch Šimša
* @author Ondřej Šlampa
*/
public class Simulator extends JPanel implements ActionListener
{
	private JPanel right_panel;
	private JLabel error_message;
	
	private JButton step_button;
	private JButton finish_button;
	private JButton end_button;
	private JButton close_button;
	
	private String name;
	private int version;
	
	private List<Place> places;
	private List<Transition> transs;
	private List<Arc> arcs;
	
	private Coder coder;
	
	/**
	* Creates simulator.
	* @param name net name
	* @param version version number
	*/
	public Simulator(String name, int version)
	{
		this.name=name;
		this.version=version;
		coder=new Coder();
		createGUI();
		start();
	}
	
	/**
	* Creates simulator GUI.
	*/
	private void createGUI()
	{
		//left panel
		JPanel left_panel=new JPanel();
		left_panel.setLayout(new BoxLayout(left_panel, BoxLayout.PAGE_AXIS));
		left_panel.setBorder(BorderFactory.createTitledBorder("Controls"));
		
		step_button=new JButton("Step");
		finish_button=new JButton("Finish");
		end_button=new JButton("End");
		close_button=new JButton("Close");
		
		step_button.addActionListener(this);
		finish_button.addActionListener(this);
		end_button.addActionListener(this);
		close_button.addActionListener(this);
		
		error_message=new JLabel();
		error_message.setForeground(Color.RED);
		
		JTextField name_field=new JTextField(name);
		JTextField version_field=new JTextField(Integer.toString(version));
		
		error_message.setMaximumSize(new Dimension(500, 50));
		name_field.setMaximumSize(new Dimension(500, 50));
		version_field.setMaximumSize(new Dimension(500, 50));
		
		name_field.setEditable(false);
		version_field.setEditable(false);
		
		left_panel.add(Box.createRigidArea(new Dimension(0,5)));
		left_panel.add(new JLabel("Net:"));
		left_panel.add(Box.createRigidArea(new Dimension(0,5)));
		left_panel.add(name_field);
		left_panel.add(Box.createRigidArea(new Dimension(0,5)));
		left_panel.add(new JLabel("Version:"));
		left_panel.add(Box.createRigidArea(new Dimension(0,5)));
		left_panel.add(version_field);
		left_panel.add(Box.createRigidArea(new Dimension(0,5)));
		left_panel.add(step_button);
		left_panel.add(Box.createRigidArea(new Dimension(0,5)));
		left_panel.add(finish_button);
		left_panel.add(Box.createRigidArea(new Dimension(0,5)));
		left_panel.add(end_button);
		left_panel.add(Box.createRigidArea(new Dimension(0,5)));
		left_panel.add(close_button);
		left_panel.add(Box.createRigidArea(new Dimension(0,5)));
		left_panel.add(error_message);
		left_panel.add(Box.createVerticalGlue());
		
		//right panel
		right_panel=new JPanel();
		right_panel.setBorder(BorderFactory.createTitledBorder("Net"));
		
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		add(left_panel);
		add(right_panel);
		
		validate();
 		repaint();
	}
	
	/**
	* Starts simulation.
	*/
	private void start()
	{
		if(!client.Main.client.connected)
		{
			error_message.setText("<html>You are not connected<br>to a server.<br>Restart this simulation.</html>");
			close_button.doClick();
			return;
		}
		
		if(!client.Main.client.loggedin)
		{
			error_message.setText("<html>You are not logged in.<br>Restart this simulation.</html>");
			close_button.doClick();
			return;
		}
		
		client.Main.client.sendRequest(coder.getSimulateRequest(name, version));
		String response=client.Main.client.getResponse();
		
		view(response);
	}
	
	/**
	* Shows current state of simulated net.
	*/
	private void view(String response)
	{
		if(response.equals(coder.getNotOKResponse()))
		{
			error_message.setText("<html>Operation failed.</html>");
			return;
		}
		
		coder.parseOneNet(response);
		
		if(coder.failed)
		{
			error_message.setText("<html>Net parsing failed.</html>");
			return;
		}
		
		places=coder.getPlaces();
		transs=coder.getTransitions();
		arcs=coder.getArcs();
		
		remove(right_panel);
		validate();
  		repaint();
		right_panel=new NetCanvas(places, transs, arcs);
		right_panel.setBorder(BorderFactory.createTitledBorder("Net"));
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
		Object src=e.getSource();
		
		//close simulator
		if(src==close_button)
		{
			end_button.doClick();
			client.Main.client.tabbed_pane.remove(this);
		}
		
		if(!client.Main.client.connected)
		{
			error_message.setText("<html>You are not connected<br>to a server.</html>");
			return;
		}
		
		if(!client.Main.client.loggedin)
		{
			error_message.setText("<html>You are not logged in.</html>");
			return;
		}
		
		//do one step
		if(src==step_button)
		{
			client.Main.client.sendRequest(coder.getStepRequest(name, version));
			String response=client.Main.client.getResponse();
			view(response);
		}
		//finish simulation
		else if(src==finish_button)
		{
			client.Main.client.sendRequest(coder.getFinishRequest(name, version));
			String response=client.Main.client.getResponse();
			view(response);
		}
		//end simulation
		else if(src==end_button)
		{
			client.Main.client.sendRequest(coder.getEndRequest(name, version));
			client.Main.client.getResponse();
			remove(right_panel);
			validate();
	  		repaint();
			right_panel=new JPanel();
			right_panel.setBorder(BorderFactory.createTitledBorder("Net"));
			add(right_panel);
			validate();
	 		repaint();
		}
	}
}

