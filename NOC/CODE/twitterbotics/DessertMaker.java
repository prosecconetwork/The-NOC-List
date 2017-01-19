package twitterbotics;

import java.util.Random;
import java.util.Vector;

// A Simple class that makes a new (and horribly creative) dessert on demand

public class DessertMaker 
{
	private Random RND = new Random();
	
	private KnowledgeBaseModule desserts   = null;
	private KnowledgeBaseModule ingredients = null;
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Constructors
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	public DessertMaker(String resDir)
	{
		desserts    = new KnowledgeBaseModule(resDir + "List of desserts.txt");
		ingredients = new KnowledgeBaseModule(resDir + "List of ingredients.txt");
	}
	
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Make a random dessert by making a random ingredient substitution
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	public String getRandomDessert()
	{
		return desserts.getAllFrames().elementAt(RND.nextInt(desserts.getAllFrames().size()));
	}
	
	
	public Vector<String> getIngredientsFor(String dessert)
	{
		return desserts.getFieldValues("Ingredients", dessert);
	}
	
	
	public String getRandomIngredient(String dessert)
	{
		Vector<String> elements = getIngredientsFor(dessert);
		
		if (elements == null || elements.size() == 0)
			return null;
		
		return elements.elementAt(RND.nextInt(elements.size()));
	}

	
	public String getRandomSubsitution(String ingredient)
	{
		Vector<String> candidates = ingredients.getFieldValues("Substitutions", ingredient);
		
		if (candidates == null || candidates.size() == 0)
			return null;
		
		return candidates.elementAt(RND.nextInt(candidates.size()));
	}
	
	
	public String makeDessertVariant()
	{
		String dessert    = getRandomDessert();
		String ingredient = getRandomIngredient(dessert);
		String substitute = getRandomSubsitution(ingredient);
		
		return dessert + " made with " + substitute + " instead of " + ingredient;
	}
	
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Main test stub
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	public static void main(String[] args)
	{
		String resDir     = "/Users/tonyveale/Dropbox/CodeCamp2016/NOC/DATA/TSV Lists/";

		DessertMaker chef = new DessertMaker(resDir);
		
		for (int i = 0; i < 10; i++)
			System.out.println(chef.makeDessertVariant());
	}
}
