package twitterbotics;

import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

import tabular.BucketTable;
import tabular.CountTable;
import tabular.SymbolCounter;
import tabular.SymbolMap;


// Generate apt metaphors for a given Twitter handle based on the Affective profile of their most recent tweets

public class AffectiveMetaphorizer 
{
	private static Random RND 	 				= new Random();
	
	private static String HULK_COLORS			= "#158202\t#00592D\t#550A55\t#849137\t#HulkRGB";
	
	public static final int META_SAMPLE			= 100;
	
	private ColorLexicon lexicon 				= null;
	
	private BucketTable stereoModel 			= null;
	private BucketTable propertyModel 			= null;
	private BucketTable singlesToPlurals 		= null;
	
	private KnowledgeBaseModule transformulas	= null;
	private KnowledgeBaseModule NOC				= null;
	
	private DessertMaker chef					= null;
		
	private Hashtable NEG_QUALITIES 		 	= null;
	private Hashtable POS_QUALITIES 		 	= null;

	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Constructors
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	
	public AffectiveMetaphorizer(String resdir, String knowledgeDir, String lexiconFilename, 
								 String stereoFilename, String pluralFilename)
	{
		lexicon = new ColorLexicon(resdir + lexiconFilename);
		
		singlesToPlurals = new BucketTable(resdir, pluralFilename);
		
		stereoModel   = new BucketTable(resdir, "stereotype model.idx");		
		propertyModel = stereoModel.invertTable();
		
		NOC           = new KnowledgeBaseModule(knowledgeDir + "Veale's The NOC List.txt", 0);
		
		NOC.invertFieldInto("Positive Talking Points", propertyModel);
		NOC.invertFieldInto("Negative Talking Points", propertyModel);
		
		POS_QUALITIES = NOC.getInvertedField("Positive Talking Points");
		NEG_QUALITIES = NOC.getInvertedField("Negative Talking Points");
				
		transformulas = new KnowledgeBaseModule(knowledgeDir + "Veale's ranked quality classifications.txt");
		
		chef          = new DessertMaker(knowledgeDir);
	}
	
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// sSeful Access Methods
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	public ColorLexicon getLexicon()
	{
		return lexicon;
	}
	
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Obtain a fully-rounded affective profile of a given Twitter user
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	
	public AffectiveProfile analzyeWords(String handle)
	{
		return new AffectiveProfile(handle, transformulas);
	}
	
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Find NOC metaphors for a given Twitter user
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	
	public BucketTable tabulateCandidateMetaphorsFor(AffectiveProfile profile, int posThreshold, int negThreshold)
	{
		return tabulateCandidateMetaphorsFrom(profile.getSortedDimensions(), posThreshold, negThreshold);
	}
	
	
	
	private BucketTable tabulateCandidateMetaphorsFrom(Vector qualities, int posThreshold, int negThreshold)
	{
		BucketTable candidates = new BucketTable("candidates");
		
		for (int q = 0; q < qualities.size(); q++)
		{
			SymbolCounter quality = (SymbolCounter)qualities.elementAt(q);
			
			if (quality.value() < Math.min(posThreshold, negThreshold)) break;
			if (!crossesThreshold(quality, posThreshold, negThreshold)) continue;
						
			Vector stereos = propertyModel.get(quality.getSymbol());
			
			if (stereos == null) 
				continue;
			
			for (int s = 0; s < stereos.size(); s++)
				tabulateQuality(candidates, (String)stereos.elementAt(s), quality.getSymbol());
		}
		
		return candidates;
	}
	
	
	
	
	private void tabulateQuality(BucketTable candidates, String stereo, String quality)
	{
		Vector previous = candidates.get(stereo);
		
		if (previous == null) {
			candidates.put(stereo, quality);
			return;
		}
		
		for (int p = 0; p < previous.size(); p++)
		{
			String prevQual = (String)previous.elementAt(p);
			
			if (prevQual.endsWith("-" + quality)) // avoid redundancy
				return;
			else
			if (quality.endsWith("-" + prevQual)) // prefer more detail where possible
			{
				previous.setElementAt(quality, p);
				return;
			}
		}
		
		candidates.put(stereo, quality);
	}
	
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Generate a disgusting "Just Deserts" metaphorical classification for a user
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	public String compareAndDisgust(AffectiveProfile profile, int posThreshold, int negThreshold)
	{
		String threat = compareAndDisgust(profile, negThreshold, 2,2);
		
		if (threat == null)
			threat = compareAndDisgust(profile, negThreshold, 2,2);
		
		return threat;
	}
	
	
	
	public String compareAndDisgust(AffectiveProfile profile, int negThreshold, int topN, int minQ)
	{
		String handle = profile.getHandle();
		
		if (handle.startsWith("@")) 
			handle = handle.substring(1);
		
		// ensure comparison is based on negative properties only
		
		BucketTable candidateTable = tabulateCandidateMetaphorsFor(profile, 1000, negThreshold);
		
		Vector candidates = candidateTable.getOrderedKeyList();
		
		System.out.println(candidates.size() + ":" + candidates);
		
		if (canTruncateCadidateList(candidates, topN, minQ))
			Dribbler.randomize(candidates);
		
		if (candidates == null || candidates.size() < topN) 
			return null;

		for (int c = 0; c < candidates.size(); c++)
		{
			String bestComparison  = ((SymbolCounter)candidates.elementAt(c)).getSymbol();
		
			Vector sharedQualities = candidateTable.get(bestComparison);
			
			if (sharedQualities.size() < minQ) continue;
			
			disperseTopN(profile, sharedQualities, minQ);

			String renderComparison = bestComparison;
			
			if (NOC.getFirstValue("Gender", bestComparison) == null)
				renderComparison = singlesToPlurals.getFirst(bestComparison);
			
			if (renderComparison == null) continue;
		
			String negPivot = (String)sharedQualities.elementAt(0);
			String altPivot = (String)sharedQualities.elementAt(1);
			
			String dessert  = chef.makeDessertVariant();

			String justice  = "For " + negPivot + " tweets that remind me of " + renderComparison + ", @" + handle + "'s " +
							  "#JustDesert is " + dessert + ".";
					
			if (justice.length() <= 140 - altPivot.length() - 5)
				justice  = "For " + negPivot + " and " + altPivot + " tweets that remind me of " + renderComparison
							+ ", @" + handle + "'s " + "#JustDesert is " + dessert + "."; 
				
			if (justice.length() <= 140 - 7)
				justice = justice + " Enjoy!";
			
			if (justice.length() <= 140)
				return justice;
		}
		
		return null;
	}
	
	
	
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Generate a threatening Hulk-themed metaphorical classification for a user
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	public String compareAndThreaten(AffectiveProfile profile, int posThreshold, int negThreshold)
	{
		String threat = compareAndThreaten(profile, posThreshold, negThreshold, 3,4);
		
		if (threat == null)
			threat = compareAndThreaten(profile, posThreshold, negThreshold, 3,3);
		
		return threat;
	}
	
	
	
	public String compareAndThreaten(AffectiveProfile profile, int posThreshold, int negThreshold, int topN, int minQ)
	{
		String handle = profile.getHandle();
		
		if (handle.startsWith("@")) 
			handle = handle.substring(1);
		
		BucketTable candidateTable = tabulateCandidateMetaphorsFor(profile, posThreshold, negThreshold);
		
		Vector candidates = candidateTable.getOrderedKeyList();
		
		// remove non-NOC list elements
		
		for (int c = candidates.size()-1; c >= 0; c--)
		{
			SymbolCounter candidate = (SymbolCounter)candidates.elementAt(c);
			
			if (NOC.getFirstValue("Gender", candidate.getSymbol()) != null)
				continue;
			
			if (singlesToPlurals.getFirst(candidate.getSymbol()) == null)
				candidates.remove(c);		
		}
		
		System.out.println(candidates.size() + ":" + candidates);
		
		if (canTruncateCadidateList(candidates, topN, minQ))
			Dribbler.randomize(candidates);
		
		if (candidates == null || candidates.size() < topN) 
			return null;
		
		for (int c = 0; c < candidates.size(); c++)
		{
			String bestComparison  = ((SymbolCounter)candidates.elementAt(c)).getSymbol();
		
			Vector sharedQualities = candidateTable.get(bestComparison);
			
			if (sharedQualities.size() < minQ) continue;
		
			disperseTopN(profile, sharedQualities, minQ);
			
			String renderComparison = bestComparison;
			
			if (NOC.getFirstValue("Gender", bestComparison) == null)
				renderComparison = singlesToPlurals.getFirst(bestComparison);
			
			if (renderComparison == null) continue;
		
			String threat = "Hulk HATE punily " + sharedQualities.elementAt(0) + " humans whose tweets remind Hulk of " + renderComparison + ". " +
							"Hulk SMASH @" + handle + "'s ";
									
			for (int q = 1; q < minQ; q++)
			{
				if (q == minQ-1)
					threat = threat + " and ";
				else
				if (q > 1)
					threat = threat + ", ";
				
				threat = threat + sharedQualities.elementAt(q);
			}
			
			threat = threat + " tweets!";		

			if (threat.length() <= 140)
				return threat + "\t" + HULK_COLORS;
		}
		
		return null;
	}
	
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Judge a Twitter user using a metaphor and a disavowal in same tweet
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	public String compareAndDisavow(AffectiveProfile profile, int posThreshold, int negThreshold, int topN, int minQ)
	{
		String handle = profile.getHandle();
		
		if (handle.startsWith("@")) 
			handle = handle.substring(1);
		
		BucketTable candidateTable = tabulateCandidateMetaphorsFor(profile, posThreshold, negThreshold);
		
		Vector candidates = candidateTable.getOrderedKeyList();
		
		System.out.println(candidates.size() + ":" + candidates);
		
		if (canTruncateCadidateList(candidates, topN, minQ))
			Dribbler.randomize(candidates);
		
		if (candidates == null || candidates.size() < topN) 
			return null;
		
		for (int c = 0; c < candidates.size(); c++)
		{
			String bestComparison  = ((SymbolCounter)candidates.elementAt(c)).getSymbol();
		
			Vector sharedQualities = candidateTable.get(bestComparison);
			
			if (sharedQualities.size() < minQ) continue;
		
			disperseTopN(profile, sharedQualities, minQ);
		
			String comparison = "Sure, @" + handle + "'s tweets seem ";
		
			for (int q = 0; q < minQ; q++)
			{
				if (q == minQ-1)
					comparison = comparison + " and ";
				else
				if (q > 0)
					comparison = comparison + ", ";
				
				comparison = comparison + sharedQualities.elementAt(q);
			}
			
			comparison = comparison + ".\nBut @" + handle + " is no " + bestComparison + "!";			

			if (comparison.length() <= 140)
				return comparison;
		}
		
		return null;
	}
	
	
	private void disperseTopN(AffectiveProfile profile, Vector qualities, int topN)
	{
		for (int t = 0; t < topN*topN; t++)
		{
			for (int i = 1; i < topN; i++)
				if (profile.getValue((String)qualities.elementAt(i)) == profile.getValue((String)qualities.elementAt(i-1)))
					swapPositions(qualities, i, RND.nextInt(qualities.size()));
		}
	}
	
	private void swapPositions(Vector elements, int left, int right)
	{
		Object temp = elements.elementAt(left);
		
		elements.setElementAt(elements.elementAt(right), left);
		elements.setElementAt(temp, right);
	}
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Suggest top-N similar ideas for a given Twitter personality
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	public String compareWithTopN(AffectiveProfile profile, int posThreshold, int negThreshold, int topN)
	{
		String handle = profile.getHandle();
		
		if (handle.startsWith("@")) 
			handle = handle.substring(1);
		
		BucketTable candidateTable = tabulateCandidateMetaphorsFor(profile, posThreshold, negThreshold);
		
		Vector candidates = candidateTable.getOrderedKeyList();
		
		System.out.println(candidates.size() + ":" + candidates);
		
		if (canTruncateCadidateList(candidates, topN, 3))
			Dribbler.randomize(candidates);

		if (candidates == null || candidates.size() < topN) 
			return null;
		
		String comparison = "#Take" + topN + " things that remind me of @" + handle + ":";
		
		int numComparisonsSoFar = 0;
		
		for (int n = 0; n < candidates.size(); n++)
		{
			SymbolCounter candidate = (SymbolCounter)candidates.elementAt(n);
			
			String rendering = candidate.getSymbol();
			
			if (rendering.length() > 20) continue;
			
			if (Character.isLowerCase(rendering.charAt(0)))
				rendering = singlesToPlurals.getFirst(rendering);
			
			if (rendering == null) continue;
			
			if (Character.isLowerCase(rendering.charAt(0)))
				rendering = Dribbler.capitalizeEach(rendering);
			
			rendering =  Dribbler.replaceWith(rendering, "_", " ");
			
			if (rendering.indexOf((int)' ') > 0)
			{
				if (comparison.indexOf(rendering.substring(0, rendering.indexOf((int)' '))) > 0)  continue;
				if (comparison.indexOf(rendering.substring(rendering.indexOf((int)' ')) + 1) > 0) continue;
			}
			
			if (comparison.indexOf(rendering) > 0) continue;
						
			numComparisonsSoFar++;
			
			comparison = comparison + "\n" + numComparisonsSoFar + ". " + rendering;
											
			if (numComparisonsSoFar == topN )
				return comparison;
		}
		
		return null;
	}
	
	
	
	private boolean canTruncateCadidateList(Vector candidates, int after, int atLeast)
	{
		if (candidates == null || candidates.size() <= after)
			return false;
		
		for (int c = after; c < candidates.size(); c++) {
			SymbolCounter candidate = (SymbolCounter)candidates.elementAt(c);
			
			if (candidate.value() < atLeast) {
				candidates.setSize(c);
				break;
			}
		}
		
		return candidates.size() > after;		
	}
	
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Suggest Color-grounded Metaphor for Twitter users based on their profiles
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	
	public String metaphorizeProfile(AffectiveProfile profile, int posThreshold, int negThreshold, Vector palette)
	{
		Vector choices = harvestMetaphorsFor(profile, posThreshold, posThreshold, META_SAMPLE, palette);
		
		if (choices == null || choices.size() == 0)
			return null;
		
		for (int c  = 0; c < choices.size(); c++) 
			System.out.println(choices.elementAt(c));
		
		if (choices.size() == 1)
			return (String)choices.elementAt(0);
		
		Dribbler.randomize(choices);
		
		for (int c = 0; c < choices.size(); c++)
			if (containsEveryWordIn((String)choices.elementAt(c), palette))
				return (String)choices.elementAt(c);
		
		return (String)choices.elementAt(RND.nextInt(choices.size()));
	}
	
	
	
	private boolean containsEveryWordIn(String text, Vector words)
	{
		if (words == null || words.size() == 0) return true;
		
		for (int w = 0; w < words.size(); w++)
			if (text.indexOf((String)words.elementAt(w)) < 0)
				return false;
		
		return true;
	}
	
	
	
	public Vector harvestMetaphorsFor(AffectiveProfile profile, int posThreshold, int negThreshold, int harvestSize, Vector palette)
	{		
		BucketTable candidateTable = tabulateCandidateMetaphorsFor(profile, posThreshold, negThreshold);
		
		Vector candidates = candidateTable.getOrderedKeyList();
		
		if (candidates == null) 
			return null;
		else
			refineCandidateSet(candidates, profile.getSortedDimensions(), candidateTable, posThreshold, negThreshold);
		
		System.out.println(candidates.size() + ":" + candidates);
		
		Vector harvested = new Vector();
		
		for (int pass = 0; pass < 2 && harvested.size() < harvestSize; pass++)
		{
			for (int c = 0; c < candidates.size()*harvestSize; c++)
			{	
				String source     = ((SymbolCounter)candidates.elementAt(RND.nextInt(candidates.size()))).getSymbol();		
				Vector qualities  = candidateTable.get(source);
				
				harvestMetaphorFor(profile, source, qualities, harvested, posThreshold, negThreshold, pass == 0, palette);
				
				if (harvested.size() >= harvestSize) break;
			}
		}
		
		return harvested;
	}
	
	
	
	
	private boolean harvestMetaphorFor(AffectiveProfile profile, String source, Vector qualities, Vector harvested, 
									   int posThreshold, int negThreshold, boolean noRedundancy, Vector palette)
	{
		String target = profile.getHandle();
		
		if (target.startsWith("@")) 
			target = target.substring(1);
			
		if (qualities == null || qualities.size() < 3) 
			return false;
		
		qualities = (Vector)(Dribbler.randomize(qualities)).clone();
		
		String spareQual  =  getStrongQualityNotIn(profile, qualities, posThreshold, negThreshold);
		
		if (spareQual != null) qualities.add(spareQual);
				
		for (int q1 = 0; q1 < qualities.size(); q1++)
		{
			String quality1 = (String)qualities.elementAt(q1);
			
			Vector stereos1 = lexicon.getColorTermsForProperty(quality1, propertyModel);
			
			if (stereos1 == null || stereos1.size() == 0) 
				continue;
			else
				Dribbler.randomize(stereos1);
			
			for (int q2 = q1+1; q2 < qualities.size(); q2++)
			{
				String quality2 = (String)qualities.elementAt(q2);
				
				if (noRedundancy && profile.getValue(quality2) == profile.getValue(quality1)) 
					continue;
				
				Vector stereos2 = lexicon.getColorTermsForProperty(quality2, propertyModel);
								
				if (stereos2 == null || stereos2.size() == 0) 
					continue;
				else
					Dribbler.randomize(stereos2);
				
				for (int q3 = q2+1; q3 < qualities.size(); q3++)
				{
					String quality3 = (String)qualities.elementAt(q3);
					
					if (noRedundancy && profile.getValue(quality3) == profile.getValue(quality2)) 
						continue;
					
					if (noRedundancy && profile.getValue(quality3) == profile.getValue(quality1)) 
						continue;
					
					Vector stereos3 = lexicon.getColorTermsForProperty(quality3, propertyModel);
					
					if (stereos3 == null || stereos3.size() == 0) 
						continue;
					else
						Dribbler.randomize(stereos3);
					
					
					for (int s1 = 0; s1 < stereos1.size(); s1++)
					{
						String stereo1    = (String)stereos1.elementAt(s1);							
						Vector hues1      = lexicon.getHuesFor(stereo1);
						
						if (hues1 == null || hues1.size() == 0) continue;
						
						for (int s2 = 0; s2 < stereos2.size(); s2++)
						{
							String stereo2   = (String)stereos2.elementAt(s2);
							Vector hues2     = lexicon.getHuesFor(stereo2);
															
							if (stereo2 == stereo1) continue;
							if (hues2 == null || hues2.size() == 0) continue;
							
							for (int s3 = 0; s3 < stereos3.size(); s3++)
							{
								String stereo3   = (String)stereos3.elementAt(s3);
								Vector hues3     = lexicon.getHuesFor(stereo3);
								
								if (stereo3 == stereo2 || stereo3 == stereo1) continue;
								if (hues3 == null || hues3.size() == 0) continue;
			
								for (int h1 = 0; h1 < hues1.size(); h1++)
								{
									String hue1 = (String)hues1.elementAt(h1);
									String rgb1 = lexicon.getRGBFor(stereo1, hue1);
									
									if (!matchesPalette(hue1, palette, 1)) continue;
													
									for (int h2 = 0; h2 < hues2.size(); h2++)
									{
										String hue2 = (String)hues2.elementAt(h2);
										String rgb2 = lexicon.getRGBFor(stereo2, hue2);
										
										if (!matchesPalette(hue2, palette, 2)) continue;
																				
										if (hue2.equals(hue1) && lexicon.getDistanceBetween(rgb1, rgb2) < 2*Lexinomicron.MIN_RGB_DIST)
											continue;
										
										if (lexicon.getDistanceBetween(rgb1, rgb2) < Lexinomicron.MIN_RGB_DIST) continue;
										
										for (int h3 = 0; h3 < hues3.size(); h3++)
										{
											String hue3 = (String)hues3.elementAt(h3);
											String rgb3 = lexicon.getRGBFor(stereo3, hue3);
											
											if (!matchesPalette(hue3, palette, 3)) continue;
											
											if (hue3.equals(hue2) && lexicon.getDistanceBetween(rgb3, rgb2) < 2*Lexinomicron.MIN_RGB_DIST)
												continue;
											
											if (hue3.equals(hue1) && lexicon.getDistanceBetween(rgb3, rgb1) < 2*Lexinomicron.MIN_RGB_DIST)
												continue;
											
											
											if (lexicon.getDistanceBetween(rgb3, rgb2) < Lexinomicron.MIN_RGB_DIST ||
												lexicon.getDistanceBetween(rgb3, rgb1) < Lexinomicron.MIN_RGB_DIST)	
												continue;
											
											String name       =  Dribbler.capitalizeEach(source);
											
											if (spareQual != null && !source.startsWith(spareQual))
												name = Dribbler.capitalizeEach(spareQual) + " " + name;
											
											name = Dribbler.replaceWith(name, "_", " ");
											
											String tweet   =    //noRedundancy + " " +
															   "I painted \"" + name + "\" after reading @***'s latest tweets, using " +
																//profile.getValue(quality1) + ":" + 
																quality1 + " " + lexicon.resolveStereoHue(stereo1, hue1, rgb1) + ", " + 
																//profile.getValue(quality2) + ":" + 
																quality2 + " " + lexicon.resolveStereoHue(stereo2, hue2, rgb2) + " and " +  
																//profile.getValue(quality3) + ":" + 
																quality3 + " " + lexicon.resolveStereoHue(stereo3, hue3, rgb3) + "."; 
											
											
											
											if (tweet.length() > 140) 
												tweet = Dribbler.replaceWith(tweet, "s latest ", "s ");
											
											if (tweet.length() > 140)
												tweet = Dribbler.replaceWith(tweet, "after reading", "from");
												
											if (tweet.length() > 140)
												tweet = Dribbler.replaceWith(tweet, "I painted", "I made");
											
											tweet = Dribbler.replaceWith(tweet, "_", " ");
											tweet = Dribbler.replaceWith(tweet, "@***", "@" + target);
											
											if (tweet.length() <= 140) {
												harvested.add(tweet + "\t" + rgb1 + "\t" + rgb2 + "\t" + rgb3);
												return true;
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
		
		return false;
	}
	
	
	
	
	
	
	private boolean matchesPalette(String hue, Vector palette, int numChoice)
	{
		if (palette == null || palette.size() == 0 || numChoice > palette.size())
			return true;
		
		int dash = hue.lastIndexOf((int)'-');
		
		if (dash > 0)
			hue = hue.substring(dash+1);
		
		return palette.indexOf(hue.intern()) >= 0;
	}
	
	
	
	
	private String getStrongQualityNotIn(AffectiveProfile profile, Vector blackoutList, int posThreshold, int negThreshold)
	{
		Vector sorted    = profile.getDimensions(); // assume already sorted
		Vector possibles = null;
		
		for (int q = 0; q < sorted.size(); q++)
		{
			SymbolCounter quality = (SymbolCounter)sorted.elementAt(q);
			
			if (!crossesThreshold(quality, posThreshold, negThreshold))
				continue;
			
			if (blackoutList.contains(quality.getSymbol())) continue;
			
			if (possibles == null) possibles = new Vector();
			
			possibles.add(quality.getSymbol());
		}
		
		if (possibles == null)
			return null;
		
		if (possibles.size() == 1)
			return (String)possibles.firstElement();
		
		return (String)possibles.elementAt(RND.nextInt(possibles.size()));
	}
	
	
	
	private boolean crossesThreshold(SymbolCounter quality, int posThreshold, int negThreshold)
	{
		if (POS_QUALITIES.get(quality.getSymbol()) != null)
			return quality.value() >= posThreshold;
			
		if (NEG_QUALITIES.get(quality.getSymbol()) != null)
			return quality.value() >= negThreshold;
				
		return quality.value() >= (negThreshold + posThreshold)/2;			
	}
	
	

	
	private void refineCandidateSet(Vector candidates, Vector userQualities, BucketTable possibilities, int posThreshold, int negThreshold)
	{
		if (candidates == null || candidates.size() == 0) return;
		
		for (int c  = 0; c < candidates.size(); c++)
		{
			SymbolCounter candidate     = (SymbolCounter)candidates.elementAt(c);
			
			if (candidate.value() < 3) {
				candidates.setSize(c);
				break;
			}
		}
		
		for (int c  = 0; c < candidates.size(); c++)
		{
			SymbolCounter candidate     = (SymbolCounter)candidates.elementAt(c);
			
			Vector sharedQualities      = possibilities.get(candidate.getSymbol());
			
			if (getNumColorableProperties(sharedQualities) == 2)
				elaborateCandidate(candidate, userQualities, sharedQualities, posThreshold, negThreshold);

		}
	}
	
	
	
	private boolean elaborateCandidate(SymbolCounter candidate, Vector userQualities, Vector sharedQualities, int posThreshold, int negThreshold)
	{
		for (int uq = 0; uq < userQualities.size(); uq++)
		{
			SymbolCounter uQual = (SymbolCounter)userQualities.elementAt(uq);
			
			if (sharedQualities.contains(uQual.getSymbol()) || candidate.getSymbol().startsWith(uQual.getSymbol())) 
				continue;
			
			if (!crossesThreshold(uQual, posThreshold, negThreshold)) 
				return false;
			
			candidate.setSymbol(uQual.getSymbol() + " " + candidate.getSymbol());
			sharedQualities.add(uQual.getSymbol());
			
			return true;
		}
		
		return false;
	}
	
	
	
	
	private int getNumColorableProperties(Vector qualities)
	{
		int count = 0;
		
		for (int q = 0; q < qualities.size(); q++) {
			Vector colors = lexicon.getColorTermsForProperty((String)qualities.elementAt(q), propertyModel);
			
			if (colors != null && colors.size() > 0) count++;
		}
		
		return count;
	}
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Main test stub
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	public static void main(String[] args)
	{
		String rdir   = "/Users/tonyveale/Desktop/Lexical Resources/Moods/";
		String kdir   = "/Users/tonyveale/Dropbox/CodeCamp2016/NOC/DATA/TSV Lists/";
		
		AffectiveMetaphorizer meta  = new AffectiveMetaphorizer(rdir, kdir, "color stereotype lexicon.idx", 
																			"stereotype model.idx",
																			"plural possession forms.idx");

		String handle = "@BotOnBotAction";
		int posThreshold = 65, negThreshold = 65;
		
		Vector palette = meta.getLexicon().getPaletteFrom("#PaintMySoul in red, blue and white");
		
		AffectiveProfile profile = meta.analzyeWords(handle);
		
		System.out.println(meta.tabulateCandidateMetaphorsFor(profile, posThreshold, negThreshold).getOrderedKeyList());
		
		System.out.println("Metaphor: " + meta.metaphorizeProfile(profile, posThreshold, negThreshold, palette));
				
		String comparisons = meta.compareWithTopN(profile, posThreshold, negThreshold, 5);
		
		if (comparisons == null) comparisons = "";
		
		System.out.println(comparisons.length() + ": " + comparisons);
		
		System.out.println("\n*** " + meta.compareAndThreaten(profile, posThreshold, negThreshold));	
		System.out.println("\n***"  + meta.compareAndDisavow(profile, posThreshold, negThreshold, 5, 4));
		System.out.println("\n*** " + meta.compareAndDisgust(profile, posThreshold, negThreshold));			
		
	}
	
}
