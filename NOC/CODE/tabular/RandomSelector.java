package tabular;

import java.util.Random;



// Randomly select one of a given selection set

public class RandomSelector 
{
	private Random dice =  new Random(System.currentTimeMillis());
	
	
	//------------------------------------------------------------//
	//------------------------------------------------------------//
	//   Constructors
	//------------------------------------------------------------//
	//------------------------------------------------------------//

	// empty constructor
	
	public RandomSelector() {}
	
	
	// Select one of two choices
	
	public Object either(Object choice1, Object choice2)
	{
		if (dice.nextInt() % 2 == 0)
			return choice1;
		else
			return choice2;
	}
	
	
	//------------------------------------------------------------//
	//------------------------------------------------------------//
	//   Main method, with simple demo
	//------------------------------------------------------------//
	//------------------------------------------------------------//
	
	public static void main(String[] args)
	{
		RandomSelector choose = new RandomSelector();
		
		int numTimesHasBoy = 0, numTimesTwoBoys = 0, numTimesBoySeen = 0;
		
		while (numTimesHasBoy < 1000)
		{
			String child1    = (String)choose.either("boy", "girl"),
				   child2    = (String)choose.either("boy", "girl");
			
			if (child1 == child2)
			{
				child1 = child1 + "1";
				child2 = child2 + "2";
			}
			
			String seenChild = (String)choose.either(child1, child2);
			
			boolean hasBoy = child1.startsWith("boy") || child2.startsWith("boy");
			
			if (hasBoy)
			{
				numTimesHasBoy++;
				
				if (seenChild.startsWith("boy"))
				{
					numTimesBoySeen++;
					
					if (child1.charAt(0) == child2.charAt(0)) // both boys
						numTimesTwoBoys++;
					
					if (seenChild == child1)
						System.out.println(numTimesBoySeen + ". see " + child1 + ", other " + child2 
													+ " " + (1000*numTimesTwoBoys)/numTimesBoySeen);	
					else
						System.out.println(numTimesBoySeen + ". see " + child2 + ", other " + child1 
											+ " " + (1000*numTimesTwoBoys)/numTimesBoySeen);
				}
			}
		}
	}
}
