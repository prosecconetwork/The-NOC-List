package twitterbotics;

// Contains information about a triggering tweet for a Twitterbot 

public class TwitterbotTrigger 
{
	private String handle = null;
	private String tweet  = null;
	
	private Long tweetId  = null;
	

	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Constructors
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	
	public TwitterbotTrigger() {} 
	
	public TwitterbotTrigger(String handle, String tweet, Long id) 
	{
		this.handle   = handle;
		this.tweet    = tweet;
		this.tweetId  = id;	
	} 
	
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Accessors
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	public String getHandle()
	{
		return handle;
	}
	
	
	public String getTweet()
	{
		return tweet;
	}
	
	public Long getTweetId()
	{
		return tweetId;
	}
	
}
