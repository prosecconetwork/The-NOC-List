
package tabular;

// This package provides extended Hashtable structures

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

// This class implements a hash table that maps symbols to counters that
// track how many times a symbol has been entered


public class CountTable 
{
	private boolean symbolical = false;
	
	private Hashtable table    = new Hashtable();
	
	private int maxCount       = 0;
	
	private int threshold      = 1000000000;
	
	private String tableName   = null;
	
	private Vector maxElements = new Vector(0);
	
	private Vector topElements = new Vector(0); 
	
	private Vector keys 	   = new Vector();
	
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//
	// Constructors
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//
	
	public CountTable()
	{
		this(false);
	}
	
	
	public CountTable(boolean bindSymbols) 
	{
		symbolical = bindSymbols;
	}

	
	
	public CountTable(String name) 
	{
		this.tableName = name;
	}
	
	

	public CountTable(String name, int threshold) 
	{
		this(name);
		
		this.threshold = threshold;
	}
	

	
	public CountTable(String dir, String filename) 
	{
		this(filename);
		
		loadTable(dir + filename);
	}
	
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//
	// Accessors
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//
	
	public String getName()
	{
		return tableName;
	}
	

	public int size()
	{
		return table.size();
	}
	
	
	public AtomicCounter get(String key)
	{
		return (AtomicCounter)table.get(key);
	}
	
	
	public Enumeration keys()
	{
		return table.keys();
	}
	
	
	public Enumeration elements()
	{
		return table.elements();
	}
	
	
	public boolean contains(String key)
	{
		return table.get(key) != null;
	}
	
	
	public boolean contains(String key, int value)
	{
		AtomicCounter contents = get(key);
		
		if (contents == null)
			return false;
		else
			return contents.value() >= value;
	}

	
	public int getCount(String key)
	{
		AtomicCounter contents = get(key);
		
		if (contents == null)
			return 0;
		else
			return contents.value();
	}

	
	
	public int getMaxCount()
	{
		return maxCount;
	}
	
	
	public Vector getMaxElements()
	{
		return maxElements;
	}
	
	
	public Vector getThreshold()
	{
		return maxElements;
	}

	
	public Vector getTopElements()
	{
		return topElements;
	}
	
	
	public Vector getKeyList()
	{
		return keys;
	}

	
	public String getMostFrequent(String term1, String term2)
	{
		if (getCount(term1) > getCount(term2))
			return term1;
		else
			return term2;
	}
	
	
	public String getMostFrequent(String term1, String term2, String term3)
	{
		return getMostFrequent(getMostFrequent(term1, term2), term3);
	}
	
	
	public String getMostFrequent(String term1, String term2, String term3, String term4)
	{
		return getMostFrequent(getMostFrequent(term1, term2), getMostFrequent(term3, term4));
	}
	
	
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//
	// Modifiers
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//
	
	
	public void setSymbolize(boolean ymbolize)
	{
		symbolical = ymbolize;
	}
	
	
	public int setThreshold(int value)
	{
		threshold = value;
		
		return value;
	}
	
	
	public Object remove(String key)
	{
		return table.remove(key);
	}
	
	
	public boolean remove(String key, int value)
	{
		AtomicCounter contents = get(key);
		
		if (contents == null)
			return false;
		
		if (value > contents.value())
			remove(key);
		else
			contents.set(contents.value()-value);
		
		return true;
	}

	
	
	public void clear()
	{
		table.clear();
		topElements.setSize(0);
		maxElements.setSize(0);
	}
	
	
	public AtomicCounter put(String key)
	{
		return put(key, 1);
	}

	
	
	public AtomicCounter put(String key, int value)
	{
		AtomicCounter contents = get(key);
		
		if (contents == null)
			return set(key, value);
		else
			return set(key, value + contents.value());
	}

	
	public AtomicCounter putMax(String key, int value)
	{
		AtomicCounter contents = get(key);
		
		if (contents == null || value > contents.value())
			return set(key, value);
		else
			return contents;
	}
	
	
	public AtomicCounter set(String key, int value)
	{
		key = key.intern();
		
		AtomicCounter contents = get(key);
		
		if (contents == null)
		{
			if (symbolical)
				contents = new SymbolCounter(key, 0);
			else
				contents = new AtomicCounter(0);
			
			table.put(key, contents);
			
			if (symbolical)
				keys.add(contents);
			else
				keys.add(key);
		}
		
		contents.set(value);
		
		if (value > maxCount)
		{
			maxCount = value;
			maxElements.setSize(0);
			maxElements.addElement(key);
		}
		else
		if (value == maxCount)
			maxElements.addElement(key);
		
		if (value == threshold)
			topElements.addElement(key);
		
		return contents;
	}
	
	
	
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//
	// Load and Save Behavior
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//
	
	
	public void saveTable(String fname)
	{
		if (!symbolical) return;
		
		try {
			OutputStreamWriter output = new OutputStreamWriter(new FileOutputStream(fname));
			
			SymbolCounter key = null;
			
			Vector counter = null;
			
			for (int i = 0; i < keys.size(); i++)
			{
				key = (SymbolCounter)keys.elementAt(i);
				
				output.write(key.getSymbol() + "\t" + key.value() + "\n");
			}
			
			output.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	

	
	public void loadTable(String filename)
	{
		String line = null;
		
		try {
			BufferedReader input   = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
			
			String token = null, count = null;
			
			int tab = 0;
			
			while ( (line = input.readLine()) != null)  // Read a line at a time
			{				
				tab = line.indexOf((int)'\t');
				
				if (tab < 0) continue;
				
				token = line.substring(0, tab);
				count = line.substring(tab+1);
								
				put(token, Integer.parseInt(count));
			}

		   input.close();  // close connection to the data source
		}
		catch (IOException e) 
		{
			System.out.println("Exception while reading CountTable file:\n " + e.toString());
				 
			e.printStackTrace();
		}
	}

	
	
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//
	// Main 
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//
	
	public static void main(String[] args)
	{
	}
	
	
}