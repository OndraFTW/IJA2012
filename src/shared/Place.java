package shared;

import java.awt.*;

/**
* petri net place.
*/
public class Place
{
	/**Place tokens.*/
	public String token;
	/**Coordinates on cancas.*/
	public Point point;
	/**Determines size of place on canvas.*/
	public Point size;
	/**Component ID*/
	public int ID;
	
	/**
	* Creates place.
	*/
	public Place(Point point, int ID, String token)
	{
		this.point=point;
		this.ID=ID;
		this.token=token;
		//update();
	}
	
	/**
	* Updates the place before, painting it on canvas.
	*/
	public void update()
	{
		int len=token.length();
		this.size=new Point(len==0?client.Main.client.font_metrics.getMaxAscent()*2:client.Main.client.font_metrics.stringWidth(token)*2, client.Main.client.font_metrics.getMaxAscent()*2);
	}
	
	public void add(Integer num)
	{
		if(num==null)
		{
		}
		else if(token.equals(""))
		{
			token=Integer.toString(num);
		}
		else
		{
			token+=","+Integer.toString(num);
		}
	}
}
