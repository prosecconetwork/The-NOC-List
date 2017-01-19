package twitterbotics;


public class BotOnBotAction 
{
	private static String CONSUMER_KEY    			= "ArBtWs6QeIOrDU6cNj4TTmWJK";
	private static String ACCESS_TOKEN 	  			= "752908753721237504-KrZ3GaCG30jokaDhYKLGxRvonadZMbm";

	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//
	//     Main Application Stub
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//
	
	// In Documents/Workspace/WordNetBrowser
	
	// Command Line:   /usr/bin/java -classpath ../:./:../twitter4j-4.0.1/lib/twitter4j-core-4.0.1.jar:../twitter4j-4.0.1/lib/twitter4j-stream-4.0.1.jar twitterbotics.BotOnBotAction
	
	
	public static void main(String[] args)
	{
		String dir      = "/Users/tonyveale/Desktop/Lexical Resources/Moods/";
		
		TweetLaunchpad generator = new TweetLaunchpad(CONSUMER_KEY, 
												 	  args[0],  // CONSUMER_SECRET, 
												 	  ACCESS_TOKEN, 
												 	  args[1]); // ACCESS_SECRET;
		
		generator.loadSpace(dir + "Venn diagram 1 tweets.idx", "Venn 1");
		generator.loadSpace(dir + "Venn diagram 2 tweets.idx", "Venn 2");
		generator.loadSpace(dir + "Venn diagram 3 tweets.idx", "Venn 3");
		generator.loadSpace(dir + "Venn diagram 4 tweets.idx", "Venn 4");
		generator.loadSpace(dir + "Venn diagram 5 tweets.idx", "Venn 5");
		generator.loadSpace(dir + "narrative 4-step tweets.idx", "Venn 4-step");
		generator.loadSpace(dir + "narrative 5-step tweets.idx", "Venn 5-step");
		generator.loadSpace(dir + "narrative 5-step joint tweets.idx", "Venn joint");
		generator.loadSpace(dir + "narrative 5-step full tweets.idx", "Venn full");
		generator.loadSpace(dir + "narrative 5-step workplace tweets.idx", "Venn workplace");
		generator.loadSpace(dir + "narrative 5-step nesting tweets.idx", "Venn nesting");
		generator.loadSpace(dir + "narrative 5-step left tweets.idx", "Venn left");
		generator.loadSpace(dir + "narrative 5-step right tweets.idx", "Venn right");
		generator.loadSpace(dir + "narrative 5-step marriage tweets.idx", "Venn marriage");
		generator.loadSpace(dir + "narrative 5-step divorce tweets.idx", "Venn divorce");
		generator.loadSpace(dir + "narrative 4-step label tweets.idx", "Venn 4-label");
		generator.loadSpace(dir + "narrative 4-step others tweets.idx", "Venn 4-others");
		generator.loadSpace(dir + "RGB 2 color tweets.idx", "RGB 2-color"); 
		generator.loadSpace(dir + "RGB NOC color tweets.idx", "RGB NOC");
		generator.loadSpace(dir + "RGB stereo color tweets.idx", "RGB stereo");
		generator.loadSpace(dir + "RGB colourlovers tweets.idx", "RGB lovers");
		generator.loadSpace(dir + "RGB monochromatic wallpaper tweets.idx", "RGB monochrome");
		
		String[] tweetTypes = {"RGB lovers", "RGB stereo", "RGB NOC", "RGB 2-color", "Venn 4-others", "Venn 4-label",  
							   "Venn left", "Venn nesting", "Venn full", "Venn joint", "Venn 5-step", "Venn 4-step", "Venn 1", "Venn 2", "Venn 3", 
							   "RGB NOC", "Venn right", "Venn 4", "RGB 2-color", "Venn workplace", "Venn 5", "RGB lovers", "RGB stereo",
							   "Venn divorce", "Venn marriage"};
		
		System.out.println("\n\nTweet Generator version: " + TweetLaunchpad.VERSION + "\n");
				
		generator.analyzeTimeline("BotOnBotAction", 10);
		
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
