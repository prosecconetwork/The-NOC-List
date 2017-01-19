
package tabular;

// This class implements atomic increment and decrement operations on an integer
// wrapped in an object shell 

// This extension allows SymbolCounter objects to be linked in a list

public class SymbolLink extends SymbolCounter
{ 
    private SymbolLink next = null;
    
	//-----------------------------------------------------------------//
	//-----------------------------------------------------------------//
	// Constructors
	//-----------------------------------------------------------------//
	//-----------------------------------------------------------------//
	
    public SymbolLink(String symbol)
    {
        this(symbol, null, 0);
    }

    public SymbolLink(String symbol, int start)
    {
       this(symbol, null, start);
    }
    
    
    public SymbolLink(String symbol, SymbolLink next, int start)
    {
       super(symbol, start);
       
       this.next = next;
    }
    
	//-----------------------------------------------------------------//
	//-----------------------------------------------------------------//
	// Maintain next pointer
	//-----------------------------------------------------------------//
	//-----------------------------------------------------------------//

    public SymbolLink next()
    {
    	return next;
    }
    
    
    public void setNext(SymbolLink next)
    {
    	this.next = next;
    }
  
}