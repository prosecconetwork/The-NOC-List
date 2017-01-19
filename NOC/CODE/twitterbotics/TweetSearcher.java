package twitterbotics;

import twitter4j.*;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URLEncoder;
import java.util.List;


public class TweetSearcher extends Dribbler
{
	private static String CONSUMER_KEY    = "dGESUpwS6sbOXb6I3vmTH53Qx";
	private static String CONSUMER_SECRET = "Never store a secret token openly in your code";
	
	private static String ACCESS_TOKEN 	  = "752908753721237504-knMgQhhj3vxz9t9zjsEgVUtf8zT3jpf";
	private static String ACCESS_SECRET	  = "Never store a secret token openly in your code";

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
    	  .setOAuthConsumerSecret(CONSUMER_SECRET)
    	  .setOAuthAccessToken(ACCESS_TOKEN)
    	  .setOAuthAccessTokenSecret(ACCESS_SECRET);
    	
    	TwitterFactory tf = new TwitterFactory(cb.build());
    	Twitter twitter = tf.getInstance();
    	
    	twitter.setOAuthAccessToken(new AccessToken(ACCESS_TOKEN, ACCESS_SECRET));

    	return twitter;
	}
	

	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//
	//    Main application stub
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//

    public static void main(String[] args) 
    {
        String queryString = "Obama";
        
        String dir      = "/Users/tonyveale/Desktop/SemEval 2015/";
        
         try {
        	 TweetSearcher searcher = new TweetSearcher();
        	 
             Twitter twitter = searcher.getTwitter();
             
             Query query = new Query().query(queryString).resultType(Query.RECENT);
             
             query.count(10);
             
             QueryResult result;
             
             searcher.openDribbleFile(dir + "metaphorical tweets.idx");
             

             
             do {
            	result = twitter.search(query);
            	
                System.out.println(query + "\n");
                
                List<Status> tweets = result.getTweets();
                
                                         
                for (Status tweet : tweets) 
                {
                	long id =  tweet.getId();
                	StringBuffer text = new StringBuffer(tweet.getText());
                	
                	for (int i = 0; i < text.length(); i++)
                		if (text.charAt(i) == '\n') 
                			text.setCharAt(i, ' ');
                	
                   searcher.printlnDribbleFile(id + "\t" + text + "\t" + tweet.getRetweetCount());
                }
             } 
             while ((query = result.nextQuery()) != null);
             
             
            
             System.exit(0);            
        }
        catch (Exception te) 
        {
            te.printStackTrace();
            System.out.println("Failed to search tweets: " + te.getMessage());
            System.exit(-1);
        }
        
        
    }

}
