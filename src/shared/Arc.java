package shared;

import client.editor.Editor;
import client.browser.Browser;
import client.NetCanvas;
import java.awt.*;

/**
* Petri net arc.
*/
public class Arc
{
	/**First component ID*/
	public int ID1;
	/**Second component ID*/
	public int ID2;
	/**First point on canvas.*/
	public Point p1;
	/**Second point on canvas.*/
	public Point p2;
	/**Component ID*/
	public int ID;
	/**Variable name.*/
	public String varname;
	/**Middle point.*/
	public Point point;
	/**Size point - determines size of varname square on canvas.*/
	public Point size;
	/**Length of varname.*/
	private int len;
	
	/**
	* Creates Arc.
	*/
	public Arc(int ID, String varname, int ID1, int ID2)
	{
		this.ID1=ID1;
		this.ID2=ID2;
		this.ID=ID;
		this.varname=varname;
		len=varname.length();
	}
	
	/**
	* Initialize arc on net canvas.
	* @param c net canvas, which includes petri net
	*/
	public void init(NetCanvas c)
	{
		p1=c.getPoint(this.ID1);
		p2=c.getPoint(this.ID2);
		
		this.point=new Point((p1.x+p2.x)/2, (p1.y+p2.y)/2);
		this.size=new Point(len==0?client.Main.client.font_metrics.getMaxAscent()*2:client.Main.client.font_metrics.stringWidth(varname)+client.Main.client.font_metrics.getMaxAscent(), client.Main.client.font_metrics.getMaxAscent()*2);
	}
}
