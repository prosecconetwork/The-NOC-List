
package tabular;

// This package provides extended Hashtable structures

// (c) Tony Veale  2007

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.Vector;
import java.util.StringTokenizer;


// This class implements a hash table that maps symbols to buckets and which
// applies uniqueness tests
 

public class BucketTable 
{
	protected Hashtable table    	= null;
	
	protected int maxCount      	= 0;
	
	protected String name			= null;
	
	private Vector keyList			= new Vector();
	
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//
	// Constructors
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//
	
	public BucketTable(String name) 
	{
		this.name = name;
		
		table = new Hashtable();
	}
	

	public BucketTable(String name, int size) 
	{
		this.name = name;
		
		table = new Hashtable(size);
	}

	
	public BucketTable(String dir, String filename) 
	{
		this(filename);
		
		loadTable(dir + filename);
	}

	
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//
	// Access Methods
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//
	
	public Hashtable getTable()
	{
		return table;
	}
	
	
	public String getName()
	{
		return name;
	}

	
	public int size()
	{
		return table.size();
	}
	

	
	public Vector get(String key)
	{
		return (Vector)table.get(key);
	}
	

	
	public Vector get(Vector keys)
	{
		if (keys == null || keys.size() == 0)
			return null;
		
		Vector all = null, some = null;
		
		String key = null, value = null;
		
		for (int i = 0; i < keys.size(); i++)
		{
			key  = (String)keys.elementAt(i);
			some = get(key);
			
			if (some == null) continue;
			
			if (all == null)
				all = new Vector();
			
			for (int j = 0; j < some.size(); j++)
			{
				if (some.elementAt(j) instanceof SymbolCounter)
					value = ((SymbolCounter)some.elementAt(j)).getSymbol();
				else
					value = (String)some.elementAt(j);
				
				if (!all.contains(value))
					all.add(value);
			}
		}
		
		return all;
	}

	
	
	public int getCount(String key)
	{
		Vector entries = (Vector)table.get(key);
		
		if (entries == null)
			return 0;
		else
			return entries.size();
	}
	
	
	public String getFirst(String key)
	{
		Vector entries = (Vector)table.get(key);
		
		if (entries == null || entries.size() == 0)
			return null;
		else
		if (entries.elementAt(0) instanceof SymbolCounter)
			return ((SymbolCounter)entries.elementAt(0)).getSymbol();
		else
			return (String)entries.elementAt(0);
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
	
	
	public boolean contains(String key, String value)
	{
		Vector contents = get(key);
		
		if (contents == null || contents.size() == 0)
			return false;
		
		for (int i = 0; i < contents.size(); i++)
			if (value.equals((String)contents.elementAt(i)))
				return true;
		
		return false;
	}
	
	
	public boolean containsModifier(String key, String mod)
	{
		Vector contents = get(key);
		
		if (contents == null || contents.size() == 0)
			return false;
		
		String content = null;
		
		boolean symbolic = contents.elementAt(0) instanceof SymbolCounter;
		
		for (int i = 0; i < contents.size(); i++)
		{
			if (symbolic)
				content = ((SymbolCounter)contents.elementAt(i)).getSymbol();
			else
				content = (String)contents.elementAt(i);
		
			if (content.startsWith(mod) && content.length() > mod.length() && content.charAt(mod.length()) == '_')
				return true;
		}
		
		return false;
	}
	
	
	public boolean containsSuffix(String key, String head)
	{
		Vector contents = get(key);
		
		if (contents == null || contents.size() == 0)
			return false;
		
		String content = null;
		
		boolean symbolic = contents.elementAt(0) instanceof SymbolCounter;
		
		for (int i = 0; i < contents.size(); i++)
		{
			if (symbolic)
				content = ((SymbolCounter)contents.elementAt(i)).getSymbol();
			else
				content = (String)contents.elementAt(i);
		
			if (content.endsWith(head) && content.length() > head.length() 
					&& content.charAt(content.length() - head.length() - 1) == ':')
				return true;
		}
		
		return false;
	}
	
	
	public boolean containsPrefix(String key, String mod)
	{
		Vector contents = get(key);
		
		if (contents == null || contents.size() == 0)
			return false;
		
		String content = null;
		
		boolean symbolic = contents.elementAt(0) instanceof SymbolCounter;
		
		for (int i = 0; i < contents.size(); i++)
		{
			if (symbolic)
				content = ((SymbolCounter)contents.elementAt(i)).getSymbol();
			else
				content = (String)contents.elementAt(i);
		
			if (content.startsWith(mod) && content.length() > mod.length() && content.charAt(mod.length()) == ':')
				return true;
		}
		
		return false;
	}

	
	public boolean containsHead(String key, String head)
	{
		Vector contents = get(key);
		
		if (contents == null || contents.size() == 0)
			return false;
		
		String content = null;
		
		boolean symbolic = contents.elementAt(0) instanceof SymbolCounter;
		
		for (int i = 0; i < contents.size(); i++)
		{
			if (symbolic)
				content = ((SymbolCounter)contents.elementAt(i)).getSymbol();
			else
				content = (String)contents.elementAt(i);
		
			if (content.endsWith(head) && content.length() > head.length() 
					&& content.charAt(content.length() - head.length() - 1) == '_')
				return true;
		}
		
		return false;
	}

	
	
	public boolean containsSuffixOf(String key, String whole)
	{
		Vector contents = get(key);
		
		if (contents == null || contents.size() == 0)
			return false;
		
		String content = null;
		
		for (int i = 0; i < contents.size(); i++)
		{
			content = (String)contents.elementAt(i);
		
			if (content.length() > 2 && whole.endsWith(content) && whole.length() > content.length() + 1)
				return true;
		}
		
		return false;
	}
	
	
	public boolean containsPrefixOf(String key, String whole)
	{
		Vector contents = get(key);
		
		if (contents == null || contents.size() == 0)
			return false;
		
		String content = null;
		
		for (int i = 0; i < contents.size(); i++)
		{
			content = (String)contents.elementAt(i);
		
			if (content.length() > 2 && whole.startsWith(content) && whole.length() > content.length() + 1)
				return true;
		}
		
		return false;
	}

	
	public int getNumSharedEntries(String word1, String word2)
	{
		Vector entries1 = get(word1);
		
		if (entries1 == null) return 0;
		
		int count  = 0;
		
		for (int i = 0; i < entries1.size(); i++)
			if (contains(word2, (String)entries1.elementAt(i)))
				count++;
		
		return count;
	}

	
	public int numContents(String key)
	{
		Vector contents = get(key);
		
		if (contents == null)
			return -1;
		else
			return contents.size();
	}

	
	
	public int getMaxCount()
	{
		return maxCount;
	}
	
	
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//
	// Key List Access Methods
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//
	
	
	public Vector getKeyList()
	{
		return keyList;
	}
	
	
	public Vector getOrderedKeyList()
	{
		Vector ordered = new Vector();
		
		for (int k = 0; k < keyList.size(); k++)
			ordered.add(new SymbolCounter((String)keyList.elementAt(k), get((String)keyList.elementAt(k)).size()));
		
		return SymbolMap.getSorted(ordered);
	}
	
	
	
	
	public Vector getNonEmptyKeyList()
	{
		Vector nonempty = new Vector(), items = null;
		
		String key = null;
		
		for (int i = 0; i < keyList.size(); i++)
		{
			key = (String)keyList.elementAt(i);
			
			items = get(key);
			
			if (items != null && items.size() > 0)
				nonempty.add(key);
		}
		
		return nonempty;
	}

	
	
	public Vector sortKeyList()
	{
		Vector sorted = new Vector(), items = null;
		
		String key = null;
			
		for (int i = 0; i < keyList.size(); i++)
		{
			key = (String)keyList.elementAt(i);
			
			items = get(key);
			
			if (items != null && items.size() > 0)
				sorted.add(new SymbolCounter(key, items.size()));
		}
		
		sorted = SymbolMap.getSorted(sorted);
		
		for (int i = 0; i < sorted.size(); i++)
		{
			key = ((SymbolCounter)sorted.elementAt(i)).getSymbol();
		
			keyList.setElementAt(key, i);
		}
		
		return sorted;
	}
	
	
	public Vector sortKeyListAlpha() 
	{
        Vector v = new Vector();
        
        for(int count = 0; count < keyList.size(); count++) 
        {
            String s = (String)keyList.elementAt(count);
            
            int i = 0;
            
            for (i = 0; i < v.size(); i++) 
            {
                int c = s.compareTo((String) v.elementAt(i));
                
                if (c < 0) 
                {
                    v.insertElementAt(s, i);
                    break;
                } 
                else 
                if (c == 0) 
                	break;
              
            }
            
            if (i >= v.size()) 
                v.addElement(s);
           
        }
        
        keyList = v;
        
        return v;
    }
	
	
	
	public Vector setKeyList(Vector keys)
	{
		if (keys != null)
			keyList = keys;
		
		return keyList;
	}
	
	
	public BucketTable getKeyMap(char separator, boolean modToHead)
	{
		BucketTable keytab = new BucketTable("mods to heads in key list keys");
		
		if (keyList == null) return null;
		
		String key = null, mod = null, head = null;
		
		int dash = 0;
		
		for (int i = 0; i < keyList.size(); i++)
		{
			key = (String)keyList.elementAt(i);
			
			dash = key.indexOf((char)separator);
			
			if (dash <= 0) continue;
			
			mod  = key.substring(0, dash);
			head = key.substring(dash+1);
			
			if (modToHead)
				keytab.put(mod, head);
			else
				keytab.put(head, mod);
		}
		
		return keytab;
	}
	
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//
	// Other Access Methods
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//
	


	
	public int indexOf(String term, String probe)
	{
		return indexOf(term, probe, 0);
	}
	
	public int indexOf(String term, String probe, int start)
	{
		if (term == null || probe == null) return -1;
		
		Vector items = get(term);
		
		if (items == null || items.size() <= start) return -1;
		
		String sym = null;
		
		for (int i = start; i < items.size(); i++)
		{
			sym = (String)items.elementAt(i);
			
			if (probe.equals(sym))
				return i;
		}
		
		return -1;
	}

	//---------------------------------------------------------------------------//
	// Modifiers
	//---------------------------------------------------------------------------//
	
	public Object remove(String key)
	{
		return table.remove(key);
	}
	
	
	public boolean remove(String key, String value)
	{
		Vector contents = get(key);
		
		if (contents == null || contents.size() == 0)
			return false;
		
		for (int i = 0; i < contents.size(); i++)
			if (value.equals((String)contents.elementAt(i)))
			{
				contents.removeElementAt(i);
				return true;
			}
		
		return false;
	}

	
	public boolean replaceWith(String key, String before, String after)
	{
		Vector contents = get(key);
		
		if (contents == null || contents.size() == 0)
			return false;
		
		for (int i = 0; i < contents.size(); i++)
			if (before.equals((String)contents.elementAt(i)))
			{
				contents.setElementAt(after, i);
				return true;
			}
			
		return false;
	}

	
	public void clear()
	{
		table.clear();
		
		keyList.setSize(0);
	}
	
	
	public Vector set(String key, String value)
	{
		Vector contents = get(key);
		
		if (contents == null)
		{
			contents = new Vector(0);
			table.put(key, contents);
			
			keyList.add(key);
		}
		else
			contents.setSize(0);
		
		contents.addElement(value);
		
		return contents;
	}
	
	
		
	public Vector put(String key)
	{
		Vector values = (Vector)table.get(key);
		
		if (values == null)
		{
			values = new Vector();
			table.put(key, values);
			
			keyList.add(key);
		}
			
		return values;
	}
	
	
	public Vector put(String key, Vector values)
	{
		if (values == null) return null;
		
		if (get(key) == null)
			keyList.add(key);
		
		table.put(key, values);
		
		return values;
	}
	
	
	public Vector override(String key, Vector values)
	{
		if (values == null) return null;
		
		table.put(key, values);
		
		return values;
	}

	
	
	public Vector put(String key, String value)
	{
		return put(key, value, false);
	}
	
	
	public Vector put(String key, String value, boolean before)
	{
		Vector contents = (Vector)table.get(key);
		
		if (contents == null)
		{
			contents = new Vector(0);
			table.put(key, contents);
			
			keyList.add(key);
		}
		
		if (contents.size() > 0)
			for (int i = 0; i < contents.size(); i++)
				if (value.equals((String)contents.elementAt(i)))
					return contents;
			
			
		if (!before || contents.size() == 0 )
			contents.addElement(value);
		else
			contents.insertElementAt(value, 0);
		
		int count = contents.size();
		
		if (count > maxCount)
		{
			maxCount = count;
		}
		
		return contents;
	}
	
	
	
	public Vector copy(String to, String from)
	{
		Vector fromElements = get(from);
		
		if (fromElements != null)
			table.put(to, fromElements);
		
		return fromElements;
	}

	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//
	// Return a bucket table that represents the inverse mapping to this one
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//

	public BucketTable invertTable()
	{
		BucketTable inv = new BucketTable("");
		
		String key  = null, sym = null;
		
		Vector vals = null;
		
		for (Enumeration keys = this.keys(); keys.hasMoreElements(); )
		{
			key = (String)keys.nextElement();
			
			vals = this.get(key);
			
			if (vals == null || vals.size() == 0) continue;
			
			for (int i = 0; i < vals.size(); i++)
			{
				if (vals.elementAt(i) instanceof SymbolCounter)
					sym = ((SymbolCounter)vals.elementAt(i)).getSymbol();
				else
					sym = (String)vals.elementAt(i);
				
				inv.put(sym, key);
			}
		}
		
		return inv;
	}
	
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//
	// Return a bucket table that is a merge of this and another table
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//

	public BucketTable mergeWith(BucketTable other)
	{
		BucketTable copy = other.copyTable();
		
		String key  = null, sym = null;
		
		Vector vals = null;
		
		for (Enumeration keys = this.keys(); keys.hasMoreElements(); )
		{
			key = (String)keys.nextElement();
			
			vals = this.get(key);
			
			if (vals == null || vals.size() == 0) continue;
			
			for (int i = 0; i < vals.size(); i++)
			{
				if (vals.elementAt(i) instanceof SymbolCounter)
					sym = ((SymbolCounter)vals.elementAt(i)).getSymbol();
				else
					sym = (String)vals.elementAt(i);
				
				copy.put(key, sym);
			}
		}
		
		return copy;
	}

	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//
	// Return a bucket table that is a copy of this one
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//

	public BucketTable copyTable()
	{
		BucketTable copy = new BucketTable("");
		
		String key  = null, sym = null;
		
		Vector vals = null;
		
		for (Enumeration keys = this.keys(); keys.hasMoreElements(); )
		{
			key = (String)keys.nextElement();
			
			vals = this.get(key);
			
			if (vals == null || vals.size() == 0) continue;
			
			for (int i = 0; i < vals.size(); i++)
			{
				if (vals.elementAt(i) instanceof SymbolCounter)
					sym = ((SymbolCounter)vals.elementAt(i)).getSymbol();
				else
					sym = (String)vals.elementAt(i);
				
				copy.put(key, sym);
			}
		}
		
		return copy;
	}

	
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//
	// Return a symmetric version, so that  x contains y   implies  y contains x
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//

	public BucketTable getSymmetricTable()
	{
		BucketTable symmetric = new BucketTable("");
		
		String key  = null, sym = null;
		
		Vector vals = null;
		
		for (Enumeration keys = this.keys(); keys.hasMoreElements(); )
		{
			key = (String)keys.nextElement();
			
			vals = this.get(key);
			
			if (vals == null || vals.size() == 0) continue;
			
			for (int i = 0; i < vals.size(); i++)
			{
				if (vals.elementAt(i) instanceof SymbolCounter)
					sym = ((SymbolCounter)vals.elementAt(i)).getSymbol();
				else
					sym = (String)vals.elementAt(i);
				
				symmetric.put(sym, key);
				symmetric.put(key, sym);
			}
		}
		
		return symmetric;
	}
	

	//--------------------------------------------------------------------//
	//--------------------------------------------------------------------//
	// Load a text file into memory as structured bucket table
	//--------------------------------------------------------------------//
	//--------------------------------------------------------------------//

	public void loadTable(String filename)
	{
		loadTable(filename, null);
	}
	
	

	public void loadTable(String filename, Hashtable ignore)
	{
		FileInputStream input;

		try {
		    input = new FileInputStream(filename);
		    
		    loadTable(input, ignore);
		}
		catch (IOException e)
		{
			System.out.println("Cannot find/load table file: " + filename);
			
			e.printStackTrace();
		}
	}
	
		

	private void loadTable(InputStream stream, Hashtable ignore)
	{
		String line = null;
		
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(stream, "UTF8"));
			
			while ( (line = input.readLine()) != null)  // Read a line at a time
			{				
				if (line.startsWith("#")) continue;
				
				int tstart = -1, nstart = -1, paren = 0, dot = line.indexOf((int)'.');
				
				String token = null, headword = null;
				
				
				char curr = ' ';
						
				Vector items = null;
				
				for (int i = 0; i < line.length(); i++)
				{
					curr = line.charAt(i);
					
					if (curr == ' ' || curr == '\t')
					{
						if (tstart >= 0 && items == null)
						{
							headword = line.substring(tstart, i);
							
							items    = get(headword);
							
							if (items == null)
							{
								items    = new Vector();
							
								put(headword, items);
							}
						}
						
						tstart = i+1;
					}
					else
					if (curr == '[')
						tstart = i+1;
					else
					if (curr == '(')
						paren = i;
					else
					if ((curr == ',' || curr == ']') && items != null && i > tstart)
					{
						if (paren > tstart)
							token = line.substring(tstart, paren);
						else
							token = line.substring(tstart, i);
						
						if (ignore == null || ignore.get(token) == null)
						{
							if (!token.startsWith("***") && !token.endsWith("***"))
							{
								token = token.intern();
								
								if (!items.contains(token))
									items.add(token);
							}
						}
					}
				}
				
				//System.out.println(headword + ": " + items);
			}

		   input.close();  // close connection to the data source
		}
		catch (IOException e) 
		{
			System.out.println("Exception while reading table file:\n " + e.toString());
				 
			e.printStackTrace();
		}
	}
	
	
	//--------------------------------------------------------------------//
	//--------------------------------------------------------------------//
	// Load a text file into memory as structured bucket table
	//--------------------------------------------------------------------//
	//--------------------------------------------------------------------//

	public void loadWikiTable(String filename)
	{
		FileInputStream input;

		try {
		    input = new FileInputStream(filename);
		    
		    loadWikiTable(input);
		}
		catch (IOException e)
		{
			System.out.println("Cannot find/load wiki table file: " + filename);
			
			e.printStackTrace();
		}
	}
	
		

	private void loadWikiTable(InputStream stream)
	{
		String line = null;
		
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(stream, "UTF8"));
			
			while ( (line = input.readLine()) != null)  // Read a line at a time
			{				
				if (line.startsWith("#")) continue;
				
				StringTokenizer tokens = new StringTokenizer(line, " :\t");  
				
				if (tokens.hasMoreTokens()) 
				{
					String headword = tokens.nextToken();
					
					while (tokens.hasMoreTokens())
						this.put(headword, tokens.nextToken());
				}
			}

		   input.close();  // close connection to the data source
		}
		catch (IOException e) 
		{
			System.out.println("Exception while reading wiki table file:\n " + e.toString());
				 
			e.printStackTrace();
		}
	}

	
	//--------------------------------------------------------------------//
	//--------------------------------------------------------------------//
	// Load a comma-seperated-values file into memory as a bucket table
	//--------------------------------------------------------------------//
	//--------------------------------------------------------------------//

	public void loadCSV(String filename)
	{
		loadCSV(filename, null);
	}
	
	

	public void loadCSV(String filename, Hashtable ignore)
	{
		FileInputStream input;

		try {
		    input = new FileInputStream(filename);
		    
		    loadCSV(input, ignore);
		}
		catch (IOException e)
		{
			System.out.println("Cannot find/load table file: " + filename);
			
			e.printStackTrace();
		}
	}
	
		

	private void loadCSV(InputStream stream, Hashtable ignore)
	{
		String line = null;
		
		try {
			BufferedReader input   = new BufferedReader(new InputStreamReader(stream));
			
			StringTokenizer tokens = null;
			
			String token = null, headword = null;
			
			Vector items = null;
			
			while ( (line = input.readLine()) != null)  // Read a line at a time
			{				
				tokens = new StringTokenizer(line, ",:");
				
				if (!tokens.hasMoreTokens()) continue;
				
				headword = tokens.nextToken().trim();
				
				if (!tokens.hasMoreTokens()) 
					continue;
				
				items = get(headword);
				
				if (items == null)
					items = new Vector();
				else
					tokens.nextToken();
				
				while (tokens.hasMoreTokens())
				{
					token = tokens.nextToken().trim().intern();
					
					if (!items.contains(token))
						items.add(token);
				}
				
				put(headword, items);
			}

		   input.close();  // close connection to the data source
		}
		catch (IOException e) 
		{
			System.out.println("Exception while reading CSV file:\n " + e.toString());
				 
			e.printStackTrace();
		}
	}
	
	
	//--------------------------------------------------------------------//
	//--------------------------------------------------------------------//
	// Load a tab-separated-values file into memory as a bucket table
	//--------------------------------------------------------------------//
	//--------------------------------------------------------------------//


	

	public void loadTabbedFile(String filename)
	{
		FileInputStream input;

		try {
		    input = new FileInputStream(filename);
		    
		    loadTabbedFile(input);
		}
		catch (IOException e)
		{
			System.out.println("Cannot find/load table file: " + filename);
			
			e.printStackTrace();
		}
	}
	
		

	private void loadTabbedFile(InputStream stream)
	{
		String line = null;
		
		try {
			BufferedReader input   = new BufferedReader(new InputStreamReader(stream));
			
			StringTokenizer tokens = null;
			
			String token = null, headword = null;
			
			Vector items = null;
			
			while ( (line = input.readLine()) != null)  // Read a line at a time
			{				
				tokens = new StringTokenizer(line, "\t");
				
				if (!tokens.hasMoreTokens()) continue;
				
				headword = tokens.nextToken().trim();
				
				if (!tokens.hasMoreTokens()) 
					continue;
				
				items = get(headword);
				
				if (items == null)
					items = new Vector();
				else
					tokens.nextToken();
				
				while (tokens.hasMoreTokens())
				{
					token = tokens.nextToken().trim().intern();
					
					if (!items.contains(token))
						items.add(token);
				}
				
				put(headword, items);
			}

		   input.close();  // close connection to the data source
		}
		catch (IOException e) 
		{
			System.out.println("Exception while reading Tabbed file:\n " + e.toString());
				 
			e.printStackTrace();
		}
	}

	
	
	//-------------------------------------------------------------//
	//-------------------------------------------------------------//
	// Prune the mapping by removing excess terms
	//-------------------------------------------------------------//
	//-------------------------------------------------------------//

	public void pruneLast(int preserve)
	{
		String key = null;
		Vector elements = null, keys = getKeyList();
		
		for (int i = 0; i < keys.size(); i++)
		{
			key = (String)keys.elementAt(i);
			
			elements = this.get(key);
			
			if (elements == null || elements.size() < preserve) continue;
			
			elements.setSize(preserve);
		}
	}
	
			
	public void pruneIfLessThan(int min)
	{
		String key = null;
		Vector elements = null, keys = getKeyList();
		
		for (int i = keys.size()-1; i >= 0; i--)
		{
			key = (String)keys.elementAt(i);
			
			elements = this.get(key);
			
			if (elements == null || elements.size() < min)
			{
				keys.remove(i);
				
				table.remove(key);
			}
		}
	}
	
	public void pruneIfMoreThan(int max)
	{
		String key = null;
		Vector elements = null, keys = getKeyList();
		
		for (int i = keys.size()-1; i >= 0; i--)
		{
			key = (String)keys.elementAt(i);
			
			elements = this.get(key);
			
			if (elements == null || elements.size() > max)
			{
				keys.remove(i);
				
				table.remove(key);
			}
		}
	}

	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//
	// Determine the extent of the overlap between two keys
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//

	public boolean areOverlapping(String key1, String key2)
	{
		Vector values = get(key1);
		
		if (values == null || get(key2) == null) return false;
		
		for (int i = 0; i < values.size(); i++)
			if (contains(key2, (String)values.elementAt(i)))
				return true;
		
		return false;
	}
	
	
	
	public int getOverlappingExtent(String key1, String key2)
	{
		Vector values = get(key1);
		
		if (values == null || get(key2) == null) return 0;
		
		int count = 0;
		
		for (int i = 0; i < values.size(); i++)
			if (contains(key2, (String)values.elementAt(i)))
				count++;
		
		return count;
	}
	
	
	public Vector getOverlappingValues(String key1, String key2)
	{
		Vector values = get(key1), overlap = null;
	
		if (values == null || get(key2) == null) return null;
		
		for (int i = 0; i < values.size(); i++)
			if (contains(key2, (String)values.elementAt(i)))
			{
				if (overlap == null)
					overlap = new Vector();
				
				overlap.add(values.elementAt(i));
			}
		
		return overlap;
	}
	
	
	
	public Vector getOverlappingValues(String key, Vector values)
	{
		Vector overlap = null;
		
		if (values == null || get(key) == null) return null;
		
		for (int i = 0; i < values.size(); i++)
			if (contains(key, (String)values.elementAt(i)))
			{
				if (overlap == null)
					overlap = new Vector();
				
				overlap.add(values.elementAt(i));
			}
		
		return overlap;
	}
	
	
	//-------------------------------------------------------------//
	//-------------------------------------------------------------//
	// Do two key terms have values that share a modifier or a head?
	//-------------------------------------------------------------//
	//-------------------------------------------------------------//

	public String getSharedModifier(String key1, String key2)
	{
		Vector terms1 = this.get(key1), terms2 = this.get(key2);
		
		if (terms1 == null || terms2 == null) return null;
		
		String term1 = null, term2 = null, mod1 = null, mod2 = null;
		
		boolean simple = !(this instanceof SymbolMap);
		
		for (int i = 0; i < terms1.size(); i++)
		{
			if (simple)
				term1 = (String)terms1.elementAt(i);
			else
				term1 = ((SymbolCounter)terms1.elementAt(i)).getSymbol();
			
			mod1 = getComplexModifier(term1);
			
			if (mod1 == null) continue;
			
			for (int j = 0; j < terms2.size(); j++)
			{
				if (simple)
					term2 = (String)terms2.elementAt(j);
				else
					term2 = ((SymbolCounter)terms2.elementAt(j)).getSymbol();
				
				if (!term2.startsWith(mod1)) continue;
				
				if (mod1.equals(getComplexModifier(term2)))
					return mod1;
			}
		}
		
		return null;
	}
	

	
	public String getSharedHead(String key1, String key2)
	{
		Vector terms1 = this.get(key1), terms2 = this.get(key2);
		
		if (terms1 == null || terms2 == null) return null;
		
		String term1 = null, term2 = null, head1 = null, head2 = null;
		
		boolean simple = !(this instanceof SymbolMap);
		
		for (int i = 0; i < terms1.size(); i++)
		{
			if (simple)
				term1 = (String)terms1.elementAt(i);
			else
				term1 = ((SymbolCounter)terms1.elementAt(i)).getSymbol();
			
			head1 = getComplexHead(term1);
			
			if (head1 == null) continue;
			
			for (int j = 0; j < terms2.size(); j++)
			{
				if (simple)
					term2 = (String)terms2.elementAt(j);
				else
					term2 = ((SymbolCounter)terms2.elementAt(j)).getSymbol();
				
				if (!term2.endsWith(head1)) continue;
				
				if (head1.equals(getComplexHead(term2)))
					return head1;
			}
		}
		
		return null;
	}

	
	//-------------------------------------------------------------//
	//-------------------------------------------------------------//
	// Return the modifier or head of a Symbol 
	//  taking into account that it may have a complex orthography
	//-------------------------------------------------------------//
	//-------------------------------------------------------------//

	public  String getComplexModifier(String term)
	{
		int dash1 = term.indexOf((int)'_');
		
		if (dash1 < 0 || dash1 == term.length()-1) 
			return null;
		
		String mod = term.substring(0, dash1);

		int dash2 = term.indexOf((int)'_', dash1 + 1);
				
		while (dash2 > 0 && Character.isUpperCase(term.charAt(dash1 + 1)))
		{
			mod = term.substring(0, dash2);
			
			dash1 = dash2;
			dash2 = term.indexOf((int)'_', dash1 + 1);
		}
				
		return mod;
	}
	
	public  String getComplexHead(String term)
	{
		int dash1 = term.indexOf((int)'_');
		
		if (dash1 < 0 || dash1 == term.length()-1) 
			return null;
		
		String head = term.substring(dash1+1);

		int dash2 = term.indexOf((int)'_', dash1 + 1);
				
		while (dash2 > 0 && Character.isUpperCase(term.charAt(dash1 + 1)))
		{
			head = term.substring(dash2+1);
			
			dash1 = dash2;
			dash2 = term.indexOf((int)'_', dash1 + 1);
		}
				
		return head;
	}
	
	
	//-------------------------------------------------------------//
	//-------------------------------------------------------------//
	// Create a cross-reference of values from a given table 
	//-------------------------------------------------------------//
	//-------------------------------------------------------------//
	
	public SymbolMap getCrossReferenceKeyMap()
	{
		return this.invertTable().getCrossReferenceMap();
	}

	public SymbolMap getCrossReferenceMap()
	{
		System.out.print("Creating Cross-reference of <" + getName() + ">  ...");
		
		SymbolMap xref = new SymbolMap("cross reference of values with each other");
		
		String key = null;
		
		Object value1 = null, value2 = null;
		
		Vector elements = null, keys = getKeyList();
		
		for (int i = keys.size()-1; i >= 0; i--)
		{
			key = (String)keys.elementAt(i);
			
			elements = this.get(key);
			
			if (elements == null || elements.size() == 0) continue;
			
			for (int j = 0; j < elements.size(); j++)
			{
				value1 = elements.elementAt(j);
				
				for (int k = j+1; k < elements.size(); k++)
				{
					value2 = elements.elementAt(k);
					
					if (value1 instanceof SymbolCounter)
					{
						xref.put(((SymbolCounter)value1).getSymbol(), ((SymbolCounter)value2).getSymbol());
						xref.put(((SymbolCounter)value2).getSymbol(), ((SymbolCounter)value1).getSymbol());					
					}
					else
					{
						xref.put((String)value1, (String)value2);
						xref.put((String)value2, (String)value1);				
					}
				}
			}
		}

		System.out.println(" values of " + keys.size() + " keys cross-referenced.");
		
		System.out.println("X-refs for <actor> = " + xref.get("actor"));
		System.out.println("X-refs for <star> = " + xref.get("star"));
		

		return xref;
	}
	
	
	//-------------------------------------------------------------//
	//-------------------------------------------------------------//
	// Basic statistics
	//-------------------------------------------------------------//
	//-------------------------------------------------------------//

	public double getAverageEntriesPerKey()
	{
		if (keyList == null || keyList.size() == 0)
			return 0;
		
		int total = 0, numKeys = keyList.size();
		
		for (int i = 0; i < numKeys; i++)
			total += this.getCount((String)keyList.elementAt(i));
		
		return (1.0*total)/numKeys;
	}
	
	//-------------------------------------------------------------//
	//-------------------------------------------------------------//
	// Save a table containing the given selection of terms 
	//-------------------------------------------------------------//
	//-------------------------------------------------------------//


	public void saveTable(String fname)
	{
		saveTable(fname, this, this.getKeyList(), null);
	}
	
	
	public void saveTable(String fname, String fileType)
	{
		saveTable(fname, this, this.getKeyList(), fileType);
	}

	
	public static void saveTable(String fname,BucketTable map, Vector keys, String fileType)
	{
		try {
			OutputStreamWriter output = null;
			
			if (fileType == null)
				output = new OutputStreamWriter(new FileOutputStream(fname));
			else
				output = new OutputStreamWriter(new FileOutputStream(fname), fileType);
			
			String key = null;
			
			Vector elements = null;
			
			int count = 0;
			
			SymbolCounter sym = null;
			
			for (int i = 0; i < keys.size(); i++)
			{
				key = (String)keys.elementAt(i);
				
				elements = map.get(key);
				
				if (elements == null || elements.size() < 1) continue;
				
				output.write(i + ". " + solidifyKey(key) + " " + elements + "\n");
			}
			
			output.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}		
	
	
	public static String solidifyKey(String key)
	{
		int space = key.indexOf((int)' ');
		
		if (space > 0)
			return key.substring(0, space) + "_" + solidifyKey(key.substring(space+1));
		else
			return key;
	}
	
	
	
	public void saveKeyList(String fname)
	{
		try {
			OutputStreamWriter output = new OutputStreamWriter(new FileOutputStream(fname));
			
			String key = null;
			
			Vector keys = getKeyList();
			
			for (int i = 0; i < keys.size(); i++)
			{
				key = (String)keys.elementAt(i);
				
				if (key.indexOf((int)'_') < 0)
					output.write(solidifyKey(key)  + "\n");
			}
			
			output.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}		
	
	//-------------------------------------------------------------------//
	//-------------------------------------------------------------------//
	// Save a BucketTable as a flat file, one line per key
	//-------------------------------------------------------------------//
	//-------------------------------------------------------------------//

	public void saveTableAsFlatFile(String fname)
	{
		saveTableAsFlatFile(fname, null);
	}

	public void saveTableAsFlatFile(String dataFile, String indexFile)
	{
		try {
			FileOutputStream   file   = new FileOutputStream(dataFile);
			
			OutputStreamWriter output = new OutputStreamWriter(file, "UTF-8");
			
			OutputStreamWriter index  = null;
			
			if (indexFile != null)
				index = new OutputStreamWriter(new FileOutputStream(indexFile));
			
			String key = null, sym = null;
			
			Vector elements = null, keys = getKeyList();
			
			int count = 0;
			
			for (int i = 0; i < keys.size(); i++)
			{
				key = (String)keys.elementAt(i);
				
				elements = get(key);
								
				if (elements == null || elements.size() < 1) continue;
				
				if (elements == null || elements.size() == 0) continue;
				
				if (indexFile != null)
					index.write(key + "\t" + file.getChannel().position() + "\n");
				
				output.write(key);
				
				for (int j = 0; j < elements.size(); j++)
				{
					sym = (String)elements.elementAt(j);
					
					output.write("\t" + sym);
				}
			
				output.write("\n");
				
				output.flush();
			}
			
			index.close();
			output.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}		
	
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//
	// Save in a tabbed file format
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//

	public void saveTabbedFile(String fname)
	{
		saveTabbedTable(fname, null);
	}
	
	public void saveTabbedTable(String fname, BucketTable ignore)
	{
		try {
			OutputStreamWriter output = new OutputStreamWriter(new FileOutputStream(fname));
			
			String key = null, value = null;
			
			Vector elements = null, keys = getKeyList();
			
			int count = 0;
			
			Object element = null;
			
			for (int i = 0; i < keys.size(); i++)
			{
				key = (String)keys.elementAt(i);
				
				if (ignore != null && ignore.get(key) != null) continue;
				
				elements = this.get(key);
				
				if (elements == null || elements.size() < 1) continue;
				
				boolean headed = false;
				
				for (int j = 0; j < elements.size(); j++)
				{
					element = elements.elementAt(j);
					
					if (element == null) continue;
										
					if (element instanceof String)
						value = (String)element;
					else
					if (element instanceof SymbolCounter)
						value = ((SymbolCounter)element).getSymbol();
					else
						continue;
					
					if (ignore != null && ignore.get(value) != null) continue;
					
					if (!headed)
					{
						output.write(key);
						headed = true;
					}
					
					output.write("\t");
					output.write(value);
				}
				
				if (headed) output.write("\n");
			}
			
			output.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}		

	

	
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//
	// Serialization Behavior
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//
	
	// Serialize this object to disk
	
		
	public void serialize(String filename)
	{
		try {
			System.out.print("\nSaving table <" + name + "> ... ");
			FileOutputStream fos = new FileOutputStream(filename);
			ObjectOutputStream out = new ObjectOutputStream(fos);
			out.writeObject(table);
			out.flush();
			out.close();
			System.out.println(" saved.\n");
		}
		catch (IOException e) {System.out.println(e);};
	}


	// Deserialize the contents of this object from disk
	
	public void deserialize(String filename)
	{
		try {
			System.out.print("\nLoading table <" + name + "> ... ");
			FileInputStream fis = new FileInputStream(filename);
			ObjectInputStream in = new ObjectInputStream(fis);
			
			table = (Hashtable)in.readObject();

			in.close();
			System.out.println(" loaded with " + table.size() + " entries.\n");
		}
		catch (Exception e) {};
	}

	
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//
	// Turn this BucketTable into a SymbolMap with counts
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//

	public SymbolMap toSymbolMap()
	{
		SymbolMap map = new SymbolMap(name);
		
		String key = null, value = null;
		
		Vector values = null;
		
		int count = 0;
		
		boolean special = false;
		
		for (int i = 0; i < keyList.size(); i++)
		{
			key = (String)keyList.elementAt(i);
			
			values = get(key);
			
			if (values == null) continue;
			
			special = key.indexOf((int)'_') < 0;
			
			for (int j = 0; j < values.size(); j++)
			{
				value = (String)values.elementAt(j);
				
				if (!special && value.hashCode() < key.hashCode())
					continue;
				
				if (value == key)
					count = values.size() + 1;
				else
					count = getOverlappingExtent(key, value) + 1;
				
				map.putMax(key, value, count);
				
				if (!special)
					map.putMax(value, key, count);
			}
		}
		
		return map;
	}
	
	
	//-------------------------------------------------------------------//
	//-------------------------------------------------------------------//
	// Main routine for test purposes
	//-------------------------------------------------------------------//
	//-------------------------------------------------------------------//

	public static void main(String[] args)
	{
		String dir      	= "/Users/tonyveale/Desktop/Lexical Resources/";

		String moodDir  	= dir + "Moods/";
		
		BucketTable entryConds = new BucketTable("entry conditions");
		
		entryConds.loadTable(moodDir + "stereotype exit conditions.idx");
		
		entryConds = entryConds.invertTable();
		
		Vector actions = entryConds.getKeyList();
		
		System.out.println(actions.size());
		
		for (int i = 1500; i < actions.size();  i++)   
		{
			String action = (String)actions.elementAt(i);
			String verb   = action.substring(0, action.indexOf((int)':'));
			String noun   = action.substring(verb.length()+1);
			
			System.out.println(action + "\t" + verb + "\t" + noun);
		}
		
		if (actions != null)  System.exit(0);
		
		BucketTable antoModel   = new BucketTable("properties to antonyms");
		
		antoModel.loadTable(moodDir + "ADJ opposites.idx");
		
		BucketTable antoInvert = antoModel.invertTable();
		
		antoModel.mergeWith(antoInvert).saveTable(moodDir + "ADJ opposites merged.idx");

				
		
		BucketTable stereoModel   = new BucketTable("stereos to properties");
		BucketTable propertyModel = new BucketTable("properties to stereos");
		
		stereoModel.loadTable(moodDir + "stereotype model.idx");
		propertyModel.loadTable(moodDir + "stereotype properties.idx");
		
		BucketTable stereoInvert = stereoModel.invertTable();
		BucketTable propertyInvert = propertyModel.invertTable();
		
		System.out.println(stereoInvert.get("black").size() + ":" + stereoInvert.get("black"));
		
		stereoModel.mergeWith(propertyInvert).saveTable(moodDir + "stereotype model merged.idx");
		propertyModel.mergeWith(stereoInvert).saveTable(moodDir + "stereotype properties merged.idx");
		
		/*
		BucketTable states   = new BucketTable("states to properties");
		
		states.loadTable(dir + "Categories/Fingerprints/states of properties.idx");
		
		Vector keys = states.getKeyList();
		
		for (int i = 1500; i < keys.size(); i++)  
			System.out.println(keys.elementAt(i));
		
		*/
		 
	}
	
}