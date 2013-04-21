package server.simulator;

import java.util.*;

public class VariableValuesIterator implements Iterator<Map<String, Integer>>
{
	private List<Integer> indexes=new ArrayList<>();
	private List<Integer> sizes=new ArrayList<>();
	private List<List<Integer>> values=new ArrayList<>();
	private List<String> names=new ArrayList<>();
	private List<Long> combination_intervals=new ArrayList<>();
	private int size=0;
	private long number_of_combinatios=1;
	private long current_combination=0;
	public boolean active=true;
	
	public VariableValuesIterator(Map<String, List<Integer>> all_vars_values)
	{
		for(String name: all_vars_values.keySet())
		{
			List<Integer> list=all_vars_values.get(name);
			int list_size=list.size();
			
			indexes.add(0);
			sizes.add(list_size);
			values.add(list);
			names.add(name);
			combination_intervals.add(number_of_combinatios);
			
			number_of_combinatios*=list_size;
			
			if(list_size==0)
			{
				active=false;
				break;
			}
		}
		
		size=names.size();
	}
	
	public Map<String, Integer> next()
	{
		Map<String, Integer> r=new HashMap<>();
		
		for(int i=0; i<size; i++)
		{
			r.put(names.get(i), values.get(i).get(indexes.get(i)));
		}
		
		setNext();
		
		return r;
	}
	
	private void setNext()
	{
		current_combination++;
		
		for(int i=0; i<size; i++)
		{
			if((current_combination%combination_intervals.get(i))==0)
			{
				inc(i);
			}
		}
	}
	
	private void inc(int i)
	{
		if(indexes.get(i).equals(sizes.get(i)-1))
		{
			indexes.set(i, 0);
		}
		else
		{
			indexes.set(i, indexes.get(i)+1);
		}
	}
	
	public boolean hasNext()
	{
		return current_combination<number_of_combinatios;
	}
	
	public void remove()
	{
	}
}
