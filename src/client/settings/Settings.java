package client.settings;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

import shared.*;

/**
* Settings Panel.
* @author Vojtěch Šimša
* @author Ondřej Šlampa
*/
public class Settings extends JPanel implements ActionListener
{
	private JColorChooser place_cc;
	private JColorChooser active_transition_cc;
	private JColorChooser transition_cc;
	private JColorChooser arc_cc;
	private JColorChooser line_cc;
	private JColorChooser font_cc;
	
	private JComboBox<String> font_chooser;
	private JComboBox<Integer> size_chooser;
	private JComboBox<String> style_chooser;
	
	private JButton save_button;
	private JButton load_button;
	private JButton close_button;
	private JButton default_button;
	
	private final int MINIMUM_FONT_SIZE=0;
	private final int MAXIMUM_FONT_SIZE=50;
	
	private String[] font_names;
	private Integer[] font_sizes;
	private String[] font_style_names=
	{
		"plain",
		"bold",
		"italic",
		"bold and italic"
	};
	
	private Coder coder;
	
	/**
	* Creates settings panel.
	*/
	public Settings()
	{
		coder=new Coder();
		createGUI();
	}
	
	/**
	* Creates setting panel GUI.
	*/
	private void createGUI()
	{
		//color panel
		JTabbedPane tabs=new JTabbedPane(JTabbedPane.LEFT);
		
		place_cc=new JColorChooser(client.Main.client.place_color);
		active_transition_cc=new JColorChooser(client.Main.client.active_transition_color);
		transition_cc=new JColorChooser(client.Main.client.transition_color);
		arc_cc=new JColorChooser(client.Main.client.arc_color);
		line_cc=new JColorChooser(client.Main.client.line_color);
		font_cc=new JColorChooser(client.Main.client.font_color);
		
		tabs.addTab("Place", place_cc);
		tabs.addTab("Active transition", active_transition_cc);
		tabs.addTab("Transition", transition_cc);
		tabs.addTab("Arc", arc_cc);
		tabs.addTab("Line", line_cc);
		tabs.addTab("Font", font_cc);
		
		JPanel color_panel=new JPanel();
		color_panel.setBorder(BorderFactory.createTitledBorder("Color"));
		color_panel.setLayout(new BoxLayout(color_panel, BoxLayout.LINE_AXIS));
		color_panel.add(tabs);
		
		//font panel
		GraphicsEnvironment env=GraphicsEnvironment.getLocalGraphicsEnvironment();
		font_names=env.getAvailableFontFamilyNames();
		
		font_sizes=new Integer[MAXIMUM_FONT_SIZE-MINIMUM_FONT_SIZE+1];
		for(int i=MINIMUM_FONT_SIZE;i<=MAXIMUM_FONT_SIZE;i++)
		{
			font_sizes[i-MINIMUM_FONT_SIZE]=i;
		}
		
		font_chooser=new JComboBox<String>(font_names);
		size_chooser=new JComboBox<Integer>(font_sizes);
		style_chooser=new JComboBox<String>(font_style_names);
		
		font_chooser.setSelectedItem(client.Main.client.font.getFamily());
		size_chooser.setSelectedIndex(client.Main.client.font_size+MINIMUM_FONT_SIZE);
		style_chooser.setSelectedIndex(client.Main.client.font_style);
		
		font_chooser.setMaximumSize(new Dimension(500, 50));
		size_chooser.setMaximumSize(new Dimension(500, 50));
		style_chooser.setMaximumSize(new Dimension(500, 50));
		
		JPanel font_panel=new JPanel();
		font_panel.setBorder(BorderFactory.createTitledBorder("Font"));
		font_panel.setLayout(new BoxLayout(font_panel, BoxLayout.LINE_AXIS));
		font_panel.add(Box.createHorizontalGlue());
		font_panel.add(new JLabel("Font:"));
		font_panel.add(Box.createRigidArea(new Dimension(5,0)));
		font_panel.add(font_chooser);
		font_panel.add(Box.createRigidArea(new Dimension(5,0)));
		font_panel.add(new JLabel("Size:"));
		font_panel.add(Box.createRigidArea(new Dimension(5,0)));
		font_panel.add(size_chooser);
		font_panel.add(Box.createRigidArea(new Dimension(5,0)));
		font_panel.add(new JLabel("Style:"));
		font_panel.add(Box.createRigidArea(new Dimension(5,0)));
		font_panel.add(style_chooser);
		font_panel.add(Box.createHorizontalGlue());
		
		//control panel
		save_button=new JButton("Save");
		load_button=new JButton("Load");
		close_button=new JButton("Close");
		default_button=new JButton("Default");
		
		save_button.addActionListener(this);
		load_button.addActionListener(this);
		close_button.addActionListener(this);
		default_button.addActionListener(this);
		
		JPanel control_panel=new JPanel();
		control_panel.setBorder(BorderFactory.createTitledBorder("Control"));
		control_panel.setLayout(new BoxLayout(control_panel, BoxLayout.LINE_AXIS));
		control_panel.add(Box.createHorizontalGlue());
		control_panel.add(save_button);
		control_panel.add(Box.createRigidArea(new Dimension(5,0)));
		control_panel.add(load_button);
		control_panel.add(Box.createRigidArea(new Dimension(5,0)));
		control_panel.add(default_button);
		control_panel.add(Box.createRigidArea(new Dimension(5,0)));
		control_panel.add(close_button);
		control_panel.add(Box.createHorizontalGlue());
		
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		add(color_panel);
		add(font_panel);
		add(control_panel);
		add(Box.createVerticalGlue());
		
		validate();
 		repaint();
	}
	
	/**
	* Sets color choosers to current colors.
	*/
	private void setCCs()
	{
		place_cc.setColor(client.Main.client.place_color);
		active_transition_cc.setColor(client.Main.client.active_transition_color);
		transition_cc.setColor(client.Main.client.transition_color);
		arc_cc.setColor(client.Main.client.arc_color);
		line_cc.setColor(client.Main.client.line_color);
		font_cc.setColor(client.Main.client.font_color);
	}
	
	/**
	* Handles action event.
	* @param e action event 
	*/
	public void actionPerformed(ActionEvent e)
	{
		//source
		Object src=e.getSource();
		
		//save current settings
		if(src==save_button)
		{
			client.Main.client.font_name=(String)font_chooser.getSelectedItem();
			client.Main.client.font_size=(Integer)size_chooser.getSelectedItem();
			
			String style=(String)style_chooser.getSelectedItem();;
			if(style.equals("plain"))
			{
				client.Main.client.font_style=Font.PLAIN;
			}
			else if(style.equals("bold"))
			{
				client.Main.client.font_style=Font.BOLD;
			}
			else if(style.equals("italic"))
			{
				client.Main.client.font_style=Font.ITALIC;
			}
			else if(style.equals("bold and italic"))
			{
				client.Main.client.font_style=Font.BOLD|Font.ITALIC;
			}
			
			//set colors according to color choosers
			client.Main.client.place_color=place_cc.getColor();
			client.Main.client.active_transition_color=active_transition_cc.getColor();
			client.Main.client.transition_color=transition_cc.getColor();
			client.Main.client.arc_color=arc_cc.getColor();
			client.Main.client.line_color=line_cc.getColor();
			client.Main.client.font_color=font_cc.getColor();
			
			client.Main.client.saveConfig();
		}
		//load settings
		else if(src==load_button)
		{
			JFileChooser fc=new JFileChooser(System.getProperty("user.dir"));
			
			if(fc.showOpenDialog(this)==JFileChooser.APPROVE_OPTION)
			{
				client.Main.client.config=fc.getSelectedFile();
				client.Main.client.setConfig();
				setCCs();
			}
		}
		//set configuration to default
		else if(src==default_button)
		{
			client.Main.client.setDefaultConfig();
			client.Main.client.saveConfig();
			setCCs();
		}
		//close
		else if(src==close_button)
		{
			client.Main.client.tabbed_pane.remove(this);
		}
	}
}

