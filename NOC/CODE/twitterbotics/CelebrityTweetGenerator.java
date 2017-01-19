package twitterbotics;

import java.util.StringTokenizer;
import java.util.Vector;

import tabular.BucketTable;
import tabular.WordTable;

import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

public class CelebrityTweetGenerator implements Runnable
{
	private static long longSleep					= 60*60*1000;
	private static long shortSleep					= 5*60*1000;
	private static long tinySleep					= 60*1000;
	
	private static String CONSUMER_KEY    			= "ArBtWs6QeIOrDU6cNj4TTmWJK";	
	private static String ACCESS_TOKEN 	  			= "752908753721237504-KrZ3GaCG30jokaDhYKLGxRvonadZMbm";

	private TweetLaunchpadWithEmoji launchpad      	= null;
	
	private WordTable celebrityList		  			= null;
	
	private AffectiveMetaphorizer metaMaker	  		= null;
	
	private int positiveThreshold		  			= 65;
	private int negativeThreshold		  			= 75;
	
	private long lastTime				  			= 0;
	
	private Vector<TwitterbotTrigger> requestQueue 	= new Vector<TwitterbotTrigger>();
	
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//
	//    Constructor
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//

	public CelebrityTweetGenerator(TweetLaunchpadWithEmoji launchpad, String resdir, String knowledgeDir, int posThreshold, int negThreshold)
	{
		positiveThreshold = posThreshold;
		negativeThreshold = negThreshold;
		
		this.launchpad 	   = launchpad;
		
		celebrityList 	   = new WordTable(resdir + "famous Twitter accounts.idx");
		
		metaMaker  	  	   = new AffectiveMetaphorizer(resdir, knowledgeDir, "color stereotype lexicon.idx", 
																			 "stereotype model.idx",
																			 "plural possession forms.idx");
		
	}


	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//
	//    Implement Thread behavior of Runnable Interface
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//

	public void run()
	{
		System.out.println("Starting request-processing thread ... ");
		
		while (true) 
		{
			try {
				if (isPendingRequest())
				{
					serveRequest();
					try {Thread.sleep(shortSleep);} catch (Exception e) {e.printStackTrace();}
				}
				else
					try {Thread.sleep(tinySleep);} catch (Exception e) {e.printStackTrace();}
			}
			catch (Exception e) {
				e.printStackTrace();
			}	
		}
	}
	
	
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//
	//    Manage the queue of requests
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//

	
	private boolean isPendingRequest()
	{
		synchronized (requestQueue) {
			return requestQueue.size() > 0;
		}
	}
	
	
	private void addRequest(TwitterbotTrigger request)
	{
		synchronized (requestQueue) {
			requestQueue.add(request);
			
			if (request.getHandle() == null)
				System.out.println("Received and queued a generic request");
			else
				System.out.println("Received request from " + "@" + request.getHandle() +": " + request.getTweet());
		}
	}
	
	
	
	private boolean serveRequest()
	{
		TwitterbotTrigger request =  null;
		
		synchronized (requestQueue) 
		{
			if (requestQueue.size() == 0)
				return false;
			
			request = requestQueue.firstElement();
			
			requestQueue.remove(0);
		}
		
		if (request == null) 
			return false;
		
		if (request.getHandle() == null) { // empty request
			if (!serveRandomMetaphor())
				serveRandomMetaphor();
		}
		else
			triggerAlert(request.getHandle(), request.getTweet(), request.getTweetId());
			
		return false;
	}
	
	

	private boolean serveRandomMetaphor()
	{
		String celebHandle = celebrityList.getRandomWord();
		
		System.out.println("\nTurning random attention to: " + celebHandle);
		
		String metaphor = null;
		
		synchronized (metaMaker)
		{	
			AffectiveProfile profile = metaMaker.analzyeWords(celebHandle);
		
			System.out.println("\nPossible stereo metaphors for " + celebHandle + ": " + 
							metaMaker.tabulateCandidateMetaphorsFor(profile, positiveThreshold, negativeThreshold));
		
			metaphor = getLeastRecentMetaphor(metaMaker.harvestMetaphorsFor(profile, positiveThreshold, negativeThreshold, 
																			      AffectiveMetaphorizer.META_SAMPLE, null));
			
			System.out.println("\nMetaphor" + metaphor);
			
			if (metaphor != null)
			{
				Long id = launchpad.postTweetWithRGB(metaphor + "\t#CelebrityRGB", null, null);
				
				if (id != null) {
					lastTime = System.currentTimeMillis();	
					launchpad.updateRecency(metaphor);
				}
			}
		}
		
		return metaphor != null;
	}
	
	
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//
	//    Create Twitter Listener
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//

	// addTwitterStatusListener
	
	public void listenForTriggerWords(TweetLaunchpad generator, String[] triggerWords)
	{
	    StatusListener listener = new StatusListener() {

	        public void onStatus(Status status) {
	        	addRequest(new TwitterbotTrigger(status.getUser().getScreenName(), status.getText(), status.getId()));
	        }

	        public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
	            System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
	        }

	        public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
	            System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
	        }

	        public void onScrubGeo(long userId, long upToStatusId) {
	            System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
	        }

	        public void onException(Exception ex) {
	            ex.printStackTrace();
	        }
	        
	        public void onStallWarning(StallWarning warning) {
	        	System.out.println("Stall Warning: " + warning.getMessage());
	        }
	    }; 
	    
	    generator.addTwitterStatusListener(listener, triggerWords);
	}
	
	
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//
	//    This method is called with a tweet is posted containing one of the bot's trigger words
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//
	
	
	synchronized public void triggerAlert(String celebHandle, String tweet, Long inReplyTo)
	{
		tweet = tweet.toLowerCase();
		
		AffectiveProfile profile = metaMaker.analzyeWords(celebHandle);
		
		
		if (tweet.indexOf("#paintmysoul") >= 0) {
			triggerMetaphorPainting(celebHandle, profile, tweet, metaMaker.getLexicon().getPaletteFrom(tweet), inReplyTo);
			
			try {Thread.sleep(shortSleep);} catch (Exception e) {e.printStackTrace();}
		}

		
		if (tweet.indexOf("#metaphorizeme") >= 0) {
			Long id = triggerMetaphorComparison(celebHandle, profile, tweet, inReplyTo);
			
			try {Thread.sleep(shortSleep);} catch (Exception e) {e.printStackTrace();}
			
			if (id != null) {
				triggerMetaphorPainting(celebHandle, profile, tweet, metaMaker.getLexicon().getPaletteFrom(tweet), id);
				
				try {Thread.sleep(shortSleep);} catch (Exception e) {e.printStackTrace();}
			}
		}
		
		
		if (tweet.indexOf("#myjustdessert") >= 0) {
			Long id = triggerMetaphorComparison(celebHandle, profile, tweet, inReplyTo, 2);
			
			try {Thread.sleep(shortSleep);} catch (Exception e) {e.printStackTrace();}
			
			if (id != null) {
				triggerMetaphorPainting(celebHandle, profile, tweet, metaMaker.getLexicon().getPaletteFrom(tweet), id);
				
				try {Thread.sleep(shortSleep);} catch (Exception e) {e.printStackTrace();}
			}
		}
				
		if (tweet.indexOf("#ticklemehulk") >= 0) {
			triggerMetaphorComparison(celebHandle, profile, tweet, inReplyTo, 1);
			
			try {Thread.sleep(shortSleep);} catch (Exception e) {e.printStackTrace();}
		}
		
	}
	
	
	
	public Long triggerMetaphorPainting(String celebHandle, AffectiveProfile profile, String tweet, Vector palette, Long inReplyTo)
	{
		System.out.println(celebHandle + " just posted this: " + tweet + " with these colors: " + palette);
		
		Long id = null;  // Twitter status id of the tweet generated by this method
		
		synchronized (metaMaker)
		{	
			System.out.println("\nPossible stereo metaphors for " + celebHandle + ": " + 
								metaMaker.tabulateCandidateMetaphorsFor(profile, positiveThreshold, negativeThreshold));
			
			String metaphor = getLeastRecentMetaphor(metaMaker.harvestMetaphorsFor(profile, positiveThreshold, negativeThreshold, 
				      															   AffectiveMetaphorizer.META_SAMPLE, palette));
			
			while (metaphor == null && palette != null && palette.size() > 0) {
				palette.remove(palette.size()-1); // remove a constraint
				
				if (palette != null && palette.size() > 1)
					metaphor = metaMaker.metaphorizeProfile(profile, positiveThreshold, negativeThreshold, palette);
				else
					metaphor = getLeastRecentMetaphor(metaMaker.harvestMetaphorsFor(profile, positiveThreshold, negativeThreshold, 
																					AffectiveMetaphorizer.META_SAMPLE, palette));				
			}
			
			System.out.println("\n" + metaphor);
			
			if (metaphor != null)
			{
				id = launchpad.postTweetWithRGB(metaphor + "\t#CelebrityRGB", inReplyTo, celebHandle);
				
				if (id != null)
					lastTime = System.currentTimeMillis();	
			}
		}
		
		return id;
	}
	
	
	
	public Long triggerMetaphorComparison(String celebHandle, AffectiveProfile profile, String tweet, Long inReplyTo)
	{
		return triggerMetaphorComparison(celebHandle, profile, tweet, inReplyTo, Dribbler.RND.nextInt(20));
	}
	
	
	
	
	public Long triggerMetaphorComparison(String celebHandle, AffectiveProfile profile, String tweet, Long inReplyTo, int choice)
	{
		System.out.println(celebHandle + " just posted this: " + tweet);
		
		Long id = null;   // Twitter status id of the tweet generated by this method
		
		synchronized (metaMaker)
		{	
			System.out.println("\nPossible stereo metaphors for " + celebHandle + ": " + 
								metaMaker.tabulateCandidateMetaphorsFor(profile, positiveThreshold, negativeThreshold));
			
			String metaphor = null;
			
			if (choice == 0)
				metaphor = metaMaker.compareWithTopN(profile, positiveThreshold, negativeThreshold, 5);
			else
			if (choice == 1)
				metaphor = metaMaker.compareAndThreaten(profile, positiveThreshold, negativeThreshold);
			else
			if (choice == 2)
				metaphor = metaMaker.compareAndDisgust(profile, positiveThreshold, negativeThreshold);
			else
				metaphor = metaMaker.compareAndDisavow(profile, positiveThreshold, negativeThreshold, 5, 4);
			
			
			System.out.println("\n" + metaphor);
			
			if (metaphor != null)
			{
				if (metaphor.endsWith("RGB"))
					id = launchpad.postTweetWithRGB(metaphor, inReplyTo, celebHandle);
				else
					id = launchpad.postTweet(metaphor, inReplyTo);
				
				if (id != null)
					lastTime = System.currentTimeMillis();	
			}
		}
		
		return id;
	}
	
	
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//
	//     Main Tweeting Loop
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//


	public void startTweeting(int numTweets, long millisecondsBetweenTweets)
	{
		new Thread(this).start();
		
		lastTime = System.currentTimeMillis() - millisecondsBetweenTweets - 1;
		
		String prevTweetType = "";
		
		for (int i = 0 ; i < numTweets; i++)
		{			
			System.out.println("Awake after: " + (System.currentTimeMillis()-lastTime)/60000 + " minutes. Last tweet type is: " + prevTweetType); 
			
			try {
				addRequest(new TwitterbotTrigger());
			
				Thread.sleep(millisecondsBetweenTweets);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	
	
	
	private String getLeastRecentMetaphor(Vector choices)
	{
		if (choices == null || choices.size() == 0) 
			return null;
		
		String bestChoice = (String)choices.elementAt(0), choice = null;
		
		int recency = 0, leastRecency = 0;
		
		for (int c = 0; c < choices.size(); c++)
		{
			choice = (String)choices.elementAt(c);
			
			recency = launchpad.getRecencyOfTheme(choice);
			
			if (leastRecency == 0 || recency < leastRecency) {
				leastRecency = recency;
				bestChoice   = choice;
			}
		}
		
		return bestChoice;
	}
	
	
	


	
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//
	//     Main Application Stub
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//
	
	// In Documents/Workspace/WordNetBrowser
		
	
	public static void main(String[] args)
	{
		String rdir   = "/Users/tonyveale/Desktop/Lexical Resources/Moods/";
		String kdir   = "/Users/tonyveale/Dropbox/CodeCamp2016/NOC/DATA/TSV Lists/";
		
		TweetLaunchpadWithEmoji launchpad = new TweetLaunchpadWithEmoji(rdir, CONSUMER_KEY, 
																			  args[0], // CONSUMER_SECRET, 
																			  ACCESS_TOKEN, 
																			  args[1]); // ACCESS_SECRET;
		
		CelebrityTweetGenerator celebFan = new CelebrityTweetGenerator(launchpad, rdir, kdir, 65, 65);
		
		/*
		String sample = "I painted \"Eager-to-impress Nut\" from @AlanCarr's tweets, using eccentric troll-grey, wacky Britney-blonde-yellow and nutty Wonka-purple.";
		
		Vector codes = celebFan.getAptEmojiCodes(sample);
		
		System.out.println(codes);
		*/
		
		System.out.println("\n\nTweet Generator version: " + TweetLaunchpad.VERSION + "\n");
		
		String[] triggers = {"#PaintMySoul", "#MetaphorizeMe", "#TickleMeHulk", "#MyJustDessert"};
				
		celebFan.listenForTriggerWords(launchpad, triggers);
		
		launchpad.analyzeTimeline("BotOnBotAction", 10);
		
		celebFan.startTweeting(1000000, CelebrityTweetGenerator.longSleep);		
	}

}
