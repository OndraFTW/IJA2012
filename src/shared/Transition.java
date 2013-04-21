package shared;

import shared.TransitionComputationException;

import java.awt.Point;
import java.util.*;

public class Transition
{
	/**Guard.*/
	public String[] guard;
	/**Coordinates on cancas.*/
	public Point point;
	/**Determines size of place on canvas.*/
	public Point size;
	/**Component ID*/
	public int ID;
	/**Activity.*/
	public boolean active;
	/**Longest line of guard.*/
	public String longest;
	
	public Boolean has_guard;
	
	public Map<String, Integer> vars;
	
	/**
	* Creates transition.
	*/
	public Transition(Point point, int ID, String guard, boolean active)
	{
		this.point=point;
		this.ID=ID;
		this.guard=guard.replace("\\n", "\n").split("\n");
		this.active=active;
		
		longest="";
		for(String str: this.guard)
		{
			if(str.length() > longest.length())
			{
				longest=str;
			}
		}
		
	}
	
	/**
	* Updates the transition, before painting it on canvas.
	*/
	public void update()
	{
		int len=client.Main.client.font_metrics.stringWidth(longest);
		this.size=new Point(len==0?client.Main.client.font_metrics.getMaxAscent()*2:len+client.Main.client.font_metrics.getMaxAscent(), client.Main.client.font_metrics.getMaxAscent()*(guard.length+1));
	}
	
	public boolean hasGuard()
	{
		if(has_guard==null)
		{
			this.has_guard=this.guard[0].matches("((([_a-zA-Z][_a-zA-Z0-9]*)|([+-]?\\d+)) ?((<)|(<=)|(>=)|(>)|(==)|(!=)) ?(([_a-zA-Z][_a-zA-Z0-9]*)|([+-]?\\d+)) ?& ?)*((([_a-zA-Z][_a-zA-Z0-9]*)|([+-]?\\d+)) ?((<)|(<=)|(>=)|(>)|(==)|(!=)) ?(([_a-zA-Z][_a-zA-Z0-9]*)|([+-]?\\d+)))");
		}
		
		return has_guard;
	}
	
	public void compute() throws TransitionComputationException
	{
		boolean first=true;
		for(String cmd: guard)
		{
			if(first && hasGuard())
			{
				first=false;
				continue;
			}
			
			List<String> parts=new ArrayList<>();
			Collections.addAll(parts, cmd.split("((?=[_a-zA-Z0-9])(?<=[=+-]))|((?=[=+-])(?<=[_a-zA-Z0-9]))"));
			
			Iterator<String> iter=parts.iterator();
			String var_name=iter.next();
			int var_value=0;
			iter.next();
			
			while(iter.hasNext())
			{
				String i=iter.next();
				
				if(i.equals("-"))
				{
					var_value-=getValue(iter.next());
				}
				else if(i.equals("+"))
				{
					var_value+=getValue(iter.next());
				}
				else
				{
					var_value+=getValue(i);
				}
			}
			
			vars.put(var_name, var_value);
		}
	}
	
	private int getValue(String s) throws TransitionComputationException
	{
		try
		{
			return Integer.parseInt(s);
		}
		catch(NumberFormatException nfe)
		{
			try
			{
				return vars.get(s);
			}
			catch(NullPointerException npe)
			{
				throw new TransitionComputationException("Undefined variable \""+s+"\" in transition "+Integer.toString(ID)+".\n");
			}
		}
	}
	
	public List<Arc> getInputArcs(List<Arc> arcs)
	{
		List<Arc> input_arcs=new ArrayList<Arc>();
		
		for(Arc arc: arcs)
		{
			if(this.ID==arc.ID2)
			{
				input_arcs.add(arc);
			}
		}
		
		return input_arcs;
	}
	
	public List<Arc> getOutputArcs(List<Arc> arcs)
	{
		List<Arc> output_arcs=new ArrayList<Arc>();
		
		for(Arc arc: arcs)
		{
			if(this.ID==arc.ID1)
			{
				output_arcs.add(arc);
			}
		}
		
		return output_arcs;
	}
}
