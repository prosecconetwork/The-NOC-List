
package tabular;

// This package provides extended Hashtable structures

//(c) Tony Veale  2007

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

 

// This class implements a hash table that maps symbolic strings to buckets of symbol counters
// Can be used to create a generalized mapping between symbols that counts the associations 

public class SymbolMap extends BucketTable
{
	private BucketTable domain = null;
	
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//
	// Constructors
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//
	
	public SymbolMap(String dir, String filename) 
	{
		this(dir + filename);
		
		loadMap(dir + filename);
	}
	
	
	public SymbolMap(String name) 
	{
		super(name);
		
		if (name != null && name.endsWith(".idx"))
			loadMap(name);
	};
	

	public SymbolMap(String name, int size) 
	{
		super(name, size);
		
		if (name != null && name.endsWith(".idx"))
			loadMap(name);
	};

	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//
	// Set the domain of the map
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//

	public void setDomain(BucketTable domain)
	{
		this.domain = domain;
	}
	
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//
	// Membership tests
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//
	
	public boolean containsModifier(String key, String mod)
	{
		Vector contents = get(key);
		
		if (contents == null || contents.size() == 0)
			return false;
		
		String content = null;
		
		for (int i = 0; i < contents.size(); i++)
		{
			content = ((SymbolCounter)contents.elementAt(i)).getSymbol();
		
			if (content.length() > mod.length() && content.startsWith(mod) && content.charAt(mod.length()) == '_')
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
		
		for (int i = 0; i < contents.size(); i++)
		{
			content = ((SymbolCounter)contents.elementAt(i)).getSymbol();
		
			if (content.endsWith(head) && content.length() > head.length() 
					&& content.charAt(content.length() - head.length() - 1) == '_')
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
		
		for (int i = 0; i < contents.size(); i++)
		{
			content = ((SymbolCounter)contents.elementAt(i)).getSymbol();
		
			if (content.length() > mod.length() && content.startsWith(mod) && content.charAt(mod.length()) == ':')
				return true;
		}
		
		return false;
	}

	
	public boolean containsBody(String key, String head)
	{
		Vector contents = get(key);
		
		if (contents == null || contents.size() == 0)
			return false;
		
		String content = null;
		
		for (int i = 0; i < contents.size(); i++)
		{
			content = ((SymbolCounter)contents.elementAt(i)).getSymbol();
		
			if (content.endsWith(head) && content.length() > head.length() 
					&& content.charAt(content.length() - head.length() - 1) == ':')
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
			content = ((SymbolCounter)contents.elementAt(i)).getSymbol();
		
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
			content = ((SymbolCounter)contents.elementAt(i)).getSymbol();
		
			if (content.length() > 2 && whole.startsWith(content) && whole.length() > content.length() + 1)
				return true;
		}
		
		return false;
	}

	
	
	
	public boolean contains(String key, String value)
	{
		return get(key, value) != null;
	}

	
	public int getNumSharedEntries(String word1, String word2)
	{
		Vector entries1 = get(word1);
		
		if (entries1 == null) return 0;
		
		int count  = 0;
		
		for (int i = 0; i < entries1.size(); i++)
			if (contains(word2, ((SymbolCounter)entries1.elementAt(i)).getSymbol()))
				count++;
		
		return count;
	}

		

	
	
	public int getCount(String key, String value)
	{
		SymbolCounter entry = get(key, value);
		
		if (entry == null)
			return 0;
		else
			return entry.value();
	}
	
	
	
	public static int getCount(String value, Vector contents)
	{
		SymbolCounter entry = get(value, contents);
		
		if (entry == null)
			return 0;
		else
			return entry.value();
	}
	
	
	
	public int getCount()
	{
		Vector keys = getKeyList(), entries = null;

		if (keys == null) return 0;
		
		int count = 0;
	
		for (int i = 0; i < keys.size(); i++)
		{
			entries = get((String)keys.elementAt(i));
			
			if (entries != null)
				count += entries.size();
		}
		
		return count;
	}

	
	
	public int getKeyCount()
	{
		Vector keys = getKeyList(), entries = null;

		if (keys == null) return 0;
		
		int count = 0;
	
		for (int i = 0; i < keys.size(); i++)
		{
			entries = get((String)keys.elementAt(i));
			
			if (entries != null && entries.size() > 0)
				count += 1;
		}
		
		return count;
	}

	
	
	public int getSum(String key)
	{
		Vector contents = get(key);
		
		if (contents == null || contents.size() == 0)
			return 0;
		
		SymbolCounter entry = null;
		
		int sum = 0;
		
		for (int i = 0; i < contents.size(); i++)
		{
			entry = (SymbolCounter)contents.elementAt(i);
			
			sum +=  entry.value();
		}
		
		return sum;
	}
	
	
	
	public SymbolCounter get(String key, String value)
	{
		Vector contents = get(key);
		
		if (contents == null || contents.size() == 0)
			return null;
		
		SymbolCounter entry = null;
		
		for (int i = 0; i < contents.size(); i++)
		{
			entry = (SymbolCounter)contents.elementAt(i);
			
			if (value.equals(entry.getSymbol()))
				return entry;
		}
		
		return null;
	}
	
	
	
	public static SymbolCounter get(String value, Vector contents)
	{
		if (contents == null || contents.size() == 0)
			return null;
		
		SymbolCounter entry = null;
		
		for (int i = 0; i < contents.size(); i++)
		{
			entry = (SymbolCounter)contents.elementAt(i);
			
			if (value.equals(entry.getSymbol()))
				return entry;
		}
		
		return null;
	}

	
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//
	// Return the median value for a given key
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//

	public int getMedianValue(String key)
	{
		Vector values = get(key);
		
		if (values == null || values.size() == 0) return 0;
		
		if (values.size() == 1)
			return ((SymbolCounter)values.elementAt(0)).value();
		
		if (values.size() == 2)
			return (((SymbolCounter)values.elementAt(0)).value() + ((SymbolCounter)values.elementAt(1)).value())/2;
		
		
		return ((SymbolCounter)values.elementAt(values.size()/2)).value();
	}
	
	
	public double getRelativeMedianValue(String key, String entry)
	{
		int count  = getCount(key, entry);
		
		if (count <= 0) return 0;
		
		return (count*1.0)/(count*1.0 + getMedianValue(key));
	}
	
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//
	// Removal of symbols from the mapping
	//---------------------------------------------------------------------------//
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
			if (value.equals(((SymbolCounter)contents.elementAt(i)).getSymbol()))
			{
				contents.removeElementAt(i);
				return true;
			}
		
		return false;
	}

	

	//---------------------------------------------------------------------------//
	// Addition of symbols to the mapping
	//---------------------------------------------------------------------------//
	
		
	public void resetCounts(int value)
	{
		Vector keys = getKeyList(), entries = null;
		
		if (keys == null) return;
		
		String key = null;
		
		SymbolCounter sym = null;
		
		for (int i = 0; i < keys.size(); i++)
		{
			key = (String)keys.elementAt(i);
			
			entries = get(key);
			
			if (entries == null) continue;
			
			for (int j = 0; j < entries.size(); j++)
			{
				sym  = (SymbolCounter)entries.elementAt(j);
				
				sym.set(value);
			}
		}
		
	}
	
	
	public Vector set(String key, String value)
	{
		Vector contents = get(key);
		
		if (contents == null)
		{
			contents = new Vector(0);
			getTable().put(key, contents);
		}
		else
			contents.setSize(0);
		
		contents.addElement(new SymbolCounter(value));
		
		return contents;
	}
	
	
	
	public Vector put(String key, String value)
	{
		return put(key, value, 1);
	}
	
	
	
	public Vector putExact(String key, String value, int score)
	{
		Vector contents = get(key);
		
		if (contents == null)
		{
			contents = new Vector(0);
			super.put(key, contents);
		}
		
		SymbolCounter entry = null;
		
		if (contents.size() > 0)
			for (int i = 0; i < contents.size(); i++)
			{
				entry = (SymbolCounter)contents.elementAt(i);
				
				if (value.equals(entry.getSymbol()))
				{
					entry.set(score);
					return contents;
				}
			}
		
		entry = new SymbolCounter(value);
		entry.set(score);
		
		contents.addElement(entry);
		
		int count = contents.size();
		
		if (count > maxCount)
		{
			maxCount = count;
		}
		
		return contents;		
	}
	
	

	public Vector putMax(String key, String value, int delta)
	{
		Vector contents = get(key);
		
		if (contents == null)
		{
			contents = new Vector(0);
			super.put(key, contents);
		}
		
		SymbolCounter entry = null;
		
		if (contents.size() > 0)
			for (int i = 0; i < contents.size(); i++)
			{
				entry = (SymbolCounter)contents.elementAt(i);
				
				if (value.equals(entry.getSymbol()))
				{
					entry.set(Math.max(delta, entry.value()));
					return contents;
				}
			}
		
		entry = new SymbolCounter(value);
		entry.inc(delta);
		
		contents.addElement(entry);
		
		int count = contents.size();
		
		if (count > maxCount)
		{
			maxCount = count;
		}
		
		return contents;		
	}

	
	
	public Vector putMaxCaseInsensitive(String key, String value, int delta)
	{
		Vector contents = get(key);
		
		if (contents == null)
		{
			contents = new Vector(0);
			super.put(key, contents);
		}
		
		SymbolCounter entry = null;
		
		String previous = null, lowerVal = value.toLowerCase();
		
		int vlen = value.length();
		
		if (contents.size() > 0)
		{
			for (int i = 0; i < contents.size(); i++)
			{
				entry    = (SymbolCounter)contents.elementAt(i);
				previous = entry.getSymbol();
							
				if (previous.length() == vlen && lowerVal.equals(previous.toLowerCase()))
				{
					if (delta > entry.value()) 
					{
						entry.set(delta);
						entry.setSymbol(value);
					}
					
					return contents;
				}
			}
		}
		
		entry = new SymbolCounter(value);
		entry.inc(delta);
		
		contents.addElement(entry);
		
		int count = contents.size();
		
		if (count > maxCount)
		{
			maxCount = count;
		}
		
		return contents;		
	}

	
	
	
	public Vector put(String key, String value, int delta)
	{
		Vector contents = get(key);
		
		if (contents == null)
		{
			contents = new Vector(0);
			super.put(key, contents);
		}
		
		SymbolCounter entry = null;
		
		if (contents.size() > 0)
			for (int i = 0; i < contents.size(); i++)
			{
				entry = (SymbolCounter)contents.elementAt(i);
				
				if (value.equals(entry.getSymbol()))
				{
					entry.inc(delta);
					return contents;
				}
			}
		
		entry = new SymbolCounter(value);
		entry.inc(delta);
		
		contents.addElement(entry);
		
		int count = contents.size();
		
		if (count > maxCount)
		{
			maxCount = count;
		}
		
		return contents;
	}
	
	
	public boolean putNew(String key, String value)
	{
		Vector contents = get(key);
		
		if (contents == null)
		{
			contents = new Vector(0);
			super.put(key, contents);
		}
		
		SymbolCounter entry = null;
		
		if (contents.size() > 0)
			for (int i = 0; i < contents.size(); i++)
			{
				entry = (SymbolCounter)contents.elementAt(i);
				
				if (value.equals(entry.getSymbol()))
				{
					entry.inc();
					return false;
				}
			}
		
		entry = new SymbolCounter(value);
		entry.inc();
		
		contents.addElement(entry);
		
		int count = contents.size();
		
		if (count > maxCount)
		{
			maxCount = count;
		}
		
		return true;
	}

	

	public boolean putNew(String key, String value, int intValue)
	{
		Vector contents = get(key);
		
		if (contents == null)
		{
			contents = new Vector(0);
			super.put(key, contents);
		}
		
		SymbolCounter entry = null;
		
		if (contents.size() > 0)
			for (int i = 0; i < contents.size(); i++)
			{
				entry = (SymbolCounter)contents.elementAt(i);
				
				if (value.equals(entry.getSymbol()))
				{
					entry.set(intValue);
					return false;
				}
			}
		
		entry = new SymbolCounter(value);
		entry.set(intValue);
		
		contents.addElement(entry);
		
		int count = contents.size();
		
		if (count > maxCount)
		{
			maxCount = count;
		}
		
		return true;
	}

	
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//
	// Replace a SymbolCounter with given key with a new symbol
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//


	public boolean replace(String key, String oldsym, String newsym)
	{
		Vector contents = get(key);
		
		if (contents == null || contents.size() == 0) 
			return false;
			
		SymbolCounter entry = null;
		
		for (int i = 0; i < contents.size(); i++)
		{
			entry = (SymbolCounter)contents.elementAt(i);
			
			if (oldsym.equals(entry.getSymbol()))
			{
				entry.setSymbol(newsym);
				
				return true;
			}
		}

		return false;	
	}

	
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//
	// Insert a SymbolCounter under a given key
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//

	public void insert(String key, SymbolCounter sym)
	{
		Vector vals = (Vector)table.get(key);
		
		if (vals == null || vals.size() == 0)
		{
			vals = new Vector();
			vals.add(sym);
			super.put(key, vals);
			return;
		}
		
		SymbolCounter val = null;
		
		int count = sym.value();
		
		// Scan to see if already present
		
		for (int i = 0; i < vals.size(); i++)
		{
			val = (SymbolCounter)vals.elementAt(i);
			
			if (val.getSymbol() == sym.getSymbol())
			{
				if (val.value() >= count) // no change
					return;
				
				vals.remove(val);
				
				break;
			}
		}
		
		// scan to find orderly insertion point
		
		for (int i = 0; i < vals.size(); i++)
		{
			val = (SymbolCounter)vals.elementAt(i);
			
			if (val.value() < count)
			{
				vals.insertElementAt(sym, i);
				return;
			}
		}
		
		vals.add(sym);
	}
	
	
	
	public int indexOf(String term, String probe)
	{
		return indexOf(term, probe, 0);
	}
	
	public int indexOf(String term, String probe, int start)
	{
		if (term == null || probe == null) return -1;
		
		Vector items = get(term);
		
		if (items == null || items.size() <= start) return -1;
		
		SymbolCounter sym = null;
		
		for (int i = start; i < items.size(); i++)
		{
			sym = (SymbolCounter)items.elementAt(i);
			
			if (probe.equals(sym.getSymbol()))
				return i;
		}
		
		return -1;
	}
	
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//
	// Return a symbol map that represents the inverse mapping to this one
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//

	public SymbolMap invertMap()
	{
		return invertMap(new SymbolMap("inverted"));
	}
	
	
	public SymbolMap invertMap(SymbolMap inv)
	{
		String key  = null;
		
		Vector vals = null;
		
		SymbolCounter sym = null;
		
			
		for (Enumeration keys = this.keys(); keys.hasMoreElements(); )
		{
			key = (String)keys.nextElement();
			
			vals = this.get(key);
			
			if (vals == null || vals.size() == 0) continue;
			
			for (int i = 0; i < vals.size(); i++)
			{
				sym = (SymbolCounter)vals.elementAt(i);
				
				inv.insert(sym.getSymbol(), new SymbolCounter(key, sym.value()));
			}
		}
		
		return inv;
	}
	
	
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//
	// Scale the elements in a map
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//

	public void scaleElements(int mult, int div)
	{
		String key  = null;
		
		Vector vals = null;
		
		SymbolCounter sym = null;
		
			
		for (Enumeration keys = this.keys(); keys.hasMoreElements(); )
		{
			key = (String)keys.nextElement();
			
			vals = this.get(key);
			
			if (vals == null || vals.size() == 0) continue;
			
			for (int i = 0; i < vals.size(); i++)
			{
				sym = (SymbolCounter)vals.elementAt(i);
				
				sym.set((sym.value()*mult)/div);
			}
		}
	}

	
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//
	// Return a map containing just the prefixes of the current elements
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//

	public SymbolMap extractPrefixMap()
	{
		return extractMapFrom((char)':', 0);
	}
	
	
	public SymbolMap extractBodyMap()
	{
		return extractMapFrom((char)':', 1);
	}

	
	public SymbolMap extractModifierMap()
	{
		return extractMapFrom((char)'_', 0);
	}
	
	
	public SymbolMap extractHeadMap()
	{
		return extractMapFrom((char)'_', 1);
	}

	
	private SymbolMap extractMapFrom(char divide, int offset)
	{
		String key  = null, val = null;
		
		Vector vals = null;
		
		SymbolCounter sym = null;
		
		SymbolMap newMap = new SymbolMap("");
		
		int div = 0;
			
		for (Enumeration keys = this.keys(); keys.hasMoreElements(); )
		{
			key = (String)keys.nextElement();
			
			vals = this.get(key);
			
			if (vals == null || vals.size() == 0) continue;
			
			for (int i = 0; i < vals.size(); i++)
			{
				sym = (SymbolCounter)vals.elementAt(i);
				
				val = sym.getSymbol();
				
				div = val.indexOf((char)divide);
				
				if (div <= 0) continue;
				
				if (offset == 0)
					newMap.put(key, val.substring(0, div));
				else
					newMap.put(key, val.substring(div+1));
			}
		}
		
		return newMap;
	}

	
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//
	// Return a symbol map that represents the inverse mapping to this one
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//

	public void sortMap()
	{
		String key  = null;
		
		Vector vals = null;
		
		SymbolCounter sym = null;
		
			
		for (Enumeration keys = this.keys(); keys.hasMoreElements(); )
		{
			key = (String)keys.nextElement();
			
			vals = this.get(key);
			
			if (vals == null || vals.size() == 0) continue;
			
			getSorted(key);
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
			if (contains(key2, ((SymbolCounter)values.elementAt(i)).getSymbol()))
				return true;
		
		return false;
	}
	
	public int getOverlappingExtent(String key1, String key2)
	{
		Vector values = get(key1);
		
		if (values == null || get(key2) == null) return 0;
		
		int count = 0;
		
		for (int i = 0; i < values.size(); i++)
			if (contains(key2, ((SymbolCounter)values.elementAt(i)).getSymbol()))
				count++;
		
		return count;
	}
	
	
	public Vector getOverlappingValues(String key1, String key2)
	{
		Vector values = get(key1), overlap = null;
		
		if (values == null || get(key2) == null) return null;
		
		for (int i = 0; i < values.size(); i++)
			if (contains(key2, ((SymbolCounter)values.elementAt(i)).getSymbol()))
			{
				if (overlap == null)
					overlap = new Vector();
				
				overlap.add(values.elementAt(i));
			}
		
		return overlap;
	}
	
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//
	// Make sure that counts in the mapping are symmetric (same in both directions)
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//

	public void makeSymmetric()
	{
		Vector keys = getKeyList(), values = null;
		
		String key = null;
		
		SymbolCounter value = null;
		
		
		for (int i = 0; i < keys.size(); i++)
		{
			key = (String)keys.elementAt(i);
			
			values = get(key);
			
			if (values == null) continue;
			
			for (int j = 0; j < values.size(); j++)
			{
				value = (SymbolCounter)values.elementAt(j);
				
				putMax(value.getSymbol(), key, value.value());
			}
		}
	}
	
	
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//
	// Sort the elements of a mapping
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//

	public Vector getSorted(String key)
	{
		return getSorted(get(key));
	}
	

	public Vector getSorted(String key, int start)
	{
		return getSorted(get(key), start);
	}

	
	public static Vector getSorted(Vector entries)
	{	
		return getSorted(entries, 0);
	}
	
	
	public static Vector getSorted(Vector entries, int start)
	{	
		if (entries == null || entries.size() < 2)
			return entries;
		
		SymbolCounter curr = null, entry = null, best = null;
		
		int where = 0;
		
		for (int i = start; i < entries.size()-1; i++)
		{
			entry  = (SymbolCounter)entries.elementAt(i);
			best   = entry;
			where  = i;
			
			for (int j = i+1; j < entries.size(); j++)
			{
				curr = (SymbolCounter)entries.elementAt(j);
				
				if (best == null || curr.value() > best.value())
				{
					best  = curr;
					where = j;
				}
			}
			
			entries.setElementAt(best, i);
			entries.setElementAt(entry, where);
		}
		
		return entries;
	}
		
	
	
	public static Vector getBinSorted(Vector entries)
	{	
		if (entries == null || entries.size() < 2)
			return entries;
		
		int max = 0, min = 0;
		
		SymbolCounter curr = null, entry = null;
		
		// establish range of bins
		
		for (int i = 0; i < entries.size(); i++)
		{
			entry  = (SymbolCounter)entries.elementAt(i);
			
			if (entry.value() > max)
				max = entry.value();
			
			if (entry.value() < min)
				min = entry.value();
		}
		
		Vector[] bins = new Vector[max - min + 1];
		

		// now fill the bins
		
		int where = 0;
		
		for (int i = 0; i < entries.size(); i++)
		{
			entry  = (SymbolCounter)entries.elementAt(i);
			
			where = entry.value() - min;
			
			if (bins[where] == null)
				bins[where] = new Vector();
			
			bins[where].add(entry);
		}
		
		Vector sorted = new Vector();
		

		// now empty the bins in descending order
		
		for (int i = max; i >= min; i--)
		{
			where = i - min;
			
			if (bins[where] == null) continue;
			
			for (int j = 0; j < bins[where].size(); j++)
				sorted.add(bins[where].elementAt(j));
		}
		
		return sorted;
	}
	
	
	public Vector getNormalized(String key, int range)
	{
		return SymbolMap.getNormalized(get(key), range);
	}
	
	
	
	public static Vector getNormalized(Vector entries, int range)
	{	
		if (entries == null || entries.size() == 0)
			return entries;
		
		SymbolCounter curr = null, entry = null, alt = null;
		
		int total = 0;
		
		Vector norm = new Vector();
		
		for (int i = 0; i < entries.size(); i++)
		{
			entry  = (SymbolCounter)entries.elementAt(i);
			
			total += entry.value();
		}
		
		for (int i = 0; i < entries.size(); i++)
		{
			entry  = (SymbolCounter)entries.elementAt(i);
			
			alt    = new SymbolCounter(entry.getSymbol(), (entry.value()*range)/total);
			
			norm.add(alt);
		}
		
		return norm;
	}
	
	//--------------------------------------------------------------------//
	//--------------------------------------------------------------------//
	// Measure the overlap between two tables
	//--------------------------------------------------------------------//
	//--------------------------------------------------------------------//

	public double measureOverlapWith(SymbolMap other)
	{
		int total = 0, common = 0;
		
		Vector keys = getKeyList();
		
		if (keys == null) return 0.0;
		
		String key = null;
		
		Vector values = null;
		
		for (int i = 0; i < keys.size(); i++)
		{
			key = (String)keys.elementAt(i);
			
			values = get(key);
			
			if (values == null) continue;
			
			for (int j = 0; j < values.size(); j++)
			{
				total++;
				
				if (other.getCount(key, ((SymbolCounter)values.elementAt(j)).getSymbol()) > 0)
					common++;
			}
				
		}
		
		if (total == 0)
			return 0.0;
		else
			return (common * 1.0)/(total * 1.0);
	}
	
	//--------------------------------------------------------------------//
	//--------------------------------------------------------------------//
	// Load a text file into memory as structured symbol map
	//--------------------------------------------------------------------//
	//--------------------------------------------------------------------//

	public void loadMap(String filename)
	{
		loadMap(filename, (Hashtable)null);
	}
	
	
	public void loadMap(String filename, String filter, String fileType)
	{
		loadMap(filename, null, filter, fileType);
	}

	
	public void loadMap(String filename, String filter, String fileType, boolean reverse)
	{
		loadMap(filename, null, filter, fileType, reverse);
	}

	
	public void loadMap(String filename, String filter)
	{
		loadMap(filename, null, filter, null);
	}

	
	public void loadMap(String filename, Hashtable ignore)
	{
		FileInputStream input;

		try {
		    input = new FileInputStream(filename);
		    
		    loadMap(filename, input, ignore, null, null, false);
		}
		catch (IOException e)
		{
			System.out.println("Cannot find/load map file: " + filename);
			
			e.printStackTrace();
		}
	}
	
		
	public void loadMap(String filename, Hashtable ignore, String filter)
	{
		loadMap(filename, ignore, filter, null);
	}
	
	
	public void loadMap(String filename, Hashtable ignore, String filter, String fileType)
	{
		loadMap(filename, ignore, filter, fileType, false);
	}
	
	
	public void loadMap(String filename, Hashtable ignore, String filter, String fileType, boolean reverse)
	{
		FileInputStream input;

		try {
		    input = new FileInputStream(filename);
		    
		    loadMap(filename, input, ignore, filter, fileType, reverse);
		}
		catch (IOException e)
		{
			System.out.println("Cannot find/load map file: " + filename);
			
			e.printStackTrace();
		}
	}

	
	

	private void loadMap(String comment, InputStream stream, Hashtable ignore, String filter, String fileType, boolean reverse)
	{
		String line = null;
		
		int total = 0, uniq = 0;
		
		// Hashtable range = new Hashtable();
		
		boolean freepass = filter != null && filter.indexOf((int)' ') >= 0;
		
		try {
			InputStreamReader input = null;
			
			if (fileType == null)
				input = new InputStreamReader(stream);
			else
				input = new InputStreamReader(stream, "Unicode");
			
			BufferedReader buffer = new BufferedReader(input);
			
			while ( (line = buffer.readLine()) != null)  // Read a line at a time
			{				
				//System.out.println(line);
				
				if (line.startsWith("#")) continue;
				
				int tstart = -1, nstart = -1;
				
				String token = null, headword = null, number = null;
				
				char curr = ' ', prev = ' ';
				
				Vector items = null;
				
				boolean empty = true;
				
				for (int i = 0; i < line.length(); i++)
				{
					prev = curr;
					curr = line.charAt(i);
					
					if (curr == ' ')
					{
						if (tstart >= 0 && items == null && headword == null)
						{
							token    = line.substring(tstart, i);
							
							if (domain != null && !domain.contains(token))
								break;
							
							headword = token.intern();
							
							if (!reverse)
							{
								items    = get(headword);
								
								if (items == null)
								{
									items = new Vector();
								
									put(headword, items);
								}
								else
									empty = false;
							}
						}
						
						tstart = i+1;
					}
					else
					if (curr == '[')
						tstart = i+1;
					else
					if (curr == '(')
					{
						token = line.substring(tstart, i);
						nstart= i+1;
					}
					else
					if ((curr == ',' || curr == ']') && prev != ')' && prev != ' ')
					{
						token = line.substring(tstart, i);
						nstart= i+1;
						
						if (domain != null && !domain.contains(token))
							continue;
						
						if (ignore == null || ignore.get(token) == null)
						{
							total += 1;
							
							uniq++;
							
							token = token.intern();
							
							if (!token.startsWith("***") && !token.endsWith("***") && token.length() > 0)
							{
								if (filter == null || filter.indexOf(token.charAt(0)) >= 0 || freepass)
								{
									if (filter != null && filter.indexOf(token.charAt(0)) >= 0)
										token = token.substring(1).intern();								
								
									if (reverse)
									{
										this.put(token, headword);
									}
									else
									{
										if (items == null)
											System.out.println(line);
										
										/*
										if (headword.equals(token) || 
												Character.isUpperCase(headword.charAt(0)) != Character.isUpperCase(token.charAt(0)))
											continue;
										*/
										
										if (empty)
											items.add(new SymbolCounter(token, 1));
										else
											this.insert(headword, new SymbolCounter(token, 1));
									}
								}
							}
						}
					}					
					else
					if (curr == ')')
					{
						if (domain != null && !domain.contains(token))
							continue;
						
						number = line.substring(nstart, i);
						
						if (ignore == null || ignore.get(token) == null)
						{
							int count = Integer.parseInt(number);
							
							total += count;
							
							uniq++;
							
							token = token.intern();
							
							// if (token.equals(headword)) continue;
							
							if (!token.startsWith("***") && !token.endsWith("***") && token.length() > 0)
							{
								if (filter == null || filter.indexOf(token.charAt(0)) >= 0 || freepass)
								{
									if (filter != null && filter.indexOf(token.charAt(0)) >= 0)
										token = token.substring(1).intern();
									
									
									if (reverse)
									{
										this.put(token, headword, count);
									}
									else
									{
										if (items == null)
											System.out.println(line);
										
										/*
										if (headword.equals(token) || 
												Character.isUpperCase(headword.charAt(0)) != Character.isUpperCase(token.charAt(0)))
											continue;
										*/
										
										if (empty)
											items.add(new SymbolCounter(token, count));
										else
											this.insert(headword, new SymbolCounter(token, count));
									}
								}
							}
						}
					}
				}
					
			}

		   buffer.close();  // close connection to the data source

		   /*
		   System.out.println("Num keys: " + getKeyList().size());
		   System.out.println("Total values: " + total);
		   System.out.println("Unique values: " + uniq);
		   System.out.println("Size of range: " + range.size());
	       */
		}
		catch (Exception e) 
		{
			System.out.println("Exception while reading map file: " + e.toString() + " in line: " + line);
				
			e.printStackTrace();
		}
	}
	
	
	//-------------------------------------------------------------//
	//-------------------------------------------------------------//
	// Change the counts of symbol entries to reflect their frequency
	// across all keys in the map
	//-------------------------------------------------------------//
	//-------------------------------------------------------------//

	public void frequentizeCounts()
	{
		CountTable freqs = new CountTable("entries to counts");
		
		Vector vals = null;
		
		SymbolCounter sym = null;
		
		String key = null;
		
		for (int pass = 0; pass < 2; pass++)
		{
			for (Enumeration keys = this.keys(); keys.hasMoreElements(); )
			{
				key = (String)keys.nextElement();
				
				vals = this.get(key);
				
				if (vals == null || vals.size() == 0) continue;
				
				for (int i = 0; i < vals.size(); i++)
				{
					sym = (SymbolCounter)vals.elementAt(i);
					
					if (pass == 0)
						freqs.put(sym.getSymbol());
					else
						sym.set(freqs.getCount(sym.getSymbol()));
				}
			}
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
			
			elements = this.getSorted(key);
			
			elements.setSize(preserve);
		}
	}

	
	//-------------------------------------------------------------//
	//-------------------------------------------------------------//
	// Prune the mapping by removing excess terms
	//-------------------------------------------------------------//
	//-------------------------------------------------------------//

	public void pruneMapping(int min)
	{
		String key = null;
		Vector elements = null, keys = getKeyList();
		
		for (int i = 0; i < keys.size(); i++)
		{
			key = (String)keys.elementAt(i);
			
			elements = this.get(key);
			
			if (elements == null || elements.size() < 1) continue;
			
			elements = this.getSorted(key);
		
			if (elements != null)
			{
				SymbolCounter item = null;
				
				for (int j = elements.size()-1; j >= 0; j--)
				{
					item = (SymbolCounter)elements.elementAt(j);
					
					if (item != null && item.value() < min)
						elements.remove(item);
				}
				
				override(key, elements);
			}
		}
	}		

	
	//-------------------------------------------------------------//
	//-------------------------------------------------------------//
	// Prune the mapping by removing excess terms
	//-------------------------------------------------------------//
	//-------------------------------------------------------------//

	public void pruneElementsNotIn(BucketTable filter)
	{
		Vector keys = getKeyList(), entries = null;
		
		if (keys == null) return;
		
		String key = null;
		
		SymbolCounter entry = null;
		
		for (int i = 0; i < keys.size(); i++)
		{
			key = (String)keys.elementAt(i);
			
			entries = get(key);
			
			if (entries == null || entries.size() == 0)
				continue;
			
			for (int j = entries.size()-1; j >= 0; j--)
			{
				entry = (SymbolCounter)entries.elementAt(j);
				
				if (filter.get(entry.getSymbol()) == null)
					entries.remove(j);
			}
		}
	}
	
	//-------------------------------------------------------------//
	//-------------------------------------------------------------//
	// Save a map of the given selection of terms 
	//-------------------------------------------------------------//
	//-------------------------------------------------------------//


	public void saveMapping(String fname)
	{
		saveMapping(fname, this, this.getKeyList());
	}
	
	
	
	public void saveMapping(String fname, String fileType)
	{
		saveMapping(fname, this, this.getKeyList(), fileType);
	}

	
	public static void saveMapping(String fname, SymbolMap map, Vector keys)
	{
		saveMapping(fname, map, keys, null);
	}
	
	
	public static void saveMapping(String fname, SymbolMap map, Vector keys, String fileType)
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
				
				elements = map.getSorted(key);
			
				output.write(i + ". " + BucketTable.solidifyKey(key) + " " + elements + "\n");
			}
			
			output.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}		
	
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	//  Calculate modulation counts between modifiers and heads of related compounds
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	public SymbolMap getModulationMap()
	{
		SymbolMap  modulation  = new SymbolMap("heads and modifiers to aligned counterparts");
		
		Vector compounds = getKeyList(), others = null;
		
		String compound1 = null, compound2 = null, mod1 = null, mod2 = null, head1 = null, head2 = null;
		
		int dash = 0;
		
		for (int i = 0; i < compounds.size(); i++)
		{
			compound1 = (String)compounds.elementAt(i);
			
			dash      = compound1.lastIndexOf((int)'_');
			
			if (dash <= 0) continue;
			
			mod1      = compound1.substring(0, dash);
			head1     = compound1.substring(dash+1);
			
			others    = get(compound1);
			
			if (others == null) continue;
			
			for (int j = 0; j < others.size(); j++)
			{
				compound2 = ((SymbolCounter)others.elementAt(j)).getSymbol();
				
				dash      = compound2.lastIndexOf((int)'_');
				
				if (dash <= 0) continue;
				
				mod2      = compound2.substring(0, dash);
				head2     = compound2.substring(dash+1);

				if (!mod1.equals(mod2))
				{
					modulation.put(mod1, mod2);
					modulation.put(mod2, mod1);
				}
				
				if (!head1.equals(head2))
				{
					modulation.put(head1, head2);
					modulation.put(head2, head1);
				}
			}
		}
		
		return modulation;
	}
	
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	//  Convert frequency scores to 0...100 similarity scores
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	public SymbolMap convertToSimilarityMap(int baseline)
	{
		SymbolMap  sims  = new SymbolMap("mapping of terms to other terms with frequency counts --> sim scores");
		
		Vector keys = getKeyList(), others = null;
		
		String term1 = null, term2 = null;
		
		int freq = 0, simScore = 0;
		
		for (int i = 0; i < keys.size(); i++)
		{
			term1 = (String)keys.elementAt(i);
			
			others    = get(term1);
			
			if (others == null) continue;
			
			for (int j = 0; j < others.size(); j++)
			{
				term2 = ((SymbolCounter)others.elementAt(j)).getSymbol();
				
				if (term1.equals(term2)) continue;
				
				if (term2.startsWith("***") || term2.endsWith("***"))
					continue;

				freq  = ((SymbolCounter)others.elementAt(j)).value();
				
				if (freq == 0) continue;
				
				simScore = (100*freq)/(freq + baseline);
				
				sims.putMax(term1, term2, simScore);
				sims.putMax(term2, term1, simScore);
			}
		}
		
		sims.sortMap();
		
		return sims;
	}
	

	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	//  Create a map of terms to modifiers
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	public SymbolMap createModifierMap()
	{
		SymbolMap  modmap  = new SymbolMap("terms to modifiers");
		
		Vector terms = getKeyList(), others = null;
		
		String term1 = null, term2 = null, mod = null, head = null;
		
		int dash = 0;
		
		for (int i = 0; i < terms.size(); i++)
		{
			term1 = (String)terms.elementAt(i);
			
			dash      = term1.lastIndexOf((int)'_');
			
			if (dash > 0) continue;
			
			others    = get(term1);
			
			if (others == null) continue;
			
			for (int j = 0; j < others.size(); j++)
			{
				term2 = ((SymbolCounter)others.elementAt(j)).getSymbol();
				
				dash      = term2.lastIndexOf((int)'_');
				
				if (dash <= 0) continue;
				
				mod      = term2.substring(0, dash);
				head     = term2.substring(dash+1);
				
				modmap.put(term1, mod);
			}
		}
		
		return modmap;
	}
	
	
	//-------------------------------------------------------------------//
	//-------------------------------------------------------------------//
	// Save a SymbolMap as a flat file, one line per key
	//-------------------------------------------------------------------//
	//-------------------------------------------------------------------//

	public void saveMapAsFlatFile(String fname)
	{
		saveMapAsFlatFile(fname, null);
	}

	public void saveMapAsFlatFile(String dataFile, String indexFile)
	{
		try {
			FileOutputStream   file   = new FileOutputStream(dataFile);
			
			OutputStreamWriter output = new OutputStreamWriter(file);
			
			OutputStreamWriter index  = null;
			
			if (indexFile != null)
				index = new OutputStreamWriter(new FileOutputStream(indexFile));
			
			String key = null;
			
			Vector elements = null, keys = getKeyList();
			
			int count = 0;
			
			SymbolCounter sym = null;
			
			for (int i = 0; i < keys.size(); i++)
			{
				key = (String)keys.elementAt(i);
				
				elements = get(key);
								
				if (elements == null || elements.size() < 1) continue;
				
				elements = getSorted(key);
				
				if (elements == null || elements.size() == 0) continue;
				
				output.flush();
				
				if (indexFile != null)
					index.write(key + "\t" + file.getChannel().position() + "\n");
				
				output.write(key);
				
				for (int j = 0; j < elements.size(); j++)
				{
					sym = (SymbolCounter)elements.elementAt(j);
					
					output.write("\t" + sym.getSymbol() + "\t" + sym.value());
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
	
			
	//-------------------------------------------------------------------//
	//-------------------------------------------------------------------//
	// Main routine for test purposes
	//-------------------------------------------------------------------//
	//-------------------------------------------------------------------//

	public static void main(String[] args)
	{
		SymbolMap rawRex = new SymbolMap("noun to noun coordinates");
		
		rawRex.loadMap("/Users/tonyveale/Desktop/wordsim353/Rex-pop matrix.idx"); 
		
		rawRex.sortKeyListAlpha();
		
		//rawRex.sortMap();
		
		rawRex.saveMapping("/Users/tonyveale/Desktop/wordsim353/Rex-pop matrix.idx"); 
				
		/*
		SymbolMap adjToAdj = new SymbolMap("adjectives to related adjectives");
		
		adjToAdj.loadMap("/Users/tonyveale/Desktop/Lexical Resources/Categories/Fingerprints/AS_pred_pred symmetric.idx");
		
		BucketTable stereoModel = new BucketTable("properties to stereotypes");
		
		stereoModel.loadTable("/Users/tonyveale/Desktop/Lexical Resources/Moods/stereotype properties.idx");
		
		BucketTable alliterations = new BucketTable("stereos to alliterative properties");
		
		Vector adjs = adjToAdj.getKeyList(), stereos = null, others = null;
		
		String adj = null, stereo = null, otherAdj = null;
		
		SymbolCounter other = null;
		
		for (int i = 0; i < adjs.size(); i++)
		{
			adj = (String)adjs.elementAt(i);
			
			stereos = stereoModel.get(adj);
			
			if (stereos == null) continue;
			
			for (int j = 0; j < stereos.size(); j++)
			{
				stereo = (String)stereos.elementAt(j);
				
				if (stereo.charAt(0) != adj.charAt(0)) continue;
				
				if (stereo.length() > 4 && adj.startsWith(stereo.substring(0, stereo.length()-2)))
					continue;
				
				if (stereo.length() <= 4 && adj.startsWith(stereo.substring(0, stereo.length()-1)))
					continue;
				
				alliterations.put(stereo, adj);
			}
			
			others = adjToAdj.get(adj);
			
			if (others == null) continue;
			
			for (int j = 0; j < others.size(); j++)
			{
				other    = (SymbolCounter)others.elementAt(j);
				
				otherAdj = other.getSymbol();
				
				if (j > 10 || other.value() < 20) break;
				
				for (int k = 0; k < stereos.size(); k++)
				{
					stereo = (String)stereos.elementAt(k);
					
					if (stereo.charAt(0) != otherAdj.charAt(0)) continue;
					
					if (stereo.length() > 4 && otherAdj.startsWith(stereo.substring(0, stereo.length()-2)))
						continue;
					
					if (stereo.length() <= 4 && otherAdj.startsWith(stereo.substring(0, stereo.length()-1)))
						continue;
					
					alliterations.put(stereo, otherAdj);
				}		
			}
		}
		
		alliterations.saveTable("/Users/tonyveale/Desktop/Lexical Resources/Moods/alliterative pairs.idx");
		*/
		
		/*
		try {
			BufferedReader buffer = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
			
			String line = null, fore = null, aft = null;
			StringTokenizer tokens = null;
			
			int sim = 0;
			
			while ( (line = buffer.readLine()) != null)  // Read a line at a time
			{	
				tokens = new StringTokenizer(line, " \t -");
				
				tokens.nextToken();
				
				sim = (int)(100*Double.parseDouble(tokens.nextToken()));
				
				fore = tokens.nextToken();
				aft  = tokens.nextToken();
				
				adjToAdj.putMax(fore, aft, sim);
				adjToAdj.putMax(aft, fore, sim);
			}
		    
			adjToAdj.saveMapping("/Users/tonyveale/Desktop/wordsim353/pairsim map.idx");
			
			buffer.close();
		}
		catch (IOException e)
		{
			System.out.println("Cannot find/load similarity file: " + filename);
			
			e.printStackTrace();
		}
		
		*/
		
	}
	
}
	
	
