package tabular;

import java.util.Hashtable;
import java.util.Vector;

// Implements a two-level index via hashtables and vectors

public class TwoTieredTable 
{
	private Hashtable table	=	new Hashtable();
	
	private Vector keys		=	new Vector();
	
	private String name		=	null;
	
	//---------------------------------------------------------------------//
	//---------------------------------------------------------------------//
	//  Constructors
	//---------------------------------------------------------------------//
	//---------------------------------------------------------------------//
	
	public TwoTieredTable(String name) 
	{
		this.name = name;
	}
	
	
	//---------------------------------------------------------------------//
	//---------------------------------------------------------------------//
	//  Accessors
	//---------------------------------------------------------------------//
	//---------------------------------------------------------------------//
	
	public String getName()
	{
		return name;
	}
	
	
	public Vector getKeys()
	{
		return keys;
	}
	
	
	public boolean contains(String level1, String level2, String entry)
	{
		if (level1 == null || level2 == null || entry == null)
			return false;
		
		level1 = level1.intern();
		level2 = level2.intern();
		entry  = entry.intern();
		
		Vector top = (Vector)table.get(level1),
			   low = null;
		
		if (top == null) return false;
		
		for (int i = 0; i < top.size(); i++)
		{
			low = (Vector)top.elementAt(i);
			
			if (low == null || low.size() == 0 || low.elementAt(0) != level2) 
				continue;
			
			for (int j = 1; j < low.size(); j++)
				if (low.elementAt(j) == entry)
					return true;
				
			return false;
		}
		
		// no top entry 
		
		return false;
	}
	
	
	
	public boolean contains(String level1, String level2)
	{
		if (level1 == null || level2 == null)
			return false;
		
		level1 = level1.intern();
		level2 = level2.intern();
		
		Vector top = (Vector)table.get(level1),
			   low = null;
		
		if (top == null) return  false;
		
		for (int i = 0; i < top.size(); i++)
		{
			low = (Vector)top.elementAt(i);
			
			if (low != null && low.size() > 0 && low.elementAt(0) == level2) 
				return true;
		}
				
		return false;
	}

	
	public Vector get(String level1, String level2)
	{
		if (level1 == null || level2 == null)
			return null;
		
		level1 = level1.intern();
		level2 = level2.intern();
		
		Vector top = (Vector)table.get(level1),
			   low = null;
		
		if (top == null) return  null;
		
		for (int i = 0; i < top.size(); i++)
		{
			low = (Vector)top.elementAt(i);
			
			if (low != null && low.size() > 0 && low.elementAt(0) == level2) 
			{
				Vector copy = new Vector(low.size()-1);
				
				for (int j = 1; j < low.size(); j++)
					copy.addElement(low.elementAt(j));
			
				return copy;
			}
		}
				
		return null;
	}	
	
	
	public Vector getSubkeys(String level1)
	{
		Vector top	= (Vector)table.get(level1),
			   low  = null;
		
		if (top == null)
			return null;
		
		Vector subs = new Vector();
		
		for (int i = 0; i < top.size(); i++)
		{
			low = (Vector)top.elementAt(i);
			
			if (low != null && low.size() > 0)
				subs.addElement(low.elementAt(0));
		}
		
		return subs;
	}
	
	
	public Vector get(String key)
	{
		return (Vector)table.get(key);
	}
	
	//---------------------------------------------------------------------//
	//---------------------------------------------------------------------//
	//  Modifiers
	//---------------------------------------------------------------------//
	//---------------------------------------------------------------------//

	public Vector put(String level1, String level2, String entry)
	{
		if (level1 == null || level2 == null || entry == null)
			return null;
		
		level1 = level1.intern();
		level2 = level2.intern();
		entry  = entry.intern();
		
		Vector top = (Vector)table.get(level1),
			   low = null;
		
		if (top == null)
		{
			top = new Vector();
			table.put(level1, top);
			keys.addElement(level1);
		}
		
		for (int i = 0; i < top.size(); i++)
		{
			low = (Vector)top.elementAt(i);
			
			if (low == null || low.size() == 0 || low.elementAt(0) != level2) 
				continue;
			
			for (int j = 1; j < low.size(); j++)
				if (low.elementAt(j) == entry)
					return low;
				
			// if here, then entry is not present
				
			low.addElement(entry);
			
			return low;
		}
		
		// no top entry 
		
		Vector branch = new Vector();
		
		branch.addElement(level2);
		branch.addElement(entry);
		
		top.addElement(branch);
		
		return branch;
	}
	
	
	
	public Vector remove(String level1, String level2, String entry)
	{
		if (level1 == null || level2 == null || entry == null)
			return null;
		
		level1 = level1.intern();
		level2 = level2.intern();
		entry  = entry.intern();
		
		Vector top = (Vector)table.get(level1),
			   low = null;
		
		if (top == null) return null;
		
		for (int i = 0; i < top.size(); i++)
		{
			low = (Vector)top.elementAt(i);
			
			if (low == null || low.size() == 0 || low.elementAt(0) != level2) 
				continue;
			
			for (int j = 1; j < low.size(); j++)
				if (low.elementAt(j) == entry)
				{
					low.removeElementAt(j);
					return low;
				}
						
			return low;
		}
		
		return null;
	}
	
	
	
	public Vector remove(String level1, String level2)
	{
		if (level1 == null || level2 == null)
			return null;
		
		level1 = level1.intern();
		level2 = level2.intern();
		
		Vector top = (Vector)table.get(level1),
			   low = null;
		
		if (top == null) return null;
		
		for (int i = 0; i < top.size(); i++)
		{
			low = (Vector)top.elementAt(i);
			
			if (low == null || low.size() == 0 || low.elementAt(0) != level2) 
				continue;
			
			top.removeElementAt(i);
			
			return top;
		}
		
		return null;
	}
	
	
	
	public Vector remove(String level1)
	{
		if (level1 == null)
			return null;

		level1 = level1.intern();
		
		Vector top = (Vector)table.get(level1);
		
		if (top != null)
		{
			table.remove(level1);
			keys.remove(level1);
		}
		
		return top;
	}
}
