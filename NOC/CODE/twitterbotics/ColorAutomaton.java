package twitterbotics;

// Implement a simple 1-dimensional multistate cellular automaton that changes from
// one generation (one line of neighboring cells) to the next

// Rather than denote states with numbers (e.g. 0, 1, etc.) that will later be mapped to colors,
// this class represents them directly as RGB colors

import java.util.Random;
import java.util.Vector;
import java.awt.Color;

import tabular.SymbolCounter;
import tabular.SymbolMap;


public class ColorAutomaton {

	public static final int SEARCH_HORIZON 	= 100000;
	
	private Random RND						= new Random();
	
	private Color[] states					= null;
	
	private int numOriginalStates			= 0;
	
	private int numGenerations 				= 0;
	private int numCells 					= 0;
	
	private Color[] rules					= null;		
	private Color[][] generations 			= null;
	
	private int[] ruleUsage					= null;
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Constructors
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	public ColorAutomaton(Color[] states, int numGenerations, int numCells)
	{
		this.states = states;
		this.numGenerations = numGenerations;
		this.numCells = numCells;
		
		this.numOriginalStates = states.length;
		
		rules 		= new Color[states.length*states.length*states.length];
		ruleUsage	= new int[states.length*states.length*states.length];
		generations = new Color[numGenerations][numCells];
		
		run();
	}
	
	
	private ColorAutomaton(Color[] states, int numGenerations, int numCells, Color[][] region)
	{
		this.states = states;
		this.numGenerations = numGenerations;
		this.numCells = numCells;
		this.generations = region;
		this.numOriginalStates = states.length;
	}
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Do a complete run of the automaton for all of the specified generations
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	
	// Use the full rule-set
	
	public void run()
	{
		initializeRandomRules();
		initializeGenerationZero();
		updateAllGenerations();
	}
	
	
	public void rerun()
	{
		initializeGenerationZero();
		updateAllGenerations();
	}
	
	
	public void run(Color[] startStates)
	{
		initializeRandomRules();
		initializeGenerationZero(startStates);
		updateAllGenerations();
	}
	
	
	public void run(Color[] ruleSet, Color[] startStates)
	{
		this.rules = ruleSet;
		
		initializeGenerationZero(startStates);
		updateAllGenerations();
	}
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Create Random rules
	// Each rule maps a triple of left-neighbor+cell+right-neighbor onto a state
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	
	private void initializeRandomRules()
	{
		for (int r = 0; r < rules.length; r++) {
			rules[r]     = states[RND.nextInt(states.length)];
			ruleUsage[r] = 0;
		}
	}
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Initialize Generation 0 to a random set of states (one per cell)
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	private void initializeGenerationZero()
	{
		for (int c = 0; c < numCells; c++)
			generations[0][c] = states[RND.nextInt(states.length)];
	}
	
	
	private void initializeGenerationZero(Color[] row0)
	{
		for (int c = 0; c < numCells; c++)
			if (c < row0.length)
				generations[0][c] = row0[c];
			else
				generations[0][c] = states[RND.nextInt(states.length)];
	}

	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Access methods
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	public int getNumGenerations() {
		return numGenerations;
	}
	
	public int getNumStates() {
		return states.length;
	}
	
	public int getNumCells() {
		return numCells;
	}
	
	public int getStateNumber(Color state)
	{
		for (int i = 0; i < states.length; i++)
			if (states[i] == state)
				return i;
		
		return -1;
	}
	
	
	public Color getStateNumbered(int stateNum)
	{
		return states[stateNum];
	}
	
	
	
	public Color[] getStates() 
	{
		return states;
	}
	
	
	public Color getStateNotIn(Color[] others)
	{
		int offset = RND.nextInt(states.length);
		 
		for (int s = 0; s < states.length; s++) 
		{
			boolean seen = false;
			
			Color candidate = states[(s + offset)%states.length];
			
			for (int o = 0; o < others.length; o++)
				if (others[o] == candidate)
					seen = true;
			
			if (!seen) return candidate;
		}
		
		return null;
	}
	
	
	public Color getStateThatIsNot(Color avoid)
	{
		for (int s = 0; s < states.length; s++) 
			if (states[s] != avoid)
				return states[s];
		
		return null;
	}
	
	

	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Add a new state to the automaton
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	public void addState(Color newState)
	{
		Color[] augmented = new Color[states.length+1];
		
		for (int s = 0; s < states.length; s++)
			augmented[s] = states[s];
		
		augmented[states.length] = newState;
		
		states = augmented;
	}
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Get specific portions of the cell array
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	
	public Color[] getGeneration(int gen)
	{
		return generations[gen];
	}
	
	
	public Color[] getCellColumn(int cellX)
	{
		Color[] column = new Color[numGenerations];
		
		for (int g = 0; g < numGenerations; g++)
			column[g] = generations[g][cellX];
		
		return column;
	}
	
	
	public Color[] getRuleSet()
	{
		return rules;
	}
	
	
	public Color[][] getAllGenerations()
	{
		return generations;
	}
	
	
	public Color[][] getRegionOfCells(int x1, int y1, int x2, int y2)
	{
		if (x1 > x2) return getRegionOfCells(x2, y1, x1, y2);
		if (y1 > y2) return getRegionOfCells(x1, y2, x2, y1);
		
		Color[][] region = new Color[Math.abs(y2-y1)+1][Math.abs(x2-x1)+1];
		
		for (int x = x1; x <= x2; x++)
			for (int y = y1; y <= y2; y++)
				region[y-y1][x-x1] = getGenX(y, x);
		
		return region;
	}
	
	
	public ColorAutomaton getSubset(int x1, int y1, int x2, int y2)
	{
		if (x1 > x2) return getSubset(x2, y1, x1, y2);
		if (y1 > y2) return getSubset(x1, y2, x2, y1);
		
		int height = Math.abs(y2-y1)+1, width = Math.abs(x2-x1)+1;
		
		Color[][] region = new Color[height][width];
		
		for (int x = x1; x <= x2; x++)
			for (int y = y1; y <= y2; y++)
				region[y-y1][x-x1] = getGenX(y, x);
		
		return new ColorAutomaton(getStates(), height, width, region);
	}
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Access the cells in any generation
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	public Color getGenX(int generation, int cellX)
	{
		while (cellX < 0)   // wrap around from far left to far right
			cellX = numCells + cellX;
		
		while (generation < 0)   // wrap around from above top to bottom
			generation = numGenerations + generation;
		
		if (cellX >= numCells)   // wrap around from far right to far left
			cellX = numCells - cellX % numCells - 1;
		
		if (generation >= numGenerations)   // wrap around from far right to far left
			generation = numGenerations - generation % numGenerations - 1;
		
		return generations[generation][cellX];
	}
	
	
	
	public void setGenX(int generation, int cellX, Color value)
	{
		while (cellX < 0)   // wrap around from far left to far right
			cellX = numCells + cellX;
		
		while (generation < 0)   // wrap around from above top to bottom
			generation = numGenerations + generation;
		
		if (cellX >= numCells)   // wrap around from far right to far left
			cellX = numCells - cellX % numCells - 1;
		
		if (generation >= numGenerations)   // wrap around from far right to far left
			generation = numGenerations - generation % numGenerations - 1;
		
		generations[generation][cellX] = value;
	}
	
	
	
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Update  cells in each and every generation using the rule set
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	
	private void updateAllGenerations()
	{
		for (int g = 1; g < numGenerations; g++)
			updateGeneration(g);
	}
	
	private void updateGeneration(int generation)
	{
		for (int c = 0; c < numCells; c++)
			updateCell(generation, c);
	}
	
	
	// Update the cell in generation g > 0
	
	private void updateCell(int generation, int cellX)
	{
		if (generation < 0 || generation >= numGenerations)
			return;
		
		Color left = getGenX(generation-1, cellX-1), middle = getGenX(generation-1, cellX), right = getGenX(generation-1, cellX+1);
		
		int ruleNum = getStateNumber(right) + states.length*getStateNumber(middle) + states.length*states.length*getStateNumber(left);
		
		ruleUsage[ruleNum]++;
		
		generations[generation][cellX] = rules[ruleNum];
	}

	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Return the most commonly used rule
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	public int mostPopularRuleNumber()
	{
		int best = 0, usage = 0;
		
		for (int r = 0; r < ruleUsage.length; r++) 
			if (ruleUsage[r] > usage) {
				best  = r;
				usage = ruleUsage[r];
			}
						
		return best;
	}
	
	
	public Vector getOrderedRules()
	{
		Vector ordered = new Vector();
		
		for (int r = 0; r < ruleUsage.length; r++) 
			if (ruleUsage[r] > 0)
				ordered.add(new SymbolCounter(getNameForRule(r), getPercentageForRule(r)));
			
		return SymbolMap.getSorted(ordered);
	}
	
	
	public int getPercentageForRule(int ruleNum)
	{
		int sum = 0;
		
		for (int r = 0; r < ruleUsage.length; r++)
			sum += ruleUsage[r];
		
		if (sum == 0) return 0;
		
		return (100*ruleUsage[ruleNum])/sum;
	}
	
	
	public String mostPopularRuleName()
	{
		return getNameForRule(mostPopularRuleNumber());
	}
	
	
	
	public String getNameForRule(int ruleNum)
	{
		for (int l = 0; l < states.length; l++)
			for (int m = 0; m < states.length; m++)
				for (int r = 0; r < states.length; r++)
					if (ruleNum == r + m*states.length + l*states.length*states.length)
						return "" + l + "" + m + "" + r + "" + getStateNumber(rules[ruleNum]);
		
		return null;
	}
	
	
	public String describeMostPopularRule()
	{
		int ruleNum     = mostPopularRuleNumber();
		String ruleName = getNameForRule(ruleNum);
		
		if (ruleName == null) return null;
		
		return ruleName + " (" + getPercentageForRule(ruleNum) + "%)";
	}
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Print all generations from top (gen 0) to bottom (last gen)
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	
	public void printGeneration(int generation)
	{
		for (int c = 0; c < numCells; c++)
		{
			int num = getStateNumber(generations[generation][c]);
			
			if (num < 0)
				System.out.print("*");
			else
			if (num < 10)
				System.out.print(num);
			else
			if (num < 36)
				System.out.print((char)('A' + num-10));
			else
				System.out.println("?");
		}
			
		
		System.out.println();
	}
	
	
	
	public void printGenerations()
	{
		for (int g = 0; g < numGenerations; g++)
			printGeneration(g);	
	}
	
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Return the percentage of the state with the greatest/smallest presence 
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	
	public int getSmallestColorPercentage()
	{
		int[] percentages = new int[states.length];
		
		for (int g = 0; g < numGenerations; g++)
			for (int c = 0; c < numCells; c++)
				percentages[getStateNumber(generations[g][c])]++;
		
		int max = numGenerations*numCells, min = max;
		
		for (int s = 0; s < states.length; s++)
			if (percentages[s] < min)
				min = percentages[s];
		
		return (min*100)/max;
	}
	
	
	public int getSmallestColorPercentage(int column)
	{
		int[] percentages = new int[states.length];
		
		for (int g = 0; g < numGenerations; g++)
			percentages[getStateNumber(generations[g][column])]++;
		
		int max = numGenerations, min = max;
		
		for (int s = 0; s < states.length; s++)
			if (percentages[s] < min)
				min = percentages[s];
		
		return (min*100)/max;
	}
	
	
	
	public Color getDominantColor()
	{
		int[] percentages = new int[states.length];
		
		for (int g = 0; g < numGenerations; g++)
			for (int c = 0; c < numCells; c++)
				percentages[getStateNumber(generations[g][c])]++;
		
		Color dominant = states[0];
		
		int best = 0;
		
		for (int s = 0; s < states.length; s++)
			if (percentages[s] > best) {
				best = percentages[s];
				dominant = states[s];
			}
		
		return dominant;
	}
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Double the resolution of the Automaton by horizontal and vertical mirroring
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	public void mirrorResolution()
	{		
		Color[][] doubled = new Color[numGenerations*2][numCells*2];
		
		for (int g = 0; g < numGenerations; g++)
		{
			for (int c = 0; c < numCells; c++)
			{
				doubled[g][c] =  generations[g][c]; // top left
				doubled[numGenerations*2-g-1][c] =  generations[g][c]; // bottom left
				doubled[g][numCells*2-c-1] =  generations[g][c]; // top right
				doubled[numGenerations*2-g-1][numCells*2-c-1] =  generations[g][c]; // bottom right
			}
		}
		
		numCells = numCells*2;
		numGenerations = numGenerations*2;
		
		generations = doubled;	
	}
	
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Double the resolution of the Automaton
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	public void doubleResolutionUntil(int genCount, int cellCount)
	{
		while (numGenerations < genCount && numCells < cellCount)
			doubleResolution();
	}
	
	
	public void doubleResolution()
	{
		Color[][] doubled = new Color[numGenerations*2][numCells*2];
		
		for (int g = 0; g < numGenerations; g++)
		{
			for (int c = 0; c < numCells; c++)
			{
				if (doubled[g*2][c*2] == null)
					doubled[g*2][c*2]         = generations[g][c];
				
				if (doubled[g*2 + 1][c*2] == null)
					doubled[g*2 + 1][c*2]     = generations[g][c];
				
				if (doubled[g*2][c*2 + 1] == null)
					doubled[g*2][c*2 + 1]     = generations[g][c];
				
				if (doubled[g*2 + 1][c*2 + 1] == null)
					doubled[g*2 + 1][c*2 + 1] = generations[g][c];
				
				if (g > 0 && c < numCells-1 && generations[g][c] == generations[g-1][c+1]) // left-leaning diagonal 
				{
					doubled[g*2][c*2 + 2] = generations[g][c];      // x
					doubled[g*2 - 1][c*2 + 1] = generations[g][c];  // y
					
				}
								
				if (g > 0 && c > 0 && generations[g][c] == generations[g-1][c-1]) // right-leaning diagonal
				{
					doubled[g*2][c*2 - 1] = generations[g][c]; // b
					doubled[g*2-1][c*2] = generations[g][c];					
				}
			}

			/* Insert smaller copy of "L" in the cells marked "a, b, x, y'
			  LL         LL
			 xLL         LLa
			LLy           bLL
			LL             LL
			*/
		}
		
		numCells = numCells*2;
		numGenerations = numGenerations*2;
		
		generations = doubled;
	}
	
	
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Return the average state/color density of an image
	// What is the probability that one cell neighbors another in the same state?
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	public int getStateDensity()
	{
		int sum = 0, count = 0;
		
		for (int g = 1; g < numGenerations-1; g++)
		{
			for (int c = 1; c < numCells-1; c++)
			{
				count++;
				
				if (generations[g][c] == generations[g][c-1]) // left
					sum++;
				
				if (generations[g][c] == generations[g][c+1]) // right
					sum++;
				
				if (generations[g][c] == generations[g-1][c]) // upper
					sum++;

				if (generations[g][c] == generations[g+1][c]) // lower
					sum++;

			}
		}
		
		return (sum*100)/(count*4);
	
	}
	
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Perform edging of colour areas with darker or brighter fringes
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	public void raiseColors(Color[] fringe, int numTimes)
	{
		for (int t = 0; t < numTimes; t++)
			raiseColors(fringe);
	}
	
	
	public void raiseColors(Color[] fringe)
	{
		Color[][] newGenerate = new Color[numGenerations][numCells];
		
		for (int g = 0; g < numGenerations; g++)
		{
			for (int c = 0; c < numCells; c++)
			{	
				if (g > 1 && !areEquivColors(generations[g][c], generations[g-1][c]) && areEquivColors(generations[g-1][c], generations[g-2][c]) &&
						getStateNumber(generations[g-1][c]) >= 0)
					newGenerate[g-1][c] = fringe[getStateNumber(generations[g-1][c])];
				else
				if (g < numGenerations - 2 && !areEquivColors(generations[g][c], generations[g+1][c]) 
						&& areEquivColors(generations[g+1][c], generations[g+2][c]) &&
						getStateNumber(generations[g+1][c]) >= 0)
					newGenerate[g+1][c] = fringe[getStateNumber(generations[g+1][c])];
				else
				if (c < numCells - 2 && !areEquivColors(generations[g][c], generations[g][c+1])
						&& areEquivColors(generations[g][c+1], generations[g][c+2]) &&
						getStateNumber(generations[g][c+1]) >= 0)
					newGenerate[g][c+1] = fringe[getStateNumber(generations[g][c+1])];
				else
				if (c > 1 && !areEquivColors(generations[g][c], generations[g][c-1]) 
						&& areEquivColors(generations[g][c-1], generations[g][c-2]) &&
						getStateNumber(generations[g][c-1]) >= 0)
					newGenerate[g][c-1] = fringe[getStateNumber(generations[g][c-1])];
				
				if (newGenerate[g][c] == null) 
					newGenerate[g][c] = generations[g][c];				
			}
		}
		
		generations = newGenerate;
	}
	    
	
	private boolean areEquivColors(Color color1, Color color2)
	{
		if (color1 == color2) return true;
		
		if (getStateNumber(color1) >= numOriginalStates == getStateNumber(color2) >= numOriginalStates)
			return false;
		
		int dist = (int)Math.sqrt((color1.getRed()-color2.getRed())*((color1.getRed()-color2.getRed())) + 
				   (color1.getGreen()-color2.getGreen())*((color1.getGreen()-color2.getGreen())) +
				   (color1.getBlue()-color2.getBlue())*((color1.getBlue()-color2.getBlue())));
		
		return dist < 50;	
	}
	
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Perform smoothing of color cells to achieve anti-aliasing of diagonals
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	public void smoothColors(int numTimes)
	{
		for (int t = 0; t < numTimes; t++)
			smoothColors();
	}

	
	public void smoothColors()
	{
		Color[][] newGenerate = new Color[numGenerations+2][numCells];
		
		newGenerate[0] = generations[0];
		newGenerate[numGenerations+1] = generations[generations.length-1];
		
		for (int g = 0; g < numGenerations; g++)
			newGenerate[g+1] = generations[g];
		
		for (int g = 1; g < numGenerations+1; g++)
		{
			newGenerate[g][0] = new Color((newGenerate[g][0].getRed()*4 + newGenerate[g-1][0].getRed() + newGenerate[g+1][0].getRed() +
					   					   newGenerate[g][1].getRed() + newGenerate[g+1][1].getRed() + newGenerate[g-1][1].getRed())/9,
					   					  (newGenerate[g][0].getGreen()*4 + newGenerate[g-1][0].getGreen() + newGenerate[g+1][0].getGreen() +
							   			   newGenerate[g][1].getGreen() + newGenerate[g+1][1].getGreen() + newGenerate[g-1][1].getGreen())/9,
							   			  (newGenerate[g][0].getBlue()*4 + newGenerate[g-1][0].getBlue() + newGenerate[g+1][0].getBlue() +
									   	   newGenerate[g][1].getBlue() + newGenerate[g+1][1].getBlue() + newGenerate[g-1][1].getBlue())/9);
			
			newGenerate[g][numCells-1] = new Color((newGenerate[g][numCells-1].getRed()*4 + newGenerate[g-1][numCells-1].getRed() + 
												    newGenerate[g+1][numCells-1].getRed() + newGenerate[g-1][numCells-2].getRed() + 
												    newGenerate[g][numCells-2].getRed() + newGenerate[g+1][numCells-2].getRed())/9,
												  (newGenerate[g][numCells-1].getGreen()*4 + newGenerate[g-1][numCells-1].getGreen() + 
												   newGenerate[g+1][numCells-1].getGreen() + newGenerate[g-1][numCells-2].getGreen() + 
									   			   newGenerate[g][numCells-2].getGreen() + newGenerate[g+1][numCells-2].getGreen())/9,
									   			  (newGenerate[g][numCells-1].getBlue()*4 + newGenerate[g-1][numCells-1].getBlue() + 
									   			   newGenerate[g+1][numCells-1].getBlue() + newGenerate[g-1][numCells-2].getBlue() + 
											   	   newGenerate[g][numCells-2].getBlue() + newGenerate[g+1][numCells-2].getBlue())/9);
			
			for (int c = 1; c < numCells-1; c++)
			{
				newGenerate[g][c] = new Color((newGenerate[g][c].getRed()*4 + newGenerate[g-1][c].getRed() + newGenerate[g+1][c].getRed() +
											   newGenerate[g][c-1].getRed() + newGenerate[g][c+1].getRed() + newGenerate[g+1][c+1].getRed() + 
											   newGenerate[g+1][c-1].getRed() + newGenerate[g-1][c-1].getRed() + newGenerate[g-1][c+1].getRed())/12,
											   (newGenerate[g][c].getGreen()*4 + newGenerate[g-1][c].getGreen() + newGenerate[g+1][c].getGreen() +
											    newGenerate[g][c-1].getGreen() + newGenerate[g][c+1].getGreen() + newGenerate[g+1][c+1].getGreen() + 
											    newGenerate[g+1][c-1].getGreen() + newGenerate[g-1][c-1].getGreen() + newGenerate[g-1][c+1].getGreen())/12,
											   (newGenerate[g][c].getBlue()*4 + newGenerate[g-1][c].getBlue() + newGenerate[g+1][c].getBlue() +
											    newGenerate[g][c-1].getBlue() + newGenerate[g][c+1].getBlue() + newGenerate[g+1][c+1].getBlue() + 
											    newGenerate[g+1][c-1].getBlue() + newGenerate[g-1][c-1].getBlue() + generations[g-1][c+1].getBlue())/12);
				
			}
		}
		
		for (int g = 0; g < numGenerations; g++)
			generations[g] = newGenerate[g+1];
	}
	
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Search the space of automata and starting configurations to find a
	// combinatipn of both that satisfies some high-level aesthetic constraints
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	
	public boolean searchForGoodConfiguration()
	{
		return searchForGoodConfiguration(null);	
	}
	
			
	
	public boolean searchForGoodConfiguration(Color colorPreference)
	{
		int minColorPercent = 100/(getNumStates()) - getNumStates();
    	int minColorDensity = 100/(getNumStates());
    	int bestRulePercent = 100/(getNumStates()+2);
    		
    	int attempt = 0;
    	
    	while (getSmallestColorPercentage() < minColorPercent ||
    		   getStateDensity() < minColorDensity || 	
    		   (colorPreference != null && attempt < SEARCH_HORIZON/2 && getDominantColor() != colorPreference) || 
    		   getPercentageForRule(mostPopularRuleNumber()) < bestRulePercent)
    	{
    		attempt++;
    		
    		if (attempt > SEARCH_HORIZON) return false;
    		
    		if (attempt % 1000 == 0)
    			System.out.println(attempt + ". " + getSmallestColorPercentage() + "%, " + getStateDensity()+ "%, " + 
    										    	getOrderedRules().firstElement());
        	
    		if (attempt % 100 == 0)
    			run();
    		else
    			rerun();
    		
    		
    	}
    	
    	return true;
	}
	
	
	public int getPercentageOfHole()
	{
		int count = 0;
		
		for (int g = numGenerations-2; g >= 0; g--)
			if (generations[g][numCells-1] == generations[numGenerations-1][numCells-1])
				count++;
			else
				break;
		
		return (count*100)/numGenerations;
	}
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Main stub for testing
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	public static void main(String[] args)
	{
		Color[] states = {Color.blue, Color.red};
		
		ColorAutomaton celly = new ColorAutomaton(states, 35, 50);
		
		while (celly.getSmallestColorPercentage() < 25)
			celly.run();
		
		celly.printGenerations();
		
		celly.doubleResolution();
		
		celly.printGenerations();
	}
}
