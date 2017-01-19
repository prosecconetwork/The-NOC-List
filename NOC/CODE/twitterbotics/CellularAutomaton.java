package twitterbotics;

// Implement a simple 1-dimensional mult-istate cellular automaton that changes from
// one generation (one line of neighboring cells) to the next

import java.util.Random;


public class CellularAutomaton {

	private Random RND			= new Random();
	
	private int numStates 		= 0;
	private int numGenerations 	= 0;
	private int numCells 		= 0;
	
	private int[] rules			= null;		
	private int[][] generations = null;
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Constructors
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	public CellularAutomaton(int numStates, int numGenerations, int numCells)
	{
		this.numStates = numStates;
		this.numGenerations = numGenerations;
		this.numCells = numCells;
		
		rules 		= new int[numStates*numStates*numStates];
		generations = new int[numGenerations][numCells];
		
		run();
	}
	
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Do a complete run of the automaton for all of the specified generations
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	public void run()
	{
		initializeRandomRules();
		initializeGenerationZero();
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
		for (int r = 0; r < rules.length; r++)
			rules[r] = RND.nextInt(numStates);
	}
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Initialize Generation 0 to a random set of states (one per cell)
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	private void initializeGenerationZero()
	{
		for (int c = 0; c < numCells; c++)
			generations[0][c] = RND.nextInt(numStates);
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
		return numStates;
	}
	
	public int getNumCells() {
		return numCells;
	}
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Access the cells in any generation
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	public int getGenX(int generation, int cellX)
	{
		if (cellX < 0)   // wrap around from far left to far right
			cellX = numCells + cellX;
		
		if (cellX >= numCells)   // wrap around from far right to far left
			cellX = cellX - numCells;
		
		return generations[generation][cellX];
	}
	
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Update  cells in each and every generation 
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
		
		int left = getGenX(generation-1, cellX-1), middle = getGenX(generation-1, cellX), right = getGenX(generation-1, cellX+1);
		
		int ruleNum = right + numStates*middle + numStates*numStates*left;
		
		generations[generation][cellX] = rules[ruleNum];
	}

	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Print all generations from top (gen 0) to bottom (last gen)
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	public void printGeneration(int generation)
	{
		for (int c = 0; c < numCells; c++)
			System.out.print(generations[generation][c]);
		
		System.out.println();
	}
	
	
	
	public void printGenerations()
	{
		for (int g = 0; g < numGenerations; g++)
			printGeneration(g);	
	}
	
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Return the percentage of the state with the smallest presence overall
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	
	public int getSmallestPercentage()
	{
		int[] states = new int[numStates];
		
		for (int g = 0; g < numGenerations; g++)
			for (int c = 0; c < numCells; c++)
				states[generations[g][c]]++;
		
		int max = numGenerations*numCells, min = max;
		
		for (int s = 0; s < numStates; s++)
			if (states[s] < min)
				min = states[s];
		
		return (min*100)/max;
	}
	
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Main stub for testing
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	public static void main(String[] args)
	{
		CellularAutomaton celly = new CellularAutomaton(2, 35, 50);
		
		while (celly.getSmallestPercentage() < 25)
			celly.run();
		
		celly.printGenerations();
	}
}
