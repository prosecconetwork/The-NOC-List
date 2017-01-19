
/*  (c)  Tony Veale, 2005, 2006 */

package tabular;

// Maintain a list of words loaded from a file

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Hashtable;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;



// Store a list of words of a particular category (such as nouns or verbs from WordNet)
 
public class WordTable  
{		
	private static Random RND			=   new Random();
	
	private Hashtable rootTable			=	new Hashtable();
	
	private Hashtable modifiers 		= 	new Hashtable();
	private Hashtable heads				=	new Hashtable();
	
	private Hashtable properTerms 		= new Hashtable();
	private Hashtable uppercaseTerms 	= new Hashtable();
	private Hashtable lowercaseTerms 	= new Hashtable();
	
	private Vector allWords				=	new Vector();
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	//  Constructors
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	
	public WordTable()
	{
	}
	
	public WordTable(String fname)
	{
		loadWordList(fname);
	}


	
	public WordTable(Vector senses)
	{
		String sense = null, lex = null;
		
		for (int i = 0; i < senses.size(); i++)
		{
			sense = (String)senses.elementAt(i);
			
			if (sense == null) continue;

			int dot = sense.lastIndexOf((int)'.');
			
			if (dot > 0)
				lex = sense.substring(0, dot);
			else
				lex = sense;
					
			if (rootTable.get(lex) == null)
				allWords.add(lex);
			
			rootTable.put(lex, sense);
		}
	}
	
	
	//-----------------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------------//
	//         Accessors
	//-----------------------------------------------------------------------------------//
	//----------------------------------------------------------------------------------//	

	public Vector getAllWords()
	{
		return allWords;
	}
	
	
	public String getRandomWord()
	{
		return (String)allWords.elementAt(RND.nextInt(allWords.size()));
	}
	
	public Hashtable getAllWordsTable()
	{
		return rootTable;
	}
	
	
	public boolean canBeProper(String term)
	{
		return properTerms.get(term) != null;
	}
	
	
	public boolean shouldBeProper(String term)
	{
		return properTerms.get(term) != null && lowercaseTerms.get(term) == null;
	}

	public boolean canBeUpperCase(String term)
	{
		return uppercaseTerms.get(term) != null;
	}

	
	public String getProperVariant(String term)
	{
		return (String)properTerms.get(term);
	}

	
	public void removeRoot(String word)
	{
		rootTable.remove(word);
	}
	
	
	
	public void addRoot(String word)
	{
		if (!rootTable.contains(word))
			allWords.add(word);
		
		rootTable.put(word, word);
	}
	
	
	
	public boolean containsRoot(String word)
	{
		return rootTable.get(word) != null;
	}
	
	
	public String getRootForm(String word)
	{
		if (containsRoot(word))
			return word;
		
		if (word.startsWith("'"))
		{
			if (containsRoot(word.substring(1)))
				return word.substring(1);
		}
		
		if (word.endsWith("ies"))
		{
			if (containsRoot(word.substring(0, word.length()-3) + "y"))
				return word.substring(0, word.length()-3) + "y";
		}
		
		if (word.endsWith("ves"))
		{
			if (containsRoot(word.substring(0, word.length()-3) + "f"))
				return word.substring(0, word.length()-3) + "f";
		}

		if (word.endsWith("en"))
		{
			if (containsRoot(word.substring(0, word.length()-2) + "an"))
				return word.substring(0, word.length()-2) + "an";
		}
		
		
		if (word.endsWith("ier"))
		{
			if (containsRoot(word.substring(0, word.length()-3) + "y"))
					return word.substring(0, word.length()-3) + "y";
		}
		else
		if (word.endsWith("er"))
		{
			if (containsRoot(word.substring(0, word.length()-2)))
				return word.substring(0, word.length()-2);
	
			if (containsRoot(word.substring(0, word.length()-2) + "e"))
					return word.substring(0, word.length()-2) + "e";
		}

		
		if (word.endsWith("ing"))
		{
			String stem = word.substring(0, word.length()-3);
			
			if (containsRoot(stem))
				return stem;
			
			if ((stem.endsWith("bb") || stem.endsWith("dd")  || stem.endsWith("gg")  || stem.endsWith("ll")
					 || stem.endsWith("mm") || stem.endsWith("nn")  || stem.endsWith("pp") || stem.endsWith("rr")  
					 || stem.endsWith("tt"))
				&& containsRoot(stem.substring(0, stem.length()-1)))
				return stem.substring(0, stem.length()-1);
		}

		
		if (word.endsWith("ied"))
		{
			if (containsRoot(word.substring(0, word.length()-3) + "y"))
				return word.substring(0, word.length()-3) + "y";
			else
				return null;
		}
		
		if (word.endsWith("ed"))
		{
			if (containsRoot(word.substring(0, word.length()-1)))
				return word.substring(0, word.length()-1);
			else
			if (containsRoot(word.substring(0, word.length()-2)))
				return word.substring(0, word.length()-2);
			else
				return null;
		}
		
		if (word.endsWith("s"))
		{
			if (containsRoot(word.substring(0, word.length()-1)))
				return word.substring(0, word.length()-1);
			else
			if (word.endsWith("hes") || word.endsWith("xes") || 
				word.endsWith("ses") || word.endsWith("zes"))
			{
				if (containsRoot(word.substring(0, word.length()-2)))
					return word.substring(0, word.length()-2);
			}
		}
		
		return null;
	}
	
	
	
	public String getPartOfSpeech(String word)
	{
		if (containsRoot(word))
			return "SINGULAR";
		
		
		if (word.startsWith("'"))
		{
			if (containsRoot(word.substring(1)))
				return "INF";
		}
		
		if (word.endsWith("ies"))
		{
			if (containsRoot(word.substring(0, word.length()-3) + "y"))
				return "PLURAL";
			else
				return null;
		}
		
		
		if (word.endsWith("ied"))
		{
			if (containsRoot(word.substring(0, word.length()-3) + "y"))
				return "PAST";
			else
				return null;
		}
		

		if (word.endsWith("ed"))
		{
			if (containsRoot(word.substring(0, word.length()-1)))
				return "PAST";
			else
			if (containsRoot(word.substring(0, word.length()-2)))
				return "PAST";
			else	
				return null;
		}

		
		if (word.endsWith("ing"))
		{
			if (containsRoot(word.substring(0, word.length()-3)))
				return "PRESENT";
			else
			if (containsRoot(word.substring(0, word.length()-3) + "e"))
				return "PRESENT";
			else
				return null;
		}
		
		if (word.endsWith("en"))
		{
			if (containsRoot(word.substring(0, word.length()-2) + "an"))
				return "PLURAL";
			else
				return null;
		}
		
		
		
		if (word.endsWith("s"))
		{
			if (containsRoot(word.substring(0, word.length()-1)))
				return "PLURAL";
			else
			if (word.endsWith("hes") || word.endsWith("xes") || 
				word.endsWith("ses") || word.endsWith("zes"))
			{
				if (containsRoot(word.substring(0, word.length()-2)))
					return "PLURAL";
			}
		}
		
		return null;
	}
	
	
	public boolean isModifier(String term)
	{
		return modifiers.get(term) != null;
	}
	
	
	public boolean isHead(String term)
	{
		return heads.get(term) != null;
	}

	//-----------------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------------//
	//          Normalize / Regularize  irregular plurals
	//-----------------------------------------------------------------------------------//
	//----------------------------------------------------------------------------------//	

	public String getNormalizedPlural(String word)
	{
		if (word == null) return null;
		
		if (word.equals("mice")) return "mouses";
		if (word.equals("geese")) return "gooses";
		if (word.equals("children")) return "childs";
		if (word.equals("women")) return "woman";
		if (word.equals("men")) return "man";
		if (word.equals("nuclei")) return "nucleuses";
		if (word.equals("millenia")) return "millenium";
		if (word.equals("radii")) return "radiuses";

		return word;
	}
	
	
	//-----------------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------------//
	//          Load a set of words from a Princeton-format WordNet file
	//-----------------------------------------------------------------------------------//
	//----------------------------------------------------------------------------------//	
	
	public void loadPrincetonWordList(String filename)
	{
		loadPrincetonWordList(filename, false, null);
	}
	
	
	public void loadPrincetonWordList(String infile, String outfile)
	{
		loadPrincetonWordList(infile, false, outfile);
	}
	
	
	
	public void loadPrincetonWordList(String filename, boolean splits, String outfile)
	{
		FileInputStream input;

		try {
		    input = new FileInputStream(filename);
		    
		    loadPrincetonWordList(input, splits, outfile);
		}
		catch (IOException e)
		{
			System.out.println("Cannot find/load Princeton WordNet data file: " + filename);
			
			e.printStackTrace();
		}
	}
	
		
	

	private void loadPrincetonWordList(InputStream stream, boolean splits, String outfile)
	{
		String line = null, head = null, syn = null, symbol = null;
		
		int gap = 0, end = 0;
		
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(stream));
			
			OutputStreamWriter output = null;
			
			if (outfile != null)
				output = new OutputStreamWriter(new FileOutputStream(outfile));

			line = input.readLine(); // first line contains number of entries
						
			while ( (line = input.readLine()) != null)  // Read a line at a time
			{
				if (line.length() == 0)
					continue;
				else
				if (!Character.isDigit(line.charAt(0)))
				{
					continue;
				}
				
				
				gap = line.indexOf((int)' ');
				
				//id  = line.substring(0, gap);
				
				gap = line.indexOf((int)' ', gap+1);  // skip lex file number
				gap = line.indexOf((int)' ', gap+1);  // skip lex cat
				end = line.indexOf((int)' ', gap+1);
				
				int numSyns = parseHex(line.substring(gap+1, end));
				
				gap = end;
				
				end = line.indexOf((int)' ', gap+1);
				
				head   = line.substring(gap+1, end);
				
				if (head.endsWith("(a)") || head.endsWith("(p)"))
					head = head.substring(0, head.length()-3);
				else
				if (head.endsWith("(ip)") )
					head = head.substring(0, head.length()-4);

				symbol = head.toLowerCase().intern();
				
				if (Character.isUpperCase(head.charAt(0)))
				{
					properTerms.put(symbol, head);
					
					rootTable.put(head, symbol);
					
					if (head.equals(head.toUpperCase()))
						uppercaseTerms.put(symbol, head);
				}
				
				if (Character.isLowerCase(head.charAt(0)))
					lowercaseTerms.put(symbol, head);
				
				head = symbol;
				
				gap = line.indexOf((int)' ', end+1);  // skip sense tag
				
				boolean hooked = false;
				
				if (rootTable.get(head) == null)
				{
					if (outfile != null)
						output.write(rootTable.size() + ". " + head + "\t\n");
					
					rootTable.put(head, head);
					
					allWords.add(head);
					
					hooked = true;
				}
					
				 
				for (int i = 0; i < numSyns-1; i++)
				{
					end = line.indexOf((int)' ', gap+1);
					
					syn = line.substring(gap+1, end);
					
					if (syn.endsWith("(a)") || syn.endsWith("(p)"))
						syn = syn.substring(0, syn.length()-3);
					else
					if (syn.endsWith("(ip)") )
						syn = syn.substring(0, syn.length()-4);
					
					symbol = syn.toLowerCase().intern();
					
					if (Character.isUpperCase(syn.charAt(0)))
						properTerms.put(symbol, syn);
					
					syn = symbol;
					
					if (rootTable.get(syn) == null)
					{
						allWords.add(syn);
						
						if (outfile != null)
						{
							if (hooked)
								output.write(rootTable.size() + ". " + syn + "\t*\t\n");
							else
								output.write(rootTable.size() + ". " + syn + "\t\n");
						}
					}
					
					rootTable.put(syn, head);
					
					hooked = true;
														
					if (splits)
						splitWord(head);
					
					gap = line.indexOf((int)' ', end+1);  // skip sense tag
				}
			}

		   input.close();  // close connection to the data source
		   
		   if (output != null)
			   output.close();
		}
		catch (IOException e) 
		{
			System.out.println("Exception while reading WordNet data file: " + e.toString());
			 
			e.printStackTrace();
		}
	}

	
	private int parseHex(String hex)
	{
		return hexValue(hex.charAt(1)) + 16*hexValue(hex.charAt(0));
	}
	

	private int hexValue(char digit)
	{
		if (Character.isDigit(digit))
			return digit - '0';
		else
			return (digit - 'a') + 10;
	}
	
	
	private void splitWord(String word)
	{
		int dash = word.indexOf((int)'_');
		
		if (dash > 0)
		{
			modifiers.put(word.substring(0, dash), word);
			heads.put(word.substring(dash+1), word);
		}
	}
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	//  Load a Word List from a given file
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	

	public void loadWordList(String filename)
	{
		loadWordList(filename, false);
	}
	
	
	public void loadWordList(String filename, boolean splits)
	{
		FileInputStream input;

		try {
		    input = new FileInputStream(filename);
		    
		    if (!loadWordList(input, null, splits)) 
		    {
		    	System.out.println(filename + " is not Unicode");
		    	
		    	input = new FileInputStream(filename);
		    	
		    	if (!loadWordList(input, null, splits))
		    		System.out.println("Cannot load: " + filename);
		    }
		}
		catch (IOException e)
		{
			System.out.println("Cannot find/load word list file: " + filename);
			
			e.printStackTrace();
		}
	}
	
		

	private boolean loadWordList(InputStream stream, String type, boolean splits)
	{
		String line = null;
		
		int slash = 0, colon = 0, space = 0, bracket = 0;
		
		BufferedReader input = null;
		
		try {
			if (type == null)
				input = new BufferedReader(new InputStreamReader(stream));
			else
				input = new BufferedReader(new InputStreamReader(stream, type));				
			
			while (input.ready())  // Read a line at a time
			{
				line = input.readLine();
				
				if (line == null || line.length() == 0 || line.startsWith("#"))
					continue;
				
				bracket = -1;
				
				if (Character.isDigit(line.charAt(0)))
					bracket = line.indexOf((int)'[');
				
				if (bracket > 0)
				{
					space = line.indexOf((int)' ');
					
					if (space > 0 && line.charAt(space-1) == '.' && space < bracket)
						line = line.substring(space+1, bracket).trim();
				}
				
				colon = line.indexOf((int)':');
				
				if (colon > 0)
				{
					slash = line.indexOf((int)'/');
					
					if (slash < 0 || slash > colon)
						slash = line.indexOf((int)'.');
					
					if (slash > 0 && slash < colon)
						line = line.substring(0, slash);
				}
				
				line  = line.trim();
				space = line.lastIndexOf((int)' ');
				
				if (space > 0)
					line = line.substring(space+1).trim();
				
				if (rootTable.get(line) == null)
					allWords.add(line);
				
				rootTable.put(line, line);
				
				//System.out.println("    " + line + ".");
				
				if (splits)
					splitWord(line);
			}

		   input.close();  // close connection to the data source
		}
		catch (IOException e) 
		{
			if (type == null)
			{
				System.out.println("Exception while reading word table data file:\n " + e.toString());
				 
				e.printStackTrace();
			}
			else
			try {
				input.close();
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
			}
			
			return false;
		}
		
		return true;
	}
	
	
	
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	//  Load a list of tab-separated words from a given file
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	
	
	public Vector loadWordListWithCounts(String filename)
	{
		return loadTabbedWordList(filename, false);
	}


	public Vector loadTabbedWordList(String filename)
	{
		loadTabbedWordList(filename, false);
		
		return getAllWords();
	}
	
	
	public Vector loadTabbedWordList(String filename, boolean splits)
	{
		FileInputStream input;

		try {
		    input = new FileInputStream(filename);
		    
		    Vector allItems = loadTabbedWordList(input, null, splits);
		    
		    if (allItems == null) 
		    {
		    	System.out.println(filename + " is not Unicode");
		    	
		    	input = new FileInputStream(filename);
		    	
		    	allItems = loadTabbedWordList(input, null, splits);
		    	
		    	if (allItems == null)
		    		System.out.println("Cannot load: " + filename);
		    }
		    
		    return allItems;
		}
		catch (IOException e)
		{
			System.out.println("Cannot find/load word list file: " + filename);
			
			e.printStackTrace();
		}
		
		return null;
	}
	
		

	private Vector loadTabbedWordList(InputStream stream, String type, boolean splits)
	{
		String line = null, token = null;
		
		BufferedReader input = null;
		
		CountTable countedItems = new CountTable(true);
		
		try {
			if (type == null)
				input = new BufferedReader(new InputStreamReader(stream));
			else
				input = new BufferedReader(new InputStreamReader(stream, type));				
			
			while (input.ready())  // Read a line at a time
			{
				line = input.readLine();
				
				if (line == null || line.length() == 0 || line.startsWith("#"))
					continue;
				
				StringTokenizer tokens = new StringTokenizer(line, "\t\n,", false);
				
				while (tokens.hasMoreTokens())
				{
					token = tokens.nextToken().trim();
					
					countedItems.put(token);
					
					if (rootTable.get(token) == null)
					{
						rootTable.put(token, token);
						allWords.add(token);
					}
				}
			}

		   input.close();  // close connection to the data source
		}
		catch (IOException e) 
		{
			if (type == null)
			{
				System.out.println("Exception while reading word table data file:\n " + e.toString());
				 
				e.printStackTrace();
			}
			else
			try {
				input.close();
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
			}
			
			return null;
		}
		
		return countedItems.getKeyList();
	}

	
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	//  Save a word list to a file
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	public void saveAs(String filename)
	{
		try {
			OutputStreamWriter file = new OutputStreamWriter(new FileOutputStream(filename));
			
			for (int i = 0; i < allWords.size(); i++)
				file.write((String)allWords.elementAt(i) + "\n");
			
			file.close();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Trim troublesome nouns
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	public void trimNouns()
	{
		trimNouns(this);
	}
	
	
	public void trimNouns(WordTable nouns)
	{
		nouns.removeRoot("in");
		nouns.removeRoot("out");
		nouns.removeRoot("a");
		nouns.removeRoot("an");
		nouns.removeRoot("or");
		nouns.removeRoot("and");
		nouns.removeRoot("does");
		nouns.removeRoot("do");
		nouns.removeRoot("as");
		nouns.removeRoot("doe");
		nouns.removeRoot("piece");	
		nouns.removeRoot("come");
		nouns.removeRoot("little");
		nouns.removeRoot("few");
		nouns.removeRoot("more");
		nouns.removeRoot("hundred");
		nouns.removeRoot("thousand");
		nouns.removeRoot("million");
		nouns.removeRoot("are");
		nouns.removeRoot("then");
		nouns.removeRoot("there");
		nouns.removeRoot("now");
		nouns.removeRoot("enough");
		nouns.removeRoot("who");
		nouns.removeRoot("using");
		nouns.removeRoot("despite");
		nouns.removeRoot("us");
		nouns.removeRoot("it");

		nouns.removeRoot("one"); nouns.removeRoot("two"); nouns.removeRoot("three"); nouns.removeRoot("four"); nouns.removeRoot("five");
		nouns.removeRoot("six"); nouns.removeRoot("seven"); nouns.removeRoot("eight"); nouns.removeRoot("nine"); nouns.removeRoot("ten");

		nouns.removeRoot("words");
		nouns.removeRoot("humans");
		nouns.removeRoot("bones");
		nouns.removeRoot("chains");
		nouns.removeRoot("elements");
		nouns.removeRoot("links");
		nouns.removeRoot("tails");
		nouns.removeRoot("heads");
		nouns.removeRoot("organs");
		nouns.removeRoot("hooks");
		nouns.removeRoot("roads");
		nouns.removeRoot("ways");
		nouns.removeRoot("waters");
		nouns.removeRoot("heights");
		nouns.removeRoot("arms");
		nouns.removeRoot("details");
		nouns.removeRoot("eyes");
		nouns.removeRoot("legs");
		nouns.removeRoot("devices");
		nouns.removeRoot("parts");
		nouns.removeRoot("brains");
		nouns.removeRoot("effects");
		nouns.removeRoot("stacks");
		nouns.removeRoot("services");
		nouns.removeRoot("spirits");
	}

	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Add new endings to a verb to get a noun
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	
	public String addER(String verb)
	{
		if (verb == null) return null;
		
		if (verb.equals("lie") && containsRoot("liar")) return "liar";
		if (verb.equals("let") && containsRoot("lettor")) return "lettor";
		if (verb.equals("bet") && containsRoot("bettor")) return "bettor";
		
		if (verb.equals("flow")) return null;
		if (verb.equals("show")) return null;
		if (verb.equals("numb")) return null;

		int vlen = verb.length();
		
		if (verb.endsWith("e") && containsRoot(verb + "r"))
			return verb + "r";
		
		if (verb.endsWith("er") && containsRoot(verb + "er"))
			return verb + "er";

		if (verb.endsWith("ct") && containsRoot(verb + "or") && !containsRoot(verb + "er"))
			return verb + "or";
		
		if (verb.endsWith("ate") && containsRoot(verb.substring(0, vlen-1) + "or") && !containsRoot(verb + "r"))
			return verb.substring(0, vlen-1) + "or";

		if (vlen > 3 && "bdfglmnprt".indexOf(verb.charAt(vlen-1)) >= 0 
				&& "aeiourlw".indexOf(verb.charAt(vlen-2)) >= 0
				&& "aeiou".indexOf(verb.charAt(vlen-3)) >= 0
				&& containsRoot(verb + "er"))
			return verb + "er";

		if (vlen > 2 && "bdfglmnprt".indexOf(verb.charAt(vlen-1)) >= 0 
				&& "aeiou".indexOf(verb.charAt(vlen-2)) >= 0
				&& !verb.endsWith("wer"))
			return verb + verb.charAt(vlen-1) + "er";
		
		if (verb.endsWith("e"))
			return verb + "r";

		if (containsRoot(verb + "er"))
			return verb + "er";
		
		if (containsRoot(verb + "or"))
			return verb + "or";
		
		return verb + "er";
	}


	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Add new endings to a verb to get a verb form
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	
	public String addING(String verb)
	{
		if (verb == null) return null;
		
		if (verb.equals("lie")) return "lying";
		
		int vlen = verb.length();
		
		if (verb.endsWith("e"))
			return verb.substring(0, vlen-1) + "ing";
		
		if (vlen > 3 && "bdfglmnprt".indexOf(verb.charAt(vlen-1)) >= 0 
				&& "aeiourlw".indexOf(verb.charAt(vlen-2)) >= 0
				&& "aeiou".indexOf(verb.charAt(vlen-3)) >= 0)
			return verb + "ing";
		
		if (vlen > 4 && verb.endsWith("en")
				&& "aeiou".indexOf(verb.charAt(vlen-3)) < 0)
			return verb + "ing";

		if (vlen > 2 && "bdfglmnprt".indexOf(verb.charAt(vlen-1)) >= 0 
				&& "aeiou".indexOf(verb.charAt(vlen-2)) >= 0
				&& !verb.endsWith("er"))
			return verb + verb.charAt(vlen-1) + "ing";
		
		return verb + "ing";
	}

	
	
	public String addED(String verb)
	{
		if (verb == null) return null;
		
		int vlen = verb.length();
		
		if (verb.endsWith("e"))
			return verb.substring(0, vlen-1) + "ed";
		
		if (verb.endsWith("y") && !verb.endsWith("ey") && verb.endsWith("ay") && verb.endsWith("uy") 
				&& verb.endsWith("oy") && verb.endsWith("iy"))
			return verb.substring(0, vlen-1) + "ied";

		if (vlen > 3 && "bdfglmnprt".indexOf(verb.charAt(vlen-1)) >= 0 
				&& "aeiourlw".indexOf(verb.charAt(vlen-2)) >= 0
				&& "aeiou".indexOf(verb.charAt(vlen-3)) >= 0)
			return verb + "ed";

		if (vlen > 2 && "bdfglmnprt".indexOf(verb.charAt(vlen-1)) >= 0 
				&& "aeiou".indexOf(verb.charAt(vlen-2)) >= 0
				&& !verb.endsWith("er"))
			return verb + verb.charAt(vlen-1) + "ed";
		
		return verb + "ed";
	}

	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	//  Morphology routines
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	public String removeING(String verb)
	{
		int space = verb.indexOf((int)' ');
		
		if (space > 0)
			verb = verb.substring(0, space);
		
		if (!verb.endsWith("ing")) return null;
		
		if (verb.equals("singing")) return "sing";
		if (verb.equals("swinging")) return "swing";
		if (verb.equals("lying")) return "lie";
		
		if ((verb.endsWith("lling") || verb.endsWith("ssing"))
				&& verb.length() > 6
				&& this.containsRoot(verb.substring(0, verb.length()-3))) // gemination
		{
			verb = verb.substring(0, verb.length()-3);
		}
		else
		if (verb.endsWith("ing")
				&& verb.length() > 5 
				&& this.containsRoot(verb.substring(0, verb.length()-4)) 
				&& verb.charAt(verb.length()-4) == verb.charAt(verb.length()-5)) // gemination
		{
			verb = verb.substring(0, verb.length()-4);
		}
		else
		if (verb.endsWith("ing")
				&& verb.length() > 4
				&& this.containsRoot(verb.substring(0, verb.length()-3) + "e") 
				&& verb.charAt(verb.length()-4) != verb.charAt(verb.length()-5))
		{
			verb = verb.substring(0, verb.length()-3) + "e";
		}
		else
		if (verb.endsWith("ing") 
				&& this.containsRoot(verb.substring(0, verb.length()-3)))
		{
			verb = verb.substring(0, verb.length()-3);
		}
		else
			return null;
		
		return verb;
	}

	
	public String removeLY(String word)
	{
		if (!word.endsWith("ly")  || word.length() < 5) return null;
		
		if (word.endsWith("ily") 
				&& this.containsRoot(word.substring(0, word.length()-3) + "y"))
		{
			word = word.substring(0, word.length()-3) + "y";
		}
		else
		if (word.endsWith("ically") 
				&& word.length() >= 9
				&& this.containsRoot(word.substring(0, word.length()-4)))
		{
			word = word.substring(0, word.length()-4);
		}
		else
		if (word.endsWith("bly") 
				&& this.containsRoot(word.substring(0, word.length()-1) + "e"))
		{
			word = word.substring(0, word.length()-1) + "e";
		}
		else
		if (word.endsWith("icly") 
				&& this.containsRoot(word.substring(0, word.length()-2)))
		{
			if (word.length() <= 8)
				word = word.substring(0, word.length()-2);
			else
				return null;
		}		
		else
		if (this.containsRoot(word.substring(0, word.length()-2)))
		{
			word = word.substring(0, word.length()-2);
		}
		else
			return null;
		
		return word;
	}
	
	
	public String removeY(String word)
	{
		if (!word.endsWith("y")  || word.length() < 3) return null;
		
		if (word.length() >= 5 &&
				this.containsRoot(word.substring(0, word.length()-2)) &&
				word.charAt(word.length()-2) == word.charAt(word.length()-3) && // gemination
				"bdfglmnprst".indexOf((char)word.charAt(word.length()-2)) >= 0)
		{
			word = word.substring(0, word.length()-2);
		}
		else
		if (!word.endsWith("ey") && this.containsRoot(word.substring(0, word.length()-1) + "e"))
		{
			word = word.substring(0, word.length()-1) + "e";
		}		
		else
		if (this.containsRoot(word.substring(0, word.length()-1)))
		{
			word = word.substring(0, word.length()-1);
		}
		else
			return null;
		
		return word;
	}

	
	
	public String removeNESS(String word)
	{
		if (!word.endsWith("ness")  || word.length() < 8) return null;
		
		if (word.endsWith("iness") 
				&& this.containsRoot(word.substring(0, word.length()-5) + "y"))
		{
			word = word.substring(0, word.length()-5) + "y";
		}
		else
		if (this.containsRoot(word.substring(0, word.length()-4)))
		{
			word = word.substring(0, word.length()-4);
		}
		else
			return null;
		
		return word;
	}

	
	
	public String removeITY(String word)
	{
		if (!word.endsWith("ity")  || word.length() < 7) return null;
		
		if (word.endsWith("ability") && word.length() > 10
				&& this.containsRoot(word.substring(0, word.length()-5) + "le"))
		{
			word = word.substring(0, word.length()-5) + "le";
		}
		else
		if (word.endsWith("ibility") && word.length() > 10
				&& this.containsRoot(word.substring(0, word.length()-5) + "le"))
		{
			word = word.substring(0, word.length()-5) + "le";
		}
		else
		if (word.endsWith("ity") 
				&& this.containsRoot(word.substring(0, word.length()-3) + "e"))
		{
			word = word.substring(0, word.length()-3) + "e";
		}
		else
		if (this.containsRoot(word.substring(0, word.length()-3)))
		{
			word = word.substring(0, word.length()-3);
		}      
		else
			return null;
		
		return word;
	}

	
	public String removeABLE(String word)
	{
		if (word.length() < 7) return null;
		
		if (!word.endsWith("ible") &&  !word.endsWith("able")) return null;
		
		if (this.containsRoot(word.substring(0, word.length()-4) + "e"))
		{
			word = word.substring(0, word.length()-4) + "e";
		}
		else
		if (this.containsRoot(word.substring(0, word.length()-4)))
		{
			word = word.substring(0, word.length()-4);
		}      
		else
			return null;
		
		return word;
	}

	
	
	public String removeS(String word)
	{
		if (!word.endsWith("s") || word.endsWith("ss") || word.length() < 4) return null;
		
		if (word.equals("dies")) return "die";
		if (word.equals("graves")) return "grave";
		if (word.equals("busses")) return "bus";
		if (word.equals("doses")) return "dose";
		if (word.equals("foes")) return "foe";

		if (word.equals("corpses")) return "corpse";
		if (word.equals("superheroes")) return "superhero";
		if (word.equals("supervillains")) return "supervillain";
		
		if (word.endsWith("ians")) return word.substring(0, word.length()-1);
		
		if (word.endsWith("us") && this.containsRoot(word))
			return null;

		if (word.endsWith("as") && this.containsRoot(word))
			return null;
		
		if (Character.isUpperCase(word.charAt(0)) && Character.isUpperCase(word.charAt(word.length()-2)))
			return word.substring(0, word.length()-1);  // e.g., PDAs
		
		if (word.equals("uses")) return "use";
		
		if (word.endsWith("eses") && word.length() > 5
				&& this.containsRoot(word.substring(0, word.length()-2) + "is"))
		{
			word = word.substring(0, word.length()-2) + "is";
		}
		else
		if (word.endsWith("ves") && word.length() > 4
				&& this.containsRoot(word.substring(0, word.length()-3) + "f"))
		{
			if (word.endsWith("caves"))
				return "cave";
			
			word = word.substring(0, word.length()-3) + "f";
		}
		else
		if (word.endsWith("ves") && word.length() > 4
				&& this.containsRoot(word.substring(0, word.length()-3) + "fe"))
		{
			if (word.endsWith("caves"))
				return "cave";
			
			word = word.substring(0, word.length()-3) + "fe";
		}
		else
		if ((word.endsWith("hes") || word.endsWith("ses") || word.endsWith("xes") || word.endsWith("zes"))
				&& this.containsRoot(word.substring(0, word.length()-1)))
		{
			word = word.substring(0, word.length()-1);
		}
		else
		if ((word.endsWith("hes") || word.endsWith("ses") || word.endsWith("xes")  || word.endsWith("oes") || word.endsWith("zes"))
				&& this.containsRoot(word.substring(0, word.length()-2)))
		{
			word = word.substring(0, word.length()-2);
		}
		else
		if (word.endsWith("ies") && this.containsRoot(word.substring(0, word.length()-3) + "y"))
		{
			word = word.substring(0, word.length()-3) + "y";
		}
		else
		if (this.containsRoot(word.substring(0, word.length()-1)))
		{
			word = word.substring(0, word.length()-1);
		}
		else
			return null;
		
		return word;
	}

	
	public String removeED(String verb)
	{
		if (!verb.endsWith("ed")) return null;
		
		if (verb.equals("seed") || verb.equals("feed")) return null;
		
		if (verb.length() >= 4 &&   
				this.containsRoot(verb.substring(0, verb.length()-1)) &&
				verb.charAt(verb.length()-3) != verb.charAt(verb.length()-4))
		{
			verb = verb.substring(0, verb.length()-1);
		}
		else
		if (verb.length() >= 4 && verb.endsWith("ied") &&
				this.containsRoot(verb.substring(0, verb.length()-3) + "y"))
		{
			verb = verb.substring(0, verb.length()-3) + "y";
		}
		else
		if (this.containsRoot(verb.substring(0, verb.length()-2)))
		{
			verb = verb.substring(0, verb.length()-2);
		}
		else
		if (verb.length() > 5 &&
				this.containsRoot(verb.substring(0, verb.length()-3)) &&
				verb.charAt(verb.length()-3) == verb.charAt(verb.length()-4)) // gemination
		{
			verb = verb.substring(0, verb.length()-3);
		}
		else
			return null;
		
		return verb;
	}
	
	

	public String removeESS(String noun)
	{
		if (!noun.endsWith("ess") || noun.length() < 6) return null;
		
		if (containsRoot(noun.substring(0, noun.length()-3)) &&
			noun.charAt(noun.length()-4) != noun.charAt(noun.length()-5))
		{
			noun = noun.substring(0, noun.length()-3);
		}
		else
		if (containsRoot(noun.substring(0, noun.length()-4)) &&
			noun.charAt(noun.length()-4) == noun.charAt(noun.length()-5)) // gemination
		{
			noun = noun.substring(0, noun.length()-4);
		}
		else
			return null;
		
		return noun;
	}

	
	public String removeISH(String noun)
	{
		if (!noun.endsWith("ish") || noun.length() < 6) return null;
		
		if (containsRoot(noun.substring(0, noun.length()-3)) &&
			noun.charAt(noun.length()-4) != noun.charAt(noun.length()-5))
		{
			noun = noun.substring(0, noun.length()-3);
		}
		else
		if (containsRoot(noun.substring(0, noun.length()-4)) &&
			noun.charAt(noun.length()-4) == noun.charAt(noun.length()-5)) // gemination
		{
			noun = noun.substring(0, noun.length()-4);
		}
		else
			return null;
		
		return noun;
	}
	

	
	public String removeISHLY(String noun)
	{
		if (!noun.endsWith("ishly") || noun.length() < 8) return null;
		
		if (containsRoot(noun.substring(0, noun.length()-5)) &&
			noun.charAt(noun.length()-6) != noun.charAt(noun.length()-7))
		{
			noun = noun.substring(0, noun.length()-5);
		}
		else
		if (containsRoot(noun.substring(0, noun.length()-6)) && noun.length() > 8 &&
			noun.charAt(noun.length()-6) == noun.charAt(noun.length()-7)) // geminate
		{
			noun = noun.substring(0, noun.length()-6);
		}
		else
			return null;
		
		return noun;
	}

	
	public String removeER(String wordform)
	{
		if (wordform.endsWith("or")) 
		{
			if (wordform.length() > 4 &&
					this.containsRoot(wordform.substring(0, wordform.length()-2)))
			{
				return wordform.substring(0, wordform.length()-2);
			}
			else
			if (wordform.length() > 4 &&
					this.containsRoot(wordform.substring(0, wordform.length()-2) + "e"))
			{
				return wordform.substring(0, wordform.length()-2) + "e";
			}
			else
				return null;
		}
		
		if (!wordform.endsWith("er")) return null;
		
		if (this.containsRoot(wordform.substring(0, wordform.length()-2)))
		{
			wordform = wordform.substring(0, wordform.length()-2);
		}
		else
		if (wordform.length() > 4 && wordform.endsWith("ier") &&
				this.containsRoot(wordform.substring(0, wordform.length()-3) + "y"))
		{
			wordform = wordform.substring(0, wordform.length()-3) + "y";
		}
		else
		if (wordform.length() > 5 &&
				this.containsRoot(wordform.substring(0, wordform.length()-3)) &&
				wordform.charAt(wordform.length()-3) == wordform.charAt(wordform.length()-4)) // gemination
		{
			wordform = wordform.substring(0, wordform.length()-3);
		}
		else
		if (wordform.length() > 4 &&
				this.containsRoot(wordform.substring(0, wordform.length()-1)) &&
				wordform.charAt(wordform.length()-3) != wordform.charAt(wordform.length()-4))
		{
			wordform = wordform.substring(0, wordform.length()-1);
		}
		else
			return null;
		
		return wordform;
	}
	
	

	public String removeEST(String wordform)
	{
		if (!wordform.endsWith("est")) 
			return null;
		
		if (wordform.length() > 5 &&
				this.containsRoot(wordform.substring(0, wordform.length()-2)) &&
				wordform.charAt(wordform.length()-4) != wordform.charAt(wordform.length()-5))
		{
			wordform = wordform.substring(0, wordform.length()-2);
		}
		else
		if (wordform.length() > 5 && this.containsRoot(wordform.substring(0, wordform.length()-3)))
		{
			wordform = wordform.substring(0, wordform.length()-3);
		}
		else
		if (wordform.length() > 6 && wordform.endsWith("iest") &&
				this.containsRoot(wordform.substring(0, wordform.length()-4) + "y"))
		{
			wordform = wordform.substring(0, wordform.length()-4) + "y";
		}
		else
		if (wordform.length() > 6 &&
				this.containsRoot(wordform.substring(0, wordform.length()-4)) &&
				wordform.charAt(wordform.length()-4) == wordform.charAt(wordform.length()-5)) // gemination
		{
			wordform = wordform.substring(0, wordform.length()-4);
		}
		else
			return null;
		
		return wordform;
	}

	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	//  Guessing routines to fill lexical gaps
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	public boolean canBeAdjective(String word)
	{
		if (this.containsRoot(word) || word.endsWith("'s"))
			return true;
		
		if (word.length() > 5)
		{
			if (word.startsWith("co") && this.containsRoot(word.substring(2)))
				return true;
			
			if (word.startsWith("un") && this.containsRoot(word.substring(2)))
				return true;

			if (word.startsWith("non") && this.containsRoot(word.substring(3)))
				return true;
			
			if (word.startsWith("pre") && this.containsRoot(word.substring(3)))
				return true;

			if (word.length() > 6 && word.startsWith("post") && this.containsRoot(word.substring(4)))
				return true;
			
			if (word.endsWith("ical") || word.endsWith("istic"))
				return true;
		}
		
		return false;
	}
	
	//-----------------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------------//
	//  Break up complex nouns to create a head to modifier mapping
	//-----------------------------------------------------------------------------------//
	//----------------------------------------------------------------------------------//	

	public void createHeadToModifierMapping(BucketTable map)
	{
		String word = null, mod = null, head = null;
		
		int dash = 0;
		
		for (int i = 0; i < allWords.size(); i++)
		{
			word = (String)allWords.elementAt(i);
			
			dash = word.indexOf((int)'_');
			
			if (dash < 0 || dash != word.lastIndexOf((int)'_')) continue;
			
			mod  = word.substring(0, dash);
			head = word.substring(dash+1);
			
			map.put(head, mod);
		}
	}
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	//  Main routine
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	public static void main(String[] args)
	{
		String dir      = "/Users/tonyveale/Desktop/Lexical Resources/Moods/";

		WordTable keys  = new WordTable();
		
		Vector allItems = keys.loadWordListWithCounts(dir + "Z Domains.idx");
		
		if (allItems != null)
		{
			allItems = SymbolMap.getSorted(allItems);
			
			for (int i = 0; i < allItems.size(); i++)
			{
				SymbolCounter item = (SymbolCounter)allItems.elementAt(i);
				
				String verb = item.getSymbol();
				
				System.out.println(verb + "\t" + item.value());	
			}
		}
	}
		


}