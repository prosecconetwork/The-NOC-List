package twitterbotics;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Hashtable;



// Use the knowledge-base(s) of famous people (real and fictional) to generate apt comparisons

public class PersonOfInterest extends Dribbler
{
	static Random DICE 						 = new Random();
	
	private String knowledgeDir				 = null;   // directory where knowledge-base(s) can be found
	
	// Various modules of the knowledge-base
	
	private KnowledgeBaseModule NOC          = null;
	private KnowledgeBaseModule CATEGORIES   = null;
	private KnowledgeBaseModule CLOTHES      = null;
	private KnowledgeBaseModule CREATIONS    = null;
	private KnowledgeBaseModule DOMAINS      = null;
	private KnowledgeBaseModule WORLDS       = null;
	private KnowledgeBaseModule VEHICLES     = null;
	private KnowledgeBaseModule WEAPONS	     = null;
	private KnowledgeBaseModule PLACES       = null;
	private KnowledgeBaseModule SUPERLATIVES = null;
	private KnowledgeBaseModule COMPARATIVES = null;
	private KnowledgeBaseModule ANTONYMS	 = null;
	private KnowledgeBaseModule PAST_PERFECTS= null;
	
	private Vector allPeople				 = null;
	private Vector fictionalPeople			 = null;
	private Vector realPeople				 = null;
	private Vector men						 = null;
	private Vector women					 = null;
	
	private Hashtable NEG_QUALITIES 		 = null;
	private Hashtable POS_QUALITIES 		 = null;
	private Hashtable ALL_QUALITIES 		 = null;
	

	private Vector attributeFields 			 = null;
	private Vector allFields	 			 = null;
	
	//-----------------------------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------------------------//
	//   Constructors
	//      --  Load the knowledge-bases, set up useful subsets of people
	//-----------------------------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------------------------//
	
	public PersonOfInterest(String kbDirectory)
	{
		knowledgeDir = kbDirectory;
		
		NOC           = new KnowledgeBaseModule(knowledgeDir + "Veale's The NOC List.txt", 0);
		CATEGORIES    = new KnowledgeBaseModule(knowledgeDir + "Veale's Category Hierarchy.txt", 0);
		CLOTHES       = new KnowledgeBaseModule(knowledgeDir + "Veale's clothing line.txt", 1);  // 1 is the column number of the key value
		CREATIONS     = new KnowledgeBaseModule(knowledgeDir + "Veale's creations.txt", 0);
		DOMAINS       = new KnowledgeBaseModule(knowledgeDir + "Veale's domains.txt", 0);
		WORLDS        = new KnowledgeBaseModule(knowledgeDir + "Veale's fictional worlds.txt", 0);
		VEHICLES      = new KnowledgeBaseModule(knowledgeDir + "Veale's vehicle fleet.txt", 1);  // 1 is the column number of the key value
		WEAPONS	      = new KnowledgeBaseModule(knowledgeDir + "Veale's weapon arsenal.txt", 1);  // 1 is the column number of the key value
		PLACES        = new KnowledgeBaseModule(knowledgeDir + "Veale's place elements.txt", 0);		
		SUPERLATIVES  = new KnowledgeBaseModule(knowledgeDir + "superlatives.txt", 0);
		COMPARATIVES  = new KnowledgeBaseModule(knowledgeDir + "comparatives.txt", 0);
		ANTONYMS	  = new KnowledgeBaseModule(knowledgeDir + "antonyms.txt", 0);
		PAST_PERFECTS = new KnowledgeBaseModule(knowledgeDir + "past perfects.txt", 0);
		POS_QUALITIES = NOC.getInvertedField("Positive Talking Points");
		NEG_QUALITIES = NOC.getInvertedField("Negative Talking Points");
		ALL_QUALITIES = NOC.getInvertedField("Positive Talking Points");
		ALL_QUALITIES = NOC.getInvertedField("Negative Talking Points", ALL_QUALITIES);
		
		allPeople       = NOC.getAllFrames();
		
		fictionalPeople = NOC.getAllKeysWithFieldValue("Fictive Status", "fictional");
		realPeople      = NOC.difference(allPeople, fictionalPeople);
		
		men			    = NOC.getAllKeysWithFieldValue("Gender", "male");
		women			= NOC.getAllKeysWithFieldValue("Gender", "female");
		
		allFields		= NOC.getFieldNames();
		
		attributeFields = new Vector();
		attributeFields.add("Negative Talking Points");
		attributeFields.add("Positive Talking Points");		
	}
	
	

	
	
	//-----------------------------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------------------------//
	// Generate Hulk Smash tweets
	//-----------------------------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------------------------//

	public void generateHulkSmashTweets(String tweetDir)
	{
		Vector exemplars = NOC.getAllFrames();
		
		openDribbleFile(tweetDir + "NOC Hulk tweets.idx");
		

		for (int e = 0; e < exemplars.size(); e++)
		{
			String exemplar   = (String)exemplars.elementAt(e);
			
			if (exemplar.indexOf("Hulk") >= 0) continue;
			
			String pronoun    = "he";
			String possPro	  = "His";
			
					
			if (NOC.hasFieldValue("Gender", exemplar, "female"))
			{
				pronoun = "she";
				possPro = "Her";
			}
			
			Vector vehicles   = NOC.getFieldValues("Vehicle of Choice", exemplar);
			Vector weapons    = NOC.getFieldValues("Weapon of Choice", exemplar);
			Vector actions    = NOC.getFieldValues("Typical Activity", exemplar);
			
			if (actions == null) continue;
			
			for (int a = 0; a < actions.size(); a++)
			{
				String action = (String)actions.elementAt(a);
			
				String hate   = "Hulk hate puny humans who tweet about " + action + ".";
				
				if (vehicles != null)
				{
					for (int v = 0; v < vehicles.size(); v++)
					{
						String vehicle = (String)vehicles.elementAt(v);
								
						String smash   = "Hulk smash " + NOC.hashtagify(exemplar) + " and the " + vehicle + " " + pronoun + " rode in on.";
						
						String tweet   = hate + "\t|" + smash;
						
						if (tweet.length() <= 140)
							printlnDribbleFile(getDribblePosition() + "\t" + NOC.hashtagify(exemplar) + "\t" + tweet);
					}
				}
				
				if (weapons != null)
				{
					for (int w = 0; w < weapons.size(); w++)
					{
						String weapon = (String)weapons.elementAt(w);
						
						if (weapon.indexOf((int)' ') < 0)
							weapon = "puny " + weapon;
								
						String smash   = "Hulk smash " + NOC.hashtagify(exemplar) + " first. " +  possPro + " " + weapon + " not frighten Hulk.";
						
						String tweet   = hate + "\t|" + smash;
						
						if (tweet.length() <= 140)
							printlnDribbleFile(getDribblePosition() + "\t" + NOC.hashtagify(exemplar) + "\t" + tweet);
					}
				}
				
			}
		}
		
		closeDribbleFile();
	}
	
	
	//-----------------------------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------------------------//
	// Generate Epitaphs
	//-----------------------------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------------------------//

	public void generateEpitaphs(String tweetDir)
	{
		Vector exemplars = NOC.getAllFrames();
		
		openDribbleFile(tweetDir + "NOC epitaph tweets.idx");
		
		KnowledgeBaseModule affordTenses = new KnowledgeBaseModule(knowledgeDir + "Veale's affordances.txt", 0);
		
		for (int e = 0; e < exemplars.size(); e++)
		{
			String exemplar   = (String)exemplars.elementAt(e);
			
			String pronoun    = "he";
			String possPro	  = "his";
			
			String canonicalName = NOC.getFirstValue("Canonical Name", exemplar), firstName = canonicalName;
			
			if (canonicalName == null) continue;
					
			if (canonicalName.indexOf((int)' ') > 0)
				firstName = canonicalName.substring(0, canonicalName.indexOf((int)' '));
					
			if (NOC.hasFieldValue("Gender", exemplar, "female"))
			{
				pronoun = "she";
				possPro = "her";
			}
			
			Vector vehicles   = NOC.getFieldValues("Vehicle of Choice", exemplar);
			Vector weapons    = NOC.getFieldValues("Weapon of Choice", exemplar);
			Vector opponents  = NOC.getFieldValues("Opponent", exemplar);
			Vector actors     = NOC.getFieldValues("Portrayed By", exemplar);
			Vector actions    = NOC.getFieldValues("Typical Activity", exemplar);
			
			if (vehicles != null && vehicles.size() > 0)
			{
				for (int v  = 0; v < vehicles.size(); v++)
				{
					String vehicle = (String)vehicles.elementAt(v);
					
					Vector affordances = VEHICLES.getFieldValues("Affordances", vehicle);
					
					if (affordances == null) continue;
					
					if (affordances.contains("driving"))			
						printlnDribbleFile(getDribblePosition() + "\t" + NOC.hashtagify(exemplar) + "\t" +
										   "I came, I saw, I ran them over in my " + vehicle + "|\t" +
										   "(Suggested epitaph for " + NOC.hashtagify(exemplar) + ")");
					else
					if (affordances.contains("flying"))			
						printlnDribbleFile(getDribblePosition() + "\t" + NOC.hashtagify(exemplar) + "\t" +
										   "I came, I saw, I dive-bomed them from my " + vehicle + "|\t" +
										   "(Suggested epitaph for " + NOC.hashtagify(exemplar) + ")");
					
				}
			}
			
			if (actions != null && actions.size() > 0)
			{
				for (int ac  = 0; ac < actions.size(); ac++)
				{
					String action = (String)actions.elementAt(ac);
					
					String epitaph =    "Here lies but the body of " + firstName + 
										",|may " + possPro + " soul forever go on " + action + ".|\t" +
										"(Suggested epitaph for " + NOC.hashtagify(exemplar) + ")";
					
					if (epitaph.length() <= 140)
						printlnDribbleFile(getDribblePosition() + "\t" + NOC.hashtagify(exemplar) + "\t" +  epitaph);
					
				}
			}
			
			if (weapons != null && weapons.size() > 0)
			{
				for (int w  = 0; w < weapons.size(); w++)
				{
					String weapon = (String)weapons.elementAt(w);
					
					Vector affordances = WEAPONS.getFieldValues("Affordances", weapon);
					
					if (affordances == null) continue;
					
					String det = WEAPONS.getFirstValue("Determiner", weapon);
					
					if (det == null) 
						det = "";
					else
						det = det + " ";
					
					for (int a = 0; a < affordances.size(); a++)
					{
						String affordance = (String)affordances.elementAt(a);
						
						Vector pastTenses = affordTenses.getFieldValues("Past Tense", affordance);
						
						if (pastTenses == null) continue;
						
						for (int pt = 0; pt < pastTenses.size(); pt++)
						{
							String pastTense = (String)pastTenses.elementAt(pt);
							String head      = pastTense.substring(0, pastTense.indexOf((int)' '));
							String tail      = pastTense.substring(head.length()+1);
							
							printlnDribbleFile(getDribblePosition() + "\t" + NOC.hashtagify(exemplar) + "\t" +
									   		   "I came, I saw, I " + head + " them all " + tail + " " + det + weapon + "|\t" +
									   		   "(Suggested epitaph for " + NOC.hashtagify(exemplar) + ")");
							
							if (opponents != null)
							{
								for (int o = 0; o < opponents.size(); o++)
								{
									String opponent = (String)opponents.elementAt(o);
									
									printlnDribbleFile(getDribblePosition() + "\t" + NOC.hashtagify(exemplar) + "\t" +
									   		   "I came, I saw, I " + head + " " + NOC.hashtagify(opponent) + " " + tail + " " + det + weapon + "|\t" +
									   		   "(Suggested epitaph for " + NOC.hashtagify(exemplar) + ")");
								}
							}
							
							if (actors != null)
							{
								for (int ac = 0; ac < actors.size(); ac++)
								{
									String actor = (String)actors.elementAt(ac);
									
									printlnDribbleFile(getDribblePosition() + "\t" + NOC.hashtagify(exemplar) + "\t" +
									   		   "I came, I saw, I " + head + " them all " + tail + " " + NOC.hashtagify(exemplar) + "'s " + weapon + "|\t" +
									   		   "(Suggested epitaph for " + NOC.hashtagify(actor) + ")");
								}
							}
						}
					}
				}
			}
		}
		
		closeDribbleFile();
	}
	
	//-----------------------------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------------------------//
	// Generate Blends of Good and Bad Qualities
	//-----------------------------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------------------------//

	public void generateGoodBadBlends(String tweetDir)
	{
		Vector exemplars = NOC.getAllFrames();
		
		Vector posFields = new Vector();  posFields.add("Positive Talking Points");
		Vector negFields = new Vector();  negFields.add("Negative Talking Points");
		
		openDribbleFile(tweetDir + "NOC blend tweets.idx");
		
		for (int e = 0; e < exemplars.size(); e++)
		{
			String exemplar = (String)exemplars.elementAt(e);
			
			Vector posSims  = NOC.getSimilarConcepts(exemplar, posFields, 2);
			
			if (posSims == null || posSims.size() < 2) continue;
			
			Vector negSims  = NOC.getSimilarConcepts(exemplar, negFields, 2);
			
			if (negSims == null || negSims.size() < 2) continue;
			
			for (int p = 0; p < posSims.size(); p++)
			{
				String posOther  = (String)posSims.elementAt(p);
				
				Vector posShared = NOC.getOverlappingFields(exemplar, posOther, posFields);
				
				for (int n = 0; n < negSims.size(); n++)
				{
					String negOther  = (String)negSims.elementAt(n);
					
					if (negOther.equals(posOther)) continue;
					
					Vector negShared = NOC.getOverlappingFields(exemplar, negOther, negFields);
					
					int unique = 0;
					
					for (int t = 0; t < negShared.size(); t++)
					{
						if (!NOC.hasFieldValue("Negative Talking Points", posOther, (String)negShared.elementAt(t)))
							unique++;
					}
					
					for (int t = 0; t < posShared.size(); t++)
					{
						if (!NOC.hasFieldValue("Positive Talking Points", negOther, (String)posShared.elementAt(t)))
							unique++;
					}
					
					if (unique < 3) continue;  // we don't want posOther and negOther to overlap too much
					
					String tweet = NOC.hashtagify(exemplar) + " combines the best of " + NOC.hashtagify(posOther) 
											+ " and the worst of " + NOC.hashtagify(negOther)
											+ ":\t" + condenseQualityList(posShared) + " yet also " + condenseQualityList(negShared);
					
					String pronoun = "He";
					
					if (NOC.hasFieldValue("Gender", exemplar, "female"))
						pronoun = "She";
					
					if (tweet.length() <= 136 - pronoun.length())
						tweet = replaceWith(tweet, ":\t", ":\t" + pronoun + " is ");
					
					if (tweet.length() <= 140)
						printlnDribbleFile(getDribblePosition() + "\t" 
										    + NOC.hashtagify(exemplar) + "Or" + NOC.hashtagify(posOther).substring(1) + " " 
										    + NOC.hashtagify(exemplar) + "Or" + NOC.hashtagify(negOther).substring(1) + "\t"
										    + tweet);
				}
			}
		}
		
		closeDribbleFile();
	}
	
	private String condenseQualityList(Vector qualities)
	{
		if (qualities == null || qualities.size() == 0)
			return "";
		
		StringBuffer condensed = new StringBuffer();
		
		for (int q = 0; q < qualities.size(); q++)
		{
			String quality = (String)qualities.elementAt(q);
			
			int delimiter  = quality.indexOf((int)'=');
			
			if (delimiter > 0)
				quality = quality.substring(delimiter+1);
			
			if (q == 0)
				condensed.append(quality);
			else
			if (q == 1 && qualities.size() == 2)
				condensed.append(" and ").append(quality);
			else
			if (q == 1 && qualities.size() > 2)
				condensed.append(", ").append(quality);
			else
			if (q == 2)
			{
				condensed.append(" and ").append(quality);
				break;
			}
		}
		
		return condensed.toString();
	}
	
	//-----------------------------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------------------------//
	//  Generate "I was Xing with Y when attacked by Z" metaphors
	//  E.g. Last night I dreamt I was cleaning floors with #GroundskeeperWillieMacDougal when we were 
	//  run over with ruthless ambition by #HillaryClinton	
	// I guess #HillaryClinton and #GroundskeeperWillieMacDougal represent warring parts of my personality:
	//   the capable vs. incompetent sides.
	//-----------------------------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------------------------//
	
	
	public void generateDreamConflicts(String tweetDir)
	{
		Vector exemplars = NOC.getAllFrames();
		
		openDribbleFile(tweetDir + "dream conflict tweets.idx");
				
		KnowledgeBaseModule dreamKB = new KnowledgeBaseModule("/Users/tonyveale/Desktop/Lexical Resources/Moods/Veale's Dream Symbols.idx");

		String preamble = "I dreamt I was ";

		for (int e = 0; e < exemplars.size(); e++)
		{
			String exemplar = (String)exemplars.elementAt(e);
			
			Vector posQuals = NOC.getFieldValues("Positive Talking Points", exemplar);
			
			if (posQuals == null) continue;
						
			String pos = null;
			
			for (int p = 0; p < posQuals.size(); p++)
			{
				pos      = (String)posQuals.elementAt(p);
								
				Vector opposites = ANTONYMS.getFieldValues("Antonym", pos);
				
				if (opposites != null)
				{
					for (int o = 0; o < opposites.size(); o++)
					{
						String opposite  = (String)opposites.elementAt(o);
						Vector instances = (Vector)NEG_QUALITIES.get(opposite);
						
						if (instances == null) continue;					
						
						for (int i = 0; i < instances.size(); i++)
						{
							String instance = (String)instances.elementAt(i);
							
							if (instance.equals(exemplar)) continue;
							
							Vector activities = NOC.getFieldValues("Typical Activity", instance);
							
							if (activities == null) continue;
							
							for (int a = 0; a < activities.size(); a++)
							{
								String activity = (String)activities.elementAt(a);
								
								Vector weapons  = NOC.getFieldValues("Weapon of Choice", exemplar);
								
								if (weapons == null) continue;
								
								for (int w = 0; w < weapons.size(); w++)
								{
									String weapon  = (String)weapons.elementAt(w);
									
									String wDet    = WEAPONS.getFirstValue("Determiner", weapon);
									
									if (wDet == null)
										wDet = "";
									else
										wDet = wDet + " ";
									
									Vector melees = WEAPONS.getFieldValues("Affordances", weapon);
									
									if (melees == null) continue;
									
									for (int m = 0; m < melees.size(); m++)
									{
										String melee   = getPastPerfectOf((String)melees.elementAt(m));
										
										if (melee == null) continue;
										
										String dream     = preamble + activity + " with " + NOC.hashtagify(instance) +
															" when we were " + melee + " " + wDet + weapon + " by " + NOC.hashtagify(exemplar);
											
										String followup  = "I guess " + NOC.hashtagify(exemplar) + " and " + NOC.hashtagify(instance) +
															" represent warring parts of my personality: the " + pos + " vs. " + opposite + " sides.";
											
										String tags      = "#" + Character.toUpperCase(pos.charAt(0)) + pos.substring(1) + "Or" +
																	Character.toUpperCase(opposite.charAt(0)) + opposite.substring(1);
										
										Vector normInterps = getDreamInterpretationsFor(exemplar, dreamKB);
										
										if (normInterps == null)
											normInterps = getDreamInterpretationsFor(instance, dreamKB);
										
										if (normInterps == null)
											normInterps = getDreamInterpretationsFor(weapon, dreamKB);
										
										if (normInterps == null) continue;
										
										String normInterp = (String)normInterps.elementAt(DICE.nextInt(normInterps.size()));
										
										if (dream.length() < 130)
											dream = "Last night " + dream;
										
										if (dream.length() <= 140 && followup.length() <= 140)
											printlnDribbleFile(getDribblePosition() + "\t" + tags + "\t" + dream + "\t" + followup + "\t" 
															  + "Well, " + Character.toLowerCase(normInterp.charAt(0)) + normInterp.substring(1));
									}
								}
							}
						}
					}
				}
			}
		}
		
		closeDribbleFile();
	}

	
	
	private Vector getDreamInterpretationsFor(String text, KnowledgeBaseModule dreamKB)
	{
		Vector symbols = getDreamSymbolsIn(text, dreamKB);
		
		if (symbols == null || symbols.size() == 0) return null;
		
		Vector allInterps = new Vector();
		
		for (int s = 0; s < symbols.size(); s++)
		{
			String symbol = (String)symbols.elementAt(s);
			
			Vector interps = dreamKB.getFieldValues("Interpretation", symbol);
			
			if (interps == null) continue;
			
			for (int i = 0; i < interps.size(); i++)
				allInterps.add(interps.elementAt(i));
		}
		
		return allInterps;
	}
	
	
	
	
	private Vector getDreamSymbolsIn(String text, KnowledgeBaseModule dreamKB)
	{
		Vector symbols = new Vector();

		if (dreamKB.getFieldValues("Interpretation", text) != null)
			symbols.add(text);
		
		StringTokenizer tokens = new StringTokenizer(text, " ,;'.()", false);
		
		while (tokens.hasMoreTokens())
		{
			String symbol = tokens.nextToken();
			
			if (dreamKB.getFieldValues("Interpretation", symbol) != null)
			{
				if (symbols == null) symbols = new Vector();
				
				symbols.add(symbol);
			}
		}
		
		return symbols;
	}
	
	
	//-----------------------------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------------------------//
	//  Generate Nietzschean  "What Doesn't Kill You" metaphors
	//      
	//  E.g.   If what doesn't kill you makes you stronger, 
	//     would being stung with a peanut shooter by #JimmyCarter make #PatchAdams more diplomatic?
	//      
	//-----------------------------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------------------------//

	public void generateNietzscheanTweets(String tweetDir)
	{
		Vector exemplars = NOC.getAllFrames();
		
		openDribbleFile(tweetDir + "Nietzschean tweets.idx");

		for (int e = 0; e < exemplars.size(); e++)
		{
			String exemplar = (String)exemplars.elementAt(e);
			Vector weapons  = NOC.getFieldValues("Weapon of Choice", exemplar);
			
			if (weapons == null) continue;
			
			for (int w = 0; w < weapons.size(); w++)
			{
				String weapon = (String)weapons.elementAt(w);
				
				String wDet   = WEAPONS.getFirstValue("Determiner", weapon);
				
				if (wDet == null) 
					wDet = "";
				else
					wDet = wDet + " ";
				
				Vector affordances = WEAPONS.getFieldValues("Affordances", weapon);
				
				if (affordances == null) continue;
				
				for (int a = 0; a < affordances.size(); a++)
				{
					String action = getPastPerfectOf((String)affordances.elementAt(a));
					
					if (action == null) continue;
						
					Vector posQuals = NOC.getFieldValues("Positive Talking Points", exemplar);
					
					if (posQuals == null) continue;
					
					String preamble = "If what doesn't kill you makes you stronger, ";
					
					String pos = null, neg = null, posMore = null, negMore = null;
					
					for (int p = 0; p < posQuals.size(); p++)
					{
						pos      = (String)posQuals.elementAt(p);
						
						posMore  = getComparativeForm(pos);
						
						if (posMore == null) continue;
						
						String ytweet = preamble 
											+ "shouldn't being " + action + " " + wDet + weapon + "\tby " + NOC.hashtagify(exemplar) 
											+ " make you " + posMore + "?";
						
						if (ytweet.length() <= 140)
							printlnDribbleFile(getDribblePosition() + "\t" + NOC.hashtagify(exemplar) + "\t" + ytweet);
						
						Vector<String> opposites = ANTONYMS.getFieldValues("Antonym", pos);
						
						if (opposites != null)
						{
							for (int o = 0; o < opposites.size(); o++)
							{
								String opposite  = (String)opposites.elementAt(o);
								Vector instances = (Vector<String>)NEG_QUALITIES.get(opposite);
								
								if (instances == null) continue;
								
								for (int i = 0; i < instances.size(); i++)
								{
									String instance = (String)instances.elementAt(i);
									
									if (instance.equals(exemplar)) continue;
									
									String attack   = "would being " + action + " " + wDet + weapon + " by " + NOC.hashtagify(exemplar);
									String followup = "make " + NOC.hashtagify(instance) + " " + posMore + "?";
									
									String tweet    = preamble + attack + "\t" + followup;
									
									if (tweet.length() <= 140)
										printlnDribbleFile(getDribblePosition() + "\t" + NOC.hashtagify(exemplar) + "\t" + tweet);
								}
							}
						}
					}
				}
			}				
		}
	
		closeDribbleFile();
	}
	
	
	
	
	private String getPastPerfectOf(String action)
	{
		String lookup = PAST_PERFECTS.getFirstValue("Past Perfect", action);
		
		if (lookup != null) return lookup;
		
		int space = action.indexOf((int)' ');
		
		if (space > 0)
		{
			lookup = PAST_PERFECTS.getFirstValue("Past Perfect", action.substring(0, space));
			
			if (lookup != null)
				return lookup + action.substring(space);
		}
		
		return null;
		
	}
	
	
	//-----------------------------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------------------------//
	//  Generate "Make X look like Y" metaphors
	//  E.g.    
	//   I see myself as expressive, but my brothers say that I make even someone 
	//     as sullen as #ConanTheBarbarian look like #HenryMiller.
	//-----------------------------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------------------------//

	private String[] associates = {"wife", "brothers", "friends", "co-workers", "colleagues", "boss"};
	private String[] speechActs = {"says", "say", "say", "say", "say", "says"};
	
	
	public void makeOthersLookGood(String tweetDir)
	{
		Vector exemplars = NOC.getAllFrames();
		
		openDribbleFile(tweetDir + "relative perspective tweets.idx");

		String preamble = "I see myself as ";

		for (int e = 0; e < exemplars.size(); e++)
		{
			String exemplar = (String)exemplars.elementAt(e);
			
			Vector posQuals = NOC.getFieldValues("Positive Talking Points", exemplar);
			
			if (posQuals == null) continue;
						
			String pos = null;
			
			for (int p = 0; p < posQuals.size(); p++)
			{
				pos      = (String)posQuals.elementAt(p);
								
				Vector opposites = ANTONYMS.getFieldValues("Antonym", pos);
				
				if (opposites != null)
				{
					for (int o = 0; o < opposites.size(); o++)
					{
						String opposite  = (String)opposites.elementAt(o);
						Vector instances = (Vector)NEG_QUALITIES.get(opposite);
						
						if (instances == null) continue;
						
						for (int i = 0; i < instances.size(); i++)
						{
							String instance = (String)instances.elementAt(i);
							
							if (instance.equals(exemplar)) continue;
							
							int choice = DICE.nextInt(associates.length);
							
							String body     = "but my " + associates[choice] + " " + speechActs[choice] + "\tthat I make even someone as " + opposite;
							String followup = " as " + NOC.hashtagify(instance) + " look like " + NOC.hashtagify(exemplar) + ".";
								
							String tweet    = preamble + pos + ", " + body + followup;
								
							if (tweet.length() <= 140)
								printlnDribbleFile(getDribblePosition() + "\t" + NOC.hashtagify(exemplar) + "\t" + tweet);
						}
					}
				}
			}
		}
		
		closeDribbleFile();
	}
	
	
	
	//-----------------------------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------------------------//
	//  Generate "Clothes Maketh The Man" metaphors
	//  E.g.    #SnakePlissken	
	//    If clothes maketh the man, would wearing #SnakePlissken's leather combat jacket and black eyepatch
	//      make you even stronger or just gruffer?
	//-----------------------------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------------------------//

	public void generateShakespeareanTweets(String tweetDir)
	{
		Vector exemplars = NOC.getAllFrames();
		
		openDribbleFile(tweetDir + "clothes maketh the man tweets.idx");

		for (int e = 0; e < exemplars.size(); e++)
		{
			String exemplar = (String)exemplars.elementAt(e);
			
			Vector clothes  = NOC.getFieldValues("Seen Wearing", exemplar);
			
			if (clothes == null) continue;
			
			Vector ensembles = getClothingEnsembles(exemplar);
			
			if (ensembles == null || ensembles.size() == 0)
				continue;
			
			Vector posQuals = NOC.getFieldValues("Positive Talking Points", exemplar);
			Vector negQuals = NOC.getFieldValues("Negative Talking Points", exemplar);
			
			if (posQuals == null || negQuals == null) continue;
			
			String preamble = "If clothes maketh the man, ";
			
			if (NOC.hasFieldValue("Gender", exemplar, "female"))
				preamble = "If clothes maketh the woman, ";

			String pos = null, neg = null, posMore = null, negMore = null;
			
			for (int p = 0; p < posQuals.size(); p++)
			{
				pos      = (String)posQuals.elementAt(p);
				
				posMore  = getComparativeForm(pos);
				
				if (posMore == null) continue;
				
				Vector opposites = ANTONYMS.getFieldValues("Antonym", pos);
				
				if (opposites != null)
				{
					for (int o = 0; o < opposites.size(); o++)
					{
						String opposite  = (String)opposites.elementAt(o);
						Vector instances = (Vector)NEG_QUALITIES.get(opposite);
						
						if (instances == null) continue;
						
						for (int i = 0; i < instances.size(); i++)
						{
							String instance = (String)instances.elementAt(i);
							
							if (instance.equals(exemplar)) continue;
							
							for (int g = 0; g < ensembles.size(); g++)
							{
								String garmentEnsemble = (String)ensembles.elementAt(g);
								
								String body     = "wouldn't wearing " + NOC.hashtagify(exemplar) + "'s " + garmentEnsemble;
								String followup = "make " + NOC.hashtagify(instance) + " " + posMore + "?";
								
								String tweet    = "If clothes maketh the man, " + body + "\t" + followup;
								
								if (NOC.hasFieldValue("Gender", instance, "female"))
									tweet  = "If clothes maketh the woman, " + body + "\t" + followup;
								
								tweet = replaceWith(tweet, " a ", " ");
								tweet = replaceWith(tweet, " an ", " ");
								
								if (tweet.length() <= 140)
									printlnDribbleFile(getDribblePosition() + "\t" + NOC.hashtagify(exemplar) + "\t" + tweet);
							}
						}
					}
				}
				
				
				
				if (!posMore.startsWith("more ")) posMore = "even " + posMore;
				
				for (int n = 0; n < negQuals.size(); n++)
				{
					neg      = (String)negQuals.elementAt(n);
					
					negMore  = getComparativeForm(neg);
					
					if (negMore == null) continue;
															
					if (!negMore.startsWith("more ")) negMore = "just " + negMore;
					
					for (int g = 0; g < ensembles.size(); g++)
					{
						String garmentEnsemble = (String)ensembles.elementAt(g);
						
						String body     = "would wearing " + NOC.hashtagify(exemplar) + "'s " + garmentEnsemble;
						String followup = "make you " + posMore + "? Or " + negMore + "?";
						
						String tweet    = preamble + body + "\t" + followup;
						
						tweet = replaceWith(tweet, " a ", " ");
						tweet = replaceWith(tweet, " an ", " ");
						
						if (tweet.length() <= 140)
							printlnDribbleFile(getDribblePosition() + "\t" + NOC.hashtagify(exemplar) + "\t" + tweet);
					}
				}				
			}
		}
		
		closeDribbleFile();
	}
	
	
	
	
	
	private String getComparativeForm(String qual)
	{
		String lookup = COMPARATIVES.getFirstValue("Comparative", qual);
		
		if (lookup != null)
			return lookup;
		
		int dash = qual.indexOf((int)'-');
		
		if (dash > 0)
		{
			lookup = COMPARATIVES.getFirstValue("Comparative", qual.substring(dash+1));
			
			if (lookup != null && lookup.startsWith("more "))
				return "more " + qual;
			
			if (lookup != null)
				return qual.substring(0, dash+1) + lookup;
		}
		
		return null;
	}
	
	

	
	//-----------------------------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------------------------//
	//   Generate "Walk a Mile " metaphors
	// E.g. #ChelseaManning	My mom says to never judge an indiscrete prisoner like #ChelseaManning	
	//            until you have walked a mile in her fetching orange overalls.
	// 
	//-----------------------------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------------------------//

	private String[] MENTORS = {"pastor", "father", "mother", "dad", "mom", "grandpa", "grandma", "professor", "preacher", "teacher", "boss"}; 
	
	
	public void walkMileInShoes(String tweetDir)
	{
		Vector negativeQualities  = (Vector)NEG_QUALITIES.get("*keylist*"),  exemplars = null, categories = null;
		
		Vector clothes = null, vehicles = null, affordances = null;
		
		String exemplar = null, quality = null, category = null, possPronoun = "", vehicle = null, garment = null, afford = null;
		
		openDribbleFile(tweetDir + "walk a mile tweets.idx");

		for (int n = 0; n < negativeQualities.size(); n++)
		{
			quality   = (String)negativeQualities.elementAt(n);
			
			exemplars = (Vector)NEG_QUALITIES.get(quality);
			
			if (exemplars == null) continue;
			
			for (int e = 0; e < exemplars.size(); e++)
			{
				exemplar = (String)exemplars.elementAt(e);
				
				categories = NOC.getFieldValues("Category", exemplar);
				
				if (categories == null) continue;
				
				if (NOC.hasFieldValue("Gender", exemplar, "male"))
					possPronoun = "his";
				else
				if (NOC.hasFieldValue("Gender", exemplar, "female"))
					possPronoun = "her";
				else
					possPronoun = "its";
				
				for (int c = 0; c < categories.size(); c++)
				{
					category = (String)categories.elementAt(c);
					
					vehicles = NOC.getFieldValues("Vehicle of Choice", exemplar);
					clothes  = NOC.getFieldValues("Seen Wearing", exemplar);

					if (vehicles != null)
						for (int v = 0; v < vehicles.size(); v++)
						{
							vehicle = (String)vehicles.elementAt(v);
							
							affordances = VEHICLES.getFieldValues("Affordances", vehicle);
							
							if (affordances == null) continue;
							
							for (int a = 0; a < affordances.size(); a++)
							{
								afford = (String)affordances.elementAt(a);
								
								String prep   = "in";
								String action = "walked";
								
								if (afford.startsWith("driving"))
									action = "driven";
								else
								if (afford.startsWith("riding"))
								{action = "ridden"; prep = "on";}
								else
								if (afford.startsWith("cycling"))
								{action = "cycled"; prep = "on";}
								else
								if (afford.startsWith("flying"))
									action = "flown";
								else
								if (afford.startsWith("peddling"))
									action = "peddled";
								else
								if (afford.startsWith("maneuvering"))
									action = "maneuvered";
								else
								if (afford.startsWith("gliding"))
								{action = "glided"; prep = "on";}
								else
								if (afford.startsWith("sailing"))
									action = "sailed";
								else
								if (afford.startsWith("travelling"))
									action = "travelled";
								else
								if (afford.startsWith("skating"))
								{action = "skated"; prep = "on";}
								else
									continue;
								
								if (afford.endsWith(" on"))
									prep = "on";
								
								if (afford.endsWith(" in"))
									prep = "in";
									
								String vtweet = "My " + getRandomValue(MENTORS) + " says to never judge " +
												 getIndefiniteArticleFor(quality) + " " + quality + " " + category.toLowerCase() + 
												 " like " + NOC.hashtagify(exemplar) + 
												 "\tuntil you have " + action + " a mile " + prep + " " + possPronoun + " " + vehicle + "."; 
								
								String preamble = "Nobody's perfect! ";
								
								if (vtweet.length() + preamble.length() <= 140)
									printlnDribbleFile(getDribblePosition() + "\t" + NOC.hashtagify(exemplar) + "\t" + preamble + vtweet);
								else
								if (vtweet.length() <= 140)
									printlnDribbleFile(getDribblePosition() + "\t" + NOC.hashtagify(exemplar) + "\t" + vtweet);
							}
						}
					
					if (clothes != null)
						for (int g = 0; g < clothes.size(); g++)
						{
							garment = (String)clothes.elementAt(g);
							
							if (!CLOTHES.hasFieldValue("Covers", garment, "feet") && !CLOTHES.hasFieldValue("Covers", garment, "legs"))
								continue;
							
							String gtweet = "My " + getRandomValue(MENTORS) + " says to never judge " +
											 getIndefiniteArticleFor(quality) + " " + quality + " " + category.toLowerCase() + 
											 " like " + NOC.hashtagify(exemplar) + 
											 "\tuntil you have walked a mile in " + possPronoun + " " + garment + "."; 
					
							String preamble = "Nobody's perfect! ";
							
							if (gtweet.length() + preamble.length() <= 140)
								printlnDribbleFile(getDribblePosition() + "\t" + NOC.hashtagify(exemplar) + "\t" + preamble + gtweet);
							else
							if (gtweet.length() <= 140)
								printlnDribbleFile(getDribblePosition() + "\t" + NOC.hashtagify(exemplar) + "\t" + gtweet);
							
						}
				}
			}
		}
		
		closeDribbleFile();
	}
	
	
	
	private String getIndefiniteArticleFor(String word)
	{
		if (word.startsWith("uni"))
		{
			if (isAdjective(word) && isAdjective(word.substring(2))) // "un" prefix
				return "an";
			else
				return "a";
		}
		else
		if (word.startsWith("hon"))
			return "an";
		else
		if (word.startsWith("eu"))
			return "a";
		else
		if ("aeiou".indexOf((char)word.charAt(0)) >= 0)
			return "an";
		else			
			return "a";
	}
	
	
	
	private boolean isAdjective(String word)
	{
		return POS_QUALITIES.get(word) != null || NEG_QUALITIES.get(word) != null ||
			   POS_QUALITIES.get("well-" + word) != null || NEG_QUALITIES.get("well-" + word) != null ||
			   POS_QUALITIES.get("ill-" + word) != null || NEG_QUALITIES.get("ill-" + word) != null ;
	}
	
	
	//-----------------------------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------------------------//
	//   Generate XYZ  (X is the Y of Z) constructs
	//      --  explore various combinations of people
	//-----------------------------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------------------------//

	public void generateXYZs(String tweetDir)
	{
		openDribbleFile(tweetDir + "NOC 1 tweets.idx");
		
		generateRealXYs(realPeople, fictionalPeople, 2);
		
		closeDribbleFile();
		
		openDribbleFile(tweetDir + "NOC 2 tweets.idx");

		generateFictionalXYs(realPeople, fictionalPeople, 2);
		
		closeDribbleFile();

		openDribbleFile(tweetDir + "NOC 3 tweets.idx");
		
		generateXYZs(realPeople, fictionalPeople, 2);
		
		closeDribbleFile();
	}
	
	
	
	//-----------------------------------------------------------------------------------------------//
	//   Generate comparisons of real people to their fictional counterparts
	//   Use World, Domain and Opponent fields where applicable
	//-----------------------------------------------------------------------------------------------//

	public void generateFictionalXYs(Vector realXs, Vector fictionalYs, int minSim)
	{
		String personX = null, personY = null;
		
		int count = 0;
		
		for (int x = 0; x < realXs.size(); x++)
		{
			personX = (String)realXs.elementAt(x);
			
			for (int y = 0; y < fictionalYs.size(); y++)
			{
				personY = (String)fictionalYs.elementAt(y);
				
				if (personX.equals(personY)) continue;
				
				Vector commonalities = NOC.getOverlappingFields(personX, personY, attributeFields);
				
				if (commonalities == null || commonalities.size() < minSim)
					continue;
				
				Vector adjectiveMixes   = getAttributeCombinations(commonalities, 
																 NOC.getFieldValues("Positive Talking Points", personX),
																 NOC.getFieldValues("Negative Talking Points", personX));
				
				if (adjectiveMixes == null || adjectiveMixes.size() == 0)
					continue;
				
				
				Vector worlds = NOC.getFieldValues("Fictional World", personY);
				
				if (worlds == null) continue;

				Vector domainsOfX     = NOC.getFieldValues("Domains", personX);
				Vector domainsOfY     = NOC.getFieldValues("Domains", personY);
				Vector domainsXnotY   = NOC.difference(domainsOfX, domainsOfY);
				
				if (domainsXnotY == null || domainsXnotY.size() == 0)
					continue;
				
				Vector opponentsX      = NOC.getFieldValues("Opponent", personX);
				Vector opponentsY      = NOC.getFieldValues("Opponent", personY);
				
				boolean counted = false;
				
				for (int w = 0; w < worlds.size(); w++)
				{
					for (int d = 0; d < domainsXnotY.size(); d++)
					{
						String domain = (String)domainsXnotY.elementAt(d);
						String world  = (String)worlds.elementAt(w);
						
						if (world.equals(domain)) continue;
						
						if (NOC.hasFieldValue("Domains", personY, domain)) continue;
						
						String qualifier = "";
								
						if (world.equals(personY)) qualifier = "the story of ";
						
						if (domain.indexOf((int)' ') < 0) continue;
						if (!Character.isUpperCase(domain.charAt(0))) continue;
																
						
						if (!counted) count++;
						counted = true;
						
						boolean tweeted = false;
						
						for (int a  = 0; a < adjectiveMixes.size(); a++)
						{				
							String longDescription  = (String)adjectiveMixes.elementAt(a);
							String shortDescription = longDescription.substring(0, longDescription.indexOf((int)','));
							
							String tweet1 = "What if " + qualifier + NOC.hashtagify(world) + " were about " + NOC.hashtagify(domain) + "? "  
									+ NOC.hashtagify(personX) + " could be its " + NOC.hashtagify(personY) + ": " + longDescription + ".";
				
	
							if (opponentsX != null && opponentsX.size() > 0 && opponentsY != null && opponentsY.size() > 0)
							{
								for (int ox = 0; ox < opponentsX.size(); ox++)
								{
									String opponentX = (String)opponentsX.elementAt(ox);
									
									for (int oy = 0; oy < opponentsY.size(); oy++)
									{
										String opponentY = (String)opponentsY.elementAt(oy);
										
										String tweet2 = "If " + NOC.hashtagify(personX) + " is like " + NOC.hashtagify(personY) 
														 + " (" + shortDescription + ") in " + qualifier + NOC.hashtagify(world) 
														 + ", is " + NOC.hashtagify(opponentX)  + " like " + NOC.hashtagify(opponentY) + "?"; 
										
										
										if (tweet2.length() <= 140)
										{
											tweeted = true;
											printlnDribbleFile(getDribblePosition() + "\t" + NOC.hashtagify(world) + "\t" + tweet1 + "\t" + tweet2);
										}
										
										String ensemble = getClothingEnsemble(personX), ensembleOpp = getClothingEnsemble(opponentX);
										
										if (ensemble == null || ensembleOpp == null) continue;
										
										String tweet3 = "If " + NOC.hashtagify(personX) + " is " + NOC.hashtagify(personY) + " in " + ensemble
												 		+ ", then " + NOC.hashtagify(opponentX) + " is " + NOC.hashtagify(opponentY) + " in " + ensembleOpp + "."; 
										
										if (tweet3.length() <= 140)
										{
											tweeted = true;							
											printlnDribbleFile(getDribblePosition() + "\t" + NOC.hashtagify(world) + "\t" + tweet1 + "\t" + tweet3);
										}
									}
								}
							}
							
							if (opponentsY != null && opponentsY.size() > 0)
							{
								for (int o = 0; o < opponentsY.size(); o++)
								{
									String opponentY = (String)opponentsY.elementAt(o);
									
									String tweet2 = "If " + NOC.hashtagify(personX) + " is like " + NOC.hashtagify(personY) 
													 + " (" + shortDescription + "), who in " + NOC.hashtagify(domain)
													 + " is most like " + NOC.hashtagify(opponentY) + "?";
									
									
									if (tweet2.length() <= 140)
									{
										tweeted = true;
										printlnDribbleFile(getDribblePosition() + "\t" + NOC.hashtagify(world) + "\t" + tweet1 + "\t" + tweet2);
									}
									
									String ensemble = getClothingEnsemble(personX);
									
									if (ensemble == null) continue;
									
									String tweet3 = "If " + NOC.hashtagify(personX) + " is " + NOC.hashtagify(personY) + " in " + ensemble
											 		+ ", who in " + qualifier + NOC.hashtagify(domain) + " is " + NOC.hashtagify(opponentY) + " most like?";
									
									if (tweet3.length() <= 140)
									{
										tweeted = true;							
										printlnDribbleFile(getDribblePosition() + "\t" + NOC.hashtagify(world) + "\t" + tweet1 + "\t" + tweet3);
									}
								}
							}
								
							if (opponentsX != null && opponentsX.size() > 0)
							{
								for (int o = 0; o < opponentsX.size(); o++)
								{
									String opponentX = (String)opponentsX.elementAt(o);
									
									String tweet2 = "If " + NOC.hashtagify(personX) + " is like " + NOC.hashtagify(personY) 
													 + " (" + shortDescription + "), who in " + qualifier + NOC.hashtagify(world) + " is " + NOC.hashtagify(opponentX) + " most like?";
									
									if (tweet2.length() <= 140)
									{
										tweeted = true;							
										printlnDribbleFile(getDribblePosition() + "\t" + NOC.hashtagify(world) + "\t" + tweet1 + "\t" + tweet2);
									}
									
									String ensemble = getClothingEnsemble(personX);
									
									if (ensemble == null) continue;
									
									String tweet3 = "If " + NOC.hashtagify(personX) + " is " + NOC.hashtagify(personY) + " in " + ensemble
											 		+ ", who in " + qualifier + NOC.hashtagify(world) + " is " + NOC.hashtagify(opponentX) + " most like?";
									
									if (tweet3.length() <= 140)
									{
										tweeted = true;							
										printlnDribbleFile(getDribblePosition() + "\t" + NOC.hashtagify(world) + "\t" + tweet1 + "\t" + tweet3);
									}			
								}
							}
						}
						
						if (!tweeted)
						{
							String tweet = "What if " + qualifier + NOC.hashtagify(world) + " were about " + NOC.hashtagify(domain) + "?\t"  
									+ NOC.hashtagify(personX) + " could be its " + NOC.hashtagify(personY) + ": " + adjectiveMixes.elementAt(0);
					
							if (tweet.length() <= 140)
								printlnDribbleFile(getDribblePosition() + "\t" + NOC.hashtagify(world) + "\t" + tweet);
						}
					}
				}
			}
		}
	}

	
	
	//-----------------------------------------------------------------------------------------------//
	//   Generate a clothing description for a given character
	//-----------------------------------------------------------------------------------------------//

	private String getClothingEnsemble(String person)
	{
		Vector ensembles = getClothingEnsembles(person);
		
		if (ensembles == null || ensembles.size() == 0)
			return null;
		
		if (ensembles.size() == 1)
			return (String)ensembles.elementAt(0);
		
		return (String)ensembles.elementAt(DICE.nextInt(ensembles.size()));
	}
	
	
	private Vector getClothingEnsembles(String person)
	{
		Vector clothes = NOC.getFieldValues("Seen Wearing", person);
		
		if (clothes == null || clothes.size() == 0) 
			return null;
		
		Vector ensembles = new Vector();

		if (clothes.size() == 1)
			ensembles.add(quantifyClothing((String)clothes.elementAt(0)));
		
		
		for (int i = 0; i < clothes.size(); i++)
		{
			String item1 = (String)clothes.elementAt(i);
			
			for (int j = 0; j < clothes.size(); j++)
			{
				if (i == j) continue;

				String item2 = (String)clothes.elementAt(j);
				
				Vector overlap = NOC.intersect(CLOTHES.getFieldValues("Covers", item1), CLOTHES.getFieldValues("Covers", item2));
				
				if (overlap == null || overlap.size() == 0)
					ensembles.add(quantifyClothing(item1) + " and " + quantifyClothing(item2));
			}
		}
		
		return ensembles;
	}
	
	
	private String quantifyClothing(String clothing)
	{
		String det = CLOTHES.getFirstValue("Determiner", clothing);
		
		if (det == null)
			return clothing;
		else
			return det + " " + clothing;
	}
	
	//-----------------------------------------------------------------------------------------------//
	//   Generate comparisons of fictional characters to their real-world counterparts
	//-----------------------------------------------------------------------------------------------//

	public void generateRealXYs(Vector fictionalXs, Vector realYs, int minSim)
	{
		String personX = null, personY = null;
		
		int count = 0;
		
		for (int x = 0; x < fictionalXs.size(); x++)
		{
			personX = (String)fictionalXs.elementAt(x);
			
			for (int y = 0; y < realYs.size(); y++)
			{
				personY = (String)realYs.elementAt(y);
				
				if (personX.equals(personY)) continue;
				
				Vector commonalities = NOC.getOverlappingFields(personX, personY, attributeFields);
				
				if (commonalities == null || commonalities.size() < minSim)
					continue;
				
				Vector descriptions   = getAttributeCombinations(commonalities, 
																 NOC.getFieldValues("Positive Talking Points", personX),
																 NOC.getFieldValues("Negative Talking Points", personX));
				
				if (descriptions == null || descriptions.size() == 0)
					continue;
				
				count++;
				
				for (int d  = 0; d < descriptions.size(); d++)
				{			
					String tweet = NOC.hashtagify(personX) + " is the real world's " + NOC.hashtagify(personY) + ":\t" + descriptions.elementAt(d);
						
					if (tweet.length() <= 140)
						printlnDribbleFile(getDribblePosition() + "\t" + NOC.hashtagify(personX) + "\t" + tweet);	
					
					Vector worlds = NOC.getFieldValues("Fictional World", personY);
					
					if (worlds == null) continue;
					
					for (int w = 0; w < worlds.size(); w++)
					{
						String world = (String)worlds.elementAt(w);
						
						tweet = "What if " + NOC.hashtagify(world) + " were real?\t" + NOC.hashtagify(personX) + " could be its " + NOC.hashtagify(personY) + ": " 
										+ descriptions.elementAt(d);
						
						if (tweet.length() <= 140)
							printlnDribbleFile(getDribblePosition() + "\t" + NOC.hashtagify(world) + "\t" + tweet);	
					}
				}
			}
		}
	}

	
	
	//-----------------------------------------------------------------------------------------------//
	//   Generate X is the Y of Z constructions
	//-----------------------------------------------------------------------------------------------//

	public void generateXYZs(Vector setX, Vector setY, int minSim)
	{
		String personX = null, personY = null;
		
		int count = 0;
		
		for (int x = 0; x < setX.size(); x++)
		{
			personX = (String)setX.elementAt(x);
			
			for (int y = 0; y < setY.size(); y++)
			{
				personY = (String)setY.elementAt(y);
				
				if (personX.equals(personY)) continue;
				
				Vector commonalities = NOC.getOverlappingFields(personX, personY, attributeFields);
				
				if (commonalities == null || commonalities.size() < minSim)
					continue;
				
				Vector domainsOfX     = NOC.getFieldValues("Domains", personX);
				Vector domainsOfY     = NOC.getFieldValues("Domains", personY);
				Vector domainsXnotY   = NOC.difference(domainsOfX, domainsOfY);
				
				if (domainsXnotY == null || domainsXnotY.size() == 0)
					continue;
				
				Vector adjectiveMixes   = getAttributeCombinations(commonalities, 
																 NOC.getFieldValues("Positive Talking Points", personX),
																 NOC.getFieldValues("Negative Talking Points", personX));
				
				if (adjectiveMixes == null || adjectiveMixes.size() == 0)
					continue;
				
				count++;
				
				String pronoun  = "He";
				
				if (NOC.hasFieldValue("Gender", personX, "female"))
					pronoun = "She";
				
				for (int d = 0; d < domainsXnotY.size(); d++)
				{
					String domain = (String)domainsXnotY.elementAt(d);
					
					if (domain.startsWith(personX)) continue;
					
					if (domain.indexOf((int)' ') < 0) continue;
					
					for (int a = 0; a < adjectiveMixes.size(); a++)
					{
						String longDescription  = (String)adjectiveMixes.elementAt(a);
						String shortDescription = longDescription.substring(0, longDescription.indexOf((int)','));
						
						String tweet1a = NOC.hashtagify(personX) + " is the " + NOC.hashtagify(personY) + " of " + NOC.hashtagify(domain) + ":";
						String tweet1b = longDescription + ".";
						
						if (tweet1a.length() + tweet1b.length() >= 140) continue;
						
						boolean tweeted = false;
						
						Vector opponentsOfX = NOC.getFieldValues("Opponent", personX);
						
						if (opponentsOfX != null && opponentsOfX.size() > 0)
						{
							String counterpoint = NOC.getFirstValue("Fictional World", personY);
							
							if (counterpoint == null)
								counterpoint = NOC.getFirstValue("Domains", personY);
							
							for (int o = 0; o < opponentsOfX.size(); o++)
							{
								if (counterpoint == null) continue;
								
								String opponentOfX = (String)opponentsOfX.elementAt(o);
								
								String tweet2 = "If " + NOC.hashtagify(personX) + " is just like " + NOC.hashtagify(personY)
												+ ", " + shortDescription + ", then who in " + NOC.hashtagify(counterpoint) 
												+ " is " + NOC.hashtagify(opponentOfX) + " most like?";
								
								if (tweet2.length() <= 140)
								{
									printlnDribbleFile(getDribblePosition() + "\t" + NOC.hashtagify(domain) + "\t" 
														+ tweet1a + " " + tweet1b + "\t" + tweet2);
									tweeted = true;
								}
							}
						}
						
						Vector opponentsOfY = NOC.getFieldValues("Opponent", personY);
						
						if (opponentsOfY != null && opponentsOfY.size() > 0)
						{
							for (int o = 0; o < opponentsOfY.size(); o++)
							{
								String opponentOfY = (String)opponentsOfY.elementAt(o);
								
								String tweet2 = "If " + NOC.hashtagify(personX) + " is just like " + NOC.hashtagify(personY)
												+ ", " + shortDescription + ", then who in " + NOC.hashtagify(domain) 
												+ " is " + NOC.hashtagify(opponentOfY) + "?";
								
								if (tweet2.length() <= 140)
								{
									printlnDribbleFile(getDribblePosition() + "\t" + NOC.hashtagify(domain) + "\t" 
														+ tweet1a + " " + tweet1b + "\t" + tweet2);
									tweeted = true;
								}
							}
						}
						
						if (!tweeted)
							printlnDribbleFile(getDribblePosition() + "\t" + NOC.hashtagify(domain) + "\t" +  tweet1a + "\t" + tweet1b);
					}
					
					// Focus on various fields of the NOC list representation
					
					Vector focus = null;
					
					String fore =  null, aft = null;
					
					for (int pass = 0; pass < 2; pass++)
					{
						if (pass == 0)
						{
							focus = getClothingEnsembles(personX);
							fore  = " just ";
							aft   = " in ";
						}
						
						if (pass == 1)
						{
							focus = NOC.getFieldValues("Typical Activity", personX);
							fore  = " the ";
							aft   = " of ";
						}
						
						if (focus == null) continue;
						
						for (int a = 0; a < adjectiveMixes.size(); a++)
						{	
							String longDescription  = (String)adjectiveMixes.elementAt(a);
							String shortDescription = longDescription.substring(0, longDescription.indexOf((int)','));
													
							for (int f = 0; f < focus.size(); f++)
							{
								String tweet1a = "When it comes to " + NOC.hashtagify(domain) + ", is " + NOC.hashtagify(personX) + fore
												    + NOC.hashtagify(personY) + aft + focus.elementAt(f) + "?";
								
								String tweet1b = pronoun + " is " + adjectiveMixes.elementAt(a);
								
								if (tweet1a.length() + tweet1b.length() >= 140) continue;
								
								boolean tweeted = false;
								
								Vector opponentsOfX = NOC.getFieldValues("Opponent", personX);
								
								if (opponentsOfX != null && opponentsOfX.size() > 0)
								{
									String counterpoint = NOC.getFirstValue("Fictional World", personY);
									
									if (counterpoint == null)
										counterpoint = NOC.getFirstValue("Domains", personY);
									
									for (int o = 0; o < opponentsOfX.size(); o++)
									{
										if (counterpoint == null) continue;
										
										String opponentOfX = (String)opponentsOfX.elementAt(o);
										
										String tweet2 = "If " + NOC.hashtagify(personX) + " is just like " + NOC.hashtagify(personY) 
														+ ", " + shortDescription 
														+ ", then who in " + NOC.hashtagify(counterpoint) + " is " 
														+ NOC.hashtagify(opponentOfX) + " most like?";
										
										if (tweet2.length() <= 140)
										{
											printlnDribbleFile(getDribblePosition() + "\t" + NOC.hashtagify(domain) + "\t" 
																+ tweet1a + " " + tweet1b + "\t" + tweet2);
											tweeted = true;
										}
									}
								}
								
								Vector opponentsOfY = NOC.getFieldValues("Opponent", personY);
								
								if (opponentsOfY != null && opponentsOfY.size() > 0)
								{
									for (int o = 0; o < opponentsOfY.size(); o++)
									{
										String opponentOfY = (String)opponentsOfY.elementAt(o);
										
										String tweet2 = "If " + NOC.hashtagify(personX) + " is just like " + NOC.hashtagify(personY) 
														+ ", " + shortDescription 
														+ ", then who in " + NOC.hashtagify(domain) + " is " + NOC.hashtagify(opponentOfY) + "?";
										
										if (tweet2.length() <= 140)
										{
											printlnDribbleFile(getDribblePosition() + "\t" + NOC.hashtagify(domain) + "\t" 
																+ tweet1a + " " + tweet1b + "\t" + tweet2);
											tweeted = true;
										}
									}
								}
												
								if (!tweeted)
									printlnDribbleFile(getDribblePosition() + "\t" + NOC.hashtagify(domain) + "\t" +  tweet1a + "\t" + tweet1b);
							}
						}
					}
				}
			

			}
		}
	}
	
	
	//-----------------------------------------------------------------------------------------------//
	//   Find 3 attributes to jointly describe a pair of similar concepts, and express in NL
	//-----------------------------------------------------------------------------------------------//

	private Vector getAttributeCombinations(Vector sharedAttributes, Vector srcPositives, Vector srcNegatives)
	{
		Vector combos = new Vector();
		
		if (sharedAttributes == null || sharedAttributes.size() < 2) 
			return combos;
		
		String attr1 = null, attr2 = null, attr3 = null;
		
		// Three attribute combinations with one but
		
		for (int a1 = 0; a1 < sharedAttributes.size(); a1++)
		{
			attr1 = (String)sharedAttributes.elementAt(a1);
			
			for (int a2 = a1+1; a2 < sharedAttributes.size(); a2++)
			{
				attr2 = (String)sharedAttributes.elementAt(a2);
				
				for (int a3 = a2+1; a3 < sharedAttributes.size(); a3++)
				{
					attr3 = (String)sharedAttributes.elementAt(a3);
									
					if (attr1.startsWith("Pos") && attr2.startsWith("Pos") && attr3.startsWith("Neg"))
						combos.add(getAttrValue(attr1) + " and " + getAttrValue(attr2) + ", but " + getAttrValue(attr3));
					
					if (attr1.startsWith("Pos") && attr2.startsWith("Neg") && attr3.startsWith("Pos"))
						combos.add(getAttrValue(attr1) + " and " + getAttrValue(attr3) + ", but " + getAttrValue(attr2));
					
					if (attr1.startsWith("Neg") && attr2.startsWith("Pos") && attr3.startsWith("Pos"))
						combos.add(getAttrValue(attr2) + " and " + getAttrValue(attr3) + ", but " + getAttrValue(attr1));
					
					if (attr1.startsWith("Pos") && attr2.startsWith("Neg") && attr3.startsWith("Neg"))
						combos.add(getAttrValue(attr2) + " and " + getAttrValue(attr3) + ", yet " + getAttrValue(attr1));
					
					if (attr1.startsWith("Neg") && attr2.startsWith("Pos") && attr3.startsWith("Neg"))
						combos.add(getAttrValue(attr1) + " and " + getAttrValue(attr3) + ", yet " + getAttrValue(attr2));
					
					if (attr1.startsWith("Neg") && attr2.startsWith("Neg") && attr3.startsWith("Pos"))
						combos.add(getAttrValue(attr1) + " and " + getAttrValue(attr2) + ", yet " + getAttrValue(attr3));					
				}
			}
		}

					
		// Two attribute combinations
		
		for (int a1 = 0; a1 < sharedAttributes.size(); a1++)
		{
			attr1 = (String)sharedAttributes.elementAt(a1);
			
			for (int a2 = a1+1; a2 < sharedAttributes.size(); a2++)
			{
				attr2 = (String)sharedAttributes.elementAt(a2);
				
				if (attr1.startsWith("Pos") && attr2.startsWith("Neg"))
				{
					String otherPos = getRandomValueIgnoring(srcPositives, getAttrValue(attr1));
					String otherNeg = getRandomValueIgnoring(srcNegatives, getAttrValue(attr2));
					
					if (otherPos != null)
						combos.add(getAttrValue(attr2) + " but " + getAttrValue(attr1)  + getPossibility(otherPos, "and"));
					
					if (otherNeg != null)
						combos.add(getAttrValue(attr1) + " yet " + getAttrValue(attr2)  + getPossibility(otherNeg, "and"));
				}
				
				if (attr1.startsWith("Neg") && attr2.startsWith("Pos"))
				{
					String otherPos = getRandomValueIgnoring(srcPositives, getAttrValue(attr2));
					String otherNeg = getRandomValueIgnoring(srcNegatives, getAttrValue(attr1));
					
					if (otherPos != null)
						combos.add(getAttrValue(attr1) + " but " + getAttrValue(attr2)  + getPossibility(otherPos, "and"));
					
					if (otherNeg != null)
						combos.add(getAttrValue(attr2) + " yet " + getAttrValue(attr1)  + getPossibility(otherNeg, "and"));
				}
				
				if (attr1.startsWith("Neg") && attr2.startsWith("Neg"))
				{
					String otherPos = getRandomValueIgnoring(srcPositives, getAttrValue(attr2));
					
					if (otherPos != null)
						combos.add(getAttrValue(attr1) + " and " + getAttrValue(attr2)  + getPossibility(otherPos, "yet"));
				}
				
				if (attr1.startsWith("Pos") && attr2.startsWith("Pos"))
				{
					String otherNeg = getRandomValueIgnoring(srcNegatives, getAttrValue(attr2));
					
					if (otherNeg != null)
						combos.add(getAttrValue(attr1) + " and " + getAttrValue(attr2)  + getPossibility(otherNeg, "yet"));
				}
			}
		}
		
		return combos;
	}
	
	

	
	
	private String getPossibility(String attr, String link)
	{
		return ", " + link + " " + attr + " too";
	}
	
	
	private String getAttrValue(String attr)
	{
		int eq = attr.indexOf((int)'=');
		
		return attr.substring(attr.indexOf((int)'=')+1);
	}
	
	
	private String getRandomValue(String[] values)
	{
		if (values == null || values.length == 0)
			return null;
		
		if (values.length == 1)
			return values[0];
		
		return values[DICE.nextInt(values.length)];
	}
	
	
	private String getRandomValue(Vector values)
	{
		if (values == null || values.size() == 0)
			return null;
		
		if (values.size() == 1)
			return (String)values.elementAt(0);
		
		return (String)values.elementAt(DICE.nextInt(values.size()));
	}

	
	private String getRandomValueIgnoring(Vector values, String ignore)
	{
		if (values == null || values.size() == 0)
			return null;
		
		String choice = null;
		
		if (values.size() == 1)
		{
			choice = (String)values.elementAt(0);
			
			if (choice.equals(ignore))
				return null;
			else
				return choice;
		}
		
		choice = (String)values.elementAt(DICE.nextInt(values.size()));
		
		while (choice.equals(ignore))
			choice = (String)values.elementAt(DICE.nextInt(values.size()));
		
		return choice;
	}
	
	

	
	
	//-----------------------------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------------------------//
	//   Application Stub
	//-----------------------------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------------------------//
	
	public static void main(String[] args)
	{
		String kdir = "/Users/tonyveale/Dropbox/CodeCamp2015/DATA/TSV Lists/";
		String tdir = "/Users/tonyveale/Desktop/Lexical Resources/Moods/";	
		
		PersonOfInterest stereonomicon = new PersonOfInterest(kdir);
		
		/*
		 
		RelationshipCategorizer relCats = new RelationshipCategorizer(stereonomicon.NOC);
		Tabular.SymbolMap catMap = relCats.getTexturedCategoryMap();
		
		catMap.saveMapping(tdir + "NOC T-Rex membership.idx");
		
		
		Tabular.SymbolMap relations	= new Tabular.SymbolMap("list of rival categories");

		stereonomicon.instantiateRelationshipsWithNOC(kdir);
		
		System.out.println("-------------------------------------");
		
		Tabular.SymbolMap enemies 	= stereonomicon.getEnemyMap(kdir, relations);
		
		Tabular.SymbolMap spouses 	= stereonomicon.getSpouseRelations(enemies);
		
		spouses.saveMapping(kdir + "relationship instances.txt");
		*/
		
	
//		stereonomicon.generateDreamConflicts(tdir);

//		stereonomicon.generateHulkSmashTweets(tdir);
		
//		stereonomicon.generateEpitaphs(tdir);
		
//		stereonomicon.generateGoodBadBlends(tdir);
		
//		stereonomicon.makeOthersLookGood(tdir);
		
//		stereonomicon.generateNietzscheanTweets(tdir);
		
//		stereonomicon.generateShakespeareanTweets(tdir);
		
//		stereonomicon.walkMileInShoes(tdir);
		
//		stereonomicon.generateXYZs(tdir);
		
	}
		

		
}
