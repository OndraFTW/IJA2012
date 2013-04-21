package client.browser;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Collections;
import java.io.*;

import nu.xom.*;

import shared.*;
import client.editor.Editor;
import client.simulator.Simulator;
import client.NetCanvas;

/**
* Petri net browser.   
* @author Vojtěch Šimša
* @author Ondřej Šlampa 
*/
public class Browser extends JPanel implements ActionListener
{
	private JPanel right_panel;
	private JLabel error_message;
	
	private JButton edit_button;
	private JButton sim_button;
	private JButton refresh_button;
	private JButton close_button;
	
	private JComboBox<String> nets;
	private JComboBox<Integer> versions;
	
	private JTextField author;
	private JTextArea description;
	private JTextField filter;
	
	private List<String> net_names;
	private List<List<Integer>> version_counts;
	
	private Coder coder;
	
	private Element nets_info;
	
	private List<Place> places;
	private List<Transition> transs;
	private List<Arc> arcs;
	
	/**
	* Creates browser.
	*/
	public Browser()
	{
		coder=new Coder();
		createGUI();
		refresh_button.doClick();
	}
	
	/**
	* Creates browser GUI.
	*/
	private void createGUI()
	{
		//left panel
		JPanel left_panel=new JPanel();
		left_panel.setLayout(new BoxLayout(left_panel, BoxLayout.PAGE_AXIS));
		left_panel.setBorder(BorderFactory.createTitledBorder("Controls"));
		
		nets=new JComboBox<String>();
		versions=new JComboBox<Integer>();
		
		nets.addActionListener(this);
		versions.addActionListener(this);
		
		refresh_button=new JButton("Refresh");
		edit_button=new JButton("Edit");
		sim_button=new JButton("Simulate");
		close_button=new JButton("Close");
		
		refresh_button.setAlignmentX(Component.LEFT_ALIGNMENT);
		edit_button.setAlignmentX(Component.LEFT_ALIGNMENT);
		sim_button.setAlignmentX(Component.LEFT_ALIGNMENT);
		close_button.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		refresh_button.addActionListener(this);
		edit_button.addActionListener(this);
		sim_button.addActionListener(this);
		close_button.addActionListener(this);
		
		author=new JTextField(5);
		description=new JTextArea(5,5);
		filter=new JTextField(5);
		
		JScrollPane scroll_pane=new JScrollPane(description);
		
		error_message=new JLabel();
		error_message.setForeground(Color.RED);
		
		nets.setMaximumSize(new Dimension(500, 50));
		versions.setMaximumSize(new Dimension(500, 50));
		error_message.setMaximumSize(new Dimension(500, 50));
		author.setMaximumSize(new Dimension(500, 50));
		description.setMaximumSize(new Dimension(500, 50));
		scroll_pane.setMaximumSize(new Dimension(500, 50));
		filter.setMaximumSize(new Dimension(500, 50));
		
		nets.setAlignmentX(Component.LEFT_ALIGNMENT);
		versions.setAlignmentX(Component.LEFT_ALIGNMENT);
		error_message.setAlignmentX(Component.LEFT_ALIGNMENT);
		author.setAlignmentX(Component.LEFT_ALIGNMENT);
		description.setAlignmentX(Component.LEFT_ALIGNMENT);
		scroll_pane.setAlignmentX(Component.LEFT_ALIGNMENT);
		filter.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		author.setEditable(false);
		description.setEditable(false);
		
		left_panel.add(Box.createRigidArea(new Dimension(0,5)));
		left_panel.add(new JLabel("Net:", SwingConstants.LEFT));
		left_panel.add(Box.createRigidArea(new Dimension(0,5)));
		left_panel.add(nets);
		left_panel.add(Box.createRigidArea(new Dimension(0,5)));
		left_panel.add(new JLabel("Version:", SwingConstants.LEFT));
		left_panel.add(Box.createRigidArea(new Dimension(0,5)));
		left_panel.add(versions);
		left_panel.add(Box.createRigidArea(new Dimension(0,5)));
		left_panel.add(new JLabel("Author:", SwingConstants.LEFT));
		left_panel.add(Box.createRigidArea(new Dimension(0,5)));
		left_panel.add(author);
		left_panel.add(Box.createRigidArea(new Dimension(0,5)));
		left_panel.add(new JLabel("Description:", SwingConstants.LEFT));
		left_panel.add(Box.createRigidArea(new Dimension(0,5)));
		left_panel.add(scroll_pane);
		left_panel.add(Box.createRigidArea(new Dimension(0,5)));
		left_panel.add(new JLabel("Filter:", SwingConstants.LEFT));
		left_panel.add(Box.createRigidArea(new Dimension(0,5)));
		left_panel.add(filter);
		left_panel.add(Box.createRigidArea(new Dimension(0,5)));
		left_panel.add(refresh_button);
		left_panel.add(Box.createRigidArea(new Dimension(0,5)));
		left_panel.add(edit_button);
		left_panel.add(Box.createRigidArea(new Dimension(0,5)));
		left_panel.add(sim_button);
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
	* Shows selected petri net.
	*/
	private void view()
	{
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
		
		String name=(String)nets.getSelectedItem();
		Integer version=(Integer)versions.getSelectedItem();
		
		if(name==null || version==null)
		{
			error_message.setText("<html>No net to view.</html>");
			return;
		}
		
		//send request and receive response
		client.Main.client.sendRequest(coder.getNetRequest(name, version));
		String response=client.Main.client.getResponse();
		
		if(response.equals(coder.getNotOKResponse()))
		{
			error_message.setText("<html>Net retrieval failed.</html>");
			return;
		}
		
		//parse the net
		coder.parseOneNet(response);
		
		if(coder.failed)
		{
			error_message.setText("<html>Net parsing failed.</html>");
			return;
		}
		
		places=coder.getPlaces();
		transs=coder.getTransitions();
		arcs=coder.getArcs();
		
		author.setText(coder.getAuthor());
		description.setText(coder.getDescription());
		
		//show the net
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
		//get source
		Object src=e.getSource();
		
		error_message.setText("");
		
		if(src==refresh_button)
		{
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
			
			//get all nets on the server
			client.Main.client.sendRequest(coder.getNetsRequest());
			
			String response_string=null;
			
			if((response_string=client.Main.client.getResponse()).equals(coder.getNotOKResponse()))
			{
				error_message.setText("<html>Net retrieval failed.</html>");
				return;
			}
			
			Document response=null;
			
			//parse response
			try
			{
				Builder parser=new Builder(false);
				response=parser.build(new ByteArrayInputStream(response_string.getBytes("UTF-8")));
			}
			catch(ParsingException ex)
			{
				error_message.setText("<html>Net parsing failed.</html>");
				return;
			}
			catch(IOException ex)
			{
				error_message.setText("<html>Net parsing failed.</html>");
				return;
			}
			
			nets_info=response.getRootElement();
			
			net_names=new ArrayList<String>();
			version_counts=new ArrayList<List<Integer>>();
			
			//iterate through all nets
			for(int i=0;i<nets_info.getChildCount();i++)
			{
				Element net;
			
				try
				{
					net=(Element)nets_info.getChild(i);
				}
				catch(ClassCastException ex)
				{
					continue;
				}
				
				//net filter
				String filter="(?s).*"+this.filter.getText()+".*";
				Integer number_of_versions=Integer.parseInt(net.getAttributeValue("versions"));
				List<Integer> versions_to_show=new ArrayList<Integer>();
				
				//iterate through net versions
				for(Integer j=0;j<number_of_versions;j++)
				{
					Element version=(Element)net.getChild(j);
					
					//if version matches the filter, it will be shown in the browser
					if(net.getAttributeValue("name").matches(filter) || version.getAttributeValue("author").matches(filter) || version.getAttributeValue("description").matches(filter))
					{
						versions_to_show.add(Integer.parseInt(version.getAttributeValue("no")));
					}
				}
				
				//if there are any net versions to show, net and it's versions will be shown  
				if(versions_to_show.size()>0)
				{
					net_names.add(net.getAttributeValue("name"));
					Collections.sort(versions_to_show,Collections.reverseOrder());
					version_counts.add(versions_to_show);
				}	
			}
			
			//show new nets
			this.nets.removeAllItems();
			for(String name: net_names)
			{
				this.nets.addItem(name);
			}
			
		}
		//net is selected
		else if(src==this.nets)
		{
			int index=net_names.indexOf(this.nets.getSelectedItem());
			
			if(index==-1)
			{
				this.versions.removeAllItems();
				return;
			}
			
			this.versions.removeAllItems();
			for(Integer item: version_counts.get(index))
			{
				this.versions.addItem(item);
			}
		}
		//show selected version
		else if(src==this.versions)
		{
			view();
		}
		//edit selected net
		else if(src==edit_button)
		{
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
			
			String name=(String)nets.getSelectedItem();
			Integer version=(Integer)versions.getSelectedItem();
			
			if(name==null || version==null)
			{
				error_message.setText("<html>No net to edit.</html>");
				return;
			}
			
			//get the net
			client.Main.client.sendRequest(coder.getNetRequest(name, version));
			String response=client.Main.client.getResponse();
			
			if(response.equals(coder.getNotOKResponse()))
			{
				error_message.setText("<html>Net retrieval failed.</html>");
				return;
			}
			
			//start editor
			client.Main.client.tabbed_pane.addTab("Editor",new Editor(response));
		}
		//simulate selected net
		else if(src==sim_button)
		{
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
			
			String name=(String)nets.getSelectedItem();
			Integer version=(Integer)versions.getSelectedItem();
			
			if(name==null || version==null)
			{
				error_message.setText("<html>No net to simulate.</html>");
				return;
			}
			
			//start simulator
			client.Main.client.tabbed_pane.addTab("Simulator", new Simulator(name, version));
		}
		//close browser
		else if(src==close_button)
		{
			client.Main.client.tabbed_pane.remove(this);
		}	
	}
}

