package twitterbotics;

import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;


public class TweetEmojifier 
{
    static String WEDDING 		= "1F492";
    static String POO 		    = "1F4A9";

	static String[] TIMES   	= {"1F55B",    "1F558", "1F55B", "1F551", "1F55E", "1F554",     "1F37E"};
	static String[] MOONS   	= {"1F315",    "1F315", "1F316", "1F317", "1F318", "1F311",     "1F320"};
	static String[] WEATHER 	= {"1F31E",    "1F31E", "1F324", "1F32C", "1F329", "1F302",     "1F308"};
	static String[] FACES   	= {"1F636",    "1F914", "1F60F", "1F625", "1F623", "1F644",     "1F602"};
	static String[] MARRIAGE 	= {"1F393",    "1F6AA", "1F44B", "1F492", "1F49E", "1F494",     "1F489"};

    private Random RND = new Random();

	private Vector numberingSchemes = new Vector(); 
	
	private Hashtable symbols	= new Hashtable();
	
	private Vector allSymbols   = new Vector();
	private Vector allAnimals   = new Vector();
	
	
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//
	//     Constructors
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//

	public TweetEmojifier()
	{
		numberingSchemes.add(TIMES);
		numberingSchemes.add(MOONS);
		numberingSchemes.add(WEATHER);	
		
		addEmojiMappings();
	}
	
	
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//
	//     Set up useful symbol to unicode mappings
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//

	private void addEmojiMappings()
	{
		addAnimal("panther", "1F63A");
		addAnimal("puma", "1F63A");
		addAnimal("jaguar", "1F638");
		addAnimal("fox", "1F63C");
		addAnimal("monkey", "1F435");
		addAnimal("lemur", "1F412");
		addAnimal("ape", "1F435");
		addAnimal("chimp", "1F435");
		addAnimal("chimpanzee", "1F435");
		addAnimal("dog", "1F436");
		addAnimal("hound", "1F415");
		addAnimal("poodle", "1F429");
		addAnimal("wolf", "1F43A");
		addAnimal("coyote", "1F43A");
		addAnimal("cat", "1F431");
		addAnimal("lion", "1F981");
		addAnimal("lioness", "1F981");
		addAnimal("tiger", "1F42F");
		addAnimal("tigress", "1F42F");
		addAnimal("horse", "1F434");
		addAnimal("pony", "1F434");
		addAnimal("stallion", "1F434");
		addAnimal("mare", "1F434");
		addAnimal("racehorse", "1F434");
		addAnimal("unicorn", "1F984");
		addAnimal("cow", "1F42E");
		addAnimal("bull", "1F403");
		addAnimal("pig", "1F437");
		addAnimal("hog", "1F437");
		addAnimal("swine", "1F437");
		addAnimal("boar", "1F417");
		addAnimal("warthog", "1F417");
		addAnimal("ram", "1F40F");
		addAnimal("sheep", "1F411");
		addAnimal("goat", "1F410");
		addAnimal("camel", "1F42A");
		addAnimal("camel", "1F42B");
		addAnimal("elephant", "1F418");
		addAnimal("mouse", "1F42D");
		addAnimal("mouse", "1F421");
		addAnimal("rat", "1F42D");
		addAnimal("rat", "1F400");
		addAnimal("hamster", "1F439");
		addAnimal("rabbit", "1F430");
		addAnimal("hare", "1F407");
		addAnimal("squirrel", "1F43F");
		addAnimal("bear", "1F43B");
		addAnimal("koala", "1F428");
		addAnimal("panda", "1F43C");
		addAnimal("turkey", "1F983");
		addAnimal("chicken", "1F414");
		addAnimal("hen", "1F414");
		addAnimal("cock", "1F413");
		addAnimal("cockerel", "1F413");
		addAnimal("rooster", "1F413");
		addAnimal("chick", "1F425");
		addAnimal("sparrow", "1F426");
		addAnimal("bluebird", "1F426");
		addAnimal("swallow", "1F426");
		addAnimal("penguin", "1F427");
		addAnimal("frog", "1F438");
		addAnimal("toad", "1F438");
		addAnimal("crocodile", "1F40A");
		addAnimal("alligator", "1F40A");
		addAnimal("turtle", "1F422");
		addAnimal("tortoise", "1F422");
		addAnimal("snake", "1F40D");
		addAnimal("serpent", "1F40D");
		addAnimal("cobra", "1F40D");
		addAnimal("viper", "1F40D");
		addAnimal("asp", "1F40D");
		addAnimal("rattler", "1F40D");
		addAnimal("rattlesnake", "1F40D");
		addAnimal("dragon", "1F432");
		addAnimal("lizard", "1F409");
		addAnimal("whale", "1F433");
		addAnimal("orca", "1F40B");
		addAnimal("dolphin", "1F42C");
		addAnimal("fish", "1F41F");
		addAnimal("trout", "1F41F");
		addAnimal("goldfish", "1F420");
		addAnimal("blowfish", "1F421");
		addAnimal("blobfish", "1F421");
		addAnimal("octopus", "1F419");
		addAnimal("crab", "1F980");
		addAnimal("slug", "1F41B");
		addAnimal("caterpiller", "1F41B");
		addAnimal("worm", "1F41B");
		addAnimal("ant", "1F41C");
		addAnimal("termite", "1F41C");
		addAnimal("snail", "1F40C");
		addAnimal("bee", "1F41D");
		addAnimal("honeybee", "1F41D");
		addAnimal("bumblebee", "1F41D");
		addAnimal("ladybird", "1F41E");
		addAnimal("spider", "1F577");
		addAnimal("black_widow", "1F577");
		addAnimal("black widow", "1F577");
		addAnimal("tarantula", "1F577");
		addAnimal("scorpion", "1F982");
		addAnimal("shrimp", "1F364");
		addAnimal("prawn", "1F364");
		addAnimal("leopard", "1F406");
		addAnimal("jaguar", "1F406");
		addAnimal("cheetah", "1F406");
		addAnimal("ox", "1F402");
		addAnimal("buffalo", "1F402");
		addAnimal("cow", "1F404");
		addAnimal("canary", "1F424");
		addAnimal("cockatoo", "1F426");
	}
	
	
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//
	//     Useful Public Routines
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//
	
	public String emojify(String tweet)
	{
		if (!hasNumberingSystem(tweet, 1, 3)) return tweet;
		
		String[] schema = (String[])numberingSchemes.elementAt(RND.nextInt(numberingSchemes.size()));
		
		if (tweet.indexOf("3. Marry") > 0)
			schema = TweetEmojifier.MARRIAGE;
		else
		if (tweet.indexOf(": \n1.") > 0 || tweet.indexOf(":\n1.") > 0)
			schema = TweetEmojifier.FACES;
		
		return insertNumberEmoji(tweet, schema, 1, 9);
	}
	
	
	
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//
	//     Private Helper Routines
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
			
			String code = emojis[i];
			
			if (tweet.indexOf("" + i + ". Marry") >= 0) 
				code = TweetEmojifier.WEDDING;
			else	
			if (tweet.indexOf("" + i + ". Get dumped") >= 0) 
				code = TweetEmojifier.POO;
			
			tweet = tweet.substring(0, pos) + getEmojiForCode(code) + tweet.substring(pos+1);
		}
		
		return tweet;
	}
	
	
	
	public static String getEmojiForCode(String code)
	{
		if (code == null) return "";
		
		int space = code.indexOf((int)' ');
		
		if (space > 0)
			return getEmojiForCode(code.substring(0, space)) + getEmojiForCode(code.substring(space+1).trim());
		else
		{
			try {
				return new String(Character.toChars(Integer.parseInt(code, 16)));
			}
			catch (Exception e)
			{
				System.out.println("Improper code: <" + code + ">");
				
				System.exit(0);
				
				return null;
			}
		}
	}
	
	
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//
	//     Maintain a symbol table with emoji symbol mapping to unicodes
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//

	public void addSymbol(String symbol, String unicode)
	{
		Vector codes = (Vector)symbols.get(symbol);
		
		if (codes == null)
		{
			codes = new Vector();
			symbols.put(symbol,  codes);
			
			allSymbols.add(symbol);
		}
		
		unicode = unicode.intern();
		
		if (codes.indexOf(unicode) < 0)
			codes.add(unicode);
	}
	
	
	public void addAnimal(String symbol, String unicode)
	{
		addSymbol(symbol, unicode);
		
		if (allAnimals.indexOf(symbol) < 0)
			allAnimals.add(symbol);
	}
	
	
	public Vector getUnicodesFor(String symbol)
	{
		return (Vector)symbols.get(symbol);
	}
	
	
	public String getRandomSymbol()
	{
		return (String)allSymbols.elementAt(RND.nextInt(allSymbols.size()));
	}
	
	
	public String getRandomAnimal()
	{
		return (String)allAnimals.elementAt(RND.nextInt(allAnimals.size()));
	}

	
	
	public String getRandomUnicodeFor(String symbol)
	{
		Vector codes = (Vector)symbols.get(symbol);
		
		if (codes == null || codes.size() == 0) return null;
		
		if (codes.size() == 1)
			return (String)codes.elementAt(0);
		
		return (String)codes.elementAt(RND.nextInt(codes.size()));
	}
	
	
	
	public String getRandomEmojiFor(String symbol)
	{
		String code = getRandomUnicodeFor(symbol);
		
		if (code == null) 
		{
			int dash = symbol.lastIndexOf((int)'_');
			
			if (dash < 0) dash = symbol.lastIndexOf((int)' ');
			
			if (dash < 0) return null;
			
			String headEmoji = getRandomEmojiFor(symbol.substring(dash+1));
			
			if (headEmoji != null)
				return symbol.substring(0, dash+1) + headEmoji;
			else
				return null;
		}
		
		return getEmojiForCode(code);
	}
	
	

	
}
