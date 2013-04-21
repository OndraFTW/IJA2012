package client;

import shared.*;

import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import java.awt.geom.*;
import java.awt.Point;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;

/**
* Canvas with petri net drawn on it.
* @author Vojtěch Šimša
* @author Ondřej Šlampa
*/
public class NetCanvas extends JPanel
{
	private List<Place> places;
	private List<Transition> transs;
	private List<Arc> arcs;
	
	/**
	* Creates canvas with petri net drawn on it.
	* @param places places of the petri net
	* @param transs transitions of the petri net
	* @param arcs arcs of the petri net
	*/
	public NetCanvas(List<Place> places, List<Transition> transs, List<Arc> arcs)
	{
		super();
		this.places=places;
		this.transs=transs;
		this.arcs=arcs;
	}
	
	/**
	* Paints this canvas.
	* @param g graphics information
	*/
	public void paintComponent(Graphics g)
	{
		Graphics2D g2D=(Graphics2D) g;
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		//set fonts and font metrics
		client.Main.client.font=new Font(client.Main.client.font_name, client.Main.client.font_style, client.Main.client.font_size);
		g2D.setFont(client.Main.client.font);
		client.Main.client.font_metrics=g2D.getFontMetrics();
		
		//update places and transitions to sizes corresponding with current font
		for(Place place: places)
		{
			place.update();
		}
		
		for(Transition trans: transs)
		{
			trans.update();
		}
		
		//draw arcs, places and transitions
		for(Arc arc: arcs)
		{
			arc.init(this);
			drawArc(g2D, arc);
		}
		
		for(Place place: places)
		{
			drawPlace(g2D, place);
		}
		
		for(Transition trans: transs)
		{
			drawTrans(g2D, trans);
		}
	}
	
	/**
	* Draws place.
	* @param g2D graphics information
	* @param place place
	*/
	private void drawPlace(Graphics2D g2D, Place place)
	{
		Ellipse2D.Float oval1=new Ellipse2D.Float(place.point.x, place.point.y, place.size.x, place.size.y);
		g2D.setColor(client.Main.client.place_color);
		g2D.fill(oval1);
		g2D.setColor(client.Main.client.line_color);
		g2D.draw(oval1);
		g2D.setColor(client.Main.client.font_color);
		g2D.drawString(place.token, place.point.x+place.size.x/4, place.point.y+place.size.y*3/4);
	}
	
	/**
	* Draws transition.
	* @param g2D graphics information
	* @param trans transition
	*/
	private void drawTrans(Graphics2D g2D, Transition trans)
	{
		Rectangle2D.Float rect1=new Rectangle2D.Float(trans.point.x, trans.point.y, trans.size.x, trans.size.y);
		
		if(trans.active)
		{
			g2D.setColor(client.Main.client.active_transition_color);
		}
		else
		{
			g2D.setColor(client.Main.client.transition_color);
		}
		
		g2D.fill(rect1);
		g2D.setColor(client.Main.client.line_color);
		g2D.draw(rect1);
		
		g2D.setColor(client.Main.client.font_color);
		for(int i=0;i<trans.guard.length;i++)
		{
			g2D.drawString(trans.guard[i], trans.point.x+client.Main.client.font_metrics.getMaxAscent()/2, trans.point.y+((i+1)*client.Main.client.font_metrics.getMaxAscent())+(client.Main.client.font_metrics.getMaxAscent()/2));
		}
	}
	
	/**
	* Draws arc.
	* @param g2D graphics information
	* @param arc arc
	*/
	private void drawArc(Graphics2D g2D, Arc arc)
	{
		Line2D.Float line1=new Line2D.Float(arc.p1.x, arc.p1.y, arc.p2.x, arc.p2.y);
		g2D.setColor(client.Main.client.line_color);
		g2D.draw(line1);
		
		Rectangle2D.Float rect1=new Rectangle2D.Float(arc.point.x, arc.point.y, arc.size.x, arc.size.y);
		g2D.setColor(client.Main.client.arc_color);
		g2D.fill(rect1);
		g2D.setColor(client.Main.client.font_color);
		g2D.drawString(arc.varname, arc.point.x+client.Main.client.font_metrics.getMaxAscent()/2, arc.point.y+client.Main.client.font_metrics.getMaxAscent()*3/2);
		
		int arrow_size=client.Main.client.font_size/2;
		double dx=arc.point.x-arc.p1.x;
		double dy=arc.point.y-arc.p1.y;
		double angle=Math.atan2(dy, dx);
		int len=(int) Math.sqrt(dx*dx+dy*dy);
		
		AffineTransform tranformation=AffineTransform.getTranslateInstance(arc.p1.x, arc.p1.y);
		tranformation.concatenate(AffineTransform.getRotateInstance(angle));
		g2D.transform(tranformation);
		g2D.fillPolygon(new int[] {len, len-arrow_size, len-arrow_size}, new int[] {0, -arrow_size, arrow_size}, 3);
		tranformation=AffineTransform.getRotateInstance(-angle);
		tranformation.concatenate(AffineTransform.getTranslateInstance(-arc.p1.x, -arc.p1.y));
		g2D.transform(tranformation);
	}
	
	/**
	* Get center of net component.
	* @param ID net component ID
	* @return point in the centre of the component
	*/
	public Point getPoint(int ID)
	{
		for(Place place: places)
		{
			if(place.ID==ID)
			{
				return new Point(place.point.x+place.size.x/2, place.point.y+place.size.y/2);
			}
		}
		
		for(Transition trans: transs)
		{
			if(trans.ID==ID)
			{
				return new Point(trans.point.x+trans.size.x/2, trans.point.y+trans.size.y/2);
			}
		}
		
		return new Point();
	}
}

