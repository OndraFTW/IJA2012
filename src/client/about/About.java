package client.about;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
* Shows information about project.
* @author Vojtěch Šimša
* @author Ondřej Šlampa
*/
public class About extends JPanel implements ActionListener
{
	/**
	* Creates information about project.
	*/
	public About()
	{
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		JLabel about_label=new JLabel("<html>IJA 2012 Project:<br>Petri net simulator version 2<br><br>Version 1 Authors:<br>Vojtěch Šimša, xsimsa01@stud.fit.vutbr.cz<br>Ondřej Šlampa, xslamp01@stud.fit.vutbr.cz<br>Version 2 auhor: Ondřej Šlampa, xslamp01@stud.fit.vutbr.cz<br><br>Version 1 was created as a java seminar project. Simulator in this version was unstable. Version 2 inc</html>");
		
		JButton close_button=new JButton("Close");
		close_button.addActionListener(this);
		
		close_button.setAlignmentX(Component.CENTER_ALIGNMENT);
		about_label.setAlignmentX(Component.CENTER_ALIGNMENT);
		about_label.setHorizontalAlignment(SwingConstants.CENTER);
		
		add(Box.createVerticalGlue());
		add(about_label);
		add(Box.createRigidArea(new Dimension(0,5)));
		add(close_button);
		add(Box.createVerticalGlue());
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
