package tabular;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;


// Do not load a table fully into memory
// Instead, load an index of the keys into memory, and load the entry for each key on demand from a disk file


public class TableFileIndex 
{
	private Hashtable index 		= new Hashtable();
	private Hashtable extents		= new Hashtable();
	
	Vector	keyList					= new Vector();
	
	private String indexFname		= null;
	private String tableFname		= null;
	
	private boolean flexibleCase 	= false;
	
	private boolean provideCounts	= true;  // are counts stored with table entries (is this a version of a SymbolMap?)
	
	//--------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------//
	//*       Constructors
	//--------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------//
	
	public TableFileIndex(String indexName, String tableName)
	{
		this(indexName, tableName, true);
	}
	
	
	public TableFileIndex(String indexName, String tableName, boolean provideCounts)
	{
		indexFname = indexName;
		tableFname = tableName;
		
		this.provideCounts = provideCounts;
		
		loadIndex(indexFname);
	}

	
	//--------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------//
	//*      Accessors
	//--------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------//

	public boolean providesCounts()
	{
		return provideCounts;
	}
	
	
	public void setFlexibleCase(boolean flexible)
	{
		flexibleCase = flexible;
	}
	
	
	public Vector getKeyList()
	{
		return keyList;
	}
	
	
	public boolean contains(String key)
	{
		return index.get(key) != null;
	}
	
	
	public int getExtent(String key)
	{
		Integer ext = (Integer)extents.get(key);
		
		if (ext == null)
			return 0;
		else
			return ext.intValue();
	}
	
	
	public long getIndexPosition(String key)
	{
		Long offset = (Long)index.get(key);
		
		if (offset == null) 
		{
			if (flexibleCase && Character.isLowerCase(key.charAt(0)))
			{
				key = Character.toUpperCase(key.charAt(0)) + key.substring(1);
				
				offset = (Long)index.get(key);
				
				if (offset != null) return offset.longValue();
			}
			
			return -1;
		}
		else
			return offset.longValue();
	}
	
	
	// Return the line of text associated with a given key
	
	public String getIndexText(String key)
	{
		long offset = getIndexPosition(key);
		
		if (offset < 0) return null;
		
		try {
			InputStreamReader input = new InputStreamReader(new FileInputStream(tableFname));
			
			BufferedReader buffer   = new BufferedReader(input);
			
			input.skip(offset);
			
			String line = buffer.readLine();

		    buffer.close();  // close connection to the data source
		    
		    //System.out.println(indexFname + " / " + tableFname);
		    //System.out.println("\t" + line);
		    
		    return line;
		}
		catch (IOException e) 
		{
			System.out.println("Exception while reading index file: " +  tableFname + "\n " + e.toString());
				 
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	//--------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------//
	//*      GET methods
	//--------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------//
	
	public synchronized Vector get(String key)
	{
		if (provideCounts)
			return getEntriesAndCounts(key);
		else
			return getEntriesOnly(key);
	}
	
	
	
	
	// Return a list of symbols and counts (a vector of SymbolCounter objects, as from a SymbolMap)
	
	// This function mirrors the GET function in SymbolMap, except data for a given key 
	//     is loaded from disk as needed
	
	private String lastKey = null;
	private Vector lastGet = null;
	
	private synchronized Vector getEntriesAndCounts(String key)
	{
		if (key == null) 
			return null;
		
		if (key.equals(lastKey))
			return lastGet;
		
		String line = getIndexText(key);
		
		if (line == null)
		{
			int dash = key.indexOf((int)'_');
			
			if (dash > 0)
				return get(key.substring(dash+1));
			else
				return null;
		}
		
		StringTokenizer tokens = new StringTokenizer(line, "\t", false);
		
		tokens.nextToken();
		
		Vector list = new Vector();
		
		String symbol = null;
		
		int count = 0;
		
		while (tokens.hasMoreTokens())
		{
			symbol = tokens.nextToken();
			count  = Integer.parseInt(tokens.nextToken());
			
			list.add(new SymbolCounter(symbol, count));
		}
		
		lastKey = key;
		lastGet = list;
		
		return list;
	}
	
	
	
	
	private synchronized Vector getEntriesOnly(String key)
	{
		if (key == null) 
			return null;
		
		if (key.equals(lastKey))
			return lastGet;
		
		String line = getIndexText(key);
		
		if (line == null)
		{
			int dash = key.indexOf((int)'_');
			
			if (dash > 0)
				return get(key.substring(dash+1));
			else
				return null;
		}
		
		StringTokenizer tokens = new StringTokenizer(line, "\t", false);
		
		tokens.nextToken();
		
		Vector list = new Vector();
		
		String symbol = null;
		
		int count = 0;
		
		while (tokens.hasMoreTokens())
		{
			symbol = tokens.nextToken();
			
			list.add(symbol);
		}
		
		lastKey = key;
		lastGet = list;
		
		return list;
	}

	//--------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------//
	//*     Determine if a given word is a plural form of a singular-form in the index
	//--------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------//

	public String getIndexedSingularForm(String word)
	{
		if (word.length() < 4) 
			return null;
		
		if (word.equals("dies") && this.contains("die")) return "die";
		if (word.equals("graves") && this.contains("grave")) return "grave";
		if (word.equals("busses") && this.contains("bus")) return "bus";
		if (word.equals("doses") && this.contains("dose")) return "dose";

		if (word.equals("corpses") && this.contains("corpse")) return "corpse";
		if (word.equals("superheroes") && this.contains("superhero")) return "superhero";
		if (word.equals("supervillains") && this.contains("supervillain")) return "supervillain";
		
		if (word.endsWith("ians") && this.contains(word.substring(0, word.length()-1)))
			return word.substring(0, word.length()-1);
		
		if (word.endsWith("us") && this.contains(word))
			return word;

		if (word.endsWith("as") && this.contains(word))
			return word;
		
		if (word.endsWith("s") 
				&& Character.isUpperCase(word.charAt(0)) 
				&& Character.isUpperCase(word.charAt(word.length()-2))
				&& this.contains(word.substring(0, word.length()-1)))
			return word.substring(0, word.length()-1);  // e.g., PDAs
		
		if (word.equals("uses") && this.contains("use")) return "use";
		
		if (word.endsWith("eses") && word.length() > 5
				&& this.contains(word.substring(0, word.length()-2) + "is"))
			return word.substring(0, word.length()-2) + "is";
		else
		if (word.endsWith("ves") && word.length() > 4
				&& this.contains(word.substring(0, word.length()-3) + "f"))
		{
			if (word.endsWith("caves"))
				return "cave";
			
			return word.substring(0, word.length()-3) + "f";
		}
		else
		if (word.endsWith("ves") && word.length() > 4
				&& this.contains(word.substring(0, word.length()-3) + "fe"))
		{
			if (word.endsWith("caves"))
				return "cave";
			
			return word.substring(0, word.length()-3) + "fe";
		}
		else
		if ((word.endsWith("hes") || word.endsWith("ses") || word.endsWith("xes") || word.endsWith("zes"))
				&& this.contains(word.substring(0, word.length()-1)))
		{
			return word.substring(0, word.length()-1);
		}
		else
		if ((word.endsWith("hes") || word.endsWith("ses") || word.endsWith("xes")  || word.endsWith("oes") || word.endsWith("zes"))
				&& this.contains(word.substring(0, word.length()-2)))
		{
			return word.substring(0, word.length()-2);
		}
		else
		if (word.endsWith("ies") && this.contains(word.substring(0, word.length()-3) + "y"))
		{
			return word.substring(0, word.length()-3) + "y";
		}
		else
		if (word.endsWith("men") && this.contains(word.substring(0, word.length()-3) + "man"))
		{
			return word.substring(0, word.length()-3) + "man";
		}
		else
		if (word.endsWith("s") && this.contains(word.substring(0, word.length()-1)))
		{
			return word.substring(0, word.length()-1);
		}
		else
			return null;		
	}
	
	//--------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------//
	//*       Load the index into memory
	//--------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------//

	private void loadIndex(String filename)
	{
		String line = null, key = null, prevKey = null;
		
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
			
			int gap = 0;
			
			long offset = 0, prevOffet = 0;
			
			while ( (line = input.readLine()) != null)  // Read a line at a time
			{
				gap = line.indexOf((int)'\t');
				
				if (gap < 1) continue;
				
				prevOffet 	= offset;
				prevKey   	= key;
				
				key     	= line.substring(0, gap).intern();
				offset  	= Long.parseLong(line.substring(gap+1));
				
				index.put(key, new Long(offset));
				
				if (prevKey != null)
					extents.put(prevKey, new Integer((int)(offset - prevOffet)));
				
				keyList.add(key);
			}

		   input.close();  // close connection to the data source
		}
		catch (Exception e) 
		{
			System.out.println("Exception while reading index file: " +  filename + "\n " + e.toString());
			
			System.out.println("***" + line);
				 
			e.printStackTrace();
		}
		
	}
	
	
	//--------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------//
	//*       Application test stub
	//--------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------//

	public static void main(String[] args)
	{
		String dir 				= "/Users/tonyveale/Desktop/Lexical Resources/Moods/";
		
		String posNounDataFile	=  dir + "bigrams pleasantness.idx";
		String negNounDataFile	=  dir + "bigrams unpleasantness.idx";

		String posNounIndexFile	=  dir + "bigrams index pleasantness.idx"; 		
		String negNounIndexFile	=  dir + "bigrams index unpleasantness.idx"; 

		String posAdjDataFile 	=  dir + "positive map indexed.idx";
		String negAdjDataFile 	=  dir + "negative map indexed.idx";
		
		String posAdjIndexFile 	=  dir + "positive index.idx";
		String negAdjIndexFile 	=  dir + "negative index.idx";
		
		String coordDataFile	= dir + "attested combinations indexed.idx";
		String coordIndexFile	= dir + "attested combinations index.idx";
		
		String possDataFile		= dir + "possessions indexed.idx";
		String possIndexFile	= dir + "possessions index.idx";
		
		String apposDataFile	= dir + "appositions indexed.idx";
		String apposIndexFile	= dir + "appositions index.idx";

		String metaDataFile		= dir + "attested metaphors indexed.idx";
		String metaIndexFile	= dir + "attested metaphors index.idx";
		
		if (dir != null)
		{
			SymbolMap xStereos = new SymbolMap("stereo nouns to extended properties");
			
			xStereos.loadMap(dir + "stereotype extended properties.idx");
			
			xStereos.saveMapAsFlatFile(dir + "extended properties indexed.idx", dir + "extended properties index.idx");
			
			System.exit(0);
		}

		System.out.print("Loading indexes ... ");
		
		TableFileIndex posNounIndexer 	= new TableFileIndex(posNounIndexFile, posNounDataFile);
		TableFileIndex negNounIndexer 	= new TableFileIndex(negNounIndexFile, negNounDataFile);
		TableFileIndex coordIndexer		= new TableFileIndex(coordIndexFile, coordDataFile);
		TableFileIndex possIndexer		= new TableFileIndex(possIndexFile, possDataFile);
		TableFileIndex apposIndexer		= new TableFileIndex(apposIndexFile, apposDataFile);
		TableFileIndex posIndexer		= new TableFileIndex(posAdjIndexFile, posAdjDataFile);
		TableFileIndex negIndexer		= new TableFileIndex(negAdjIndexFile, negAdjDataFile);
		TableFileIndex metaIndexer		= new TableFileIndex(metaIndexFile, metaDataFile);
		
		System.out.println("Loaded.");
		
		String test = "devil";
		
		System.out.println("\n\nNegative Adj data for <" + test + ">:\n\n" + negNounIndexer.get(test));
		
		System.out.println("\n\nPositive Adj data for <" + test + ">:\n\n" + posNounIndexer.get(test));
		
		System.out.println("\n\nCoordinate data for <" + test + ">:\n\n" + coordIndexer.get(test));

		System.out.println("\n\nNegative Adj data for <" + test + ">:\n\n" + negIndexer.get(test));

		System.out.println("\n\nPositive Adj data for <" + test + ">:\n\n" + posIndexer.get(test));
		
		System.out.println("\n\n Attested metaphors for <" + test + ">:\n\n" + metaIndexer.get(test));

		System.out.println("\n\n Possessions of <" + test + ">:\n\n" + possIndexer.get(test));
		
		System.out.println("\n\n Appositions of <" + test + ">:\n\n" + apposIndexer.get(test));	
	}
}
