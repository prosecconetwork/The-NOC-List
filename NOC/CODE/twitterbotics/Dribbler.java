package twitterbotics;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Random;
import java.util.Vector;

import Narrative.NLP;
import Narrative.StoryAction;
import Narrative.StoryObject;

public class Dribbler 
{
	static Random RND 						 = new Random();
	static Boolean ONLY_TO_SCREEN			 = false;
	
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	//  Simple Constructor
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	
	public Dribbler()
	{
	}
	
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	//  Manage a dribble file for recording output
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	private FileOutputStream   dribbleStream  = null;
	private OutputStreamWriter dribbleFile    = null;
	
	
	protected void openDribbleFile(String filename)
	{
		try {
			if (!ONLY_TO_SCREEN)
			{
				dribbleStream = new FileOutputStream(filename);
				dribbleFile   = new OutputStreamWriter(dribbleStream, "UTF-8");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	protected void closeDribbleFile()
	{
		try {
			if (dribbleFile != null)
			{
				dribbleFile.flush();
				dribbleFile.close();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	protected long getDribblePosition()
	{
		try {
			if (dribbleStream == null)
				return 0;
			else
				return dribbleStream.getChannel().position();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			
			return -1;
		}
	}
	
	
	protected void printlnDribbleFile(String line)
	{
		try {
			if (dribbleFile != null)
			{
				dribbleFile.write(line);
				dribbleFile.write("\n");
				dribbleFile.flush();
			}
			
			System.out.println(line);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	protected void printDribbleFile(String line)
	{
		try {
			if (dribbleFile != null)
			{
				dribbleFile.write(line);
				dribbleFile.flush();
			}
			
			System.out.print(line);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	

	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	//  Some useful text-processing / output routines
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	static protected String capitalizeFirst(StoryObject object)
	{
		return capitalizeFirst(object.toString());
	}

	
	static protected String capitalizeFirst(StoryAction action)
	{
		return capitalizeFirst(action.toString());
	}
	
	
	static protected String capitalizeFirst(String phrase)
	{
		if (phrase == null || phrase.length() == 0)
			return phrase;
		else
		if (phrase.charAt(0) == '\"')
			return "\"" + capitalizeFirst(phrase.substring(1));
		else
		if (phrase.length() > 1 && phrase.charAt(0) == 'i' && Character.isUpperCase(phrase.charAt(1)))
			return phrase;
		else
			return Character.toUpperCase(phrase.charAt(0)) + phrase.substring(1);
	}
	
	
	static protected String capitalizeEach(String phrase)
	{
		StringBuffer capped = new StringBuffer(phrase);
		
		for (int i = 0; i < phrase.length(); i++)
			if (i == 0 || phrase.charAt(i-1) == ' ' || phrase.charAt(i-1) == '_')
				capped.setCharAt(i, Character.toUpperCase(phrase.charAt(i)));
		
		return capped.toString();
	}
	
	// Do in-line string replacement
	
	static protected String replaceWith(String whole, String before, String after)
	{
		int where = whole.indexOf(before);
		
		while (where >= 0)
		{
			whole = whole.substring(0, where) + after + whole.substring(where + before.length());
			
			where = whole.indexOf(before, where + after.length());
		}
		
		return whole;
	}
	
	
	
	static protected Vector randomize(Vector input)
	{
		if (input == null || input.size() < 2)
			return input;
				
		for (int i = 0; i < input.size(); i++)
		{
			int pos1 = RND.nextInt(input.size()), pos2 = RND.nextInt(input.size());
			
			Object obj1 = input.elementAt(pos1), obj2 = input.elementAt(pos2);
			
			input.setElementAt(obj2, pos1);
			input.setElementAt(obj1, pos2);
		}
		
		return input;
	}
	
	
	static protected Object[] randomize(Object[] input)
	{
		if (input == null || input.length < 2)
			return input;
				
		for (int i = 0; i < input.length; i++)
		{
			int pos1 = RND.nextInt(input.length), pos2 = RND.nextInt(input.length);
			
			Object obj1 = input[pos1], obj2 = input[pos2];
			
			input[pos1] = obj2;
			input[pos2] = obj1;
		}
		
		return input;
	}
}
