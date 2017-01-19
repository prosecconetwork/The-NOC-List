package twitterbotics;

import java.util.StringTokenizer;
import java.util.Vector;

import tabular.BucketTable;

public class TweetLaunchpadWithEmoji extends TweetLaunchpad
{
	private BucketTable wordsToEmojis				= new BucketTable("Unicodes for words");
	

	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//
	//     Constructors
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//
	
	public TweetLaunchpadWithEmoji(String resDir, String cKey, String cSecret, String aKey, String aSecret)
	{		
		super(cKey, cSecret, aKey, aSecret);
		
		KnowledgeBaseModule unicodeConnotations = new KnowledgeBaseModule(resDir + "Unicode Connotations.idx");
		
		unicodeConnotations.invertFieldInto("Connotations", wordsToEmojis);
	}
	
	
	public TweetLaunchpadWithEmoji()
	{
		super();
	}
	
	
	public Long postTweetWithRGB(String tweet, Long inResponseTo, String inResponseToHandle)
	{
		return super.postTweetWithRGB(tweet, inResponseTo, inResponseToHandle, getAptEmojiCodes(tweet));
	}
	
	
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//
	//   Return a list of Emoji unicodes that may be apt for a given tweet
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//
	
	public Vector getAptEmojiCodes(String tweet)
	{
		return getAptEmojiCodes(tweet, wordsToEmojis);
	}

	
	public Vector getAptEmojiCodes(String tweet, BucketTable emojiIndex)
	{
		Vector codes = new Vector();
		
		getAptEmojiCodes(new StringTokenizer(tweet, " \t.,-;\"'()"), codes, emojiIndex);
		getAptEmojiCodes(new StringTokenizer(tweet.toLowerCase(), " \t.,-;\"'()"), codes, emojiIndex);
		
		return codes;
	}
	
	
	private void getAptEmojiCodes(StringTokenizer tokens, Vector codes, BucketTable emojiIndex)
	{	
	   String current = null, previous = null;
		
		while (tokens.hasMoreTokens())
		{
			previous = current;
			current  = tokens.nextToken();
			
			if (emojiIndex.get(current) != null)
				addToList(emojiIndex.get(current), codes);
			
			if (previous != null && emojiIndex.get(previous + " " + current ) != null)
				addToList(emojiIndex.get(previous + " " + current), codes);
			
			if (previous != null && emojiIndex.get(previous + "-" + current) != null)
				addToList(emojiIndex.get(previous + "-" + current), codes);

		}
	}
	
	
	private void addToList(Vector src, Vector tgt)
	{
		for (int i = 0; i < src.size(); i++)
			if (tgt.indexOf(src.elementAt(i)) < 0)
				tgt.add(src.elementAt(i));
	}
	
}
