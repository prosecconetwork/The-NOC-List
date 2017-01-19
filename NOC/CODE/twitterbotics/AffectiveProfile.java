package twitterbotics;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;
import java.util.Vector;

import tabular.CountTable;
import tabular.SymbolMap;

// Acquire affective dimensions of a Twitter user from a web-site

public class AffectiveProfile 
{
	private final String baseURL = "http://analyzewords.com/index.php?handle=";
	
	private final String labelPrefix = "\"return nd();\">";
	private final String labelSuffix = " (";
	
	private final String valuePrefix = "title=\"";
	private final String valueSuffix = "\">";
		
	private CountTable dimensions = new CountTable(true);
	
	private String handle = null;
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Constructors
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	public AffectiveProfile(String handle)
	{
		if (handle.startsWith("@"))
			handle = handle.substring(1);
		
		this.handle = handle;
		
		scrapeAffectWebsite(baseURL + handle);
	}
	
	
	public AffectiveProfile(String handle, KnowledgeBaseModule transformulas)
	{
		this(handle);
		
		calculateTransFormulas(transformulas);
	}
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Access Methods
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	public String getHandle()
	{
		return handle;
	}
	
	
	public int getValue(String dimName)
	{
		return dimensions.getCount(dimName);
	}
	
	
	public Vector getSortedDimensions()
	{
		return SymbolMap.getSorted(dimensions.getKeyList());
	}
	
	
	public Vector getDimensions()
	{
		return dimensions.getKeyList();
	}
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Calculate derived (jnferred) qualities using transformational formulas
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	
	
	private void calculateTransFormulas(KnowledgeBaseModule transformulas)
	{
		Vector quals = transformulas.getAllFrames();
		
		for (int q = 0; q < quals.size(); q++)
		{
			String quality  = (String)quals.elementAt(q);
			
			Vector formulas = transformulas.getFieldValues("Formulas", quality);
			
			if (formulas == null) continue;
			
			int prev = getValue(quality);
			
			for (int f = 0; f < formulas.size(); f++)
			{
				String formula = (String)formulas.elementAt(f);
				
				int value = calculateFormula(formula);
				
				System.out.println(quality + " (" + formula + ") = " + value);
				
				if (value > prev)
					dimensions.putMax(quality, value);				
			}
		}
	}
	
	
	private int calculateFormula(String formula)
	{
		int amp = formula.indexOf((int)'&');
		
		if (amp > 0)
			return Math.min(calculateFormula(formula.substring(0, amp)), 
						    calculateFormula(formula.substring(amp+1)));
		
		if (formula.startsWith("-"))
			return 100 - calculateFormula(formula.substring(1));
		
		return getValue(formula);	
	}
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Scrape the relevant website
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	
	private void scrapeAffectWebsite(String pageURL)
	{
		try {
			URL website         = new URL(pageURL);
			
			URLConnection conn  = website.openConnection();
			
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
	        
			BufferedReader in   = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			
			String inputLine    = null;
			
			System.out.println(pageURL);
			
			while ((inputLine = in.readLine()) != null) 
			{
				//System.out.println(inputLine);
				
				int labpfixPos = inputLine.indexOf(labelPrefix);
				
				while (labpfixPos >= 0) 
				{				
					int labsfixPos = inputLine.indexOf(labelSuffix, labpfixPos + labelPrefix.length());
				
					if (labsfixPos > 0)
					{				
						int valpfixPos = inputLine.indexOf(valuePrefix, labsfixPos + labelSuffix.length());
				
						if (valpfixPos > 0)
						{				
							int valsfixPos = inputLine.indexOf(valueSuffix, valpfixPos + valuePrefix.length());
							
							if (valsfixPos > 0)
							{				
								setDimension(inputLine.substring(labpfixPos + labelPrefix.length(), labsfixPos), 
											 inputLine.substring(valpfixPos + valuePrefix.length(), valsfixPos));	
							}
						}
					}
					
					labpfixPos = inputLine.indexOf(labelPrefix, labpfixPos+1);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	private void setDimension(String dimName, String dimValue)
	{
		if (dimValue == null || dimValue.length() < 1 || !Character.isDigit(dimValue.charAt(0)))
			return;
		
		int slash = dimName.indexOf((int)'/');
		
		if (slash > 0)
		{
			setDimension(dimName.substring(0, slash), dimValue);
			setDimension(dimName.substring(slash+1), dimValue);
		}
		else
			dimensions.put(cleanDmmensionName(dimName), Integer.parseInt(dimValue));
	}
	
	
	private String cleanDmmensionName(String dimName)
	{
		StringBuffer clean = new StringBuffer(dimName.toLowerCase());
		
		for (int c = 0; c < clean.length(); c++)
			if (dimName.charAt(c) == '-' || dimName.charAt(c) == ' ')
				clean.setCharAt(c, '_');
		
		return clean.toString();
	}
	
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Main application stub
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	public static void main(String[] args)
	{
		String handle = "@MetaphorMagnet";
		String kdir   = "/Users/tonyveale/Dropbox/CodeCamp2016/NOC/DATA/TSV Lists/";
		
		KnowledgeBaseModule transformulas = new KnowledgeBaseModule(kdir + "Veale's ranked quality classifications.txt");
		
		AffectiveProfile profile = new AffectiveProfile(handle, transformulas);
		
		System.out.println("Upbeat" + " = " +  profile.getValue("upbeat"));
		System.out.println("Worried" + " = " +  profile.getValue("worried"));
		System.out.println("Angry" + " = " +  profile.getValue("angry"));
		System.out.println("Depressed" + " = " +  profile.getValue("depressed"));
		System.out.println("Plugged In" + " = " +  profile.getValue("plugged_in"));
		System.out.println("Personable" + " = " +  profile.getValue("personable"));
		System.out.println("Arrogant" + " = " +  profile.getValue("arrogant"));
		System.out.println("Distant" + " = " +  profile.getValue("distant"));
		System.out.println("Spacy" + " = " +  profile.getValue("spacy"));
		System.out.println("Analytic" + " = " +  profile.getValue("analytic"));
		System.out.println("Sensory" + " = " +  profile.getValue("sensory"));
		System.out.println("In-the-moment" + " = " +  profile.getValue("in_the_moment"));
		
		System.out.println(profile.getSortedDimensions());

	}
	
}
