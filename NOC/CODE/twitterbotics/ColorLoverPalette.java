package twitterbotics;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;

// A Color palette obtained from ColourLovers.com

public class ColorLoverPalette 
{
	private static Random RND		= new Random();
	
	private static String BaseURL   = "http://www.colourlovers.com/palette/";
	
	private static String palIntro  = "var _paletteColorsUI = '{\"_x\":380,\"_y\":195,\"_colors\":[";
	private static String palClose  = "],";
	
	private static String nameIntro = "<meta name=\"description\" content=\"";
	private static String nameClose = "\" />";
	private static String nameCredit= " color palette by ";
	
	private static String loveIntro = "<h5>Loves</h5><h4 class=\"pointer\" onclick=\"window.location.href='#comments';\">";
	private static String loveClose = "<";
	
	private Color[] colors 			= null;
	private Vector hexCodes			= new Vector();
	
	private String authorName		= null;
	private String paletteName		= null;
	
	private int numLoves			= 0;

	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Constructors
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	public ColorLoverPalette() 
	{
		this(RND.nextInt(1000000));
	}
	
	
	
	public ColorLoverPalette(int number) 
	{
		loadFromWeb(BaseURL + number);
	}
	
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Access methods
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	public int getNumLoves()
	{
		return numLoves;
	}
	
	public Color[] getColors()
	{
		return colors;
	}
	
	public Vector getHexCodes()
	{
		return hexCodes;
	}
	
	public String getName()
	{
		return paletteName;
	}
	
	
	public String getAuthor()
	{
		return authorName;
	}

	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Load Palette info from ColourLovers website
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	
	private void loadFromWeb(String pageURL)
	{
		try {
			URL website         = new URL(pageURL);
			
			URLConnection conn  = website.openConnection();
			
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
	        
			BufferedReader in   = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			
			String inputLine    = null;
			
			//System.out.println(pageURL);
			
			while ((inputLine = in.readLine()) != null) 
			{
				int rgbPos = inputLine.indexOf(palIntro);
				
				int rgbEnd = inputLine.lastIndexOf(palClose);
					
				if (rgbPos >= 0 && rgbEnd > rgbPos)
				{
					//System.out.println(inputLine.substring(rgbPos, rgbEnd));
					
					StringTokenizer tokens = new StringTokenizer(inputLine.substring(rgbPos + palIntro.length(), rgbEnd), "\" ,", false);
					
					int numCols = tokens.countTokens();
					
					colors = new Color[numCols];
					
					for (int c = 0; c < numCols; c++)
					{
						String hex = tokens.nextToken();
						hexCodes.add(hex);
						
						colors[c] = PaintedCanvas.getRGBColor(hex);
					}
				}
				
				int namePos = inputLine.indexOf(nameIntro);
				int credPos = inputLine.indexOf(nameCredit);
				int nameEnd = inputLine.indexOf(nameClose);
				
				//if (namePos > 0) System.out.println("@@@" + inputLine);
				
				if (namePos >= 0 && credPos > namePos && nameEnd > credPos)
				{
					paletteName = inputLine.substring(namePos + nameIntro.length(), credPos).trim();
					authorName  = inputLine.substring(credPos + nameCredit.length(), nameEnd).trim();
					
					if (authorName.endsWith(".")) 
						authorName = authorName.substring(0, authorName.length()-1);
				}
				
				int lovePos = inputLine.indexOf(loveIntro);
				int loveEnd = inputLine.indexOf(loveClose, lovePos + loveIntro.length());
				
				if (lovePos > 0 && loveEnd > lovePos) {
					numLoves = Integer.parseInt(inputLine.substring(lovePos + loveIntro.length(), loveEnd));
				}
			}
		} 
		catch (Exception e) 
		{			
			System.out.println("Cannot load color info from: " + pageURL);
			e.printStackTrace();
		}		
	}
	

	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Load a large range of palettes from ColorLovers and save to a file
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	public void loadAndSaveMany(String filename, int secondsToWait)
	{
		Dribbler dribby = new Dribbler();
		
		dribby.openDribbleFile(filename);
		
		int count = 0;
		
		while (true)
		{
			ColorLoverPalette pally = new ColorLoverPalette();
			
			String author  = cleanupName(pally.getAuthor());
			String name    = cleanupName(pally.getName());
			
			if (author == null || name == null) continue;
			
			Vector rgbs    = pally.getHexCodes();
			
			if (author == null || name == null || name.length() < 3 || name.length() > 20 || rgbs.size() < 2 || name.startsWith("_"))
			{
				System.out.println("Skip over: <" + pally.getAuthor() + ">  and  <" + name + ">       with  " + pally.getNumLoves() + " loves");
			
				waitFor(1);
				
				continue;
			}
			
			count++;
			
			dribby.printlnDribbleFile(count + ". " + author + "|" + name + "|" + pally.getNumLoves() + " " + rgbs);
			
			waitFor(secondsToWait);
		}
	}
	
	
	private void waitFor(int secondsToWait)
	{
		try {Thread.sleep(secondsToWait*1000);} catch (Exception e) {e.printStackTrace();}
	}
	
	
	private String cleanupName(String name)
	{
		if (name == null) return null;
		
		StringBuffer clean = new StringBuffer(name);
		
		for (int c = 0; c < name.length(); c++)
		{
			if (name.charAt(c) == ' ')
				clean.setCharAt(c, '_');
			
			if ("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ '-?!".indexOf(name.charAt(c)) < 0)
				return null;
		}
		
		return clean.toString();
	}
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Main application stub
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	public static void main(String[] args)
	{
		ColorLoverPalette pally = new ColorLoverPalette();
		
		System.out.println(pally.getName() + " by " + pally.getAuthor() + " (" + pally.getNumLoves() + " loves): " + pally.getHexCodes());
		
		Color[] colors = pally.getColors();
		
		for (int c = 0; c < colors.length; c++)
			System.out.println(colors[c]);
		
		String dir = "/Users/tonyveale/Desktop/Lexical Resources/Moods/";
		
		pally.loadAndSaveMany(dir + "RGB palettes.idx", 1);
	}
	
}
