package server.simulator;

import java.util.*;

import shared.*;

/**
* Processes the simulation.
* @author Vojtěch Šimša
* @author Ondřej Šlampa
*/

public class Simulator
{
	private List<Place> places;
	private List<Transition> transs;
	private List<Arc> arcs;

	boolean finished;

	public Simulator(List<Place> places, List<Transition> transs, List<Arc> arcs)
	{
		this.places=places;
		this.transs=transs;
		this.arcs=arcs;
		this.finished=false;
	}
	
/**
* Runs single step of the simulation
*/
	public void step() throws TransitionComputationException
	{
		for(Transition trans: transs)
		{
			List<Arc> input_arcs=trans.getInputArcs(arcs);
			trans.vars=getVars(trans , getAllVarsValues(input_arcs));
			
			if(!(trans.active=(trans.vars!=null)))
			{
				continue;
			}
			
			for(Map.Entry<String, Integer> entry: trans.vars.entrySet())
			{
				List<Place> var_places=getVarPlaces(entry.getKey(), input_arcs);
				String value=Integer.toString(entry.getValue());
				boolean found=false;
				
				for(Place place: var_places)
				{
					List<String> tokens=new ArrayList<>();
					Collections.addAll(tokens, place.token.split(","));
					
					String token;
					Iterator<String> iter=tokens.iterator();
					while(iter.hasNext())
					{
						token=iter.next();
						if(token.equals(value))
						{
							iter.remove();
							found=true;
							break;
						}
					}
					
					if(found)
					{
						place.token=listToString(tokens);
						break;
					}
				}
			}
		}
		
		finished=true;
		
		for(Transition trans: transs)
		{
			if(trans.active)
			{
				finished=false;
				trans.compute();
				
				List<Arc> output_arcs=trans.getOutputArcs(arcs);
				
				for(Arc arc: output_arcs)
				{
					for(Place place: places)
					{
						if(place.ID==arc.ID2)
						{
							place.add(trans.vars.get(arc.varname));
							break;
						}
					}
				}
			}
		}
	}

	private String listToString(List<String> list)
	{
		Iterator<String> iter=list.iterator();
		
		if(!iter.hasNext())
		{
			return "";
		}
		
		String r=iter.next();
		
		while(iter.hasNext())
		{
			r+=","+iter.next();
		}
		return r;
	}

	private List<Place> getVarPlaces(String name, List<Arc> arcs)
	{
		List<Place> r=new ArrayList<>();
		
		for(Arc arc: arcs)
		{
			if(arc.varname.equals(name))
			{
				r.add(getPlace(arc.ID1));
			}
		}
		
		return r;
	}

	private Place getPlace(int ID)
	{
		for(Place place: places)
		{
			if(place.ID==ID)
			{
				return place;
			}
		}
		
		return null;
	}

	private Map<String, Integer> getVars(Transition trans, Map<String, List<Integer>> all_vars_values) throws TransitionComputationException
	{
		Map<String, Integer> i=null;
		VariableValuesIterator iter=new VariableValuesIterator(all_vars_values);
		
		if(!iter.active)
		{
			return null;
		}
		
		if(!trans.hasGuard())
		{
			return iter.next();
		}
		
		while(iter.hasNext())
		{
			i=iter.next();
			if(guarder(trans.guard[0], i))
			{
				return i;
			}
		}
		
		return null;
	}

	private Map<String, List<Integer>> getAllVarsValues(List<Arc> arcs)
	{
		Map<String, List<Integer>> vars=new HashMap<String, List<Integer>>();
		
		for(Arc arc: arcs)
		{
			if(!vars.containsKey(arc.varname))
			{
				vars.put(arc.varname, new ArrayList<Integer>());
			}
			
			List<Integer> nums=vars.get(arc.varname);
			
			for(Place place: places)
			{
				if(place.ID==arc.ID1)
				{
					if(place.token.equals(""))
					{
						break;
					}
					
					String[] strs=place.token.split(",");
				
					for(String s: strs)
					{
						nums.add(Integer.parseInt(s));
					}
					
					break;
				}
			}
		}
		
		return vars;
	}

	private boolean guarder(String guards, Map<String, Integer> vars) throws TransitionComputationException
	{
		boolean r=true;
		
		for(String guard: guards.split("&"))
		{
			String[] parts=guard.split("(>=)|(<=)|(!=)|(==)|(<)|(>)");
			
			int a=0;
			int b=0;
			
			try
			{
				a=Integer.parseInt(parts[0]);
			}
			catch(NumberFormatException nfe)
			{
				try
				{
					a=vars.get(parts[0]);
				}
				catch(NullPointerException npe)
				{
					throw new TransitionComputationException("Undefined variable \""+parts[0]+"\" in transition.\n");
				}
			}
			
			try
			{
				b=Integer.parseInt(parts[1]);
			}
			catch(NumberFormatException nfe)
			{
				try
				{
					b=vars.get(parts[1]);
				}
				catch(NullPointerException npe)
				{
					throw new TransitionComputationException("Undefined variable \""+parts[1]+"\" in transition.\n");
				}
			}
			
			if(guard.contains(">="))
			{
				r=r&&a>=b;
			}
			else if(guard.contains("<="))
			{
				r=r&&a<=b;
			}
			else if(guard.contains(">"))
			{
				r=r&&a>b;
			}
			else if(guard.contains("<"))
			{
				r=r&&a<b;
			}
			else if(guard.contains("=="))
			{
				r=r&&a==b;
			}
			else
			{
				r=r&&a!=b;
			}
			
			if(!r)
			{
				return r;
			}
		}
		
		return r;
	}

/**
* Runs the whole simulation
*/
	public void finish() throws TransitionComputationException
	{
		while(!finished)
		{
			step();
		}
	}

/**
* Returns list of Places used in simulation
*/
	public List<Place> getPlaces()
	{
		return places;
	}

/**
* Returns list of Transitions used in simulation
*/
	public List<Transition> getTransitions()
	{
		return transs;
	}

/**
* Returns list of Arc used in simulation
*/
	public List<Arc> getArcs()
	{
		return arcs;
	}
	
	static void printLMap(Map<String, List<Integer>> map)
	{
		System.out.print("LMap begin\n");
		
		for(String name: map.keySet())
		{
			List<Integer> list=map.get(name);
			
			System.out.printf("%5s: ", name);
			
			for(Integer i: list)
			{
				System.out.printf("%5d, ", i);
			}
			
			System.out.print("\n");
		}
		
		System.out.print("LMap end\n");
	}
	
	static void printMap(Map<String, Integer> map)
	{
		System.out.print("Map begin\n");
		
		for(String name: map.keySet())
		{
			Integer i=map.get(name);
			
			System.out.printf("%5s: %d\n", name, i);
		}
		
		System.out.print("Map end\n");
	}
}
