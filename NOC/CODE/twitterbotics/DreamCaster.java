package twitterbotics;

import java.util.Random;
import java.util.Vector;

import tabular.BucketTable;
import tabular.SymbolCounter;
import tabular.SymbolMap;



public class DreamCaster 
{
	static Random DICE 						 = new Random();
	
	private String knowledgeDir				 = null;   // directory where knowledge-base(s) can be found

	private KnowledgeBaseModule dreamKB      = null;
	private KnowledgeBaseModule entryActions = null;
	private KnowledgeBaseModule exitActions  = null;
	
	private BucketTable stereoModel			 = new BucketTable("stereotype concepts to their properties");
	private BucketTable stereoProperties	 = null; 
	private BucketTable opposites			 = new BucketTable("properties to their opposites"); 
	
	private BucketTable entryMap			 = new BucketTable("entry actions to stereotypes");
	private BucketTable exitMap				 = new BucketTable("exit actions to stereotypes");
	
	private BucketTable statesToProperties	 = new BucketTable("states to their defining properties");
	
	//-----------------------------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------------------------//
	//   Constructors
	//      --  Load the knowledge-base
	//-----------------------------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------------------------//

	
	public DreamCaster(String kbDirectory, String dreamDirectory)
	{
		knowledgeDir = kbDirectory;
		
		dreamKB      = 	new KnowledgeBaseModule(dreamDirectory + "Veale's Dream Symbols.txt", 0);
		entryActions = new KnowledgeBaseModule(dreamDirectory + "Veale's entry actions.txt", 0);
		exitActions  = new KnowledgeBaseModule(dreamDirectory + "Veale's exit actions.txt", 0);

		stereoModel.loadTable(kbDirectory + "stereotype model.idx");
		
		stereoProperties = stereoModel.invertTable();
		
		opposites.loadTable(kbDirectory + "ADJ opposites.idx");
		
		entryMap.loadTable(kbDirectory + "stereotype entry conditions.idx");		
		entryMap = entryMap.invertTable();
		
		exitMap.loadTable(kbDirectory + "stereotype exit conditions.idx");		
		exitMap = exitMap.invertTable();
		
		statesToProperties.loadTable(kbDirectory + "states of properties");
		
		findDreamSituations(exitActions.getAllFrames());
	}
	
	
	//-----------------------------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------------------------//
	//   Find norm pairs that have dream interpretations
	//-----------------------------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------------------------//
	
	private void findDreamSituations(Vector actions)
	{
		int counter = 0;
		
		for (int i = 0; i < actions.size(); i++)
		{
			String action   = ((String)actions.elementAt(i)).trim();
			String verb  	= action.substring(0, action.indexOf((int)':'));
			String object  	= action.substring(verb.length()+1);
			
			Vector entrySubjects = entryMap.get(action);
			Vector exitSubjects  = exitMap.get(action);
			
			Vector  objDreams 	= dreamKB.getFieldValues("Interpretation", object);
			
			Vector anchoredSubjects = new Vector();
			
			if (entrySubjects != null)
			{
				for (int s = 0; s < entrySubjects.size(); s++)
				{
					String subject      = (String)entrySubjects.elementAt(s);
					
					Vector  subjDreams 	= dreamKB.getFieldValues("Interpretation", subject);
					
					if (subjDreams == null  || subjDreams.size() == 0)  continue;
					
					if (!anchoredSubjects.contains(subject))
						anchoredSubjects.add(subject);
				}
			}
		
			if (exitSubjects != null)
			{
				for (int s = 0; s < exitSubjects.size(); s++)
				{
					String subject      = (String)exitSubjects.elementAt(s);
					
					Vector  subjDreams 	= dreamKB.getFieldValues("Interpretation", subject);
					
					if (subjDreams == null  || subjDreams.size() == 0)  continue;
					
					if (!anchoredSubjects.contains(subject))
						anchoredSubjects.add(subject);
				}
			}
		
			if ((objDreams == null  || objDreams.size() == 0) &&  anchoredSubjects.size() == 0)
				continue;
			
			System.out.print((++counter) + ". " + anchoredSubjects + " " + verb + " " + object);
			
			if (objDreams != null  && objDreams.size() > 0)
				System.out.print("*");
			
			System.out.println();
		}
	}
	
	
	
	// Find apt subjects for a given action
	
	private Vector getAptSubjects(String verb, String object)
	{
		Vector properties = (Vector)statesToProperties.get(object);
		
		if (properties == null || properties.size() == 0) return null;
		
		Vector aptSubjects = new Vector();
		
		return aptSubjects;
		
		
	}
	
	//-----------------------------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------------------------//
	//   Application Stub
	//-----------------------------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------------------------//
	
	public static void main(String[] args)
	{
		String ddir = "/Users/tonyveale/Dropbox/CC Course Helsinki/";
		String kdir = "/Users/tonyveale/Desktop/Lexical Resources/Moods/";		
		
		DreamCaster dreamer = new DreamCaster(kdir, ddir);
		
		
	}

}
