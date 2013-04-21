package client.editor;

import shared.*;
import client.*;
import shared.*;

import nu.xom.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import javax.swing.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.Point;
import java.awt.Dimension;
import java.awt.Color;
import java.io.*;

/**
* Editor.
* @author Vojtěch Šimša
* @author Ondřej Šlampa
*/
public class Editor extends JPanel implements ActionListener, MouseListener, KeyListener
{
	private JPanel right_panel;
	
	private JButton save_local_button;
	private JButton save_server_button;
	private JButton close_button;
	
	private JButton add_place_button;
	private JButton add_trans_button;
	private JButton add_arc_button;
	
	private JButton delete_button;
	
	private JTextField name_field;
	private JTextArea description_field;
	
	private JLabel error_message;
	
	private JFrame frame; 
	
	private JButton ok_button;
	private JButton cancel_button;
	
	private JTextArea text;
	
	private Point point1;
	private Point point2;
	
	private int last_ID;
	
	private int ID1;
	private int ID2;
	
	private List<Place> places;
	private List<Transition> transs;
	private List<Arc> arcs;
	
	private Coder coder;
	
	private EditorState state;
	
	/**
	* Creates editor with empty net.
	*/
	public Editor()
	{
		last_ID=0;
		places=new ArrayList<Place>();
		transs=new ArrayList<Transition>();
		arcs=new ArrayList<Arc>();
		coder=new Coder();
		state=EditorState.NOTHING;
		createGUI();
		name_field.setText("");
		description_field.setText("");
	}
	
	/**
	* Creates editor with net stored in file.
	* @param file file with net stored in it
	*/
	public Editor(File file)
	{
		createGUI();
		coder=new Coder();
		coder.openOneNet(file);
		
		//parse failed
		if(coder.failed)
		{
			error_message.setText("<html>Net parsing failed.</html>");
			
			save_local_button.setEnabled(false);
			save_server_button.setEnabled(false);
			add_place_button.setEnabled(false);
			add_trans_button.setEnabled(false);
			add_arc_button.setEnabled(false);
			delete_button.setEnabled(false);
			
			return;
		}
		
		//get net components
		places=coder.getPlaces();
		transs=coder.getTransitions();
		arcs=coder.getArcs();
		
		//get highest component ID
		last_ID=0;
		for(Place place: places)
		{
			if(last_ID<place.ID)
			{
				last_ID=place.ID;
			}
		}
		
		for(Transition trans: transs)
		{
			if(last_ID<trans.ID)
			{
				last_ID=trans.ID;
			}
		}
		
		for(Arc arc: arcs)
		{
			if(last_ID<arc.ID)
			{
				last_ID=arc.ID;
			}
		}
		
		last_ID++;
		state=EditorState.NOTHING;
		name_field.setText(coder.getName());
		description_field.setText(coder.getDescription());
		draw();
	}
	
	/**
	* Creates editor with net stored in string.
	* @param net_string net stored in string
	*/
	public Editor(String net_string)
	{
		coder=new Coder();
		createGUI();
		coder.parseOneNet(net_string);
		
		//parse failed
		if(coder.failed)
		{
			error_message.setText("<html>Net parsing failed.</html>");
			
			save_local_button.setEnabled(false);
			save_server_button.setEnabled(false);
			add_place_button.setEnabled(false);
			add_trans_button.setEnabled(false);
			add_arc_button.setEnabled(false);
			delete_button.setEnabled(false);
			
			return;
		}
		
		//get net components
		places=coder.getPlaces();
		transs=coder.getTransitions();
		arcs=coder.getArcs();
		
		//get highest component ID
		last_ID=0;
		for(Place place: places)
		{
			if(last_ID<place.ID)
			{
				last_ID=place.ID;
			}
		}
		
		for(Transition trans: transs)
		{
			if(last_ID<trans.ID)
			{
				last_ID=trans.ID;
			}
		}
		
		for(Arc arc: arcs)
		{
			if(last_ID<arc.ID)
			{
				last_ID=arc.ID;
			}
		}
		
		last_ID++;
		state=EditorState.NOTHING;
		name_field.setText(coder.getName());
		description_field.setText(coder.getDescription());
		draw();
	}
	
	/**
	* Creates editor GUI.
	*/
	private void createGUI()
	{
		//left panel
		JPanel left_panel=new JPanel();
		left_panel.setLayout(new BoxLayout(left_panel, BoxLayout.PAGE_AXIS));
		left_panel.setBorder(BorderFactory.createTitledBorder("Controls"));
		
		save_local_button=new JButton("Save locally");
		save_server_button=new JButton("Save on sever");
		close_button=new JButton("Close");
		add_place_button=new JButton("Add Place");
		add_trans_button=new JButton("Add Transition");
		add_arc_button=new JButton("Add Arc");
		delete_button=new JButton("Delete");
		name_field=new JTextField(5);
		description_field=new JTextArea(5, 5);
		error_message=new JLabel("");
		
		save_local_button.addActionListener(this);
		save_server_button.addActionListener(this);
		close_button.addActionListener(this);
		add_place_button.addActionListener(this);
		add_trans_button.addActionListener(this);
		add_arc_button.addActionListener(this);
		delete_button.addActionListener(this);
		name_field.addActionListener(this);
		error_message.setForeground(Color.RED);
		
		JScrollPane scroll_pane=new JScrollPane(description_field);
		
		name_field.setMaximumSize(new Dimension(500, 50));
		description_field.setMaximumSize(new Dimension(500, 50));
		scroll_pane.setMaximumSize(new Dimension(500, 50));
		error_message.setMaximumSize(new Dimension(500, 50));
		
		left_panel.add(Box.createRigidArea(new Dimension(0,5)));
		left_panel.add(save_local_button);
		left_panel.add(Box.createRigidArea(new Dimension(0,5)));
		left_panel.add(save_server_button);
		left_panel.add(Box.createRigidArea(new Dimension(0,5)));
		left_panel.add(add_place_button);
		left_panel.add(Box.createRigidArea(new Dimension(0,5)));
		left_panel.add(add_trans_button);
		left_panel.add(Box.createRigidArea(new Dimension(0,5)));
		left_panel.add(add_arc_button);
		left_panel.add(Box.createRigidArea(new Dimension(0,5)));
		left_panel.add(delete_button);
		left_panel.add(Box.createRigidArea(new Dimension(0,5)));
		left_panel.add(new JLabel("Name:"));
		left_panel.add(Box.createRigidArea(new Dimension(0,5)));
		left_panel.add(name_field);
		left_panel.add(Box.createRigidArea(new Dimension(0,5)));
		left_panel.add(new JLabel("Description:"));
		left_panel.add(Box.createRigidArea(new Dimension(0,5)));
		left_panel.add(scroll_pane);
		left_panel.add(Box.createRigidArea(new Dimension(0,5)));
		left_panel.add(close_button);
		left_panel.add(Box.createRigidArea(new Dimension(0,5)));
		left_panel.add(error_message);
		left_panel.add(Box.createVerticalGlue());
		
		//right panel
		right_panel=new JPanel();
		right_panel.setBorder(BorderFactory.createTitledBorder("Net"));
		right_panel.addMouseListener(this);
		
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		add(left_panel);
		add(right_panel);
		
		validate();
 		repaint();
	}
	
	/**
	* Add place part 1.
	* Disables buttons and starts waiting for the click.
	*/
	private void addPlace1()
	{
		state=EditorState.PLACE_CLICK;
		disableButtons();
	}
	
	/**
	* Add place part 2.
	* Shows new window and asks for place's token.
	*/
	private void addPlace2()
	{
		state=EditorState.PLACE_FRAME;
		
		frame=new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		JPanel panel=new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		
		panel.add(new JLabel("Token:"));
		
		text=new JTextArea(10, 20);
		text.addKeyListener(this);
		JScrollPane scroll_pane=new JScrollPane(text);
		panel.add(scroll_pane);
		
		ok_button=new JButton("OK");
		ok_button.addActionListener(this);
		panel.add(ok_button);
		
		cancel_button=new JButton("Cancel");
		cancel_button.addActionListener(this);
		panel.add(cancel_button);
		
		frame.add(panel);
		frame.pack();
		frame.setVisible(true);
	}
	
	/**
	* Add place part 3.
	* Closes window, creates new place and draws new net.
	*/
	private void addPlace3()
	{
		places.add(new Place(point1, last_ID++, text.getText().replaceAll(" ","")));
		frame.setVisible(false);
		frame.dispose();
		state=EditorState.NOTHING;
		enableButtons();
		error_message.setText("");
		draw();
	}
	
	/**
	* Add transition part 1.
	* Disables buttons and starts waiting for the click.
	*/
	private void addTrans1()
	{
		state=EditorState.TRANS_CLICK;
		disableButtons();
	}
	
	/**
	* Add transition part 2.
	* Shows new window and asks for transition's guard.
	*/
	private void addTrans2()
	{
		state=EditorState.TRANS_FRAME;
		
		frame=new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		JPanel panel=new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		
		panel.add(new JLabel("Guard:"));
		
		text=new JTextArea(10, 20);
		text.addKeyListener(this);
		JScrollPane scroll_pane=new JScrollPane(text);
		panel.add(scroll_pane);
		
		ok_button=new JButton("OK");
		ok_button.addActionListener(this);
		panel.add(ok_button);
		
		cancel_button=new JButton("Cancel");
		cancel_button.addActionListener(this);
		panel.add(cancel_button);
		
		frame.add(panel);
		frame.pack();
		frame.setVisible(true);
	}
	
	/**
	* Add place part 3.
	* Closes window, creates new transition and draws new net.
	*/
	private void addTrans3()
	{
		transs.add(new Transition(point1, last_ID++, text.getText().replaceAll(" ",""), false));
		frame.setVisible(false);
		frame.dispose();
		state=EditorState.NOTHING;
		enableButtons();
		error_message.setText("");
		draw();
	}
	
	/**
	* Add arc part 1.
	* Disables buttons and starts waiting for the click.
	*/
	private void addArc1()
	{
		state=EditorState.ARC_CLICK1;
		disableButtons();
	}
	
	/**
	* Add arc part 2.
	* Shows new window and asks for arc's variable name.
	*/
	private void addArc2()
	{
		ID1=getID(point1);
		ID2=getID(point2);
		
		//arc control - arc connects place and transition
		if(ID1==-1 || ID2==-1 || ID1==ID2 || isArc(ID1) || isArc(ID2) || (isPlace(ID1) && isPlace(ID2)) || (isTransition(ID1) && isTransition(ID2)))
		{
			state=EditorState.NOTHING;
			enableButtons();
			error_message.setText("<html>Arc must connect<br> place and transition.</html>");
			return;
		}
		
		state=EditorState.ARC_FRAME;
		
		frame=new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		JPanel panel=new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		
		panel.add(new JLabel("Variable name:"));
		
		text=new JTextArea(10, 20);
		text.addKeyListener(this);
		JScrollPane scroll_pane=new JScrollPane(text);
		panel.add(scroll_pane);
		
		ok_button=new JButton("OK");
		ok_button.addActionListener(this);
		ok_button.setEnabled(false);
		panel.add(ok_button);
		
		cancel_button=new JButton("Cancel");
		cancel_button.addActionListener(this);
		panel.add(cancel_button);
		
		frame.add(panel);
		frame.pack();
		frame.setVisible(true);
	}
	
	/**
	* Add arc part 3.
	* Closes window, creates new arc and draws new net.
	*/
	private void addArc3()
	{
		Arc arc=new Arc(last_ID++, text.getText().replaceAll(" ",""), ID1, ID2);
		arcs.add(arc);
		frame.setVisible(false);
		frame.dispose();
		state=EditorState.NOTHING;
		enableButtons();
		error_message.setText("");
		draw();
	}
	
	/**
	* Gets ID of net component on specified point.
	* @param point point
	* @return ID of net component on specified point, or -1 if no such component is found
	*/
	private int getID(Point point)
	{
		for(Place place: places)
		{
			if(point.x>place.point.x && point.x<(place.size.x+place.point.x) && point.y>place.point.y && point.y<(place.size.y+place.point.y))
			{
				return place.ID;
			}
		}
		
		for(Transition trans: transs)
		{
			if(point.x>trans.point.x && point.x<(trans.size.x+trans.point.x) && point.y>trans.point.y && point.y<(trans.size.y+trans.point.y))
			{
				return trans.ID;
			}
		}
		
		for(Arc arc: arcs)
		{
			if(point.x>arc.point.x && point.x<(arc.size.x+arc.point.x) && point.y>arc.point.y && point.y<(arc.size.y+arc.point.y))
			{
				return arc.ID;
			}
		}
		
		return -1;
	}
	
	/**
	* Determines if component is place.
	* @param ID component ID
	* @return true if component is place, false otherwise
	*/
	boolean isPlace(int ID)
	{
		for(Place place: places)
		{
			if(place.ID==ID)
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	* Determines if component is transition.
	* @param ID component ID
	* @return true if component is transition, false otherwise
	*/
	boolean isTransition(int ID)
	{
		for(Transition trans: transs)
		{
			if(trans.ID==ID)
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	* Determines if component is arc.
	* @param ID component ID
	* @return true if component is arc, false otherwise
	*/
	boolean isArc(int ID)
	{
		for(Arc arc: arcs)
		{
			if(arc.ID==ID)
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	* Deletes net component.
	*/
	void delete()
	{
		//components ID
		int ID=getID(point1);
		
		//no component to delete
		if(ID==-1)
		{
			state=EditorState.NOTHING;
			enableButtons();
			return;
		}
		
		//delete place
		for(Place place: places)
		{
			if(place.ID==ID)
			{
				//delete arcs leading from/to place
				for(Iterator it=arcs.iterator(); it.hasNext(); )
				{
					Arc arc=(Arc) it.next();
					if(arc.ID1==place.ID || arc.ID2==place.ID)
					{
						it.remove();
					}
				}
								
				places.remove(place);
				state=EditorState.NOTHING;
				enableButtons();
				draw();
				return;
			}
		}
		
		//delete transition
		for(Transition trans: transs)
		{
			if(trans.ID==ID)
			{
				//delete arcs leading from/to transition
				for(Iterator it=arcs.iterator(); it.hasNext(); )
				{
					Arc arc=(Arc) it.next();
					if(arc.ID1==trans.ID || arc.ID2==trans.ID)
					{
						it.remove();
					}
				}
				
				transs.remove(trans);
				state=EditorState.NOTHING;
				enableButtons();
				draw();
				return;
			}
		}
		
		//delete arc
		for(Iterator it=arcs.iterator(); it.hasNext(); )
		{
			Arc arc=(Arc) it.next();
			if(arc.ID==ID)
			{
				arcs.remove(arc);
				state=EditorState.NOTHING;
				enableButtons();
				draw();
				return;
			}
		}
	}
	
	/**
	* Draws net.
	*/
	private void draw()
	{
		remove(right_panel);
		validate();
 		repaint();
		right_panel=new NetCanvas(places, transs, arcs);
		right_panel.setBorder(BorderFactory.createTitledBorder("Net"));
		right_panel.addMouseListener(this);
		add(right_panel);
		validate();
 		repaint();
	}
	
	/**
	* Enables buttons and text fields.
	*/
	private void enableButtons()
	{
		//client.Main.client.menu_bar.setEnabled(true);
		//client.Main.client.tabbed_pane.setEnabled(true);
		
		save_local_button.setEnabled(true);
		save_server_button.setEnabled(true);
		close_button.setEnabled(true);
		add_place_button.setEnabled(true);
		add_trans_button.setEnabled(true);
		add_arc_button.setEnabled(true);
		delete_button.setEnabled(true);
		
		name_field.setEnabled(true);
		description_field.setEnabled(true);
	}
	
	/**
	* Disable buttons and text fields.
	*/
	private void disableButtons()
	{
		//client.Main.client.menu_bar.setEnabled(false);
		//client.Main.client.tabbed_pane.setEnabled(false);
		
		save_local_button.setEnabled(false);
		save_server_button.setEnabled(false);
		close_button.setEnabled(false);
		add_place_button.setEnabled(false);
		add_trans_button.setEnabled(false);
		add_arc_button.setEnabled(false);
		delete_button.setEnabled(false);
		
		name_field.setEnabled(false);
		description_field.setEnabled(false);
	}
	
	/**
	* Handles action event.
	* @param e action event 
	*/
	public void actionPerformed(ActionEvent e)
	{
		//source
		Object src=e.getSource();
		
		//save locally
		if(src==save_local_button)
		{
			JFileChooser fc=new JFileChooser(System.getProperty("user.dir"));
			
			//net name control
			if(!name_field.getText().matches("[_a-zA-Z][_a-zA-Z0-9]*"))
			{
				error_message.setText("<html>Invalid net name.</html>");
				return;
			}
			
			//show dialog window
			if(fc.showSaveDialog(this)==JFileChooser.APPROVE_OPTION)
			{
				coder.saveOneNet(places, transs, arcs, name_field.getText(), client.Main.client.username, description_field.getText(), fc.getSelectedFile());
				
				if(coder.failed)
				{
					error_message.setText("<html>Net saving failed.</html>");
					return;
				}
			}
		}
		//save on server
		else if(src==save_server_button)
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
			
			//net name control
			String name=name_field.getText();
			if(!name.matches("^[_a-zA-Z][_a-zA-Z0-9]*$"))
			{
				error_message.setText("<html>Invalid net name.</html>");
				return;
			}
			
			//encode net
			Document doc=coder.encodeOneNet(places,transs,arcs,name,client.Main.client.username,description_field.getText());
			
			if(coder.failed)
			{
				error_message.setText("<html>Net encoding failed.</html>");
				return;
			}
			
			//send request
			String msg=coder.getSaveRequest(client.Main.client.username,doc);	
			client.Main.client.sendRequest(msg);
			
			//get response
			if(client.Main.client.getResponse().equals(coder.getNotOKResponse()))
			{
				error_message.setText("<html>Saving net on<br>server failed.</html>");
				return;
			}
			
			error_message.setText("");
		}
		//close editor
		else if(src==close_button)
		{
			client.Main.client.tabbed_pane.remove(this);
		}
		else if(src==add_place_button)
		{
			addPlace1();
		}
		else if(src==add_trans_button)
		{
			addTrans1();
		}
		else if(src==add_arc_button)
		{
			addArc1();
		}
		else if(src==delete_button)
		{
			state=EditorState.DELETE_CLICK;
			disableButtons();
		}
		else if(src==ok_button)
		{
			switch(state)
			{
				case NOTHING:break;
				case PLACE_FRAME:
				{
					addPlace3();
				}
				break;
				case TRANS_FRAME:
				{
					addTrans3();
				}
				break;
				case ARC_FRAME:
				{
					addArc3();
				};
			}
		}
		else if(src==cancel_button)
		{
			frame.setVisible(false);
			frame.dispose();
			state=EditorState.NOTHING;
			enableButtons();
		}
	}
	
	/**
	* Handles mouse clicks.
	* @param e mouse event 
	*/
	public void mouseClicked(MouseEvent e)
	{
		switch(state)
		{
			case NOTHING:break;
			case PLACE_CLICK:
			{
				point1=e.getPoint();
				addPlace2();
			}
			break;
			case TRANS_CLICK:
			{
				point1=e.getPoint();
				addTrans2();
			}
			break;
			case ARC_CLICK1:
			{
				point1=e.getPoint();
				state=EditorState.ARC_CLICK2;
			}
			break;
			case ARC_CLICK2:
			{
				point2=e.getPoint();
				addArc2();
			}
			break;
			case DELETE_CLICK:
			{
				point1=e.getPoint();
				delete();
			}
			break;
		}
	}
	
	/**
	* Does nothing.
	* @param e mouse event 
	*/
	public void mouseEntered(MouseEvent e)
	{
	}
	
	/**
	* Does nothing.
	* @param e mouse event 
	*/
	public void mouseExited(MouseEvent e)
	{
	}
	
	/**
	* Does nothing.
	* @param e mouse event 
	*/
	public void mousePressed(MouseEvent e)
	{
	}
	
	/**
	* Does nothing.
	* @param e mouse event 
	*/
	public void mouseReleased(MouseEvent e)
	{
	}
	
	/**
	* Handles keyboard events - key presses.
	* @param e keyboard event 
	*/
	public void keyPressed(KeyEvent e)
	{
		//source
		Object src=e.getSource();
		
		if(src==text)
		{
			switch(state)
			{
				//add new place
				case PLACE_FRAME:
				{
					//place's token must match this regex
					if(text.getText().matches("^(([+-]?\\d+, ?)*([+-]?\\d+))|()$"))
					{
						ok_button.setEnabled(true);
					}
					else
					{
						ok_button.setEnabled(false);
					}
				}
				break;
				//add new transition
				case TRANS_FRAME:
				{
					//transition's guard must match this regex
					if(text.getText().matches("^((((([_a-zA-Z][_a-zA-Z0-9]*)|([+-]?\\d+)) ?((<)|(<=)|(>=)|(>)|(==)|(!=)) ?(([_a-zA-Z][_a-zA-Z0-9]*)|([+-]?\\d+)) ?& ?)*((([_a-zA-Z][_a-zA-Z0-9]*)|([+-]?\\d+)) ?((<)|(<=)|(>=)|(>)|(==)|(!=)) ?(([_a-zA-Z][_a-zA-Z0-9]*)|([+-]?\\d+))))((\n([_a-zA-Z][_a-zA-Z0-9]*) ?= ?(([_a-zA-Z][_a-zA-Z0-9]*)|([+-]?\\d+))( ?[+-] ?(([_a-zA-Z][_a-zA-Z0-9]*)|(\\d+)))*)*))$") || text.getText().matches("^((([_a-zA-Z][_a-zA-Z0-9]*) ?= ?(([_a-zA-Z][_a-zA-Z0-9]*)|([+-]?\\d+))( ?[+-] ?(([_a-zA-Z][_a-zA-Z0-9]*)|(\\d+)))*)(\n([_a-zA-Z][_a-zA-Z0-9]*) ?= ?(([_a-zA-Z][_a-zA-Z0-9]*)|([+-]?\\d+))( ?[+-] ?(([_a-zA-Z][_a-zA-Z0-9]*)|(\\d+)))*)*)$"))
					{
						ok_button.setEnabled(true);
					}
					else
					{
						ok_button.setEnabled(false);
					}
				}
				break;
				//add new frame
				case ARC_FRAME:
				{
					//arcs's varname must match this regex
					if(text.getText().matches("^([_a-zA-Z][_a-zA-Z0-9]*)|([+-]?\\d+)$"))
					{
						ok_button.setEnabled(true);
					}
					else
					{
						ok_button.setEnabled(false);
					}
				}
				break;
			}
		}
	}
	
	/**
	* Handles keyboard events - key releases.
	* @param e keyboard event 
	*/
	public void keyReleased(KeyEvent e)
	{
		keyPressed(e);
	}
	
	/**
	* Handles keyboard events.
	* @param e keyboard event 
	*/
	public void keyTyped(KeyEvent e)
	{
		keyPressed(e);
	}
}

/**
* Editor states.
*/
enum EditorState
{
	NOTHING,
	PLACE_CLICK,
	PLACE_FRAME,
	TRANS_CLICK,
	TRANS_FRAME,
	ARC_CLICK1,
	ARC_CLICK2,
	ARC_FRAME,
	DELETE_CLICK
}

