
package twitterbotics;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.text.Normalizer;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;


import tabular.AtomicCounter;
import tabular.BucketTable;
import tabular.SymbolMap;

import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.FilterQuery;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.UploadedMedia;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.Status;


// A class for scheduling and generating a series of tweets, selected at random from a pool of candidates


public class TweetLaunchpad 
{
	public static String VERSION 		= "1.5";
			
	public static int MAXLEN 			= 140;
	
	public static int MAXMEM 			= 3000;  // put a maximum size on memory of words seen, to avoid memory filling up
	
    private String CONSUMER_KEY 		= "to be provided by Twitterbot";
    private String CONSUMER_KEY_SECRET 	= "to be provided by Twitterbot; Never store a secret token openly in your code";

    private String ACCESS_TOKEN 		= "to be provided by Twitterbot";
    private String ACCESS_TOKEN_SECRET 	= "to be provided by Twitterbot; Never store a secret token openly in your code";

    private Random RND = new Random();
		
	private int currentCounter			= 0;
	
	private Hashtable seenWhen			= new Hashtable();   // track when a word/idea was last used (wrt currentCounter)
	
	private Vector seenOrder			= new Vector();
	
	private Hashtable typesToIndexes	= new Hashtable();
	
	private TweetEmojifier emoji		= new TweetEmojifier();
	
	private String lastRetrievedID		= null;
	
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//
	//     Constructors
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//
	
	public TweetLaunchpad(String cKey, String cSecret, String aKey, String aSecret)
	{		
		this();
		
		CONSUMER_KEY 		= cKey;
		CONSUMER_KEY_SECRET = cSecret;
		
		ACCESS_TOKEN		= aKey;
		ACCESS_TOKEN_SECRET	= aSecret;
	}
	
	
	
	public TweetLaunchpad()
	{
		seenWhen.put("my", "not counted");
		seenWhen.put("over", "not counted");
		seenWhen.put("with", "not counted");
		seenWhen.put("is", "not counted");
		seenWhen.put("was", "not counted");
		seenWhen.put("were", "not counted");
		seenWhen.put("Remember", "not counted");
		seenWhen.put("when", "not counted");
		seenWhen.put("a", "not counted");
		seenWhen.put("an", "not counted");
		seenWhen.put("that", "not counted");
		seenWhen.put("which", "not counted");
		seenWhen.put("only", "not counted");
		seenWhen.put("Now", "not counted");
		seenWhen.put("can", "not counted");
		seenWhen.put("by", "not counted");
		seenWhen.put("from", "not counted");
		seenWhen.put("in", "not counted");
		seenWhen.put("on", "not counted");
		seenWhen.put("about", "not counted");
		seenWhen.put("for", "not counted");
		seenWhen.put("to", "not counted");
		seenWhen.put("via", "not counted");
		seenWhen.put("with", "not counted");
		seenWhen.put("as", "not counted");
		seenWhen.put("Some", "not counted");
		seenWhen.put("all", "not counted");
		seenWhen.put("most", "not counted");
		seenWhen.put("the", "not counted");
		seenWhen.put("Not", "not counted");
		seenWhen.put("also", "not counted");
		seenWhen.put("are", "not counted");
		seenWhen.put("What", "not counted");
		seenWhen.put("but", "not counted");
		seenWhen.put("When", "not counted");
		seenWhen.put("it", "not counted");
		seenWhen.put("comes", "not counted");
		seenWhen.put("to", "not counted");
		seenWhen.put("they", "not counted");
		seenWhen.put("some", "not counted");
		seenWhen.put("better", "not counted");
		seenWhen.put("more", "not counted");
		seenWhen.put("than", "not counted");
		seenWhen.put("For", "not counted");
		seenWhen.put("way", "not counted");
		seenWhen.put("noun", "not counted");
		seenWhen.put("A", "not counted");
		seenWhen.put("An", "not counted");
		seenWhen.put("who", "not counted");
		seenWhen.put("would", "not counted");
		seenWhen.put("rather", "not counted");
		seenWhen.put("than", "not counted");
		seenWhen.put("be", "not counted");
		seenWhen.put("Collective", "not counted");
		seenWhen.put("term", "not counted");
		seenWhen.put("group", "not counted");
		seenWhen.put("of", "not counted");
		seenWhen.put("yet", "not counted");
		seenWhen.put("quite", "not counted");
		seenWhen.put("less", "not counted");
		seenWhen.put("than", "not counted");
		seenWhen.put("If", "not counted");
		seenWhen.put("doesn't", "not counted");
		seenWhen.put("make", "not counted");
		seenWhen.put("you", "not counted");
		seenWhen.put("would", "not counted");
		seenWhen.put("being", "not counted");
		seenWhen.put("by", "not counted");
		seenWhen.put("wouldn't", "not counted");
		seenWhen.put("When", "not counted");
		seenWhen.put("#Irony", "not counted");
		seenWhen.put("1", "not counted");
		seenWhen.put("2", "not counted");
		seenWhen.put("3", "not counted");
		seenWhen.put("4", "not counted");
		seenWhen.put("5", "not counted");
		seenWhen.put("Would", "not counted");
		seenWhen.put("you", "not counted");
		seenWhen.put("rather", "not counted");
		seenWhen.put("be", "not counted");
		seenWhen.put("in", "not counted");
		seenWhen.put("More", "not counted");
		seenWhen.put("like", "not counted");
		seenWhen.put("most", "not counted");
		seenWhen.put("I'm", "not counted");
		seenWhen.put("I", "not counted");
		seenWhen.put("aim", "not counted");
		seenWhen.put("to", "not counted");
		seenWhen.put("be", "not counted");
		seenWhen.put("But", "not counted");
		seenWhen.put("yet", "not counted");
		seenWhen.put("So", "not counted");
		seenWhen.put("Turns", "not counted");
		seenWhen.put("It'll", "not counted");
		seenWhen.put("you", "not counted");
		seenWhen.put("they", "not counted");
		seenWhen.put("said", "not counted");
		seenWhen.put("Yea", "not counted");
		seenWhen.put("Verily", "not counted");
		seenWhen.put("better", "not counted");

		seenWhen.put("What", "not counted");
		seenWhen.put("if", "not counted");
		seenWhen.put("were", "not counted");
		seenWhen.put("real", "not counted");
		seenWhen.put("real?", "not counted");
		seenWhen.put("could", "not counted");
		seenWhen.put("its", "not counted");
		seenWhen.put("and", "not counted");
		seenWhen.put("too", "not counted");
		
		seenWhen.put("Here", "not counted");
		seenWhen.put("lies", "not counted");
		seenWhen.put("may", "not counted");
		seenWhen.put("his", "not counted");
		seenWhen.put("her", "not counted");
		seenWhen.put("on", "not counted");
		seenWhen.put("forever", "not counted");


		seenWhen.put("rapes", "please avoid");
		seenWhen.put("rape", "please avoid");
		seenWhen.put("Rapes", "please avoid");
		seenWhen.put("Rape", "please avoid");
		seenWhen.put("Derstorms", "please avoid");
		seenWhen.put("derstorms", "please avoid");
		
		seenWhen.put("rapists", "please avoid");
		seenWhen.put("rapist", "please avoid");
		seenWhen.put("Rapists", "please avoid");
		seenWhen.put("Rapist", "please avoid");
		seenWhen.put("null", "please avoid");
		seenWhen.put("Null", "please avoid");
		seenWhen.put("nulls", "please avoid");
		
		seenWhen.put("puted", "please avoid");

		seenWhen.put("whore", "please avoid");
		seenWhen.put("whores", "please avoid");
		seenWhen.put("Whores", "please avoid");
		seenWhen.put("Whore", "please avoid");
		
		seenWhen.put("pedophile", "please avoid");
		seenWhen.put("pedophiles", "please avoid");
		seenWhen.put("Pedophiles", "please avoid");
		seenWhen.put("Pedophile", "please avoid");
		
		seenWhen.put("paedophile", "please avoid");
		seenWhen.put("paedophiles", "please avoid");
		seenWhen.put("Paedophiles", "please avoid");
		seenWhen.put("Paedophile", "please avoid");	
		
		seenWhen.put("abortion", "please avoid");
		seenWhen.put("abortions", "please avoid");
		seenWhen.put("Abortions", "please avoid");
		seenWhen.put("Abortion", "please avoid");	

		seenWhen.put("Jew", "please avoid");
		seenWhen.put("Jews", "please avoid");
		seenWhen.put("Jewess", "please avoid");
		seenWhen.put("Jewesses", "please avoid");
	}
	
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//
	//    Return an instance of a Twitter connection object
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//

	
	public Twitter getTwitter() throws TwitterException, IOException 
	{
    	ConfigurationBuilder cb = new ConfigurationBuilder();
    	
    	cb.setDebugEnabled(true)
    	  .setOAuthConsumerKey(CONSUMER_KEY)
    	  .setOAuthConsumerSecret(CONSUMER_KEY_SECRET);
    	
    	TwitterFactory tf = new TwitterFactory(cb.build());
    	Twitter twitter = tf.getInstance();
    	
    	twitter.setOAuthAccessToken(new AccessToken(ACCESS_TOKEN, 
    												ACCESS_TOKEN_SECRET));

    	return twitter;
	}
	
	
	
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//
	//    Add a listener for keywords
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//

	public void addTwitterStatusListener(StatusListener listener, String[] triggerWords)
	{
		ConfigurationBuilder cb = new ConfigurationBuilder();
    	
    	cb.setDebugEnabled(true)
    	  .setOAuthConsumerKey(CONSUMER_KEY)
    	  .setOAuthConsumerSecret(CONSUMER_KEY_SECRET);
    	
		TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
		
		twitterStream.setOAuthAccessToken(new AccessToken(ACCESS_TOKEN, ACCESS_TOKEN_SECRET));
	   
	    FilterQuery triggerFilter = new FilterQuery();
	   
	    triggerFilter.track(triggerWords);

	    twitterStream.addListener(listener);
	    twitterStream.filter(triggerFilter);      
	}
	    	
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//
	//   Obtain twitter time line
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//

	public void analyzeTimeline(String screenName, int numPages)
	{
		try {
			Twitter twitter = getTwitter();
			
			Vector tweets = new Vector();
				
			int count = 0;
			
			for (int p = 1; p <= numPages; p++)
			{
				System.out.println("Page: " + p);
				
				Paging page = new Paging(p);
				
				ResponseList<Status> updates = twitter.getHomeTimeline(page);
				
				for (Status update: updates) 
					if (update.getUser().getScreenName().equals(screenName))
					{
						tweets.add(update.getText());
						
						System.out.println(update.getText());
					}
			} 
			
			for (int i = tweets.size()-1; i >= 0; i--)
			{
				updateRecency((String)tweets.elementAt(i));
				advanceCounter();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//
	//    Central loop to generate successive tweets
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//


	public void startTweeting(int numTweets, long millisecondsBetweenTweets, String[] tweetTypes)
	{
		long lastTime = System.currentTimeMillis() - millisecondsBetweenTweets - 1;
		
		String prevTweetType = "";
		
		for (int i = 0 ; i < numTweets; i++)
		{
			for (int j = 0 ; j < tweetTypes.length; j++)
			{						
				while (System.currentTimeMillis() < lastTime + millisecondsBetweenTweets)
				{
					try {Thread.sleep(10000);} catch (Exception e) {e.printStackTrace();}
				}
				
				try {Thread.sleep(3000);} catch (Exception e) {e.printStackTrace();}
				
				System.out.println("Awake after: " + (System.currentTimeMillis()-lastTime)/60000 + " minutes. Last tweet type is: " + prevTweetType); 
				
				String randomType = tweetTypes[0];
				
				if (i > 0 || j > 0)
					randomType = tweetTypes[RND.nextInt(tweetTypes.length)];
				
				if (randomType.equals(prevTweetType))
					randomType = tweetTypes[RND.nextInt(tweetTypes.length)];		
				
				if (i == 0 && j == 0 && randomType.indexOf((int)'|') > 0)
					randomType = randomType.substring(0, randomType.indexOf((int)'|'));  // choose first option in this special case
				
				String next = getLeastRecentRandomCandidate(randomType, 100);
				
				if (next == null) continue;
				
				Long id = null;
				
				if (next.endsWith("Venn"))
					id = postTweetWithVenn(next);
				else
				if (next.endsWith("RGB"))
					id = postTweetWithRGB(next);
				else
				{
					StringTokenizer tweetset = new StringTokenizer(next, "\t");
					
					Vector orderedTweets = new Vector();
					
					while (tweetset.hasMoreTokens()) 
						orderedTweets.add(emoji.emojify(tweetset.nextToken()));
								
					id = postTweets(orderedTweets);
				}
						
				prevTweetType = randomType;

				if (id != null)
					lastTime = System.currentTimeMillis();	
			}	
		}
	}
	
	
	
	public Long postTweetWithVenn(String tweet)
	{
		StringTokenizer tweetset = new StringTokenizer(tweet, "\t");
		
		Vector orderedTweets = new Vector();
		
		String hashtag = null, blurb = cleanUpMessage(tweetset.nextToken());
		
		while (tweetset.hasMoreTokens()) 
		{
			String label = tweetset.nextToken();
			
			if (tweetset.hasMoreTokens()) 
				orderedTweets.add(label);
			else
				hashtag = label;
		}
		
		VennDiagram ved = null;
		
		if (orderedTweets.size() == 4)
			ved = new VennDiagram((String)orderedTweets.elementAt(0), 
								  (String)orderedTweets.elementAt(1),
								  (String)orderedTweets.elementAt(2),
								  (String)orderedTweets.elementAt(3));
		else
		if (orderedTweets.size() == 3)
			ved = new VennDiagram((String)orderedTweets.elementAt(0), 
								  (String)orderedTweets.elementAt(1),
								  (String)orderedTweets.elementAt(2));
		else
			return null;
					
        ved.setRandomColor();
        
        if (blurb.length() + hashtag.length() < 140)
        	blurb = blurb + "\n" + hashtag;
        
		try {
			Twitter twitter = getTwitter();
			
	        StatusUpdate update = new StatusUpdate(emoji.emojify(blurb));
	        
	        update.setMedia(hashtag.substring(1), ved.toInputStream());
	        	        
	        Status status = twitter.updateStatus(update);
	        System.out.println("Successfully updated the status to [" + status.getText() + "][" + status.getId() + "].");

			return status.getId();
		}
		catch (Exception e)
		{
	        e.printStackTrace();
			return null;
		}
	}
	
	
	public Long postTweetWithRGB(String tweet)
	{
		return postTweetWithRGB(tweet, null, null);
	}
	
	
	public Long postTweetWithRGB(String tweet, Long inResponseTo)
	{
		return postTweetWithRGB(tweet, inResponseTo, null);
	}
	
	
	public Long postTweetWithRGB(String tweet, Long inResponseTo, String inResponseToHandle)
	{
		return postTweetWithRGB(tweet, inResponseTo, inResponseToHandle, null);
	}
	
	
	public Long postTweetWithRGB(String tweet, Long inResponseTo, String inResponseToHandle, Vector emojiUnicodes)
	{
		StringTokenizer tweetset = new StringTokenizer(tweet, "\t");
		
		Vector orderedTweets = new Vector();
		
		String hashtag = null, blurb = cleanUpMessage(tweetset.nextToken());
		
		while (tweetset.hasMoreTokens()) 
		{
			String label = tweetset.nextToken();
			
			if (tweetset.hasMoreTokens()) 
				orderedTweets.add(label);
			else
				hashtag = label;
		}
		
		PaintedCanvas pc = null;
				
		if (orderedTweets.size() == 2)
			pc =  new PaintedCanvas((String)orderedTweets.elementAt(0), 
								    (String)orderedTweets.elementAt(1));
		else
		if (orderedTweets.size() == 3)
			pc =  new PaintedCanvas((String)orderedTweets.elementAt(0), 
								    (String)orderedTweets.elementAt(1),
								    (String)orderedTweets.elementAt(2));
		else
		if (orderedTweets.size() == 4)
			pc =  new PaintedCanvas((String)orderedTweets.elementAt(0), 
								    (String)orderedTweets.elementAt(1),
								    (String)orderedTweets.elementAt(2),
								    (String)orderedTweets.elementAt(3));
		else
			return null;
		
		if (emojiUnicodes != null && emojiUnicodes.size() > 0)
			pc.setEmojiUnicode((String)emojiUnicodes.elementAt(RND.nextInt(emojiUnicodes.size())));
					
		if (blurb.length() + hashtag.length() < 140)
        	blurb = blurb + "\n" + hashtag;
		
		String emojified = emoji.emojify(blurb);
        
		try {
			Twitter twitter = getTwitter();
			
			if (inResponseToHandle != null && inResponseTo != null)
			{
				if (inResponseToHandle.startsWith("@"))
					inResponseToHandle = inResponseToHandle.substring(1);
				
				String embeddedLink = "https://twitter.com/" + inResponseToHandle + "/status/" + inResponseTo;
				
				if (embeddedLink.length() + emojified.length() < 140)
					emojified = emojified + " " + embeddedLink;
			}
			
			System.out.println(emojified.length() + " --> " + emojified);
			
	        StatusUpdate update = new StatusUpdate(emojified);
	        
	        
	        if (inResponseTo != null && inResponseToHandle == null)
	        	update = update.inReplyToStatusId(inResponseTo);
	        
	        update.setMedia(hashtag.substring(1), pc.toInputStream());
	        	        
	        Status status = twitter.updateStatus(update);
	        System.out.println("Successfully updated the status to [" + status.getText() + "][" + status.getId() + "].");
 
			return status.getId();
		}
		catch (Exception e)
		{
	        e.printStackTrace();
			return null;
		}
	}
	
	
	
	public Long postTweet(String tweet)
	{
		return postTweet(tweet, null);
	}
	
	
	public Long postTweet(String tweet, Long inResponseTo)
	{
		try {
			Twitter twitter = getTwitter();
			
			StatusUpdate status = new StatusUpdate(tweet);
			
			if (inResponseTo != null)
				status = status.inReplyToStatusId(inResponseTo);
							
			return twitter.updateStatus(status).getId();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	
	public Long postTweets(Vector orderedTweets)
	{
		try {
			Twitter twitter = getTwitter();
			
			Long id = null;
			
			for (int tw = orderedTweets.size()-1; tw >= 0; tw--)
			{
				String tweet = (String)orderedTweets.elementAt(tw);
				
				System.out.println("(" + (tw+1) + " of " + orderedTweets.size() + ") " + tweet);

				StatusUpdate status = new StatusUpdate(tweet);
				
				if (id != null)
					status = status.inReplyToStatusId(id);
				
				id = twitter.updateStatus(status).getId();
				
				advanceCounter();			
				updateRecency(tweet);
							
				Thread.sleep(1000);
			}
			
			return id;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	
	public List<Status> findAllTweetsMatching(String queryString, int min)
	{
		Vector<Status> allMatches = new Vector<Status>();
		 
		try {
			Twitter twitter  = getTwitter();
			 
			Query query      = new Query().query(queryString)
										        .resultType(Query.RECENT);			
			query.count(10);
			
			while (query != null && allMatches.size() < min)
			{
				QueryResult result   = twitter.search(query);
							
				List<Status> matches = result.getTweets();
	                      		
				for (Status match : matches) 
					allMatches.add(match);
				
				query = result.nextQuery();
			}
        } 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return allMatches;
 	}
	
	
	public void showAllTweetsMatching(String queryString)
	{	
		List<Status> matches = findAllTweetsMatching(queryString, 100);
		
		for (Status match : matches) 
			System.out.println(match.getId() + ":\t" + match.getText());
	}
	
	
	public void respondToMatchesWith(String queryString, String response)
	{	
		List<Status> matches = findAllTweetsMatching(queryString, 100);
		
		for (Status match : matches) 
			postTweet("@" + match.getUser().getScreenName() 
					      + ": " + response,
					  match.getId());
	}
	
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//
	//     Select and evaluate random candidates
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//

	private Vector getRandomOffsets(Hashtable typeIndex, String hashtags)
	{
		Vector entry = (Vector)typeIndex.get(hashtags);
		
		if (entry == null || entry.size() < 2) return null;
		
		if (entry.elementAt(1) instanceof Vector) // vector of vectors
			return (Vector)entry.elementAt(1 + RND.nextInt(entry.size()-1));
		else
			return entry;
	}
	
	
	
	private String getRandomCandidate(String tweetType, String tweetFilename, long skip)
	{
		Hashtable typeIndex   = (Hashtable)typesToIndexes.get(tweetType);
		
		String randomTweet    = getTweetText(tweetFilename, skip);
		
		if (randomTweet == null) return null;
				
		StringTokenizer tokens = new StringTokenizer(randomTweet, "\t", false);
		
		lastRetrievedID = (String)tokens.nextElement();  // ignore preamble
		
		if (!tokens.hasMoreTokens()) return null;

		String randomHashtags = tokens.nextToken();  
		
		//System.out.println(randomHashtags + ":  " + randomTweet);
		
		String randomSetup     = tokens.nextToken();  
		
		if (!tokens.hasMoreTokens()) return null;
		
		String randomFollowup  = tokens.nextToken();
		String link			   = " ";
		
		if (Character.isWhitespace(randomSetup.charAt(randomSetup.length()-1)) || randomSetup.charAt(randomSetup.length()-1) == '|')
			link = "";
		
		String randomPostscript  = null;
		
		if (tokens.hasMoreTokens())
		{
			randomPostscript = tokens.nextToken();
			
			while (tokens.hasMoreTokens())
				randomPostscript = randomPostscript + "\t" + tokens.nextToken();
		}

		if (randomHashtags.endsWith("Venn") || randomHashtags.endsWith("RGB")) 
		{
			if (randomPostscript != null)
				return randomSetup + "\t" + randomFollowup + "\t" + randomPostscript + "\t" + randomHashtags;
			else
				return randomSetup + "\t" + randomFollowup + "\t" + randomHashtags;
		}

		String single = randomSetup + link + randomFollowup;
		
		if (single.indexOf(randomHashtags) < 0)
			single = appendHashtagsIfRoom(single, randomHashtags);
		
		
		single = cleanUpMessage(single);
		
		if (randomPostscript != null)
			return cleanUpMessage(appendHashtagsIfRoom(randomSetup, randomHashtags) + "\t" +
					              appendHashtagsIfRoom(randomFollowup, randomHashtags) + "\t" +
					              appendHashtagsIfRoom(randomPostscript, randomHashtags));
		else
		if (single.length() <= MAXLEN)
		{
			if (randomSetup.startsWith("1.") && (randomHashtags + " " + randomSetup + link + randomFollowup).length() <= MAXLEN)
				return cleanUpMessage(randomHashtags + " " + randomSetup + link + randomFollowup);
			else
				return cleanUpMessage(single);
		}
		else
		{
			single = cleanUpMessage(randomSetup + link + randomFollowup);
		
			if (single.length() <= MAXLEN && single.indexOf((int)'#') >= 0)
				return single;
			else
				return cleanUpMessage(appendHashtagsIfRoom(randomSetup, randomHashtags) + "\t" + appendHashtagsIfRoom(randomFollowup, randomHashtags));
		}
	}
	
	
	private String appendHashtagsIfRoom(String tweet, String tags)
	{
		if (tweet.indexOf(tags) >= 0)
			return tweet;
		
		String link = "|";
		
		if (Character.isWhitespace(tweet.charAt(tweet.length()-1)) || tweet.charAt(tweet.length()-1) == '|')
			link = "";
		
		if (tweet.length() + tags.length() + link.length() <= MAXLEN)
			return tweet + link + tags;
		else
			return tweet;
	}
	
	
	private String getRandomTweetType(String tweetType)
	{
		if (tweetType.indexOf((int)'|') < 0)
			return tweetType;
		
		StringTokenizer tokens = new StringTokenizer(tweetType, "|", false);
		
		Vector options = new Vector();
		
		while (tokens.hasMoreTokens())
			options.add(tokens.nextToken());
		
		return (String)options.elementAt(RND.nextInt(options.size()));
	}
	
	
	
	public String getLeastRecentRandomCandidate(String tweetType, int numTries)
	{	
		String leastRecent = null, mostRecentID = lastRetrievedID;
		
		if (tweetType.indexOf((int)'|') > 0)
			tweetType = getRandomTweetType(tweetType);  // type token might contain disjunctive choices

		try {
			Hashtable typeIndex     = (Hashtable)typesToIndexes.get(tweetType);
			
			String tweetFilename    = (String)typeIndex.get("*filename");
			
			Long lastOffset			= (Long)typeIndex.get("*lastoffset");
		
			long offsetChoice       = Math.abs(RND.nextLong())%lastOffset.longValue();
			
			leastRecent 			= getRandomCandidate(tweetType, tweetFilename, offsetChoice);
			
			int attempt 		    = 1;
			
			
			while (leastRecent == null)
			{
				attempt++;
				
				System.out.println("[" + attempt + "] Attempting to find random candidate from <" + tweetFilename + "> of type {" 
									 + tweetType + "} at position " + offsetChoice);
				
				offsetChoice        = Math.abs(RND.nextLong())%lastOffset.longValue();
				
				leastRecent 		= getRandomCandidate(tweetType, tweetFilename, offsetChoice);
				
				mostRecentID = lastRetrievedID;
				
				if (leastRecent == null && attempt >= 100) return null;
			}
				
		
			if (numTries <= 1) return leastRecent;
			
			int leastRecency = getRecencyOfTheme(leastRecent);
			
			
			for (int i = 1; i < numTries; i++)
			{
				offsetChoice  = Math.abs(RND.nextLong())%lastOffset.longValue();
				
				String choice    = getRandomCandidate(tweetType, tweetFilename, offsetChoice);
				String choiceID  = lastRetrievedID;  
								
				int recency      = getRecencyOfTheme(choice);
				
				if (recency == 0) 
				{
					System.out.println(choiceID);
					return choice;
				}
				
				if (recency <= leastRecency)
				{
					leastRecency = recency;
					leastRecent  = choice;
					mostRecentID = choiceID;
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		System.out.println(mostRecentID);
		
		return leastRecent;
	}
	
	
	
	
	private String cleanUpMessage(String message)
	{				
		int stricken = message.indexOf("-stricken_by");
		
		if (stricken >= 0)
			message = message.substring(0, stricken) + "-stricken" + message.substring(stricken + "-stricken_by".length());
		
		
		StringBuffer clean = new StringBuffer();
		
		boolean inHashtag = false, inHandle = false;
		
		for (int i = 0; i < message.length(); )
		{
			int codePoint = Character.codePointAt(message, i);
			
			if (message.charAt(i) == '#')
				inHashtag = true;
			else
			if (message.charAt(i) == '@')
				inHandle = true;
			else
			if (Character.isWhitespace(message.charAt(i)) || message.charAt(i) == '.' || message.charAt(i) == '|' || message.charAt(i) == ',')
			{
				inHashtag = false;
				inHandle  = false;
			}
			
			if (i > 4 && inHashtag 
					&& Character.isUpperCase(message.charAt(i)) 
					&& message.charAt(i-1) == 'r' && message.charAt(i-2) == 'O'
					&& Character.isLowerCase(message.charAt(i-3)))
			{
				clean.setCharAt(i-2, '=');
				clean.setCharAt(i-1, '#');
			}
			
			if (i > 4 && inHashtag 
					&& Character.isUpperCase(message.charAt(i)) 
					&& message.charAt(i-1) == 's' && message.charAt(i-2) == 'I'
					&& Character.isLowerCase(message.charAt(i-3)) )
			{
				clean.setCharAt(i-2, '=');
				clean.setCharAt(i-1, '#');
			}
			
			if (message.charAt(i) == '_' && !inHashtag && !inHandle)
				clean.append(' ');
			else
			if (message.charAt(i) == '|')
				clean.append('\n');
			else
			if (Character.charCount(codePoint) == 1)
				clean.append(message.charAt(i));
			else
				clean.append(new String(Character.toChars(codePoint)));
			
			if (i > 3 && message.charAt(i) == ' ' && message.charAt(i-1) == '.' 
					&& Character.isDigit(message.charAt(i-2))
					&& Character.isWhitespace(message.charAt(i-3)))
				clean.setCharAt(i-3, '\n');
			
			i = i + Character.charCount(codePoint);
		}
		
		return clean.toString();
		
	}
	
	

	
	private String cleanupHashtag(String hashtag) // insert underscores to link broken compound terms
	{
		StringBuffer clean = new StringBuffer();
		
		for (int i = 0; i < hashtag.length(); i++)
			if (hashtag.charAt(i) == ' ' && i < hashtag.length()-1 && hashtag.charAt(i+1) != '#')
				clean.append('_');
			else
				clean.append(hashtag.charAt(i));
			
		return clean.toString();
	}
	
	
	private String cleanupAndDivideHashtag(String hashtag)
	{
		StringBuffer clean = new StringBuffer();
		
		for (int i = 0; i < hashtag.length(); i++)
			if (hashtag.charAt(i) == ' ' && i < hashtag.length()-1 && hashtag.charAt(i+1) != '#')
				clean.append('_');
			else
				clean.append(hashtag.charAt(i));
		
		int divider = hashtag.indexOf("Or", 1);
		
		if (divider < 0)
			divider = hashtag.indexOf("Is", 1);
		
		if (divider > 1)
		{
			clean.setCharAt(divider, '=');
			clean.setCharAt(divider+1, '#');
		}
			
		return clean.toString();
	}
	
	
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//
	//   Insert Time Emoji into the tweet
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//

	private boolean hasNumberingSystem(String tweet, int min, int max)
	{
		for (int i = min; i <= max; i++)
			if (tweet.indexOf("" + i + ".") < 0) return false;
		
		return true;
	}
	
	
	
	private String insertNumberEmoji(String tweet, String[] emojis, int min, int max)
	{
		for (int i = min; i <= max; i++)
		{
			int pos = tweet.indexOf("" + i + ".");
			
			if (pos < 0) continue;
			
			tweet = tweet.substring(0, pos) 
					 + new String(Character.toChars(Integer.parseInt(emojis[i-1], 16)))
					 + tweet.substring(pos+1);			
		}
		
		return tweet;
		
	}
	
	
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//
	//    Manage the current count
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//

	public int getRecencyOfTheme(String tweet)
	{
		StringTokenizer tokens = new StringTokenizer(tweet, " \t\n,.?:;='\"()[]_-", false);
		
		int recency = 0, count = 1;
		
		while (tokens.hasMoreTokens())
		{
			recency += getRecencyOfWord(tokens.nextToken());	
			count++;
		}
		
		return (100*recency)/count;
	}
	
	
	public void updateRecency(String tweet)
	{
		StringTokenizer tokens = new StringTokenizer(tweet, " \t\n,.?=:;.", false);
		
		Integer recency = new Integer(currentCounter);
		
		while (tokens.hasMoreTokens())
			updateCountOf(tokens.nextToken(), recency);	
	}
	
	
	//--------------------------------------------------------------------------------------------------//

	
	protected int advanceCounter()
	{
		currentCounter = currentCounter + 1;
		
		return currentCounter;
	}
	
	
	
	
	private void updateCountOf(String word, Integer recency)
	{
		if (seenWhen.get(word) == "not counted" || seenWhen.get(word) == "please avoid") 
			return;
		
		if (seenWhen.get(word) == null) // not seen so far
		{
			seenOrder.add(word);  // now we have a record of the order in which it was seen, relative to other words
			
			while (seenOrder.size() > MAXMEM)  // we need to forget the older words, to stop memory filling up
			{
				seenWhen.remove(seenOrder.elementAt(0));
				seenOrder.remove(0);
			}
		}
		
		seenWhen.put(word, recency);
	}
	
	
	
	
	private int getRecencyOfWord(String word)
	{
		Object entry = seenWhen.get(word);
		
		if (entry == null && word.startsWith("#"))
			entry = seenWhen.get(word.substring(1));
		
		if (entry == "please avoid")
			return 1000000;
		
		if (entry == null || entry == "not counted")
			return 0;
		
		return ((Integer)entry).intValue();
	}
		
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//
	//     Load Candidates to tweet later
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//
	
	public void loadSpace(String filename, String tweetType)
	{
		Hashtable typeIndex = (Hashtable)typesToIndexes.get(tweetType);
				
		if (typeIndex == null)
		{
			typeIndex = new Hashtable();
			typeIndex.put("*filename", filename);
			typesToIndexes.put(tweetType, typeIndex);
		}
		
		
		try {
			RandomAccessFile input = new RandomAccessFile(filename, "r");
			
			long lastOffset = input.length() - MAXLEN - MAXLEN-2;
			
			System.out.println("Loaded " + filename + " with more than " + lastOffset + " bytes");

			typeIndex.put("*lastoffset", new Long(lastOffset));

			input.close();
		}
		catch (IOException e)
		{
			System.out.println("Cannot find/load file: " + filename);
			
			e.printStackTrace();
		}
	}
	
		
	
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//
	//     Get the tweet text from a given file
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//
	
	
	
	private String getTweetText(String tweetFilename, long skip)
	{
		try {
			FileInputStream rawfile = new FileInputStream(tweetFilename);
			
			rawfile.skip(skip);
			
			BufferedReader buffer = new BufferedReader(new InputStreamReader(rawfile, "UTF8"));
						
			String line = buffer.readLine();
			
			line = buffer.readLine();
			
			buffer.close();
			
			//System.out.println(line);
		
			return line;
		}
		catch (IOException e) 
		{
			System.out.println("Exception while reading tweet file: " +  tweetFilename + " at offset: " + skip + "\n " + e.toString());
				 
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	
	private int getNumTabsIn(String line)
	{
		if (line == null) return 0;
		
		int count = 0, tab = line.indexOf((int)'\t');
		
		while (tab > 0)
		{
			count++;
			tab = line.indexOf((int)'\t', tab+1);
		}
		
		return count;
	}
	


	
}
