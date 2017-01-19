package twitterbotics;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;

import tabular.BucketTable;
import tabular.CountTable;
import tabular.SymbolCounter;

public class Lexinomicron extends Dribbler
{
	public static final int MIN_RGB_DIST = 100;
	

	private static Random RND = new Random();

	private static final int SAMPLE_SIZE = 3;
	
	private ColorLexicon lexicon = null;
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Constructors
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	public Lexinomicron(String filename)
	{
		lexicon = new ColorLexicon(filename);
	}
	

	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Load N-grams containing "readymade" color-names and generate apt canvases
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	private Hashtable modsForHeads = new Hashtable();
	
	public Vector loadBracketedBigrams(String filename)
	{
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(filename),"UTF-8"));
		    
		    String line = null;
		    
		    while (input.ready())  // Read a line at a time
			{
				line = input.readLine();
				
				if (line == null || line.length() == 0 || line.startsWith("#"))
					continue;
							
				StringTokenizer tokens = new StringTokenizer(line, "\t\n", false);
				
				String det = tokens.nextToken().toLowerCase().intern();  

				if (det == "the" || det == "a" || det == "an")
					det = Dribbler.capitalizeFirst(det) + " ";
				else
					det = "";
					
				String stereo1 = tokens.nextToken().trim().intern();
				String stereo2  = tokens.nextToken().trim().intern();
				
				tokens.nextToken();  // skip opening bracket
				
				int freq  = Integer.parseInt(tokens.nextToken().trim());
				
				Vector huelist1 = lexicon.getHuesFor(stereo1), huelist2 = lexicon.getHuesFor(stereo2); 
				
				if (huelist1 == null || huelist2 == null) continue;
				
				for (int h1 = 0; h1 < huelist1.size(); h1++)
				{
					String hue1 = (String)huelist1.elementAt(h1);
					
					String rgb1  = lexicon.getRGBFor(stereo1, hue1);
					
					String alt1  = lexicon.getRandomStereoForHue(hue1);
					
					while (stereo1.equals(alt1))
						alt1  = lexicon.getRandomStereoForHue(hue1);

					for (int h2 = 0; h2 < huelist2.size(); h2++)
					{
						String hue2 = (String)huelist2.elementAt(h2);
						
						if (hue2.equals(hue1)) continue;
						
						String rgb2  = lexicon.getRGBFor(stereo2, hue2);
						
						String alt2  = lexicon.getRandomStereoForHue(hue2);
						
						while (stereo2.equals(alt2))
							alt2  = lexicon.getRandomStereoForHue(hue2);
						
						Vector modsForStereo2 = (Vector)modsForHeads.get(stereo2);
						
						if (modsForStereo2 == null)
						{
							modsForStereo2 = new Vector();
							modsForHeads.put(stereo2, modsForStereo2);
						}
						
						if (!modsForStereo2.contains(stereo1))
							modsForStereo2.add(stereo1);
						
						String tweet = "I composed this wallpaper using " + capitalizeFirst(alt1) + "-" + hue1 + " on " + 
									   capitalizeFirst(alt2) + "-" + hue2 +  ". When I asked for apt names, " + 
									   freq + " of you suggested \""  + det + capitalizeFirst(stereo1) + " " + 
									   capitalizeFirst(stereo2) + ".\"";
						
						if (tweet.length() > 140)
							tweet = Dribbler.replaceWith(tweet, " wallpaper ", " canvas ");

						String hashtags = "#" + Dribbler.capitalizeFirst(stereo1) + Dribbler.capitalizeFirst(stereo2) + "RGB";
						
						if (tweet.length() <= 140)
							printlnDribbleFile(getDribblePosition() + "\t" +  hashtags + "\t" +
										  	   tweet + "\t" + rgb1.substring(1) + "\t" + rgb2.substring(1));
					}
				}
			}

		   input.close();  // close connection to the data source
		}
		catch (IOException e)
		{
			System.out.println("Cannot find/load ngram file: " + filename);
			
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	
	
	public Vector loadPluralBigrams(String filename)
	{
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
		    
		    String line = null;
		    
		    while (input.ready())  // Read a line at a time
			{
				line = input.readLine();
				
				if (line == null || line.length() == 0 || line.startsWith("#"))
					continue;
							
				StringTokenizer tokens = new StringTokenizer(line, "\t\n", false);
								
				String singleStereo1 = tokens.nextToken().trim().intern();
				String pluralStereo2  = tokens.nextToken().trim().intern();
				
				int freq  = Integer.parseInt(tokens.nextToken().trim());
				
				String singleStereo2 = tokens.nextToken().trim().intern();
				
				
				Vector huelist1 = lexicon.getHuesFor(singleStereo1), huelist2 = lexicon.getHuesFor(singleStereo2); 
				
				if (huelist1 == null || huelist2 == null) continue;
				
				for (int h1 = 0; h1 < huelist1.size(); h1++)
				{
					String hue1 = (String)huelist1.elementAt(h1);					
					String rgb1  = lexicon.getRGBFor(singleStereo1, hue1);
					
					String alt1  = lexicon.getRandomStereoForHue(hue1);
					
					while (singleStereo1.equals(alt1))
						alt1  = lexicon.getRandomStereoForHue(hue1);

					for (int h2 = 0; h2 < huelist2.size(); h2++)
					{
						String hue2 = (String)huelist2.elementAt(h2);
						String rgb2 = lexicon.getRGBFor(singleStereo2, hue2);
												
						if (hue2.equals(hue1)) 
						{
							if (lexicon.getDistanceBetween(rgb1, rgb2) < 2*MIN_RGB_DIST)
								continue;
						}
						
						if (lexicon.getDistanceBetween(rgb1, rgb2) < MIN_RGB_DIST)
							continue;
						
						String alt2  = lexicon.getRandomStereoForHue(hue2);
						
						while (singleStereo2.equals(alt2))
							alt2  = lexicon.getRandomStereoForHue(hue2);
						
						String tweet = "I composed this wallpaper using " + capitalizeFirst(alt1) + "-" + hue1 + " on " + 
									   capitalizeFirst(alt2) + "-" + hue2 +  ". Yet when I asked for apt names, " + 
									   freq + " of you suggested \""  + capitalizeFirst(singleStereo1) + " " + 
									   capitalizeFirst(pluralStereo2) + ".\"";
						
						if (tweet.length() > 140)
							tweet = Dribbler.replaceWith(tweet, " composed ", " made ");

						if (tweet.length() > 140)
							tweet = Dribbler.replaceWith(tweet, " Yet when ", " When ");
						
						if (tweet.length() > 140)
							tweet = Dribbler.replaceWith(tweet, " wallpaper ", " image ");

						String hashtags = "#" + Dribbler.capitalizeFirst(singleStereo1) + Dribbler.capitalizeFirst(singleStereo2) + "RGB";
						
						if (tweet.length() <= 140)
							printlnDribbleFile(getDribblePosition() + "\t" +  hashtags + "\t" +
											   						          tweet + "\t" + rgb1.substring(1) + "\t" + rgb2.substring(1));
						
						Vector modsForStereo1 = (Vector)modsForHeads.get(singleStereo1);
						
						if (modsForStereo1 == null) continue;
						
						for (int m = 0; m < modsForStereo1.size(); m++)
						{
							String singleStereo0 = (String)modsForStereo1.elementAt(m);
							
							if (singleStereo0.equals(singleStereo1) || singleStereo0.equals(singleStereo2))
								continue;
							
							Vector huelist0 = lexicon.getHuesFor(singleStereo0); 
							
							if (huelist0 == null) continue;
							
							for (int h0 = 0; h0 < huelist0.size(); h0++)
							{
								String hue0 = (String)huelist0.elementAt(h0);
								String rgb0 = lexicon.getRGBFor(singleStereo0, hue0);								
								
								if (hue0.equals(hue1)) {
									if (lexicon.getDistanceBetween(rgb0, rgb1) < 2*MIN_RGB_DIST)
										continue;
								}
								
								if (hue0.equals(hue2))  {
									if (lexicon.getDistanceBetween(rgb0, rgb2) < 2*MIN_RGB_DIST)
										continue;
								}
								
								if (lexicon.getDistanceBetween(rgb0, rgb1) < MIN_RGB_DIST ||
									lexicon.getDistanceBetween(rgb0, rgb2) < MIN_RGB_DIST)
									continue;
								
								String alt0  = lexicon.getRandomStereoForHue(hue0);
								
								while (singleStereo0.equals(alt0))
									alt0  = lexicon.getRandomStereoForHue(hue0);
								
								String tweet2 = "I made this wallpaper from " + capitalizeFirst(alt0) + "-" + lexicon.resolveHue(hue0, rgb0) + ", " + 
										   		 capitalizeFirst(alt1) + "-" + lexicon.resolveHue(hue1, rgb1) +  " and " + 
										   		 capitalizeFirst(alt2) + "-" +  lexicon.resolveHue(hue2, rgb2) + 
										   		 ". But I now christen it \""  +  capitalizeFirst(singleStereo0) + " " + 
										   		 capitalizeFirst(singleStereo1) + " " +  capitalizeFirst(pluralStereo2) + ".\"";
								
								
								String hashtags2 = "#" + Dribbler.capitalizeFirst(singleStereo0) +  
														 Dribbler.capitalizeFirst(singleStereo1) + 
														 Dribbler.capitalizeFirst(singleStereo2) + "RGB";

								if (tweet2.length() <= 140)
									printlnDribbleFile(getDribblePosition() + "\t" +  hashtags2 + "\t" + tweet2 + "\t" +  
																rgb0.substring(1) + "\t" + rgb1.substring(1) + "\t" + rgb2.substring(1));							
							}
						}
					}
				}
			}

		   input.close();  // close connection to the data source
		}
		catch (IOException e)
		{
			System.out.println("Cannot find/load ngram file: " + filename);
			
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	
	//-----------------------------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------------------------//
	// Generate Stereotype Color tweets
	//-----------------------------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------------------------//


	public void generateColorStereotypetweets(String tweetDir, String kbDir, String resDir, String stereoModelFile)
	{
		KnowledgeBaseModule NOC   = new KnowledgeBaseModule(kbDir + "Veale's The NOC List.txt", 0);
		Hashtable POS_QUALITIES   = NOC.getInvertedField("Positive Talking Points");
		Hashtable NEG_QUALITIES   = NOC.getInvertedField("Negative Talking Points");
		
		BucketTable stereoModel   = new BucketTable(resDir, stereoModelFile);		
		BucketTable propertyModel = stereoModel.invertTable();
		
		Vector exemplars 		  = stereoModel.getKeyList();
		Vector posQuals 		  = new Vector();
		Vector negQuals 		  = new Vector();
		
		for (int e = 0; e < exemplars.size(); e++)
		{
			String exemplar   = (String)exemplars.elementAt(e);
			
			if (Character.isUpperCase(exemplar.charAt(0))) continue; // proper-names handled in other code
			
			posQuals.setSize(0);
			negQuals.setSize(0);
			
			Vector allQuals   = stereoModel.get(exemplar);
			
			if (allQuals == null) continue;
			
			for (int q = 0; q < allQuals.size(); q++)
			{
				String qual = (String)allQuals.elementAt(q);
				
				if (lexicon.getColorTermsForProperty(qual, propertyModel) == null) // not colorable so ignore
					continue;
				
				if (POS_QUALITIES.get(qual) != null && NEG_QUALITIES.get(qual) != null)
				{
					if (((Vector)POS_QUALITIES.get(qual)).size() > ((Vector)NEG_QUALITIES.get(qual)).size()) // more positive than negative
						posQuals.add(qual);
					else
						negQuals.add(qual);
				}
				else
				if (POS_QUALITIES.get(qual) != null)
					posQuals.add(qual);
				else
				if (NEG_QUALITIES.get(qual) != null)
					negQuals.add(qual);
			}
			
			if (posQuals.size() == 0 || negQuals.size() == 0) continue;
						
			generatePersonalityColorTweets(exemplar, NOC.hashtagify(exemplar), 
										   posQuals, negQuals, 
										   stereoModel, propertyModel);
		}
	}
	
	
	
	//-----------------------------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------------------------//
	// Generate ColourLovers Palette tweets
	//-----------------------------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------------------------//

	public void generateColourLoverTweets(String dir, String paletteFile)
	{
		BucketTable palettes	=	new BucketTable(dir, paletteFile);
		
		palettes.saveTable(paletteFile);
		
		Vector palSpecs			=	palettes.getKeyList();
		
		for (int ps = 0; ps < palSpecs.size(); ps++) 
		{
			String palSpec  = (String)palSpecs.elementAt(ps);
			String author   = palSpec.substring(0, palSpec.indexOf((int)'|'));
			String palName  = palSpec.substring(palSpec.indexOf((int)'|')+1, palSpec.lastIndexOf((int)'|'));
			
			int  loveCount  = Integer.parseInt(palSpec.substring(palSpec.lastIndexOf((int)'|')+1));
			
			Vector palette	= palettes.get(palSpec);
			
			for (int c1 = 0; c1 < palette.size()-2; c1++)
			{
				String color1 = (String)palette.elementAt(c1);
				
				for (int c2 = c1+1; c2 < palette.size()-1; c2++)
				{
					String color2 = (String)palette.elementAt(c2);
					
					if (lexicon.getDistanceBetween(color1, color2) < MIN_RGB_DIST)
						continue;
					
					for (int c3 = c2+1; c3 < palette.size(); c3++)
					{
						String color3 = (String)palette.elementAt(c3);
						
						if (lexicon.getDistanceBetween(color3, color2) < MIN_RGB_DIST)
							continue;
						
						if (lexicon.getDistanceBetween(color3, color1) < MIN_RGB_DIST)
							continue;
						
						String period = ".";
						
						if (palName.endsWith(".") || palName.endsWith("!") || palName.endsWith("?"))
							period = "";
						
						
						String credit = "This picture goes out to @" + author + " on ColourLovers.com, who suggested the colours.|" +
									    "We call it \"" + replaceWith(capitalizeEach(palName), "_", " ")  + period + "\"";
						
						if (credit.length() <= 140)
							printlnDribbleFile(getDribblePosition() + "\t#ColourLoversRGB\t" + credit + "\t#" + color1 + "\t#" + color2 + "\t#" + color3);
						
						
						/*
						String metaphor = "This visual metaphor is for @" + author + " on ColourLovers.com, thanks for the colours!|" +
										  "It's titled \"" + replaceWith(capitalizeEach(palName), "_", " ")  + period + "\"";
				
						if (metaphor.length() <= 140)
							printlnDribbleFile(getDribblePosition() + "\t#VisMetRGB\t" + metaphor + "\t#" + color1 + "\t#" + color2 + "\t#" + color3);							
						*/
					}
				}
			}
		}
	}
	
	
	//-----------------------------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------------------------//
	// Generate Personality Color tweets
	//-----------------------------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------------------------//

	
	public void generateColorNOCtweets(String tweetDir, String kbDir, String resDir, String stereoModelFile)
	{
		KnowledgeBaseModule NOC   = new KnowledgeBaseModule(kbDir + "Veale's The NOC List.txt", 0);
		Hashtable POS_QUALITIES   = NOC.getInvertedField("Positive Talking Points");
		Hashtable NEG_QUALITIES   = NOC.getInvertedField("Negative Talking Points");
		
		Vector exemplars 		  = NOC.getAllFrames();
		
		BucketTable stereoModel   = new BucketTable(resDir, stereoModelFile);		
		BucketTable propertyModel = stereoModel.invertTable();
		
		for (int e = 0; e < exemplars.size(); e++)
		{
			String exemplar   = (String)exemplars.elementAt(e);
			
			Vector posQuals   = NOC.getFieldValues("Positive Talking Points", exemplar), posColors = null;
			Vector negQuals   = NOC.getFieldValues("Negative Talking Points", exemplar), negColors = null;
			
			if (posQuals == null || negQuals == null) continue;
						
			generatePersonalityColorTweets(exemplar, NOC.hashtagify(exemplar), 
										   posQuals, negQuals, 
										   stereoModel, propertyModel);
		}
	}
	
	
	
	
	private void generatePersonalityColorTweets(String exemplar, String hashtag,
												Vector posQuals, Vector negQuals, 
											    BucketTable stereoModel, BucketTable propertyModel)
	{
		String posQual = null, negQual = null;
		
		for (int p1 = 0; p1 < posQuals.size(); p1++)
		{
			posQual   = (String)posQuals.elementAt(p1);				
			
			Vector posColors = lexicon.getColorTermsForProperty(posQual, propertyModel);
			
			if (posColors == null) continue;
			
			// pick top 3 iff we can rank candidates by similarity to the exemplar	
			if (stereoModel.get(exemplar) != null && posColors.size() > SAMPLE_SIZE) 			
				posColors = getTopMostSimilar(exemplar, posColors, stereoModel, SAMPLE_SIZE);
			
			for (int n1 = 0; n1 < negQuals.size(); n1++)
			{
				negQual   = (String)negQuals.elementAt(n1);		
				
				if (negQual == posQual) continue;
				
				Vector negColors = lexicon.getColorTermsForProperty(negQual, propertyModel);
				
				if (negColors == null) continue;
				
				// pick top 3 iff we can rank candidates by similarity to the exemplar	
				if (stereoModel.get(exemplar) != null && negColors.size() > SAMPLE_SIZE) 			
					negColors = getTopMostSimilar(exemplar, negColors, stereoModel, SAMPLE_SIZE);
				
				// at this point we have a concept with one positive property and one negative property 
				// that can each be rendered using a specific color
				
				for (int pt = 0; pt < posColors.size(); pt++)
				{
					String posColor = (String)posColors.elementAt(pt);
					
					if (areTooSimilar(exemplar, posColor)) continue;
					
					Vector posHues  = lexicon.getHuesFor(posColor);
					
					if (posHues == null) continue;
					
					for (int nt = 0; nt < negColors.size(); nt++)
					{
						String negColor = (String)negColors.elementAt(nt);
						
						if (negColor.equals(posColor) || areTooSimilar(exemplar, negColor)) 
							continue;
						
						Vector negHues  = lexicon.getHuesFor(negColor);
						
						if (negHues == null) continue;
						
						for (int ph = 0; ph < posHues.size(); ph++)
						{
							String posHue = (String)posHues.elementAt(ph);
							String posRGB = lexicon.getRGBFor(posColor, posHue);
							
							if (posRGB == null) continue;
							
							for (int nh = 0; nh < negHues.size(); nh++)
							{
								String negHue = (String)negHues.elementAt(nh);
								String negRGB = lexicon.getRGBFor(negColor, negHue);
								
								if (negRGB == null) continue;
								
								if (lexicon.getDistanceBetween(negRGB, posRGB) < MIN_RGB_DIST)
									continue;
								
								printColorTweet(exemplar, hashtag, "but", posQual, negQual, posColor, negColor, posHue, negHue, posRGB, negRGB);
																
								// Try adding an extra negative quality
								
								for (int n2 = n1+1; n2 < negQuals.size(); n2++)
								{
									String negQual2   = (String)negQuals.elementAt(n2);				
									Vector negColors2 = lexicon.getColorTermsForProperty(negQual2, propertyModel);
									
									if (negColors2 == null || negQual2 == negQual) continue;
									
									for (int nt2 = 0; nt2 < negColors2.size(); nt2++)
									{
										String negColor2 = (String)negColors2.elementAt(nt2);
										
										if (negColor2.equals(posColor) || negColor2.equals(negColor) || areTooSimilar(exemplar, negColor2)) 
											continue;
										
										Vector negHues2  = lexicon.getHuesFor(negColor2);
										
										if (negHues2 == null) continue;
										
										for (int nh2 = 0; nh2 < negHues2.size(); nh2++)
										{
											String negHue2 = (String)negHues2.elementAt(nh2);
											String negRGB2 = lexicon.getRGBFor(negColor2, negHue2);
											
											if (negRGB2 == null) continue;
											
											if (lexicon.getDistanceBetween(negRGB2, posRGB) < MIN_RGB_DIST ||
												lexicon.getDistanceBetween(negRGB2, negRGB) < MIN_RGB_DIST)	
												continue;
											
											printColorTweet(exemplar, hashtag, "tarnished",
														    posQual, negQual, negQual2, 
														    posColor, negColor, negColor2,
														    posHue, negHue, negHue2,
														    posRGB, negRGB, negRGB2);
										}
									}
								}
								
								// Try adding an extra positive quality
								
								for (int p2 = p1+1; p2 < posQuals.size(); p2++)
								{
									String posQual2   = (String)posQuals.elementAt(p2);				
									Vector posColors2 = lexicon.getColorTermsForProperty(posQual2, propertyModel);
									
									if (posColors2 == null || posQual2 == posQual) continue;
									
									for (int pt2 = 0; pt2 < posColors2.size(); pt2++)
									{
										String posColor2 = (String)posColors2.elementAt(pt2);
										
										if (posColor2.equals(posColor) || posColor2.equals(posColor) || areTooSimilar(exemplar, posColor2)) 
											continue;
										
										Vector posHues2  = lexicon.getHuesFor(posColor2);
										
										if (posHues2 == null) continue;
										
										for (int ph2 = 0; ph2 < posHues2.size(); ph2++)
										{
											String posHue2 = (String)posHues2.elementAt(ph2);
											String posRGB2 = lexicon.getRGBFor(posColor2, posHue2);
											
											if (posRGB2 == null) continue;
											
											if (lexicon.getDistanceBetween(posRGB2, posRGB) < MIN_RGB_DIST ||
												lexicon.getDistanceBetween(posRGB2, negRGB) < MIN_RGB_DIST)	
												continue;
											
											printColorTweet(exemplar, hashtag, "innermost",
														    posQual, posQual2, negQual, 
														    posColor, posColor2, negColor, 
														    posHue, posHue2, negHue, 
														    posRGB, posRGB2, negRGB);
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	
	
	private Vector getTopMostSimilar(String target, Vector candidates, BucketTable stereoModel, int topN)
	{
		Vector ordered = new Vector(), topNelements = new Vector();
		
		for (int c = 0; c < candidates.size(); c++)
		{
			String candidate = (String)candidates.elementAt(c);
			
			if (candidate.equals(target)) continue;
			
			int sim = stereoModel.getOverlappingExtent(target, candidate);
			
			if (sim <= 0) continue;
			
			ordered.add(new tabular.SymbolCounter(candidate, sim*1000 + RND.nextInt(1000))); // add random element to discourage equal weights
		}
		
		ordered = tabular.SymbolMap.getSorted(ordered);
		
		for (int n = 0; n < topN && n < ordered.size(); n++)
		{
			if (n >= ordered.size()) break;
			
			topNelements.add(((tabular.SymbolCounter)ordered.elementAt(n)).getSymbol());
		}
		
		return topNelements;
	}
	
	
	
	private void printColorTweet(String exemplar, String hashtag,  String soulMod,
								 String qual1,    String qual2,    String qual3, 
								 String color1,   String color2,   String color3,
								 String hue1,     String hue2,     String hue3,
								 String rgb1,     String rgb2,     String rgb3)
	{  
		String pigment1 = color1, pigment2 = color2, pigment3 = color3;
		
		if (color1.endsWith("_" + hue1))
			pigment1 = color1.substring(0, color1.length() - hue1.length()-1) + " " + 
						 lexicon.resolveHue(hue1, rgb1);
		else
			pigment1 = color1 + "-" + lexicon.resolveHue(hue1, rgb1);
		
		if (color2.endsWith("_" + hue2))
			pigment2 = color2.substring(0, color2.length() - hue2.length()-1) + " " + 
						 lexicon.resolveHue(hue2, rgb2);
		else
			pigment2 = color2 + "-" + lexicon.resolveHue(hue2, rgb2);
		
		if (color3.endsWith("_" + hue3))
			pigment3 = color3.substring(0, color3.length() - hue3.length()-1) + " " + 
						  lexicon.resolveHue(hue3, rgb3);
		else
			pigment3 = color3 + "-" + lexicon.resolveHue(hue3, rgb3);
		
		String blurb = null;
		
		if (Character.isUpperCase(exemplar.charAt(0)))
			blurb = "I painted this picture of " + hashtag + "'s " + soulMod + " soul using " +
			     	 qual1  + " " + pigment1 + ", " + qual2 + " " + pigment2 + " and " +
			     	 qual3 + " " + pigment3 + ".";
		else
			blurb = "I call this picture \"" + replaceWith(capitalizeFirst(exemplar), "_", " ") + "\". I painted it with " +
			     	 qual1  + " " + pigment1 + ", " + qual2 + " " + pigment2 + " and " +
			     	 qual3 + " " + pigment3 + ".";

		if (blurb.length() <= 140)
			printlnDribbleFile(getDribblePosition() + "\t" + hashtag + "RGB\t" + blurb + "\t" + 
						       rgb1 + "\t" + rgb2 + "\t" + rgb3);  
		
	}
	
	
	
	
	
	private void printColorTweet(String exemplar, String hashtag, String link,
								 String qual1,    String qual2, 
								 String color1,   String color2,
								 String hue1,     String hue2,
								 String rgb1,     String rgb2)
	{
		String posPigment = color1, negPigment = color2;
		
		if (color1.endsWith("_" + hue1))
			posPigment = color1.substring(0, color1.length() - hue1.length()-1) + " " + 
						 lexicon.resolveHue(hue1, rgb1);
		else
			posPigment = color1 + "-" + lexicon.resolveHue(hue1, rgb1);
		
		if (color2.endsWith("_" + hue2))
			negPigment = color2.substring(0, color2.length() - hue2.length()-1) + " " + 
						 lexicon.resolveHue(hue2, rgb2);
		else
			negPigment = color2 + "-" + lexicon.resolveHue(hue2, rgb2);
		
		String blurb = null;
		
		if (Character.isUpperCase(exemplar.charAt(0)))
			blurb = "I painted this picture of " + exemplar + "'s " + qual1 + " " + link + " " + qual2 + " soul using " +
					 qual1 + " " + posPigment + " and " + qual2 + " " + negPigment + ".";
		else
			blurb = "I call this picture \"" + replaceWith(capitalizeFirst(exemplar) +  ", " + 
														   capitalizeFirst(qual1) + " " + link + " " + capitalizeFirst(qual2), "_", " ") + 
					"\". I painted it with " + qual1 + " " + posPigment + " and " + qual2 + " " + negPigment + ".";
		
		if (blurb.length() <= 140)
			printlnDribbleFile(getDribblePosition() + "\t" + hashtag + "RGB\t" + blurb + "\t" + 
						       rgb1 + "\t" + rgb2);  
		
	}
	
	
	
	
	
	
	private boolean areTooSimilar(String conceptName, String colorName)
	{
		if (conceptName.toLowerCase().equals(colorName.toLowerCase()))
			return true;
		
		int dash = colorName.indexOf((int)'_');
		
		while (dash > 0) {
			colorName = colorName.substring(0, dash) + " " + colorName.substring(dash+1);
			dash = colorName.indexOf((int)'_');
		}
		
		return (conceptName.startsWith(colorName) || conceptName.endsWith(colorName));
	}
	
	
	
	

	
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Main test stub
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	public static void main(String[] args)
	{
		String dir   = "/Users/tonyveale/Desktop/Lexical Resources/Moods/";
		String kdir  = "/Users/tonyveale/Dropbox/CodeCamp2016/NOC/DATA/TSV Lists/";
		
		Lexinomicron lexinomicron = new Lexinomicron(dir + "color stereotype lexicon.idx");
		
		lexinomicron.ONLY_TO_SCREEN = false;
		
		/*
		lexinomicron.openDribbleFile(dir + "RGB 2 color tweets.idx");
		
		lexinomicron.loadBracketedBigrams(dir + "bracketed color stereotype bigrams.idx");
		lexinomicron.loadPluralBigrams(dir + "plural color stereotype bigrams.idx");
	
		
		lexinomicron.openDribbleFile(dir + "RGB NOC color tweets.idx");
		lexinomicron.generateColorNOCtweets(dir, kdir, dir, "stereotype model.idx");
		*/
		
//		lexinomicron.openDribbleFile(dir + "RGB stereo color tweets.idx");
//		lexinomicron.generateColorStereotypetweets(dir, kdir, dir, "stereotype model.idx");
		
		lexinomicron.openDribbleFile(dir + "RGB colourlovers tweets.idx");

//		lexinomicron.openDribbleFile(dir + "RGB visual metaphor tweets.idx");
		lexinomicron.generateColourLoverTweets(dir, "RGB palettes.idx");
		
//		lexinomicron.closeDribbleFile();
 		 
	}
}
