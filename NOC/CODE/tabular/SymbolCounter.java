
package tabular;

// This class implements atomic increment and decrement operations on an integer
// wrapped in an object shell 

// Consider it a version of Integer than permits changes to its contents

//(c) Tony Veale  2007

import java.util.Vector;

public class SymbolCounter extends AtomicCounter implements Comparable
{ 
    String symbol = null;
    
	//-----------------------------------------------------------------//
	//-----------------------------------------------------------------//
	// Constructors
	//-----------------------------------------------------------------//
	//-----------------------------------------------------------------//
	
    public SymbolCounter(String symbol)
    {
        this(symbol, 0);
    }

    public SymbolCounter(String symbol, int start)
    {
        super(start);
        
        set(start);
		
		this.symbol = symbol.intern();
    }

	//-----------------------------------------------------------------//
	//-----------------------------------------------------------------//
	// Comparable Behavior
	//-----------------------------------------------------------------//
	//-----------------------------------------------------------------//

    public int compareTo(Object other)
    {
    	if (other == null || !(other instanceof SymbolCounter))
    		return 0;
    	
    	SymbolCounter otherSym = (SymbolCounter)other;
    	
    	if (value() < otherSym.value())
    		return -1;
    	else
    	if (value() > otherSym.value())
    		return +1;
    	else
    		return 0;
    }
    
    
	//-----------------------------------------------------------------//
	// Display Behavior
	//-----------------------------------------------------------------//

	
	public String toString()
	{
		return symbol + "(" + counter + ")";
	}

	
	//-----------------------------------------------------------------//
	// Accessors
	//-----------------------------------------------------------------//

	public boolean equals(Object other)
	{
		if (other == null || !(other instanceof SymbolCounter))
			return false;
		
		SymbolCounter comp = (SymbolCounter)other;
		
		if (symbol != comp.getSymbol())
			return false;
		
		return (counter == comp.value());
	}
	
	
	public boolean inList(Vector list)
	{
		if (list == null) return false;
		
		for (int i = list.size()-1; i >= 0; i--)
		{
			if (this.equals(list.elementAt(i)))
				return true;
		}
		
		return false;
	}
	
	
	
	public SymbolCounter copy()
	{
		return new SymbolCounter(symbol, counter);
	}
	
	
    public String getSymbol()
    {
        return symbol;
    }
    
    
    public void setSymbol(String sym)
    {
    	symbol = sym.intern();
    }
    
    
    
    public boolean contains(char ch)
    {
    	return symbol.indexOf((char)ch) > 0;
    }
    
    
	public String getModifier()
	{
		int dash = symbol.indexOf((int)'_');
		
		if (dash > 0)
			return symbol.substring(0, dash);
		else
			return symbol;
	}
	
	
	public String getHead()
	{
		int dash = symbol.lastIndexOf((int)'_');
		
		if (dash > 0)
			return symbol.substring(dash+1);
		else
			return symbol;
	}
	
	
	public String getPrefix()
	{
		int dash = symbol.indexOf((int)':');
		
		if (dash > 0)
			return symbol.substring(0, dash).intern();
		else
			return null;
	}
	
	
	public String getBody()
	{
		int dash = symbol.lastIndexOf((int)':');
		
		if (dash > 0)
			return symbol.substring(dash+1).intern();
		else
			return symbol;
	}
 }
