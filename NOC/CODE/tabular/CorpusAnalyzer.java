package tabular;

// This package provides extended Hashtable structures

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;


// This class tracks the co-occurrence of terms in a corpus of files
// to calculate some measure of inter-term association  (e.g.,  DICE, or PMI)


public class CorpusAnalyzer 
{
	private int windowSize = 20;
	private int currDocPosition = 0;
	
	private int totalNumTokens  = 0;
	
	private Hashtable termPositions = new Hashtable();
	
	private Integer[] termIds = new Integer[50000];
	
	private BucketTable termMatrix = new BucketTable("terms to coordinated terms");
	
	private SymbolMap termToTermCountMap = new SymbolMap("terms to coordinated terms, with counts");
	
	private Hashtable termCountMap = new Hashtable(); 
	
	
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//
	// Constructors
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//
	
	public CorpusAnalyzer(String matrixFile)
	{
		System.out.print("Loading term coordinates ...");
		
		termMatrix.loadTable(matrixFile);
		
		System.out.println("  Done.");
	}
	
	
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//
	// Initialize for new Document / Window Size
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//

	public void reset(int windowSize)
	{
		this.windowSize = windowSize;
		
		this.currDocPosition = 0;
		
		termPositions.clear();
	}
	
	
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//
	// Remove plural marker from a word
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//

	// assume that all the nouns of interest are keys in the termMatrix
	
	public String removeS(String word)
	{
		// First deal with problem words
		
		if (word.equals("clothes")) return "clothes";
		if (word.equals("genus")) return null;
		if (word.equals("wales")) return null;
		if (word.equals("kansas")) return null;
		if (word.equals("https")) return null;
		if (word.equals("looses")) return null;
		if (word.equals("vegas")) return "vegas";
		if (word.equals("tetris")) return "tetris";
		if (word.equals("does")) return null;
		if (word.equals("anus")) return null;
		if (word.equals("genera")) return "genus";
		if (word.equals("species")) return "species";
		if (word.equals("oases")) return "oasis";
		if (word.equals("diagnoses")) return "diagnosis";
		if (word.equals("graves")) return "grave";
		if (word.equals("serves")) return "serve";
		if (word.equals("lenses"))return "lens";
		if (word.equals("leaves")) return "leaf";
		if (word.equals("serves")) return "serve";
			
		// Now deal with regular cases that can be handled with rules/heuristics
		
		if (word.endsWith("ss"))
			return null;
		else
		if (word.endsWith("sses") && termMatrix.contains(word.substring(0, word.length()-2)))
		{
			word = word.substring(0, word.length()-2);
		}
		else
		if (word.endsWith("ses") && word.length() > 4
				&& termMatrix.contains(word.substring(0, word.length()-1)))
		{
			word = word.substring(0, word.length()-1);
		}
		else
		if ((word.endsWith("hes") || word.endsWith("ses") || word.endsWith("xes") || word.endsWith("zes"))
				&& termMatrix.contains(word.substring(0, word.length()-2)))
		{
			word = word.substring(0, word.length()-2);
		}
		else
		if (word.endsWith("ves") && word.length() > 4
				&& termMatrix.contains(word.substring(0, word.length()-3) + "f"))
		{
			word = word.substring(0, word.length()-3) + "f";
		}
		else
		if (word.endsWith("ies") && termMatrix.contains(word.substring(0, word.length()-3) + "y"))
		{
			word = word.substring(0, word.length()-3) + "y";
		}
		else
		if (word.endsWith("s") && termMatrix.contains(word.substring(0, word.length()-1)))
		{
			word = word.substring(0, word.length()-1);
		}
		else
		if (word.endsWith("ves") && word.length() > 4
				&& termMatrix.contains(word.substring(0, word.length()-3) + "fe"))
		{
			word = word.substring(0, word.length()-3) + "fe";
		}
		else
			return null;
		
		return word;
	}
	
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//
	// Term positions in text
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//

	public void markTermPositionInText(String term, int position)
	{
		termPositions.put(term, getWrappedInteger(position));
	}
	
	
	public int getTermPositionInText(String term)
	{
		Integer pos = (Integer)termPositions.get(term);
		
		if (pos == null)
			return -1;
		else
			return pos.intValue();
	}

	
	
	public boolean isTermWithinCurrentWindow(String term)
	{
		int termPos = getTermPositionInText(term);
		
		if (termPos < 0) return false;
		
		return this.currDocPosition - termPos <= this.windowSize;
	}
	
	
	public boolean isRepeatedTermInContextOf(String term, String assoc)
	{
		if (!isTermWithinCurrentWindow(term) || !isTermWithinCurrentWindow(assoc)) 
			return false;
		
		return (Math.abs(getTermPositionInText(term) -  getTermPositionInText(assoc)) < this.windowSize);
	}
	
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//
	// Term counts 
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//

	public void incrementTermCount(String term)
	{
		termCountMap.put(term, getWrappedInteger(getTermCount(term) + 1));
	}
	
	
	public int getTermCount(String term)
	{
		Integer count = (Integer)termCountMap.get(term);
		
		if (count == null)
			return 0;
		else
			return count.intValue();
	}
	
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//
	// Helper functions
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//

	// Ensure we never create more than one copy of a wrapped integer
	
	public Integer getWrappedInteger(int count)
	{
		if (count < 0 || count >= termIds.length)
			return new Integer(count);
		
		Integer cached = termIds[count];
		
		if (cached == null)
		{
			cached = new Integer(count);
			termIds[count] = cached;
		}
		
		return cached;
	}
	
	

	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//
	// Accessors
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//

	public SymbolMap getTermToTermCountMap()
	{
		return termToTermCountMap;
	}
	
	
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//
	// Process a New Document
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//

	public void processDocument(String filename, int windowSize)
	{
		reset(windowSize);
		
		try {
			
			System.out.println("Processing:  " + filename);
			
			BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
			
			String line = null;
			
			while ( (line = input.readLine()) != null)  // Read a line at a time
			{				
				processDocumentLine(line);
			}
			
			input.close();
			
			System.out.println("Processed:  " + filename +   " (contains " + this.currDocPosition + " useful terms)");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	public void processDocumentLine(String line)
	{
		StringTokenizer tokens = new StringTokenizer(line, " .,\"()[]<>/\\;:*&^%$@!?~", false);
		
		String token = null;
		
		while (tokens.hasMoreTokens())
		{
			token = tokens.nextToken().toLowerCase();
			
			this.currDocPosition++;
			
			if (!termMatrix.contains(token))
			{
				if (token.endsWith("s"))
					token = removeS(token);
				
				if (token == null || !termMatrix.contains(token))
					continue;
			}
			
			totalNumTokens++;
			
			incrementTermCount(token);
			
			processDocumentToken(token);
			
			markTermPositionInText(token, this.currDocPosition);
		}
	}
	
	
	public void processDocumentToken(String token)
	{
		Vector assocs = termMatrix.get(token);
		
		if (assocs == null) return;
		
		String assoc = null;
		
		for (int i = 0; i < assocs.size(); i++)
		{
			assoc = (String)assocs.elementAt(i);
			
			if (!assoc.equals(token) && isTermWithinCurrentWindow(assoc))
			{
				termToTermCountMap.put(assoc, token);
				termToTermCountMap.put(token, assoc);
				
				if (isRepeatedTermInContextOf(token, assoc))
					incrementTermCount(assoc);
				
				if ((assoc.equals("flotsam") && token.equals("jetsam"))
						||
					(assoc.equals("jetsam") && token.equals("flotsam")))
					System.out.println("  " + currDocPosition + ": " 
										    + assoc + "(" + getTermCount(assoc) + "/" + getTermPositionInText(assoc) + ") & " 
							 				+ token + "(" + getTermCount(token) + "/" + getTermPositionInText(token) + ")");
			}
		}
	}
	
	
	
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//
	// Go through the termToTermCountMap and change all the raw counts
	// into some relativized measure of association, such as DICE or PMI
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//

	public void convertCountsToDiceCoefficients()
	{
		convertCountsToAssociationMeasures(true, false);
	}
	
	
	public void convertCountsToPMI()
	{
		convertCountsToAssociationMeasures(false, true);
	}

	
	public void convertCountsToAssociationMeasures(boolean dice, boolean pmi)
	{
		Vector terms = termToTermCountMap.getKeyList(), assocs = null;
		
		if (terms == null) return;
		
		String term = null;
		
		SymbolCounter assoc = null;
		
		int strength = 0;
		
		for (int i = 0; i < terms.size(); i++)
		{
			term = (String)terms.elementAt(i);
			
			assocs = termToTermCountMap.get(term);
			
			if (assocs == null) continue;
			
			for (int j = 0; j < assocs.size(); j++)
			{
				assoc = (SymbolCounter)assocs.elementAt(j);
				
				if (dice)
					strength = getDiceCoefficient(term, assoc.getSymbol(), assoc.value());
				else
				if (pmi)
					strength = getPMI(term, assoc.getSymbol(), assoc.value());
				
				assoc.set(strength);
			}
		}
		
		termToTermCountMap.sortMap();
	}
	
	
	
	private int getDiceCoefficient(String term1, String term2, int comboFreq)
	{
		if (comboFreq <= 0) return 0;
		
		int freq1 = getTermCount(term1);
		
		if (freq1 <= 0) return 0;
		
		int freq2 = getTermCount(term2);
		
		if (freq2 <= 0) return 0;
		
		return (2*1000*comboFreq)/(freq1 + freq2);
	}
	
	//  PMI(x, y) = log (  p(x,y)/p(x)*p(y)  )
	//
	//  p(x,y)    =  p(x)p(y|x)  
	//  p(x)      =  #x/#all
	//  p(y)      =  #y/#all
	//  p(y|x)    =  #xy/#x
	
	private int getPMI(String term1, String term2, int comboFreq)
	{
		if (comboFreq <= 0) return 0;
		
		double freq1 = getTermCount(term1);
		
		if (freq1 <= 0) return 0;
		
		double freq2 = getTermCount(term2);
		
		if (freq2 <= 0) return 0;
		
		double p1  = freq1/totalNumTokens;
		double p2  = freq2/totalNumTokens;
		
		double p21 = comboFreq/freq1;
		
		double p12 = p1*p21;
		
		double pmi = Math.log(p12/(p1*p2));
		
		return (int)(1000*pmi);
	}
	
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//
	// Main Stub / Tester
	//---------------------------------------------------------------------------//
	//---------------------------------------------------------------------------//

	public static void main(String[] args)
	{
		String TEST_DIR 			= "/Users/tonyveale/Desktop/Wikipedia/";
		String MATRIX_DIR 			= "/Users/tonyveale/Desktop/Lexical Resources/Bootstrapping/";
		
		String test1				= "Superman.htm";
		String test2				= "Kryptonite.htm";
		String test3				= "Zeus.htm";
		
		int windowSize				= 50;
		
		CorpusAnalyzer omac			= new CorpusAnalyzer(MATRIX_DIR + "term to term coordinates.idx");
		
		omac.processDocument(TEST_DIR + test1, windowSize);
		omac.processDocument(TEST_DIR + test2, windowSize);
		omac.processDocument(TEST_DIR + test3, windowSize);
		
		omac.convertCountsToPMI();
		
		omac.getTermToTermCountMap().saveMapping(TEST_DIR + "dice matrix.idx");
	}
}