package twitterbotics;

// Maintain a random-access lexicon of color stereotypes (e.g. lemon, sky, grass, blood, etc.)

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;

import tabular.BucketTable;



public class ColorLexicon extends Dribbler
{
	private static Random RND      = new Random();
	
	private Hashtable stereoHues   = new Hashtable();
	
	private Hashtable stereoHueRGB = new Hashtable();
	
	private Hashtable hueStereos   = new Hashtable();

	private Vector stereoList      = new Vector();

	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Constructors
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	public ColorLexicon(String filename)
	{
		loadLexicon(filename);
	}
	
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Access the Lexicon
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	public void clear()
	{
		stereoHues.clear();
		stereoHueRGB.clear();
		stereoList.setSize(0);
	}
	
	
	public Vector getStereoList()
	{
		return stereoList;
	}
	
	
	public Vector getHuesFor(String stereo)
	{
		Vector hues = (Vector)stereoHues.get(stereo);
		
		// Deal with the complex situation in which the "stereo" is a multi-word proper-named individal
		if (hues == null)
		{
			int dash = stereo.indexOf((int)'_');
			
			if (dash < 0) return null;
			
			String fore = stereo.substring(0, dash), aft = stereo.substring(dash+1);
			
			if (getRGBFor(fore, aft) != null)
			{
				hues = new Vector();
				hues.add(aft);
			}
		}
		
		return hues;
	}
	
	
	public Vector getStereosFor(String hue)
	{
		return (Vector)hueStereos.get(hue);
	}

	
	public String getRGBFor(String stereo, String hue)
	{
		String rgb = (String)stereoHueRGB.get(stereo + "-" + hue);
		
		// Deal with the complex situation in which the "stereo" is a multi-word proper-named individal
		if (rgb == null)
		{
			int dash = stereo.indexOf((int)'_');
			
			if (dash < 0) return null;
			
			String fore = stereo.substring(0, dash), aft = stereo.substring(dash+1);
			
			if (aft.equals(hue))
				rgb = (String)stereoHueRGB.get(fore + "-" + aft);
 		}
		
		return rgb;
	}
	
	
	public String getRandomStereoForHue(String hue)
	{
		Vector stereos = (Vector)hueStereos.get(hue);
		
		if (stereos == null) return null;
		
		if (stereos.size() == 0) return (String)stereos.elementAt(0);
		
		return (String)stereos.elementAt(RND.nextInt(stereos.size()));
	}
	
	
	public Vector getPaletteFrom(String tweet)
	{
		Vector palette = null;
		
		StringTokenizer tokens = new StringTokenizer(tweet.toLowerCase(), " \t\"'.,[]()-*&;");
		
		while (tokens.hasMoreTokens())
		{
			String token = tokens.nextToken();
			
			if (getStereosFor(token) == null) continue;
			
			token = token.intern();
			
			if (palette == null) palette = new Vector();
			
			if (!palette.contains(token))
				palette.add(token);
		}
		
		return palette;
	}
	
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Some color names are complex and may be the names of proper individuals
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	
	public boolean isColorName(String name)
	{
		Vector hues = (Vector)stereoHues.get(name);
		
		if (hues != null && hues.size() > 0) return true;
		
		int dash = name.indexOf((int)'_');
		
		if (dash > 0)
		{
			String fore = (String)name.substring(0, dash);
			String  aft = (String)name.substring(dash+1);
			
			return getRGBFor(fore, aft) != null;
		}
		
		return false;
	}
	
	
	// E.g. Given Ed Wood #663300  the resolution of Wood (#663300) is Wood-brown
	
	public String resolveStereoHue(String stereo, String givenHue, String rgb)
	{
		String hue = resolveHue(givenHue, rgb);
		
		if (stereo.toLowerCase().endsWith(hue.toLowerCase()))
			return stereo;
		else
		if (stereo.endsWith("_" + givenHue) && hue.startsWith(givenHue + "-"))
			return stereo + hue.substring(givenHue.length());
		else
			return stereo + "-" + hue;
	}
	
	
	
	public String resolveHue(String givenHue, String rgb)
	{
		String hue 	     = givenHue;   
		Vector otherHues = (Vector)stereoHues.get(hue);  // e.g. Simpson --> [yellow]
		
		if (otherHues == null && Character.isUpperCase(givenHue.charAt(0)))
		{
			hue       = givenHue.toLowerCase(); // e.g.  Wood --> wood
			otherHues = (Vector)stereoHues.get(hue);  // e.g. wood --> [brown], moss --> [green]
		}
		
		if (otherHues == null) return givenHue;
		
		for (int rh = 0; rh < otherHues.size(); rh++)
		{
			String other    = (String)otherHues.elementAt(rh); // e.g. wood --> brown, moss --> green
			
			String otherRGB = getRGBFor(hue, other);  // e.g.  wood,brown --> 663300
			
			if (rgb.equals(otherRGB)) 
				return givenHue + "-" + other;
		}
		
		return givenHue;
	}
	

	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Given a property (Adjective) return a list of apt color stereotypes
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	
	public Vector getColorTermsForProperty(String property, BucketTable propertyModel)
	{
		Vector terms = null, candidates = propertyModel.get(property);
		
		if (candidates == null) return terms;
		
		for (int c = 0; c < candidates.size(); c++)
		{
			String candidate = (String)candidates.elementAt(c);
			
			if (isColorName(candidate))
			{
				if (terms == null) 
					terms = new Vector();
				
				if (candidate.hashCode() < property.hashCode()) // vary the ordering of elements in the list
					terms.add(candidate);
				else
					terms.insertElementAt(candidate, 0);
			}			
		}
		
		return terms;
	}
	
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Determine how close two  RGB colors are in 3D RGB space
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

    public int getDistanceBetween(String hex1, String hex2)
    {
    	if (hex1.startsWith("#")) hex1 = hex1.substring(1);
       	if (hex2.startsWith("#")) hex2 = hex2.substring(1);
           	
    	if (hex1.length() != 6 || hex2.length() != 6) 
    		return 0;
    	
    	int r1 = getHexValue(hex1.charAt(0))*16 + getHexValue(hex1.charAt(1));
       	int g1 = getHexValue(hex1.charAt(2))*16 + getHexValue(hex1.charAt(3));
      	int b1 = getHexValue(hex1.charAt(4))*16 + getHexValue(hex1.charAt(5));
      	           	
       	int r2 = getHexValue(hex2.charAt(0))*16 + getHexValue(hex2.charAt(1));
       	int g2 = getHexValue(hex2.charAt(2))*16 + getHexValue(hex2.charAt(3));
      	int b2 = getHexValue(hex2.charAt(4))*16 + getHexValue(hex2.charAt(5));
 
      	return (int)Math.sqrt((r1-r2)*(r1-r2) + (b1-b2)*(b1-b2) + (g1-g2)*(g1-g2));
    }
    
    
    private int getHexValue(char code)
    {
 		if (code >= 'A')
 			return 10 + code - 'A';
 		
 		if (code >= '0')
 			return code - '0';
 		
 		return 0;
    }
   
   
   
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Load the Lexicon
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	
	public Vector loadLexicon(String filename)
	{
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"));
		    
		    String line = null;
		    
		    while (input.ready())  // Read a line at a time
			{
				line = input.readLine();
				
				if (line == null || line.length() == 0 || line.startsWith("#"))
					continue;
				
				StringTokenizer tokens = new StringTokenizer(line, "\t\n,", false);
				
				while (tokens.hasMoreTokens())
				{
					String stereo = tokens.nextToken().trim().intern();
					String hue    = tokens.nextToken().trim().intern();
					String rgb    = tokens.nextToken().trim().intern();
					
					Vector huelist = (Vector)stereoHues.get(stereo);
					
					if (huelist == null)
					{
						huelist = new Vector();
						stereoHues.put(stereo, huelist);
						stereoList.add(stereo);
					}
					
					if (!huelist.contains(hue))
						huelist.add(hue);
					
					Vector stereoList = (Vector)hueStereos.get(hue);
					
					if (stereoList == null)
					{
						stereoList = new Vector();
						hueStereos.put(hue, stereoList);
					}
					
					if (!stereoList.contains(stereo))
						stereoList.add(stereo);
					
					stereoHueRGB.put(stereo + "-" + hue, rgb);
				}
			}

		   input.close();  // close connection to the data source
		}
		catch (IOException e)
		{
			System.out.println("Cannot find/load lexicon file: " + filename);
			
			e.printStackTrace();
		}
		
		return null;
	}
	

	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Main test stub
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	public static void main(String[] args)
	{
		String dir      = "/Users/tonyveale/Desktop/Lexical Resources/Moods/";
		
		ColorLexicon lexicon = new ColorLexicon(dir + "color stereotype lexicon.idx");
	
		System.out.println(lexicon.getStereoList());
		
		System.out.println(lexicon.getHuesFor("almond"));
		System.out.println(lexicon.getRGBFor("almond", "brown"));
		System.out.println(lexicon.getRGBFor("almond", "orange"));
		
		System.out.println(lexicon.getHuesFor("bee"));
		System.out.println(lexicon.getRGBFor("bee", "yellow"));
		System.out.println(lexicon.getHuesFor("Coriolanus_Snow"));
		
		System.out.println(lexicon.resolveStereoHue("Coriolanus_Snow", "Snow", lexicon.getRGBFor("snow", "white")));

		System.out.println(lexicon.resolveHue("Moss", "#4A5D23"));
	}
}
