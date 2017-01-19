
package tabular;

// This class implements atomic increment and decrement operations on an integer
// wrapped in an object shell 

// Consider it a version of Integer than permits changes to its contents

//(c) Tony Veale  2007


public class AtomicCounter implements java.io.Serializable
{ 
    int counter = 0;
    
	//-----------------------------------------------------------------//
	// Constructors
	//-----------------------------------------------------------------//
	
    public AtomicCounter()
    {
        counter = 0;
    }

    public AtomicCounter(int start)
    {
        counter = start;
    }

	//-----------------------------------------------------------------//
	// Display Behavior
	//-----------------------------------------------------------------//

	
	public String toString()
	{
		return "[" + counter + "]";
	}

	
	//-----------------------------------------------------------------//
	// Accessors
	//-----------------------------------------------------------------//

	
    synchronized public int value()
    {
        return counter;
    }
    
        
	//-----------------------------------------------------------------//
	// Modifiers
	//-----------------------------------------------------------------//

	
	synchronized public int inc()
    {
        counter++;
        
        return counter;
    }
    
       

	synchronized public int inc(int delta)
    {
        counter += delta;
        
        return counter;
    }
    
       

	
	synchronized public int dec()
    {
        if (counter > 0) 
        	return --counter; 
        else 
        	return 0;
    }

	
	synchronized public int dec(int delta)
    {
		counter -= delta;
        
        return counter;
    }

	
	synchronized public int set(int value)
    {
        counter = value;
		
		return value;
    }
	
	

    synchronized public int setMin(int value)
    {
        counter = Math.min(counter, value);
		
		return counter;
    }



    synchronized public int setMax(int value)
    {
        counter = Math.max(counter, value);
		
		return counter;
    }
}
