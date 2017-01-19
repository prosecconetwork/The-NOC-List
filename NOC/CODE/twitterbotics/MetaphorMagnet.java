package twitterbotics;


public class MetaphorMagnet 
{
    private static String CONSUMER_KEY = "rJ179bgec98exA9DkSWk42Wpv";
    private static String ACCESS_TOKEN = "2428936177-QNvevcemjILj4ReDSN1kmUfBGcaYYDUroCMfNfp";
 
	
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//
	//     Main Application Stub
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//
	
	// In Documents/Workspace/WordNetBrowser
	
	// Command Line:   /usr/bin/java -classpath ../:./:../twitter4j-4.0.1/lib/twitter4j-core-4.0.1.jar:../twitter4j-4.0.1/lib/twitter4j-stream-4.0.1.jar twitterbotics.TweetGenerator 
	
	
	public static void main(String[] args)
	{
		String dir      = "/Users/tonyveale/Desktop/Lexical Resources/Moods/";
		
		TweetLaunchpadWithEmoji generator = new TweetLaunchpadWithEmoji(dir, CONSUMER_KEY, 
																			 args[0], // CONSUMER_SECRET, 
																			 ACCESS_TOKEN, 
																			 args[1]); // ACCESS_SECRET;
		
		generator.loadSpace(dir + "Venn diagram 1 tweets.idx", "Venn 1");
		generator.loadSpace(dir + "Venn diagram 2 tweets.idx", "Venn 2");
		generator.loadSpace(dir + "Venn diagram 3 tweets.idx", "Venn 3");
		generator.loadSpace(dir + "Venn diagram 4 tweets.idx", "Venn 4");
		generator.loadSpace(dir + "Venn diagram 5 tweets.idx", "Venn 5");
		generator.loadSpace(dir + "narrative 5-step joint tweets.idx", "Venn joint");
		generator.loadSpace(dir + "Jesus transformation tweets.idx", "Jesus transformation");
		generator.loadSpace(dir + "Jesus contrast tweets.idx", "Jesus contrast");
		generator.loadSpace(dir + "NOC epitaph tweets.idx", "epitaphs");
		generator.loadSpace(dir + "group meme tweets.idx", "group meme");
		generator.loadSpace(dir + "group walk meme tweets.idx", "walk meme");
		generator.loadSpace(dir + "narrative group tweets.idx", "group narrative");
		generator.loadSpace(dir + "narrative 5-step nesting tweets.idx", "nesting");
		generator.loadSpace(dir + "narrative animal tweets.idx", "animal");
		generator.loadSpace(dir + "narrative remarkable animal tweets.idx", "remarkable animal");
		generator.loadSpace(dir + "narrative negative animal tweets.idx", "negative animal");
		generator.loadSpace(dir + "narrative 5-step divorce tweets.idx", "divorce");
		generator.loadSpace(dir + "Google question tweets.idx", "Google");
		generator.loadSpace(dir + "about simile tweets.idx", "Web simile");
		generator.loadSpace(dir + "narrative 5-step workplace tweets.idx", "workplace");
		generator.loadSpace(dir + "narrative 4-step label tweets.idx", "narrative 4-steps label");
		generator.loadSpace(dir + "narrative 5-step marriage tweets.idx", "narrative 5-steps marriage");
		generator.loadSpace(dir + "narrative 5-step left tweets.idx", "narrative 5-steps left");
		generator.loadSpace(dir + "narrative 5-step right tweets.idx", "narrative 5-steps right");
		generator.loadSpace(dir + "narrative 5-step tweets.idx", "narrative 5-steps");
		generator.loadSpace(dir + "narrative 4-step tweets.idx", "narrative 4-steps");
		generator.loadSpace(dir + "dream conflict tweets.idx", "dream conflict");
		generator.loadSpace(dir + "NOC blend tweets.idx", "NOC blends");
		generator.loadSpace(dir + "Nietzschean tweets.idx", "Nietzsche");
		generator.loadSpace(dir + "clothes maketh the man tweets.idx", "Shakespeare");
		generator.loadSpace(dir + "walk a mile tweets.idx", "walk a mile");		
		generator.loadSpace(dir + "relative perspective tweets.idx", "relative perspective");		
		generator.loadSpace(dir + "verb change tweets.idx", "change");
		generator.loadSpace(dir + "analogical alignment tweets.idx", "alignment");
		generator.loadSpace(dir + "superlative tweets.idx", "superlative");		
		generator.loadSpace(dir + "what-if dream tweets.idx", "whatif_dream");		
		generator.loadSpace(dir + "tweets from 4-grams with dreamers.idx", "dream 4-gram");
		generator.loadSpace(dir + "tweets from 3-grams with dreamers.idx", "dream 3-gram");		
		generator.loadSpace(dir + "dream contrast tweets.idx", "dream contrast");
		generator.loadSpace(dir + "dream norm tweets.idx", "dream norm");
		generator.loadSpace(dir + "entry parallel tweets.idx", "dream parallel");
		generator.loadSpace(dir + "NOC 3 tweets.idx", "NOC3");		
		generator.loadSpace(dir + "take 5 named tweets.idx", "Take5NOC");	
		generator.loadSpace(dir + "NOC 1 tweets.idx", "NOC1");	
		generator.loadSpace(dir + "NOC 2 tweets.idx", "NOC2");	
		generator.loadSpace(dir + "necessary change tweets.idx", "necessities");	
		generator.loadSpace(dir + "utopian and dystopian tweets.idx", "utopias");		
		generator.loadSpace(dir + "what-if tweets.idx", "whatif");		
		generator.loadSpace(dir + "not exactly tweets.idx", "not exactly");
		generator.loadSpace(dir + "take 5 metaphor tweets.idx", "take 5 metaphors");
		generator.loadSpace(dir + "ironic thematic contrast tweets.idx", "ironic themes");
		generator.loadSpace(dir + "state combination tweets.idx", "state combos");
		generator.loadSpace(dir + "possession contrast tweets.idx", "possessions");
		generator.loadSpace(dir + "property contrast tweets.idx", "verb+property contrasts");
		generator.loadSpace(dir + "entry and exit tweets.idx", "entry and exit");
		generator.loadSpace(dir + "tweets from 4-grams.idx", "4-grams");
		generator.loadSpace(dir + "tweets from 3-grams.idx", "3-grams");
		generator.loadSpace(dir + "tweets from 4-grams with actors.idx", "4-grams actors");
		generator.loadSpace(dir + "tweets from 3-grams with actors.idx", "3-grams actors");
		generator.loadSpace(dir + "simile tweets.idx", "similes");
		generator.loadSpace(dir + "theme identity tweets.idx", "thematic identities");
		generator.loadSpace(dir + "theme identity 3 tweets.idx", "thematic identities 3");
		generator.loadSpace(dir + "theme identity 4 tweets.idx", "thematic identities 4");
		generator.loadSpace(dir + "theme identity 3 tweets with actors.idx", "thematic identities 3 actors");
		generator.loadSpace(dir + "theme identity 4 tweets with actors.idx", "thematic identities 4 actors");
		generator.loadSpace(dir + "criss-cross tweets.idx", "criss-cross");
		generator.loadSpace(dir + "affective comparison tweets.idx", "affective comparison");
		generator.loadSpace(dir + "affective crisscross tweets.idx", "affective crisscross");
		generator.loadSpace(dir + "group membership tweets.idx", "group membership");
		generator.loadSpace(dir + "group antagonism tweets.idx", "collective noun");
		generator.loadSpace(dir + "tenor possibility tweets.idx", "tenor possibilities");
		generator.loadSpace(dir + "norm reasoning tweets.idx", "norm reasoning");
		generator.loadSpace(dir + "norm reasoning tweets choices.idx", "norm choices");
		generator.loadSpace(dir + "blended metaphor tweets.idx", "norm blends");
		generator.loadSpace(dir + "ironic contrast tweets.idx", "ironic contrasts");
		generator.loadSpace(dir + "ironic analogy tweets.idx", "ironic analogies");
		generator.loadSpace(dir + "ironic aboutness tweets.idx", "ironic about");
		generator.loadSpace(dir + "verb contrast tweets.idx", "verb contrasts");
		generator.loadSpace(dir + "take 5 tweets.idx", "take 5");
		generator.loadSpace(dir + "take 5 comparison tweets.idx", "take 5 comparisons");
		generator.loadSpace(dir + "Boromir walking tweets.idx", "Boromir");
		generator.loadSpace(dir + "Hulk smash tweets.idx", "Hulk");
		generator.loadSpace(dir + "NOC Hulk tweets.idx", "Hulk NOC");
		generator.loadSpace(dir + "Yoda transformation tweets.idx", "Yoda");
		generator.loadSpace(dir + "narrative animal scripted tweets.idx", "animal scripts");
		generator.loadSpace(dir + "narrative animal pairs tweets.idx", "animal sequences");
		generator.loadSpace(dir + "every business tweet.idx", "business");
		generator.loadSpace(dir + "narrative animal emoji tweets.idx", "animal emoji");
		generator.loadSpace(dir + "narrative recursive NOC tweets.idx", "animal NOC emoji");  // "recursive" was "animal"
		generator.loadSpace(dir + "narrative 5-step full tweets.idx", "Venn full");
		generator.loadSpace(dir + "narrative 4-step others tweets.idx", "Venn 4-others");
		generator.loadSpace(dir + "RGB 2 color tweets.idx", "RGB 2-color");
		generator.loadSpace(dir + "RGB NOC color tweets.idx", "RGB NOC");
		generator.loadSpace(dir + "RGB stereo color tweets.idx", "RGB stereo");
		generator.loadSpace(dir + "RGB visual metaphor tweets.idx", "RGB vismet");

		String[] tweetTypes = {"RGB stereo","RGB vismet", "Venn 4-others", "narrative 4-steps label", "narrative 5-steps left", 
							   "Venn 1", "Venn 2", "Venn 3", "Venn 4", "Venn 5", "narrative 5-steps", "Venn joint", "Venn full",   
							   "narrative 4-steps", "Take5NOC|take 5 comparisons|take 5", "animal NOC emoji", "RGB NOC", "workplace", 
							   "nesting", "business", "animal scripts", "Jesus transformation|Jesus contrast", "Yoda", //  "RGB 2-color",
							   "epitaphs|Nietzsche|Shakespeare|walk a mile|relative perspective",  "RGB stereo", "ironic contrasts", 
							   "Boromir", "animal", "remarkable animal", "negative animal", "walk meme|group meme", "divorce",  
							   "narrative 5-steps marriage", "narrative 5-steps right",   "Hulk NOC|Hulk", 
							   "dream conflict", "NOC blends", "change", "whatif_dream", "NOC3", "dream 4-gram|dream 3-gram",  
							   "thematic identities 3 actors", "thematic identities 4 actors", "RGB 2-color", "RGB NOC",
							   "alignment", "superlative", "Web simile", "Google", "thematic identities",
							   "group narrative", "similes", "possessions", "4-grams actors|4-grams actors|4-grams actors|3-grams actors",  "NOC1", 
							   "verb+property contrasts","4-grams|4-grams|4-grams|3-grams",  "collective noun",  "dream norm",
							   "verb contrasts", "norm blends", "affective comparison", "group membership", "state combos",  	"dream contrast", 					  
							   "whatif", "utopias", "not exactly",  "ironic themes", "NOC2", "norm choices", "tenor possibilities", "ironic analogies",
							   "norm reasoning", "affective crisscross", "ironic about", "criss-cross", "necessities",
							   "thematic identities 3", "thematic identities 4",  "dream parallel"};						   
				
		System.out.println("\n\nTweet Generator version: " + TweetLaunchpad.VERSION + "\n");
		
		generator.analyzeTimeline("MetaphorMagnet", 15);
		
		generator.startTweeting(1000000, 60*60*1000, tweetTypes);
		
	    		
		/*
		for (int t = 0; t < 1; t++) //tweetTypes.length
		{
			for (int i = 0 ; i < 100; i++)
			{		
				String next =  generator.getLeastRecentRandomCandidate(tweetTypes[t], 100);
				
				if (next == null) continue;
								
				StringTokenizer tweetset = new StringTokenizer(next, "\t");
				
				Vector orderedTweets = new Vector();
				
				while (tweetset.hasMoreTokens()) orderedTweets.add(tweetset.nextToken());
				
				System.out.println("\nStory " + (i+1) + ":");
				
				for (int tw = 0; tw < orderedTweets.size(); tw++)
				{
					String tweet = (String)orderedTweets.elementAt(tw);
					
					if (generator.hasNumberingSystem(tweet, 1, 3))
						tweet = generator.insertNumberEmoji(tweet, TweetEmojifier.TIMES, 1, 5);
					
					System.out.println("\n" + tweet);				
				}
				
				generator.advanceCounter();
				
				generator.updateCounts(next);		
			}	
		}
 		*/
		
		/*
		for (int i = 0 ; i < 2; i++)
		{
			for (int j = 0 ; j < tweetTypes.length; j++)
			{
				String next = generator.getLeastRecentRandomCandidate(tweetTypes[j], 100), setup = null, followup = null;
							
				int divider     = next.indexOf((int)'\t');
				
				if (divider > 0)
				{
					setup    = next.substring(0, divider);
					followup = next.substring(setup.length()+1);
				}
				else
				{
					setup    = next;
					followup = next;
				}
				
				System.out.println("\nTweet " + i + ":\n" + setup + " (" + setup.length() + ")");
				
				if (!followup.equals(setup))
					System.out.println(followup + " (" + followup.length() + ")");
				
				generator.advanceCounter();
				
				generator.updateCounts(next);
			}
		}	
		*/
	}

}
