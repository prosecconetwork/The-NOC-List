package twitterbotics;


public class BestOfBotWorlds 
{
	private static String CONSUMER_KEY    = "EOnEROGRPUfRIAhCbAoIyMiCe";
	private static String ACCESS_TOKEN 	  = "3434394268-uY9T1FaztoPfThqv09vzExrVAYJXsCelQpeYYsX";
	
	
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//
	//     Main Application Stub
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//
	
	// In Documents/Workspace/WordNetBrowser
	
	
	public static void main(String[] args)
	{
		String dir      = "/Users/tonyveale/Desktop/Lexical Resources/Moods/";
		
		TweetLaunchpadWithEmoji generator = new TweetLaunchpadWithEmoji(dir, CONSUMER_KEY, 
																			 args[0],  // CONSUMER_SECRET, 
																			 ACCESS_TOKEN, 
																			 args[1]); // ACCESS_SECRET;
		
		generator.loadSpace(dir + "Venn diagram 1 tweets.idx", "Venn 1");
		generator.loadSpace(dir + "Venn diagram 2 tweets.idx", "Venn 2");
		generator.loadSpace(dir + "Venn diagram 3 tweets.idx", "Venn 3");
		generator.loadSpace(dir + "Venn diagram 4 tweets.idx", "Venn 4");
		generator.loadSpace(dir + "Venn diagram 5 tweets.idx", "Venn 5");
		generator.loadSpace(dir + "narrative 5-step joint tweets.idx", "Venn joint");
		generator.loadSpace(dir + "Yoda transformation tweets.idx", "Yoda transformation");
		generator.loadSpace(dir + "Jesus transformation tweets.idx", "Jesus transformation");
		generator.loadSpace(dir + "Buddha transformation tweets.idx", "Buddha transformation");
		generator.loadSpace(dir + "Lamarck transformation tweets.idx", "Lamarck transformation");
		generator.loadSpace(dir + "Jesus contrast tweets.idx", "Jesus contrast");
		generator.loadSpace(dir + "Boromir walking tweets.idx", "Boromir");
		generator.loadSpace(dir + "every Islam tweet.idx", "Islam");
		generator.loadSpace(dir + "NOC epitaph tweets.idx", "epitaphs");
		generator.loadSpace(dir + "Jesus 3-gram tweets.idx", "3-grams");
		generator.loadSpace(dir + "Jesus 4-gram tweets.idx", "4-grams");
		generator.loadSpace(dir + "every religion tweet.idx", "generic");
		generator.loadSpace(dir + "every business tweet.idx", "business");
		generator.loadSpace(dir + "narrative karmic animal tweets.idx", "karma");
		generator.loadSpace(dir + "Hulk smash tweets.idx", "Hulk");
		generator.loadSpace(dir + "NOC Hulk tweets.idx", "Hulk NOC");
		generator.loadSpace(dir + "Successful habits tweets.idx", "habits");
		generator.loadSpace(dir + "Commandment tweets.idx", "Moses");
		generator.loadSpace(dir + "narrative animal scripted tweets.idx", "animal scripts");
		generator.loadSpace(dir + "narrative animal pairs tweets.idx", "animal sequences");
		generator.loadSpace(dir + "narrative animal emoji tweets.idx", "animal emoji");
		generator.loadSpace(dir + "narrative recursive NOC tweets.idx", "animal NOC emoji"); // "recursive" was "animal"
		generator.loadSpace(dir + "theme identity tweets.idx", "thematic identities");
		generator.loadSpace(dir + "theme identity 3 tweets.idx", "thematic identities 3");
		generator.loadSpace(dir + "theme identity 4 tweets.idx", "thematic identities 4");
		generator.loadSpace(dir + "theme identity 3 tweets with actors.idx", "thematic identities 3 actors");
		generator.loadSpace(dir + "theme identity 4 tweets with actors.idx", "thematic identities 4 actors");
		generator.loadSpace(dir + "criss-cross tweets.idx", "criss-cross");		
		generator.loadSpace(dir + "narrative 5-step full tweets.idx", "Venn full");
		generator.loadSpace(dir + "narrative 4-step others tweets.idx", "Venn 4-others");
		generator.loadSpace(dir + "RGB NOC color tweets.idx", "RGB NOC");
		generator.loadSpace(dir + "RGB stereo color tweets.idx", "RGB stereo");
		
		String[] tweetTypes = {"RGB NOC", "RGB stereo", "Venn 4-others", "Venn full", "Venn joint", "Venn 1", "Venn 2", "Venn 3", "Venn 4", "Venn 5",
							   "thematic identities", "thematic identities 3 actors", "thematic identities 4 actors", "thematic identities 3", 
							   "thematic identities 4", "animal NOC emoji", "animal emoji", "business", "animal scripts", "Moses", "habits", "Hulk NOC|Hulk",  
							   "Boromir", "Lamarck transformation",  "epitaphs",  //"generic", "animal sequences", 
							   "business", "Buddha transformation", "Islam", "karma", "Jesus contrast", "3-grams", "4-grams",
							   "Yoda transformation", "Jesus transformation"};
		
		System.out.println("\n\nTweet Generator version: " + TweetLaunchpad.VERSION + "\n");
		
		
		generator.analyzeTimeline("BestOfBotWorlds", 10);
		
		generator.startTweeting(1000000, 60*60*1000, tweetTypes);
		   
		/*
		for (int t = 0; t < 1; t++) //tweetTypes.length
		{
			for (int i = 0 ; i < 100; i++)
			{		
				String next = generator.getLeastRecentRandomCandidate(tweetTypes[t], 100), setup = null, followup = null;
							
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
								
				System.out.println("\nTweet " + i + ":\n" + setup);
				
				if (!followup.equals(setup))
					System.out.println(followup);
				
				generator.advanceCounter();
				
				generator.updateCounts(next);		
			}	
		}
		
		*/
	}

}
