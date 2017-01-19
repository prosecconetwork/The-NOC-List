package twitterbotics;

// Tony Veale 2016
// Generate a Canvas with a multicolored image in the style of Pollack, Rothko, etc., 

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Random;


public class PaintedCanvas extends Frame
{
	public static int MAX_EMOJI_SIZE		= 512; 
	public static int MID_EMOJI_SIZE		= 128; 
	public static int MIN_EMOJI_SIZE		= 64; 

	public static String EMOJI_TYPE 		= "png";
	public static String EMOJI_DIR  		=  "/Users/tonyveale/Dropbox/Emoji/";
	public static String EMOJI_BW_DIR		= "png_bw_64";
	
	private static final int PERTURB_PROB 	= 0;
	private static final int PERTURBATION 	= 100;
	
	private static Random RND = new Random();
	
    private static final int SCALE = 1, WIDTH=SCALE*1024, HEIGHT=SCALE*1024, HEIGHT_DIFF = SCALE*100, CHUNKY_DIV = SCALE*32, FINE_DIV = SCALE*16;
    private static final int NUM_CHECKS = 8, RING_THICK = 96, FRINGE_SIZE = 3, SMOOTHNESS = 3;
    
    private Color color1 = null, color2 = null, color3 = null, color4 = null; // the colors/states of the automaton image
       
    private String emojiUnicode 			= null; // set by client, 
    											    // causes Emoji graphic to be written into the image automaton directly
    
	private int emojiSize    				= MAX_EMOJI_SIZE;
	
	private int numberOfStyles 				= 17;  // the number of rendering strategies to choose from

	private int predeterminedChoice			= -1; // negative means style not predetermined
    
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Constructors
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

    public PaintedCanvas(String color1, String color2) 
    {
       this.color1 = getRGBColor(color1);
       this.color2 = getRGBColor(color2);
       
       prepareFrame();
    }
    
    
    public PaintedCanvas(Color color1, Color color2) 
    {
       this.color1 = color1;
       this.color2 = color2;
      
       prepareFrame();
    }
    
    
 
    public PaintedCanvas(String color1, String color2, String color3) 
    {
       this.color1 = getRGBColor(color1);
       this.color2 = getRGBColor(color2);
       this.color3 = getRGBColor(color3);
             
       prepareFrame();
    }
    
    
    
    public PaintedCanvas(String color1, String color2, String color3, String color4) 
    {
       this.color1 = getRGBColor(color1);
       this.color2 = getRGBColor(color2);
       this.color3 = getRGBColor(color3);
       this.color4 = getRGBColor(color4);
             
       prepareFrame();
    }
    
    
    
    public PaintedCanvas(Color color1, Color color2, Color color3) 
    {
       this.color1 = color1;
       this.color2 = color2;
       this.color3 = color3;
             
       prepareFrame();
    }
    

	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Specify a Unicode for an Emoji to incorporate into the painted image
    // The Emoji is woven into the cellular automaton that produces the image
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

    public void setEmojiUnicode(String emojiUnicode)
    {
    	this.emojiUnicode = emojiUnicode;
    }
    
    
    
    public void setEmojiUnicode(String emojiUnicode, int size)
    {
    	this.emojiUnicode = emojiUnicode;
    	
    	if (size == MIN_EMOJI_SIZE || size == MID_EMOJI_SIZE || MAX_EMOJI_SIZE == 512)
    		emojiSize    = size;
    }
    
    
    //--------------------------------------------------------------------------//
  	//--------------------------------------------------------------------------//
  	//  PRe-determine the choice of stype for the image
  	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
    
    public void predetermineStyle(int choice)
    {
    	predeterminedChoice = choice;
    }
    
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Painting the image (frame preparation and style selection)
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

    private void prepareFrame() 
    {
    	setSize(WIDTH, HEIGHT);   		
    }
    
    
    
    public void paint(Graphics g) 
    {
        // Setting white background
       
     	Color[] allStates = null;    	
    	    	   	
    	if (color3 == null)
    	{
    		color3       = mixColors(color1, color2); 
    		allStates    = new Color[3];       	
    	}
    	else
    	{
    		allStates 	 = new Color[4];   
    		
    		if (color4 == null)
    			color4 = mixColors(color1, color2, color3);
    		
    		allStates[3] = color4;
    	}
    	
   		allStates[0] = color1;	
		allStates[1] = color2; 
		allStates[2] = color3;

    	int extra = allStates.length-1;
    	
		if (distanceBetweenColors(allStates[extra], Color.black) > distanceBetweenColors(allStates[extra], Color.white))
			allStates[extra] = allStates[extra].brighter();
		else
			allStates[extra] = allStates[extra].darker();
    	
       	if (emojiUnicode != null)
       		numberOfStyles *= 2;
       		      	
    	int choice 	  = predeterminedChoice; 
    	
    	if (choice < 0)
    		choice = RND.nextInt(numberOfStyles);
    	       
    	if (choice == 0)
    		paintHorizontalHalfSplit(g, bigOrSmallAutomaton(allStates), bigOrSmallAutomaton(allStates));
    	else
    	if (choice == 1)
    		paintHorizontalTriSplit(g, bigOrSmallAutomaton(allStates), bigOrSmallAutomaton(allStates));
    	else
    	if (choice == 2)
    		paintVerticalTriSplit(g, bigOrSmallAutomaton(allStates), bigOrSmallAutomaton(allStates));
    	else
    	if (choice == 3)
    		paintYinYang(g, bigOrSmallAutomaton(allStates), color1, color2, color3); 
    	else
    	if (choice == 4)
    		paintGrid(g, bigOrSmallAutomaton(allStates), bigOrSmallAutomaton(allStates));
    	else
    	if (choice == 5)
    		paintExplosion(g, bigOrSmallAutomaton(allStates), allStates);
    	else
    	if (choice == 6)
    		paintConcentricRings(g, bigOrSmallAutomaton(allStates), bigOrSmallAutomaton(allStates));
    	else
    	if (choice == 7)
    		paintVerticalHalfSplit(g, bigOrSmallAutomaton(allStates), bigOrSmallAutomaton(allStates));
    	else
    	if (choice == 8)
    		paintDonut(g, bigOrSmallAutomaton(allStates), bigOrSmallAutomaton(allStates), color1, color2, color3);
    	else
    	if (choice == 9)
    		paintEye(g, bigOrSmallAutomaton(allStates), color1, color2, color3);
    	else
        if (choice == 10)
        	paintDiagonalalDownSplit(g, bigOrSmallAutomaton(allStates), bigOrSmallAutomaton(allStates));
    	else
    	if (choice == 11)
    		paintDiagonalalUpSplit(g, bigOrSmallAutomaton(allStates), bigOrSmallAutomaton(allStates));
    	else
    	if (choice == 12)
           paintHourglassSplit(g, bigOrSmallAutomaton(allStates), bigOrSmallAutomaton(allStates));
        else
    	if (choice == 13)
    		paintCenterpiece(g, bigOrSmallAutomaton(allStates), bigOrSmallAutomaton(allStates));
    	else
    	if (choice == 14)
    		paintCellularCombo(g, bigOrSmallAutomaton(allStates), bigOrSmallAutomaton(allStates));
    	else
    	if (choice % 2 == 0)
    		paintCellularAutomaton(g, bigOrSmallAutomaton(allStates));
    	else
    		paintAsymmetricAutomaton(g, bigOrBigAutomaton(allStates));
    }
    
    
  
    
    private ColorAutomaton bigOrSmallAutomaton(Color[] allStates)
    {
    	if (RND.nextInt(2) == 0 || emojiUnicode != null)
    		return new ColorAutomaton(allStates, HEIGHT/CHUNKY_DIV, WIDTH/CHUNKY_DIV);
    	
    	ColorAutomaton celly =  new ColorAutomaton(allStates, HEIGHT/FINE_DIV, WIDTH/FINE_DIV);
    	
    	if (celly.searchForGoodConfiguration())
    		return celly;
    	
    	return new ColorAutomaton(allStates, HEIGHT/CHUNKY_DIV, WIDTH/CHUNKY_DIV);
    }
    
    
    
    private ColorAutomaton bigOrBigAutomaton(Color[] allStates)
    {
    	ColorAutomaton celly =  new ColorAutomaton(allStates, HEIGHT/FINE_DIV, WIDTH/FINE_DIV);
    	
    	if (!celly.searchForGoodConfiguration())
    		celly.searchForGoodConfiguration(); // try again
    	
    	if (!celly.searchForGoodConfiguration() && emojiUnicode != null)
    		celly.searchForGoodConfiguration(); // try again
    	
    	return celly;
    }
    
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Brighten or Darken a color depending on its existing brightness
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
    
    private Color[] createFringeColors(Color[] colors)
    {
    	Color[] fringe = new Color[colors.length];
    	
    	for (int c = 0; c < colors.length; c++)
    		fringe[c] = getFringeColor(colors[c]);
    		
    	return fringe;
    }
    
    
    private Color getFringeColor(Color color)
    {
    	if (distanceBetweenColors(color, Color.black) > distanceBetweenColors(color, Color.white))
			return color.darker().darker().darker(); 
		else
			return color.brighter().brighter().brighter(); 
    }
    
    
    private Color getBrightestOf(Color[] states)
    {
    	Color brightest = states[0];
    	
    	for (int s = 1; s < states.length; s++)
    		if (distanceBetweenColors(states[s], Color.black) > distanceBetweenColors(brightest, Color.black))
	    		brightest = states[s];
   	
    	return brightest;
    }
    
    
    
    private Color getDarkestOf(Color[] states)
    {
    	Color darkest = states[0];
    	
    	for (int s = 1; s < states.length; s++)
    		if (distanceBetweenColors(states[s], Color.black) < distanceBetweenColors(darkest, Color.black))
    			darkest = states[s];
   	
    	return darkest;
    }
    

    private Color brightenOrDarken(Color[] colors)
    {
    	Color candidate = null, bestCandidate = brightenOrDarken(colors[RND.nextInt(colors.length)]);
    	
    	int currScore = 0, bestScore = 0;
    	
    	for (int i = 0; i < colors.length; i++)
    	{
    		candidate = colors[i].brighter();
    		
    		for (int pass = 0; pass < 2; pass++) 
    		{
	    		currScore = 0;
	    		
	    		for (int j = 0; j < colors.length; j++)
	    		{
	    			if (j == i) continue;
	    			
	    			int dist   = distanceBetweenColors(candidate, colors[j]);
	    			
	    			currScore += dist*dist;    			
	    		}
	    		
	    		if (currScore > bestScore) {
	    			bestScore     = currScore;
	    			bestCandidate = candidate;
	    		}
	    		
	    		candidate = colors[i].darker();
    		}
    	}
    	
    	return bestCandidate;

    }
    
    
    private Color brightenOrDarken(Color color)
    {
    	if (distanceBetweenColors(color, Color.white) < distanceBetweenColors(color, Color.black))
    		return color.darker();
    	else
    		return color.brighter();
    }
    
    

	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Brighten or Darken a color depending on its existing brightness
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
    
    
    
    private int distanceBetweenColors(Color color1, Color color2)
    {
    	return (int)Math.sqrt((color1.getRed()-color2.getRed())*((color1.getRed()-color2.getRed())) + 
    						  (color1.getGreen()-color2.getGreen())*((color1.getGreen()-color2.getGreen())) +
    						  (color1.getBlue()-color2.getBlue())*((color1.getBlue()-color2.getBlue())));
    }
    
    
    private int distanceBetweenColors(Color color1, int RGB_INT_A)
    {
    	int alpha 	= (RGB_INT_A >> 24) & 0xFF;		
		int red2	= (RGB_INT_A >> 16) & 0xFF;
		int green2 	= (RGB_INT_A >>  8) & 0xFF;
		int blue2 	= RGB_INT_A & 0xFF;
		
    	return (int)Math.sqrt((color1.getRed()-red2)*((color1.getRed()-red2)) + 
    						  (color1.getGreen()-green2)*((color1.getGreen()-green2)) +
    						  (color1.getBlue()-blue2)*((color1.getBlue()-blue2)));
    }
    
    
    private Color[] remotestColorPair(Color[] given)
    {
    	if (given.length == 2)
    		return given;
    	
    	Color[] pair = new Color[2];
    	
    	int maxDistance = -1;
    	
    	for (int i = 0; i < given.length; i++)
    		for (int j = i+1; j < given.length; j++)
    		{
    			int dist = distanceBetweenColors(given[i], given[j]);
    			
    			if (dist > maxDistance)
    			{
    				maxDistance = dist;
    				pair[0] = given[i];
    				pair[1] = given[j];
    			}
    		}
    	
    	return pair;
    }
    
    
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Paint an Emoji graphics file onto a graphics context
    // Assume PNG format for Emoji files
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

    private BufferedImage getEmojiImage(String emojiFilename)
    {
    	int divide = emojiFilename.indexOf((int)'|');
    	
    	if (divide > 0) {
    		String ext  = emojiFilename.substring(emojiFilename.lastIndexOf((int)'.'));
    		String path = emojiFilename.substring(0, emojiFilename.lastIndexOf((int)'/')+1);
    		
    		System.out.println(emojiFilename.substring(0, divide) + ext);
    		System.out.println(path + emojiFilename.substring(divide + 1));
    		
    		return combineEmojiImages(getEmojiImage(emojiFilename.substring(0, divide) + ext),
    								  getEmojiImage(path + emojiFilename.substring(divide + 1)));
    	}
    		
    	BufferedImage emojiIMG = null;
    	
    	try { 
 	    	emojiIMG = ImageIO.read(new File(emojiFilename));
	
	    	BufferedImage withAlpha = new BufferedImage(emojiIMG.getWidth(), emojiIMG.getHeight(), 
	    												BufferedImage.TYPE_INT_ARGB);
		
	    	Graphics2D alphaG = withAlpha.createGraphics();
	    	alphaG.drawImage(emojiIMG, 0, 0, null);
	    	alphaG.dispose();
    	}
    	catch (IOException e) 
    	{
    		System.out.println(emojiFilename);
    		
    		e.printStackTrace();
    	}
    	
    	return emojiIMG;
    }
    
    
    
    private BufferedImage combineEmojiImages(BufferedImage image1, BufferedImage image2)
    {
    	for (int x = 0; x < image2.getWidth(); x++)
    		for (int y = 0; y < image2.getHeight(); y++)
    		{
    			int overlapRGB 	    = image2.getRGB(x, y);    			
    			int overlapAlpha 	= (overlapRGB >> 24) & 0xFF;
    			
    			if (overlapAlpha > 0)
    				image1.setRGB(x, y, overlapRGB);
    		}
    	
    	return image1;
    }
    
    
    
    private void paintEmoji(String emojiFilename, ColorAutomaton celly, int cX, int cY)
    {
    	BufferedImage emojiIMG = getEmojiImage(emojiFilename);
    	
    	paintEmoji(emojiIMG, celly, cX, cY);
    }	
   
    
    
    
    // Insert the pixels of an emoji into a Color cellular automaton
    
    
    private void paintEmoji(BufferedImage multiIMG, ColorAutomaton celly, int cX, int cY)
    { 	       
    	int startX = cX-multiIMG.getWidth()/2, startY = cY-multiIMG.getHeight()/2;
    	int   endX = cX + multiIMG.getWidth()/2, endY = cY + multiIMG.getHeight()/2;
    	
    	Color[] coreStates = celly.getStates();
    	
    	int[] colorCounts  = new int[5000];
   	
       	Hashtable colorMap = new Hashtable();
             	
       	for (int x = 0; x < multiIMG.getWidth(); x++)
    		for (int y = 0; y < multiIMG.getHeight(); y++)
    		{
    			int multiRGB 	= multiIMG.getRGB(x, y);    			
    			int multiAlpha 	= (multiRGB >> 24) & 0xFF;
    			int multiRed 	= (multiRGB >> 16) & 0xFF;
    			int multiGreen 	= (multiRGB >>  8) & 0xFF;
    			int multiBlue 	= multiRGB & 0xFF;
    			
    			if (multiAlpha == 0) continue;
    			
				Color givenColor = new Color(multiRed, multiGreen, multiBlue, multiAlpha);
				
				if (distanceBetweenColors(givenColor, Color.white) <= 50) {
					givenColor = celly.getGenX(y + startY, x + startX).brighter();
					colorMap.put(givenColor, givenColor);
				}
				else 				
				if (distanceBetweenColors(givenColor, celly.getGenX(y + startY, x + startX)) <= 10)
					continue;
				
				Color nearest    = nearestColorTo(givenColor, celly.getStates());
				
				if (distanceBetweenColors(givenColor, nearest) < 10) {
					celly.setGenX(y + startY, x + startX, nearest);
						
					int stateNum = celly.getStateNumber(nearest);
					
					if (stateNum < colorCounts.length)
						colorCounts[stateNum]++;
				}
				else
				{
					celly.addState(givenColor);
					celly.setGenX(y + startY, x + startX, givenColor);
					
					int stateNum = celly.getStateNumber(givenColor);
					
					if (stateNum < colorCounts.length)
						colorCounts[stateNum]++;
				} 		
    		}
    	
    	
    	// Now blend the colours of the emoji with those of the automaton
    	// Loop through the colors of the EMoji in descending order of usage in the image
    	// so map most popular colors first
    	
    	
    	Color[] newStates = celly.getStates(), palette = (Color[])coreStates.clone();
    	
    	Dribbler.randomize(palette);
    	
    	int nextChoice = 0;
    	
    	for (int c = coreStates.length; c < newStates.length; c++)
    	{
    		int   mostPopularCount  = -1;
    		int   mostPopularChoice = 0;
    		
    		Color oldState = newStates[c];
    		
    		for (int c2 = c; c2 < Math.min(colorCounts.length, newStates.length); c2++)
    		{
    			if (colorCounts[c2] > mostPopularCount)
    			{
    				mostPopularCount  = colorCounts[c2];
    				mostPopularChoice = c2;
    			}  			
    		}
    		
    		newStates[c] = newStates[mostPopularChoice];  // make sure the next color we visit
    		newStates[mostPopularChoice] = oldState;      // is the most popular unvisited color
    		
    		Color coreMatch  = (Color)colorMap.get(newStates[c]);
    		
    		if (coreMatch == null)
    			coreMatch = palette[(nextChoice++)%palette.length];
    		
    		System.out.println(mostPopularCount + ": " + newStates[c] + " --> " + coreMatch);
    		
    		Color admixture  = mixColors(newStates[c], coreMatch, 1, 99);
    		
    		colorMap.put(newStates[c], admixture);
    		
    		newStates[c]     = admixture;    				
    	}
    	    	
    	for (int x = 0; x < multiIMG.getWidth(); x++)
    		for (int y = 0; y < multiIMG.getHeight(); y++)
    		{
    			int multiAlpha 	= (multiIMG.getRGB(x, y) >> 24) & 0xFF;
    			
    			if (multiAlpha == 0) continue;
    			
    			Color lookup = (Color)colorMap.get(celly.getGenX(y + startY, x + startX));
    			
    			if (lookup != null)
    				celly.setGenX(y + startY, x + startX, lookup);
    		}
    	
    }
    
    
    
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Find the colors nearest or farthest away from a given reference point
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

    
    
    private Color nearestColorTo(Color given, Color[] choices)
    {
    	Color nearest = choices[0];
    	
    	for (int c  = 1; c < choices.length; c++)
    		if (distanceBetweenColors(choices[c], given) < distanceBetweenColors(choices[c], nearest))
    			nearest = choices[c];
    	
    	return nearest;
    }
    
    
    
    private Color furthestColorFrom(Color given, Color[] choices)
    {
    	Color furthest = choices[0];
    	
    	for (int c  = 1; c < choices.length; c++)
    		if (distanceBetweenColors(choices[c], given) > distanceBetweenColors(choices[c], furthest))
    			furthest = choices[c];
    	
    	return furthest;
    }
    
    
    
    private Color furthestColorFrom(Color given1,  Color given2, Color[] choices)
    {
    	Color furthest = choices[0];
    	
    	int bestDistance = distanceBetweenColors(furthest, given1) + distanceBetweenColors(furthest, given2);
    	
    	for (int c  = 1; c < choices.length; c++)
    	{
    		int localDistance =  distanceBetweenColors(choices[c], given1) + distanceBetweenColors(choices[c], given2);
    	
    		if (localDistance > bestDistance) {
    			furthest = choices[c];
    			
    			bestDistance = localDistance;
    		}
    	}
    	
    	return furthest;
    }
    
   
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Paint a Cellular Automaton without other visual trickery
    // (except for lateral and vertical symmetry)
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//


    
    private void paintCellularAutomaton(Graphics g, ColorAutomaton celly)
    {   
    	celly.searchForGoodConfiguration();
    	
    	celly.printGenerations();

    	System.out.println("\nSmallest contribution of any state: " + celly.getSmallestColorPercentage() + "%");
    	System.out.println("\nAverage Color density: " + celly.getStateDensity()+ "%");
    	System.out.println("\nMost popular rules are: " + celly.getOrderedRules());
    	
    	celly.doubleResolutionUntil(MIN_EMOJI_SIZE, MIN_EMOJI_SIZE);
    	celly.mirrorResolution();
    	
     	celly.doubleResolutionUntil(HEIGHT, WIDTH);
    	
    	if (emojiUnicode != null) 
    		paintEmoji(EMOJI_DIR + EMOJI_TYPE + "_" + emojiSize + "/" + emojiUnicode + "." + EMOJI_TYPE, celly, WIDTH/2, HEIGHT/2);
        
        celly.raiseColors(createFringeColors(celly.getStates()), FRINGE_SIZE);
        celly.smoothColors(SMOOTHNESS);
 
        
        for (int x = 0; x < WIDTH; x++)
        {
         	for (int y = 0; y < HEIGHT; y++)
 	        {
 	        	g.setColor(perturbColor(celly.getGenX(y, x)));

 	        	g.fillRect(x, y, 1, 1);
 	        }
        }
    }
    
    
    
    private BufferedImage bufferEmojiAutomaton(ColorAutomaton celly)
    {
    	BufferedImage emoji = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    	
    	paintEmoji(EMOJI_DIR + EMOJI_BW_DIR + "/" + emojiUnicode + "." + EMOJI_TYPE, celly, WIDTH/2, HEIGHT/2);
        
        celly.doubleResolutionUntil(HEIGHT, WIDTH);
                
        Graphics2D emojiGraphics = emoji.createGraphics();
        
        for (int x = 0; x < WIDTH; x++)
        	for (int y = 0; y < HEIGHT; y++)
	        {
        		emojiGraphics.setColor(perturbColor(celly.getGenX(y, x)));	 
	        	
        		emojiGraphics.fillRect(x, y, 1, 1);
	        }	        
               
        return emoji; 
    }
    
    
    
    private BufferedImage scaleImageBuffer(BufferedImage fullSize, int newWIDTH, int newHEIGHT)
    {
        Image tmp = fullSize.getScaledInstance(newWIDTH, newHEIGHT, Image.SCALE_SMOOTH);
        BufferedImage reducedSize = new BufferedImage(newWIDTH, newHEIGHT, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = reducedSize.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        
        return reducedSize;
    }
    
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Paint a chunky cellular automaton plainly, without anti-aliasing 
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
    
    private void paintChunkyCellularAutomaton(Graphics g, ColorAutomaton celly)
    {   
//    	celly.searchForGoodConfiguration();
    	
    	celly.printGenerations();

     	celly.doubleResolutionUntil(HEIGHT/2, WIDTH/2);

     	System.out.println("\nSmallest contribution of any state: " + celly.getSmallestColorPercentage() + "%");
    	System.out.println("\nAverage Color density: " + celly.getStateDensity()+ "%");
    	System.out.println("\nMost popular rules are: " + celly.getOrderedRules());
    	
    	int w = WIDTH/celly.getNumCells(), h = HEIGHT/celly.getNumGenerations();
    	         	
     	for (int x = 0; x < celly.getNumCells(); x++)
        {
         	for (int y = 0; y < celly.getNumGenerations(); y++)
 	        {
 	        	g.setColor(celly.getGenX(y, x));

 	        	g.fillRect(x*w, y*h, w, h);
 	        }
        }
    }

    
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Paint a combination of two Cellular Automata on the same canvas
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

    
    private void paintCellularCombo(Graphics g, ColorAutomaton celly1, ColorAutomaton celly2)
    {
    	 celly1.searchForGoodConfiguration();
    	
   	   	 while (celly1.getPercentageOfHole() > 20)
   	   	 {
 	 		celly1.run();
 	 		celly1.searchForGoodConfiguration();
   	   	 }

   	   	 celly1.printGenerations();
      	 
      	 celly1.doubleResolutionUntil(HEIGHT/2, WIDTH/2);
    	    	
 
      	 celly2.searchForGoodConfiguration();
   	   	    
    	 celly2.printGenerations();

    	 celly2.doubleResolutionUntil(HEIGHT/2, WIDTH/2);
 
         
    	 int outer      = (HEIGHT-RING_THICK/2)/2;
    	 
    	 boolean square = RND.nextBoolean();
     	      	 	
    	 for (int x = 0; x < WIDTH/2; x++)
         {
          	for (int y = 0; y < HEIGHT/2; y++)
  	        {
 		    	int	 d          = distanceTo(x, y, WIDTH/2, HEIGHT/2);
		    	
		    	boolean inside = d < outer; // inside a circle
		    	
		    	if (square)  // inside a square instead of a circle
		    		inside = x > WIDTH/2 - 4*outer/5 && x < WIDTH/2 + 4*outer/5 && y > HEIGHT/2 - 3*outer/4 && y < HEIGHT/2 + 3*outer/4;
		    	
		    	if (inside)
		    		celly1.setGenX(y, x, celly2.getGenX(y, x));
		    }
		}
    	 
    	 
         if (emojiUnicode != null) {
         	celly1.mirrorResolution();
         	paintEmoji(EMOJI_DIR + EMOJI_TYPE + "_" + emojiSize + "/" + emojiUnicode + "." + EMOJI_TYPE, celly1, WIDTH/2, HEIGHT/2);
         }
		 
      	 celly1.raiseColors(createFringeColors(celly1.getStates()), FRINGE_SIZE);
      	 celly1.smoothColors(SMOOTHNESS);

      	 for (int x = 0; x < WIDTH; x++)
         {
      		 for (int y = 0; y < HEIGHT; y++)
      		 {
 	        	g.setColor(perturbColor(celly1.getGenX(y, x)));

 	        	g.fillRect(x, y, 1, 1);
      		 }
         }   
    }
    
    
    

	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Paint a Yin Yang symbol
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

    private void paintYinYang(Graphics g, ColorAutomaton celly, Color color1, Color color2, Color color3)
    {
    	int outer    = (HEIGHT-RING_THICK)/2, middle = outer/2, inner = outer/5;  	
    	int middleUX = WIDTH/2, middleUY = RING_THICK/2 + middle; 	
    	int middleLX = WIDTH/2, middleLY = RING_THICK/2 + middle*3;
    	int innerUX  = WIDTH/2, innerUY = RING_THICK/2 + middle;   	
    	int innerLX  = WIDTH/2, innerLY = RING_THICK/2 + middle*3;
    	     	
    	Color[] yinyang   = remotestColorPair(celly.getStates());
    	
     	Color[] yinColors = new Color[3];
     	yinColors[0] = yinyang[0]; yinColors[1] = yinColors[0].darker(); yinColors[2] = yinColors[0].brighter(); 
    	
     	Color[] yangColors = new Color[3];
     	yangColors[0] = yinyang[1]; yangColors[1] = yangColors[0].darker(); yangColors[2] = yangColors[0].brighter();
     	
     	ColorAutomaton yin  = new ColorAutomaton(yinColors, celly.getNumGenerations(), celly.getNumCells());
     	
     	yin.searchForGoodConfiguration();
    	 
     	ColorAutomaton yang = new ColorAutomaton(yangColors, celly.getNumGenerations(), celly.getNumCells());
     	
     	yang.searchForGoodConfiguration();
       	      	
     	celly.searchForGoodConfiguration(celly.getStateNotIn(yinyang));
     	
     	yin.mirrorResolution();
        yang.mirrorResolution();
    	celly.mirrorResolution();
    	
    	if (emojiUnicode != null) 
     	{   		 
	 		setEmojiUnicode(emojiUnicode, MID_EMOJI_SIZE);
	 		 
	 		yin.doubleResolutionUntil(HEIGHT, WIDTH);
	 		yang.doubleResolutionUntil(HEIGHT, WIDTH);
	 		 
	 		paintEmoji(EMOJI_DIR + EMOJI_TYPE + "_" + emojiSize + "/" + emojiUnicode + "." + EMOJI_TYPE, 
	 				   yin, innerLX, innerLY);
	 		 
	 		paintEmoji(EMOJI_DIR + EMOJI_TYPE + "_" + emojiSize + "/" + emojiUnicode + "." + EMOJI_TYPE, 
				       yang, innerUX, innerUY);
	 	}
     	
     	celly.printGenerations();
     	
      	celly.doubleResolutionUntil(HEIGHT, WIDTH);
        
     	 
     	for (int x = 0; x < WIDTH; x++)
        {
        	for (int y = 0; y < HEIGHT; y++)
	        {
	        	if (distanceTo(x, y, innerLX, innerLY) <= inner)    
	        		celly.setGenX(y, x, yin.getGenX(y, x));
	        	else
	        	if (distanceTo(x, y, innerUX, innerUY) <= inner)    
	        		celly.setGenX(y, x, yang.getGenX(y, x));
	        	else
	        	if (distanceTo(x, y, middleUX, middleUY) <= middle)    
	        		celly.setGenX(y, x, yin.getGenX(y, x));
	        	else
	        	if (distanceTo(x, y, middleLX, middleLY) <= middle)    
	        		celly.setGenX(y, x, yang.getGenX(y, x));
	        	else
	        	if (distanceTo(x, y, WIDTH/2, HEIGHT/2) <= outer)
	        	{
	        		if (x < WIDTH/2)
	        			celly.setGenX(y, x, yang.getGenX(y, x));	
	        		else
	        			celly.setGenX(y, x, yin.getGenX(y, x));	
	        	}
	        }
        } 
     	
   			 
     	celly.raiseColors(createFringeColors(celly.getStates()), FRINGE_SIZE);
     	celly.smoothColors(SMOOTHNESS);
     	
     	for (int x = 0; x < WIDTH; x++)
        {
     		 for (int y = 0; y < HEIGHT; y++)
     		 {
	        	g.setColor(perturbColor(celly.getGenX(y, x)));

	        	g.fillRect(x, y, 1, 1);
     		 }
        }  

        /*
        int strokeW = 12;
        
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(strokeW));
        g2.setColor(Color.black);
 
        g2.drawRect(0, 0, WIDTH, HEIGHT);
        
        g2.drawOval(WIDTH/2 - outer, HEIGHT/2 - outer, outer*2, outer*2);
        g2.drawOval(innerUX - inner - strokeW/2 + 1, innerUY - inner - strokeW/2, inner*2+strokeW/2, inner*2+strokeW/2);
        g2.drawOval(innerLX - inner - strokeW/2 + 1, innerLY - inner - strokeW/2, inner*2+strokeW/2, inner*2+strokeW/2);
        
        g2.draw(new Arc2D.Double(middleUX-middle, middleUY-middle, middle*2, middle*2, 90, 180, Arc2D.OPEN));
        g2.draw(new Arc2D.Double(middleLX-middle, middleLY-middle, middle*2, middle*2, 90, -180, Arc2D.OPEN));
        */
   }
    
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Paint a torus with icing (a donut)
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

    private void paintDonut(Graphics g, ColorAutomaton celly1, ColorAutomaton celly2, Color color1, Color color2, Color color3)
    {
    	int outer = (HEIGHT-RING_THICK)/2, middle = (3*outer)/4, inner = outer/4;
    	   	
     	celly1.searchForGoodConfiguration();
   	
    	while (celly1.getPercentageOfHole() > 20)
	 	{
	   		celly1.run();
	   		celly1.searchForGoodConfiguration();
	 	}
    	
     	celly1.printGenerations();
     	 
     	celly1.doubleResolutionUntil(HEIGHT/2, WIDTH/2);
     	
     	if (emojiUnicode != null) 
     	{
     		setEmojiUnicode(emojiUnicode, MID_EMOJI_SIZE);
     		
        	celly1.mirrorResolution();
    		paintEmoji(EMOJI_DIR + EMOJI_TYPE + "_" + emojiSize + "/" + emojiUnicode + "." + EMOJI_TYPE, celly1, WIDTH/2, HEIGHT/2);
        }
     	
     	celly1.raiseColors(createFringeColors(celly1.getStates()), FRINGE_SIZE);
     	celly1.smoothColors(SMOOTHNESS);
    	
    	celly2.searchForGoodConfiguration();
       	
     	celly2.printGenerations();
     	 
     	celly2.doubleResolutionUntil(HEIGHT/2, WIDTH/2);
     	
     	celly2.raiseColors(createFringeColors(celly2.getStates()), FRINGE_SIZE);
     	celly2.smoothColors(SMOOTHNESS);
     	
       
        for (int x = 0; x < WIDTH; x++)
        {       	        	
	        for (int y = 0; y < HEIGHT; y++)
	        {
	      		int d = distanceTo(x, y, WIDTH/2, HEIGHT/2);
	        	
	        	if (d <= inner)    
	        		if (emojiUnicode == null)
	        			g.setColor(celly1.getGenX(y, x).darker());
	        		else
	        			g.setColor(celly1.getGenX(y, x));
	        	else
	        	if (d <= 3*inner/2 +  RND.nextInt(inner))
		        	g.setColor(celly2.getGenX(y, x).darker().darker());
	        	else
	        	if (d <= middle + RND.nextInt(outer/6))
	        		g.setColor(celly2.getGenX(y, x).brighter());	
	        	else
	        	if (d <= outer)
	        		g.setColor(celly2.getGenX(y, x).darker());
	        	else
	        	if (d <= outer + RND.nextInt(outer/12))
		        	g.setColor(celly1.getGenX(y, x).darker().darker());
	        	else
	        		g.setColor(celly1.getGenX(y, x));	        	
	        	
	        	g.fillRect(x, y, 1, 1);
	        }
        }  
        
     	
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(12));
        g2.setColor(Color.black);
 
        g2.drawRect(0, 0, WIDTH, HEIGHT);
        
        g2.drawOval(WIDTH/2-inner, HEIGHT/2 - inner, inner*2, inner*2);
        
        g2.drawOval(WIDTH/2-outer, HEIGHT/2 - outer, outer*2, outer*2);
    }
    
    
    
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Paint a torus with icing (a donut)
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

    private void paintEye(Graphics g, ColorAutomaton celly0, Color color1, Color color2, Color color3)
    {
    	int outer = (HEIGHT-RING_THICK/2)/2, inner = outer/4;
    	
    	int vr = outer, hr = outer*2;
    	
    	celly0.searchForGoodConfiguration();
   	
     	celly0.printGenerations();
     	 
     	celly0.doubleResolutionUntil(HEIGHT/2, WIDTH/2);
     	
     	Color[] states1 = new Color[3];
     	states1[0] = color1; 
     	states1[1] = color1.darker();
     	states1[2] = color1.brighter();
     	
     	Color[] states2 = new Color[3];
     	states2[0] = color2; 
     	states2[1] = color2.darker();
     	states2[2] = color2.brighter();
    	
    	Color[] states3 = new Color[3];
     	states3[0] = color3; 
     	states3[1] = color3.darker();
     	states3[2] = color3.brighter();
 
     	ColorAutomaton celly1 = new ColorAutomaton(states1, HEIGHT/CHUNKY_DIV, WIDTH/CHUNKY_DIV);
     	
     	celly1.searchForGoodConfiguration();
     	celly1.doubleResolutionUntil(HEIGHT/2, WIDTH/2);
 
     	ColorAutomaton celly2 = new ColorAutomaton(states2, HEIGHT/CHUNKY_DIV, WIDTH/CHUNKY_DIV);
     	
     	celly2.searchForGoodConfiguration();
     	celly2.doubleResolutionUntil(HEIGHT/2, WIDTH/2);
 
    	ColorAutomaton celly3 = new ColorAutomaton(states3, HEIGHT/CHUNKY_DIV, WIDTH/CHUNKY_DIV);
    	
    	celly3.searchForGoodConfiguration();
    	
	   	while (celly3.getPercentageOfHole() > 20)
	 	{
	   		celly3.run();
	 		celly3.searchForGoodConfiguration();
	 	}
    	
    	celly3.doubleResolutionUntil(HEIGHT/2, WIDTH/2);
    

     	for (int x = 0; x < WIDTH/2; x++)
        {
        	for (int y = 0; y < HEIGHT/2; y++)
	        {
	        	int d = distanceTo(x, y, WIDTH/2, HEIGHT/2);
	        	
	        	int elliptical  = ((x - WIDTH/2)*(x - WIDTH/2)*1000)/(hr*hr) + 
	        				      ((y - HEIGHT/2)*(y - HEIGHT/2)*1000)/(vr*vr);
	        	
	        	if (d <= inner)   
	        		celly0.setGenX(y, x, celly3.getGenX(y, x));
	        	else
	        	if (d <= outer)
	        		celly0.setGenX(y, x, celly0.getGenX(y, x));
	        	else 
	        	if (elliptical < WIDTH)
	        		celly0.setGenX(y, x, celly1.getGenX(y, x));
	        	else
	        		celly0.setGenX(y, x, celly2.getGenX(y, x));
	        }
        }  
     	
       
       	if (emojiUnicode != null) 
    	{        	
         	celly0.mirrorResolution();

         	setEmojiUnicode(emojiUnicode, MAX_EMOJI_SIZE);
        	
    		paintEmoji(EMOJI_DIR + EMOJI_TYPE + "_" + emojiSize + "/" + emojiUnicode + "." + EMOJI_TYPE, celly0, WIDTH/2, HEIGHT/2);
        }
       	
     	celly0.raiseColors(createFringeColors(celly0.getStates()), FRINGE_SIZE);
     	celly0.smoothColors(SMOOTHNESS);
     	
     	// now draw combined automaton
     	
     	for (int x = 0; x < WIDTH; x++)
        {
        	for (int y = 0; y < HEIGHT; y++)
	        {
        		g.setColor(celly0.getGenX(y, x));
        		g.fillRect(x, y, 1, 1);
	        }
        } 	
   }
    
	
    
    
    
    private int distanceTo(int x1, int y1, int x2, int y2)
    {
    	return (int)Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
    }
    

	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Paint concentric rings of alternating colors
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

    
    private void paintConcentricRings(Graphics g, ColorAutomaton celly1, ColorAutomaton celly2)
    {
   	 	celly1.searchForGoodConfiguration();
   	
     	celly1.printGenerations();
     	 
     	celly1.doubleResolutionUntil(HEIGHT/2, WIDTH/2);
   	    	
  	   	celly2.searchForGoodConfiguration();
 
  	   	celly2.printGenerations();

  	   	celly2.doubleResolutionUntil(HEIGHT/2, WIDTH/2);
  	   	
  	   	// now interleave rings of celly2 into celly1
  	   	
		for (int x = 0; x < WIDTH; x++)
		{
	        for (int y = 0; y < HEIGHT; y++)
	        {
	        	int dist   		= distanceTo(x, y, WIDTH/2, HEIGHT/2);
	        	int layer  		= dist/(RING_THICK);
	        		        
	        	if (layer % 2 == 1)
	        		celly1.setGenX(y, x, celly2.getGenX(y, x));
	        }
		}
		
        if (emojiUnicode != null) {
        	celly1.mirrorResolution();
    		paintEmoji(EMOJI_DIR + EMOJI_TYPE + "_" + emojiSize + "/" + emojiUnicode + "." + EMOJI_TYPE, celly1, WIDTH/2, HEIGHT/2);
        }

		// and smooth out the joins
  	   	
     	celly1.raiseColors(createFringeColors(celly1.getStates()), FRINGE_SIZE);
     	celly1.smoothColors(SMOOTHNESS);
  	         
        for (int x = 0; x < WIDTH; x++)
        {
 	        for (int y = 0; y < HEIGHT; y++)
	        {
	        	g.setColor(perturbColor(celly1.getGenX(y, x)));
	        		        	
	        	g.fillRect(x, y, 1, 1);
	        }
        }
    }
    
    

	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Paint concentric rings of alternating colors
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

    
    private void paintExplosion(Graphics g, ColorAutomaton celly0, Color[] states)
    {
    	int grainSize = WIDTH/celly0.getNumCells();
    	
    	celly0.searchForGoodConfiguration();
    	celly0.doubleResolutionUntil(HEIGHT/grainSize, WIDTH/grainSize);
    	celly0.printGenerations();
    	     	
    	Color[] states1 = new Color[4];
     	states1[0] = color1; 
     	states1[1] = color1.darker();
     	states1[2] = color1.brighter();
     	states1[3] = color2; 
     	
     	Color[] states2 = new Color[4];
     	states2[0] = color2; 
     	states2[1] = color2.darker();
     	states2[2] = color2.brighter();
     	states2[3] = color3; 
    	
    	Color[] states3 = new Color[4];
     	states3[0] = color3; 
     	states3[1] = color3.darker();
     	states3[2] = color3.brighter();
    	states3[3] = color4;
    	
    	if (states3[3] == null) states3[3] = color2;
    	 
     	ColorAutomaton celly1 = new ColorAutomaton(states1, HEIGHT/CHUNKY_DIV, WIDTH/CHUNKY_DIV);
     	
     	celly1.searchForGoodConfiguration();
     	celly1.doubleResolutionUntil(HEIGHT/grainSize, WIDTH/grainSize);
 
     	ColorAutomaton celly2 = new ColorAutomaton(states2, HEIGHT/CHUNKY_DIV, WIDTH/CHUNKY_DIV);
     	
     	celly2.searchForGoodConfiguration();
     	celly2.doubleResolutionUntil(HEIGHT/grainSize, WIDTH/grainSize);
 
    	ColorAutomaton celly3 = new ColorAutomaton(states3, HEIGHT/CHUNKY_DIV, WIDTH/CHUNKY_DIV);
    	
    	celly3.searchForGoodConfiguration();
    	
    	celly3.doubleResolutionUntil(HEIGHT/grainSize, WIDTH/grainSize);
    	
 
        int r = 3*RING_THICK/grainSize;

        for (int x = 0; x < WIDTH/grainSize; x++)
        {
	        for (int y = 0; y < HEIGHT/grainSize; y++)
	        {
	        	int dist = distanceTo(x, y, WIDTH/grainSize, HEIGHT/grainSize);
	        	
	        	if (dist < r) continue;
	        	
	        	int ringNum = (grainSize*dist /  RING_THICK) % 3 + 1;
	        	
	        	if (ringNum == 1)
	        		celly0.setGenX(y, x, celly1.getGenX(y, x));
	        	
	        	if (ringNum == 2)
	        		celly0.setGenX(y, x, celly2.getGenX(y, x));
	        	
	        	if (ringNum == 3)
	        		celly0.setGenX(y, x, celly3.getGenX(y, x));
	        }
        }
    	
        celly0.doubleResolutionUntil(HEIGHT/2, WIDTH/2);
        
        if (emojiUnicode != null) {
        	celly0.mirrorResolution();
    		paintEmoji(EMOJI_DIR + EMOJI_TYPE + "_" + emojiSize + "/" + emojiUnicode + "." + EMOJI_TYPE, celly0, WIDTH/2, HEIGHT/2);
        }

    	celly0.raiseColors(createFringeColors(celly0.getStates()), FRINGE_SIZE);
     	celly0.smoothColors(SMOOTHNESS);

     	for (int x = 0; x < WIDTH; x++)
        {
 	        for (int y = 0; y < HEIGHT; y++)
	        {
	        	g.setColor(perturbColor(celly0.getGenX(y, x)));
	        		        	
	        	g.fillRect(x, y, 1, 1);
	        }
        }
    }
    
    
    
    
    
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Split the canvas vertically into two colour segments
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

    
    private void paintVerticalTriSplit(Graphics g, ColorAutomaton celly1, ColorAutomaton celly2)
    {
    	ColorAutomaton celly3 = new ColorAutomaton(celly1.getStates(), celly1.getNumGenerations(), celly1.getNumCells());

    	Color[] contrast = remotestColorPair(celly1.getStates());

    	celly1.searchForGoodConfiguration(contrast[0]);
   	
     	celly1.printGenerations();
     	 
  	   	celly2.searchForGoodConfiguration(contrast[1]);
  	  
  	   	celly2.printGenerations();

  	   	for (int x = 0; x < celly1.getNumCells(); x++)
  	   		for (int y = 0; y < celly1.getNumGenerations()/3; y++)	   		
  	   			celly1.setGenX(y + 2*celly1.getNumGenerations()/3, x, celly2.getGenX(y + celly1.getNumGenerations()/3, x));
 	   	
  	   	
  	   	celly1.doubleResolutionUntil(HEIGHT/2, WIDTH/2);
  	   	
        if (emojiUnicode != null) {
        	celly1.mirrorResolution();
    		paintEmoji(EMOJI_DIR + EMOJI_TYPE + "_" + emojiSize + "/" + emojiUnicode + "." + EMOJI_TYPE, celly1, WIDTH/2, HEIGHT/2);
        }

     	celly1.raiseColors(createFringeColors(celly1.getStates()), FRINGE_SIZE);
     	celly1.smoothColors(SMOOTHNESS);
   	    	

        for (int x = 0; x < WIDTH; x++)
        {
	        for (int y = 0; y < HEIGHT; y++)
	        {
	        	g.setColor(perturbColor(celly1.getGenX(y, x)));	
	        		        	
	        	g.fillRect(x, y, 1, 1);
	        }
        }
    }
    
    
    
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Split the canvas horizonally into two colour segments
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

    private void paintHorizontalTriSplit(Graphics g, ColorAutomaton celly1, ColorAutomaton celly2)
    {
    	ColorAutomaton celly3 = new ColorAutomaton(celly1.getStates(), celly1.getNumGenerations(), celly1.getNumCells());

    	Color[] contrast = remotestColorPair(celly1.getStates());

    	celly1.searchForGoodConfiguration(contrast[0]);
   	
     	celly1.printGenerations();
     	 
  	   	celly2.searchForGoodConfiguration(contrast[1]);
  	  
  	   	celly2.printGenerations();
  	   	
  	   	for (int y = 0; y < celly1.getNumGenerations(); y++)
  	   		for (int x = 0; x < celly1.getNumCells()/3; x++)
  	   			celly1.setGenX(y, x + 2*celly1.getNumCells()/3, celly2.getGenX(y, x + celly1.getNumCells()/3));

  	   	celly1.doubleResolutionUntil(HEIGHT/2, WIDTH/2);
  	   	
        if (emojiUnicode != null) {
        	celly1.mirrorResolution();
    		paintEmoji(EMOJI_DIR + EMOJI_TYPE + "_" + emojiSize + "/" + emojiUnicode + "." + EMOJI_TYPE, celly1, WIDTH/2, HEIGHT/2);
        }

     	celly1.raiseColors(createFringeColors(celly1.getStates()), FRINGE_SIZE);
     	celly1.smoothColors(SMOOTHNESS);
   	    	
    	for (int x = 0; x < WIDTH; x++)
        {
        	for (int y = 0; y < HEIGHT; y++)
	        {
        		g.setColor(perturbColor(celly1.getGenX(y, x)));	
        		
	        	g.fillRect(x, y, 1, 1);
	        }
        }
    }
    
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Split the canvas along a diagonal separating two colour segments 
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

    
    
    private void paintVerticalHalfSplit(Graphics g, ColorAutomaton celly1, ColorAutomaton celly2)
    {
     	Color[] contrast = remotestColorPair(celly1.getStates());
	    	
  	 	celly1.searchForGoodConfiguration(contrast[0]);
   	
     	celly1.printGenerations();
     	 
    	celly2.searchForGoodConfiguration(contrast[1]);
 	    
  	   	celly2.printGenerations();

  	   	// copy the bottom half of celly2 into celly1
	   	
  	   	for (int y = 0; y < celly1.getNumGenerations()/2; y++)
  	   		for (int x = 0; x < celly1.getNumCells(); x++)
  	   			celly1.setGenX(y + celly1.getNumGenerations()/2, x, celly2.getGenX(y, x)); 
  	   	
  	   		   	
  	   	celly1.doubleResolutionUntil(HEIGHT/2, WIDTH/2);
  	   	
        if (emojiUnicode != null) {
        	celly1.mirrorResolution();
    		paintEmoji(EMOJI_DIR + EMOJI_TYPE + "_" + emojiSize + "/" + emojiUnicode + "." + EMOJI_TYPE, celly1, WIDTH/2, HEIGHT/2);
        }

  	   	
     	celly1.raiseColors(createFringeColors(celly1.getStates()), FRINGE_SIZE);
  	   	celly1.smoothColors(SMOOTHNESS);
    
  
        for (int x = 0; x < WIDTH; x++)
        {
        	for (int y = 0; y < HEIGHT; y++)
	        {
	        	g.setColor(perturbColor(celly1.getGenX(y, x)));
	        	
	        	g.fillRect(x, y, 1, 1);
	        }
        }
    }
    
    
    
    
    private void paintHorizontalHalfSplit(Graphics g, ColorAutomaton celly1, ColorAutomaton celly2)
    {
     	Color[] contrast = remotestColorPair(celly1.getStates());

   	 	celly1.searchForGoodConfiguration(contrast[0]);
   	
     	celly1.printGenerations();
     	 
  	   	celly2.searchForGoodConfiguration(contrast[1]);
  	  
  	   	celly2.printGenerations();
  	   	
  	   	// copy the left half of celly2 into right half of celly1
	   	
  	   	for (int y = 0; y < celly1.getNumGenerations(); y++)
  	   		for (int x = 0; x < celly1.getNumCells()/2; x++)
  	   			celly1.setGenX(y, x + celly1.getNumCells()/2, celly2.getGenX(y, x)); 
  	   	
  	    celly1.doubleResolutionUntil(HEIGHT/2, WIDTH/2);
  	    
        if (emojiUnicode != null) {
        	celly1.mirrorResolution();
    		paintEmoji(EMOJI_DIR + EMOJI_TYPE + "_" + emojiSize + "/" + emojiUnicode + "." + EMOJI_TYPE, celly1, WIDTH/2, HEIGHT/2);
        }

     	celly1.raiseColors(createFringeColors(celly1.getStates()), FRINGE_SIZE);
  	   	celly1.smoothColors(SMOOTHNESS);
  	   	
        for (int x = 0; x < WIDTH; x++)
        {
	        for (int y = 0; y < HEIGHT; y++)
	        {
	        	g.setColor(perturbColor(celly1.getGenX(y, x)));
	        	
	        	g.fillRect(x, y, 1, 1);
	        }
        }
    }
    
    
    
    
    private void paintDiagonalalDownSplit(Graphics g, ColorAutomaton celly1, ColorAutomaton celly2)
    {
     	Color[] contrast = remotestColorPair(celly1.getStates());
    	
  	 	celly1.searchForGoodConfiguration(contrast[0]);
     	
      	celly1.printGenerations();
      	
      	celly1.mirrorResolution();
      	 
      	celly1.doubleResolution();
    	    	
   	   	celly2.searchForGoodConfiguration(contrast[1]);
  
    	celly2.printGenerations();

      	celly2.mirrorResolution();

      	celly2.doubleResolution();
  
        for (int x = 0; x < celly1.getNumCells(); x++)
        {
 	        for (int y = 0; y < celly1.getNumGenerations(); y++)
 	        	if (x >= (y*WIDTH/HEIGHT))
	        		celly1.setGenX(y, x, celly2.getGenX(y, x));	 
        }
        
        celly1.doubleResolutionUntil(HEIGHT, WIDTH);
        
        if (emojiUnicode != null) 
        	paintEmoji(EMOJI_DIR + EMOJI_TYPE + "_" + emojiSize + "/" + emojiUnicode + "." + EMOJI_TYPE, celly1, WIDTH/2, HEIGHT/2);
        
     	celly1.raiseColors(createFringeColors(celly1.getStates()), FRINGE_SIZE);
  	   	celly1.smoothColors(SMOOTHNESS);
  	   	
	  	for (int x = 0; x < WIDTH; x++)
	      for (int y = 0; y < HEIGHT; y++)
          {
	    	  g.setColor(celly1.getGenX(y, x));	 
        	
	    	  g.fillRect(x, y, 1, 1);
          }
    }
    
    
    
    private void paintDiagonalalUpSplit(Graphics g, ColorAutomaton celly1, ColorAutomaton celly2)
    {
   	Color[] contrast = remotestColorPair(celly1.getStates());
    	
  	 	celly1.searchForGoodConfiguration(contrast[0]);
     	
      	celly1.printGenerations();
      	
      	celly1.mirrorResolution();
      	 
      	celly1.doubleResolution();
    	    	
   	   	celly2.searchForGoodConfiguration(contrast[1]);
  
    	celly2.printGenerations();

      	celly2.mirrorResolution();

      	celly2.doubleResolution();
  
        for (int x = 0; x < celly1.getNumCells(); x++)
        {
 	        for (int y = 0; y < celly1.getNumGenerations(); y++)
 	        	if (x < (y*WIDTH/HEIGHT))
	        		celly1.setGenX(y, x, celly2.getGenX(y, x));	 
        }
        
        celly1.doubleResolutionUntil(HEIGHT, WIDTH);
        
        if (emojiUnicode != null) 
        	paintEmoji(EMOJI_DIR + EMOJI_TYPE + "_" + emojiSize + "/" + emojiUnicode + "." + EMOJI_TYPE, celly1, WIDTH/2, HEIGHT/2);
        
     	celly1.raiseColors(createFringeColors(celly1.getStates()), FRINGE_SIZE);
  	   	celly1.smoothColors(SMOOTHNESS);
  	   	
	  	for (int x = 0; x < WIDTH; x++)
	      for (int y = 0; y < HEIGHT; y++)
          {
	    	  g.setColor(celly1.getGenX(y, x));	 
        	
	    	  g.fillRect(WIDTH - x - 1, y, 1, 1);
          }
    }
    
    
    
    
    
    private void paintHourglassSplit(Graphics g, ColorAutomaton celly1, ColorAutomaton celly2)
    {
   	Color[] contrast = remotestColorPair(celly1.getStates());
    	
  	 	celly1.searchForGoodConfiguration(contrast[0]);
     	
      	celly1.printGenerations();
      	
   	   	celly2.searchForGoodConfiguration(contrast[1]);
  
    	celly2.printGenerations();

        for (int x = 0; x < celly1.getNumCells(); x++)
 	        for (int y = 0; y < celly1.getNumGenerations(); y++)
 	        	if (x >= (y*WIDTH/HEIGHT))
	        		celly1.setGenX(y, x, celly2.getGenX(y, x));	 
        
        celly1.doubleResolutionUntil(HEIGHT/2, WIDTH/2);
        
        if (emojiUnicode != null) {
        	celly1.mirrorResolution();
    		paintEmoji(EMOJI_DIR + EMOJI_TYPE + "_" + emojiSize + "/" + emojiUnicode + "." + EMOJI_TYPE, celly1, WIDTH/2, HEIGHT/2);
        }
      
     	celly1.raiseColors(createFringeColors(celly1.getStates()), FRINGE_SIZE);
  	   	celly1.smoothColors(SMOOTHNESS);
  	   	
	  	for (int x = 0; x < WIDTH; x++)
	      for (int y = 0; y < HEIGHT; y++)
          {
	    	  g.setColor(celly1.getGenX(y, x));	 
        	
	    	  g.fillRect(x, y, 1, 1);
          }
    }
    
    

    
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Paint the canvas in the style of a Centrepiece setting
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

    private void paintCenterpiece(Graphics g, ColorAutomaton celly1, ColorAutomaton celly2)
    {
    	celly1.searchForGoodConfiguration();
    	
	   	while (celly1.getPercentageOfHole() > 20)
	 	{
	   		celly1.run();
	   		celly1.searchForGoodConfiguration();
	 	}
  
	   	celly1.printGenerations();
      	 
      	celly1.doubleResolutionUntil(HEIGHT/2, WIDTH/2);
           	    	
   	   	celly2.searchForGoodConfiguration();
   	   	
	   	while (celly2.getPercentageOfHole() > 20)
	 	{
	 		celly2.run();
	 		celly2.searchForGoodConfiguration();
	 	}
  
    	celly2.printGenerations();

    	celly2.doubleResolutionUntil(HEIGHT/2, WIDTH/2);
     	 
     	int r = HEIGHT/3;
        
        for (int x = 0; x < WIDTH/2; x++)
        {
   	        for (int y = 0; y < HEIGHT/2; y++)
	        {
	        	if (distanceTo(0, 0, x, y) < r)
	        		celly1.setGenX(y, x, celly1.getGenX(y, x));
	        	else
	        	if (distanceTo(WIDTH/2, HEIGHT/2, x, y) < r/2)
	        		celly1.setGenX(y, x, celly2.getGenX(y, x));
	        	else
	        	if (distanceTo(WIDTH/2, HEIGHT/2, x, y) < r)
	        		celly1.setGenX(y, x, celly1.getGenX(y, x));
	        	else
	        		celly1.setGenX(y, x, celly2.getGenX(y, x));
	        }	        
         }
        
         if (emojiUnicode != null) {
        	celly1.mirrorResolution();
    		paintEmoji(EMOJI_DIR + EMOJI_TYPE + "_" + emojiSize + "/" + emojiUnicode + "." + EMOJI_TYPE, celly1, WIDTH/2, HEIGHT/2);
         }
         
      	 celly1.raiseColors(createFringeColors(celly1.getStates()), FRINGE_SIZE);
  	   	 celly1.smoothColors(SMOOTHNESS);
  	   	
  	   	 for (int x = 0; x < WIDTH; x++) {
  	   		for (int y = 0; y < HEIGHT; y++)
  	   		{
  	   			g.setColor(celly1.getGenX(y, x));	 
        	
  	   			g.fillRect(x, y, 1, 1);
  	   		}
         }
    }
    
    
    
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Paint the canvas in the style of a Battenburg grid
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

    
    private void paintGrid(Graphics g, ColorAutomaton celly1, ColorAutomaton celly2)
    {
     	Color[] contrast = remotestColorPair(celly1.getStates());
    	
  	 	celly1.searchForGoodConfiguration(contrast[0]);
   	
     	celly1.printGenerations();
     	 
  	   	celly2.searchForGoodConfiguration(contrast[1]);
  	  
  	   	celly2.printGenerations();

  	   	for (int y = 0; y < celly1.getNumGenerations(); y++)
  	   		for (int x = 0; x < celly1.getNumCells(); x++)
  	   		{
  	   			if (y > 2*celly1.getNumGenerations()/3 && x < 2*celly1.getNumCells()/3)
  	   				celly1.setGenX(y, x, celly2.getGenX(y, x));
  	   			
  	   			if (y < 2*celly1.getNumGenerations()/3 && x > 2*celly1.getNumCells()/3)
	   				celly1.setGenX(y, x, celly2.getGenX(y, x));
  	   		}
  	   	
  	   	
  	   	celly1.doubleResolutionUntil(HEIGHT/2, WIDTH/2);
  	   	
        if (emojiUnicode != null) {
        	celly1.mirrorResolution();
    		paintEmoji(EMOJI_DIR + EMOJI_TYPE + "_" + emojiSize + "/" + emojiUnicode + "." + EMOJI_TYPE, celly1, WIDTH/2, HEIGHT/2);
        }

     	celly1.raiseColors(createFringeColors(celly1.getStates()), FRINGE_SIZE);
  	   	celly1.smoothColors(SMOOTHNESS);
   	    	    	 
    	
        for (int x = 0; x < WIDTH; x++)
        {
	        for (int y = 0; y < HEIGHT; y++)
	        {
	        	g.setColor(perturbColor(celly1.getGenX(y, x)));	 
		        
	        	g.fillRect(x, y, 1, 1);
	        }
        }

    }
    
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Paint the canvas with the contents of an automaton, without mirroring
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

    private void paintAsymmetricAutomaton(Graphics g, ColorAutomaton celly)
    {
    	celly.searchForGoodConfiguration();
    	
    	celly.doubleResolution();
    	
    	BufferedImage original = null;    

        celly.printGenerations();
    	 
        celly.doubleResolutionUntil(HEIGHT/2, WIDTH/2);
        
       	if (emojiUnicode != null) 
    		paintEmoji(EMOJI_DIR + EMOJI_TYPE + "_" + emojiSize + "/" + emojiUnicode + "." + EMOJI_TYPE, celly, WIDTH/4, HEIGHT/4);
              	
       	celly.doubleResolution();
       	
     	celly.raiseColors(createFringeColors(celly.getStates()), FRINGE_SIZE*2);
  	   	celly.smoothColors(SMOOTHNESS*2);
   	    	    	  
    	for (int x = 0; x < WIDTH; x++)
        {
	        for (int y = 0; y < HEIGHT; y++)
	        {
	        	g.setColor(perturbColor(celly.getGenX(y, x)));	 
	        	
	        	g.fillRect(x, y, 1, 1);
	        }	        
         }
    	
    	/*
    	if (original != null)
    	{
    		((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)0.3));
    		
    	    g.drawImage(original, WIDTH/2-original.getWidth()/2, HEIGHT/2-original.getHeight()/2, null); 
    	}
    	*/
    }
    
    
    
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Random shape drawing utilities
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

    private void paintRandomCircle(Graphics g, Color col, int radius) 
    {
    	int xPos   = RND.nextInt(WIDTH) - RND.nextInt(WIDTH/CHUNKY_DIV);
    	int yPos   = RND.nextInt(HEIGHT) - RND.nextInt(HEIGHT/CHUNKY_DIV);
    
    	g.setColor(col);
    	g.fillOval(xPos, yPos, radius*2, radius*2);
   	
   }
    
    
    private void paintRandomBox(Graphics g, Color col, int radius) 
    {
    	int xPos   = RND.nextInt(WIDTH) - RND.nextInt(WIDTH/CHUNKY_DIV);
    	int yPos   = RND.nextInt(HEIGHT) - RND.nextInt(HEIGHT/CHUNKY_DIV);
    
    	g.setColor(col);
    	g.fillRoundRect(xPos, yPos, radius*2, radius*2, RND.nextInt(20), RND.nextInt(20));
    }
    
    
    private void paintRandomTriangle(Graphics g, Color col, int radius) 
    {
    	int[] xp = new int[3];
       	int[] yp = new int[3];
           	
    	xp[0]  = RND.nextInt(WIDTH) - RND.nextInt(WIDTH/CHUNKY_DIV);
    	xp[1]  = xp[0] - radius - RND.nextInt(radius+1);
    	xp[2]  = xp[0] + radius + RND.nextInt(radius+1);

    	yp[0]  = RND.nextInt(HEIGHT) - RND.nextInt(HEIGHT/CHUNKY_DIV);
       	yp[1]  = yp[0] - radius - RND.nextInt(radius+1);
       	yp[2]  = yp[0] + radius + RND.nextInt(radius+1);
           
    	g.setColor(col);
    	g.fillPolygon(xp, yp, 3);
    }
    
    
    
    
    private void paintRandomShapes(Graphics g, Color col, int x, int y, int radius) 
    {
    	g.setColor(col);
    	paintRandomShapes(g, x, y, radius);
    }
    
    
    
    private void paintRandomShapes(Graphics g, int x, int y, int radius) 
    {
       	paintSquare(g, x, y, radius/2);
        
       	paintTriangle(g, x, y, radius/2);
    	
    	paintCircle(g, x, y, radius);
    }
    
    
    private void paintRandomShape(Graphics g, Color col, int x, int y, int radius) 
    {
    	g.setColor(col);
    	paintRandomShape(g, x, y, radius);
    }
    
    
    
    private void paintRandomShape(Graphics g, int x, int y, int radius) 
    {
    	if (RND.nextBoolean())
    		paintTriangle(g, x, y, radius/2);
    	else
    		paintCircle(g, x, y, radius);
    }
    
    
    
    
    private void paintTriangle(Graphics g, int x, int y, int radius) 
    {
    	int[] xp = new int[3];
       	int[] yp = new int[3];
           	
    	xp[0]  = x;
    	xp[1]  = xp[0] - radius - RND.nextInt(radius+1);
    	xp[2]  = xp[0] + radius + RND.nextInt(radius+1);

    	yp[0]  = y;
       	yp[1]  = yp[0] - radius - RND.nextInt(radius+1);
       	yp[2]  = yp[0] + radius + RND.nextInt(radius+1);
           
    	g.fillPolygon(xp, yp, 3);
    }
    
    
    private void paintCircle(Graphics g, int x, int y, int radius) 
    {
    	g.fillOval(x, y, radius, radius);
    }


    private void paintSquare(Graphics g, int x, int y, int radius) 
    {
    	g.fillRect(x, y, radius, radius);
    }
   
    
 
 
        
    
    //--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Basic Colour manipulation
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	// Vary a colour to create a textured effect in the canvas

    public Color perturbColor(Color color)
    {
    	if (PERTURB_PROB == 0 || RND.nextInt(100) >= PERTURB_PROB) 
    		return color;
    	
    	Color variant = new Color(perturbComponent(color.getRed(), PERTURBATION),
    							  perturbComponent(color.getGreen(), PERTURBATION),
    							  perturbComponent(color.getBlue(), PERTURBATION), 255);
    	
    	return variant;
    }
    
    
    private int perturbComponent(int value, int range)
    {
    	value = value + RND.nextInt(range) - range/2;
    	
    	if (value < 0) value = 0;
    	
    	if (value > 255) value = 255;
    	
    	return value;
    }
    
    
    
    
    private Color mixColors(Color color1, Color color2)
    {
    	return new Color((color1.getRed() + color2.getRed())/2,
    			         (color1.getGreen() + color2.getGreen())/2,
    			         (color1.getBlue() + color2.getBlue())/2);
    }
    
    
    private Color mixColors(Color color1, Color color2, int ratio1, int ratio2)
    {
    	return new Color((color1.getRed()*ratio1 + color2.getRed()*ratio2)/(ratio1 + ratio2),
    			         (color1.getGreen()*ratio1 + color2.getGreen()*ratio2)/(ratio1 + ratio2),
    			         (color1.getBlue()*ratio1 + color2.getBlue()*ratio2)/(ratio1 + ratio2));
    }
    
    
    private Color mixColors(Color color1, Color color2, Color color3)
    {
    	return new Color((color1.getRed() + color2.getRed() + color3.getRed())/3,
    					 (color1.getGreen() + color2.getGreen()  + color3.getGreen())/3,
    					 (color1.getBlue() + color2.getBlue() +  + color3.getBlue())/3);
    }
    
    

    public static Color getRGBColor(String hex)
    {
    	if (hex.startsWith("#")) hex = hex.substring(1);
    	
    	if (hex.length() != 6) return Color.white;
    	
    	int r = getHexValue(hex.charAt(0))*16 + getHexValue(hex.charAt(1));
       	int g = getHexValue(hex.charAt(2))*16 + getHexValue(hex.charAt(3));
      	int b = getHexValue(hex.charAt(4))*16 + getHexValue(hex.charAt(5));
      	           	
      	return new Color(r, g, b, 255);
    }
    
    
    
    public static int getHexValue(char code)
    {
    	if (code >= 'A')
    		return 10 + code - 'A';
    	
    	if (code >= '0')
    		return code - '0';
    	
    	return 0;
    }
    
    
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Save the image to a File or to an input stream (for uploading to Twitter)
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

    
    public File saveImage(String filename) 
    {

        // Image can be saved to either JPEG or PNG. PNG gives a much clearer result though
        BufferedImage bi = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        
        Graphics g = bi.createGraphics();
        this.paint(g);  //this == JComponent
        g.dispose();
        
        try {
        	File file = new File(filename);
        	
            ImageIO.write(bi, "jpg", file);
            
            return file;
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        return null;
    }
    
    

    public InputStream toInputStream() 
    {
        BufferedImage bi = new BufferedImage(this.getSize().width, this.getSize().height, BufferedImage.TYPE_INT_RGB);
        Graphics g = bi.createGraphics();
        
        this.paint(g);  
        
        g.dispose();
        
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        
        try {
            ImageIO.write(bi, "jpg", os);
        } 
        catch (Exception e) {
        	e.printStackTrace();
        }
        
        InputStream fis = new ByteArrayInputStream(os.toByteArray());
        return fis;
    }
    
    
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Main Stub
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

    public static void main(String[] args)
    {
    	Color lemon     = PaintedCanvas.getRGBColor("#F5C71A");
    	Color custard   = PaintedCanvas.getRGBColor("#FDF0B5");
    	
    	Color chocolate = PaintedCanvas.getRGBColor("#7B3F00");
       	Color sky       = PaintedCanvas.getRGBColor("#87CEEB");
       	
       	Color demon     = PaintedCanvas.getRGBColor("#7D1C1C");
                  	
       	Color chimney   = PaintedCanvas.getRGBColor("#1C1D1F");
       	Color cinder    = PaintedCanvas.getRGBColor("#878D90");
       	
       	Color hulkGreen0= PaintedCanvas.getRGBColor("#158202");
       	Color hulkGreen1= PaintedCanvas.getRGBColor("#00592D");
       	Color hulkGreen2= PaintedCanvas.getRGBColor("#550A55");
       	Color hulkPurple= PaintedCanvas.getRGBColor("#849137");
       	
       	Color azure     = PaintedCanvas.getRGBColor("#007FFF");
 
        PaintedCanvas m = new PaintedCanvas(hulkGreen0, hulkGreen1);
        
//      PaintedCanvas m = new PaintedCanvas(lemon, sky, demon);
        
        m.setEmojiUnicode("1f680");  // gun = 1f52b temple = 1f3ef shop = 1f3ec ATM = 1f3e7 hotel = 1f3e8 castle = 1f3f0 urn = 1f3fa
        							 // shop = 1f3ec  pumpkin = 1f383 robot = 1f916 lightbulb = 1f4a1 muscle = 1f4aa wreck - 1f3da
        							 // ghost = 1f47b rocket = 1f680 stadium = 1f3df poo = 1f4a9 alien = 1f47d dancer = 1f483
        							 // egpplant = 1f346 donut = 1f369 fries = 1f35f burger = 1f354 cake = 1f370 Martini = 1f378
        							 // fried egg = 1f373 volcano = 1f30b stone head = 1f5ff yinyang = 262f pencil = 270F
        							 // voting = 1f5f3 coffin = 26B0 lollipop = 1f36d hotdog = 1f32d banana = 1f34c ares = 2648
        							 // shamrock = 2618 medal = 1f3c5 cactus = 1f335 cheese = 1f9c0 birthday = 1f382
         							 // Santa = 1f385 scissors = 2702 key = f5dd clown = 1f921 selfie = 1f933-1f3fb prince = 1f934
        							 // lion = 1f981 bacon = 1f953 web = 1f578 baby = 1f476 doh = 1f926 mommy = 1f930 tuxedo = 1f935
        							 // juggler = 1f939 first = 1f947 rock = 1f954 bat = 1f987 storm = 26c8 coffee = 2615
         							 // comet = 2604 rain = 2614 snowman = 2603 Gump = 1f3d3 ape = 1f98d butterfly = 1f98b
        							 // peanut = 1f95c boxing = 1f94a wrestling
  
 //       m.predetermineStyle(16);
        
        // Hulkisms: 1f44a 1f4aa 1f5ef 1f30b 1f4a5 1f91b 1f91c
       
        m.saveImage("canvas.jpg");
        
        
    }
   

}
