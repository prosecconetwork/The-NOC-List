package tabular;

import java.util.Vector;



public class AnalogicalMapping extends SymbolMap
{
	private BucketTable slots 			= new BucketTable("slot to fillers");
	
	private SymbolMap kb     			= null;
	private SymbolMap coordinates 			= null;
	private BucketTable referenceSet		= null;
	
	private String currentSrc			= null;
	
	
	//-----------------------------------------------------------------//
	//-----------------------------------------------------------------//
	//      Constructors
	//-----------------------------------------------------------------//
	//-----------------------------------------------------------------//
	
	public AnalogicalMapping(SymbolMap kb, SymbolMap coordinates)
	{
		super("analogical mapping");
		
		this.kb 		 = kb;
		this.coordinates = coordinates;
	}
	
	
	public AnalogicalMapping(String kbname, String coordname)
	{
		super("analogical mapping");
		
		this.kb 		 = new SymbolMap("terms to their content");
		this.coordinates = new SymbolMap("terms to coordinated terms");
		
		System.out.print("Loading knowledge-base ...");
		
		this.kb.loadMap(kbname);
		
		System.out.print("  Loaded.  \nLoading coordinates ...");
		
		this.coordinates.loadMap(coordname);
		
		System.out.println(" Loaded.\n");
	}

	
	public void setReferenceSet(BucketTable refset)
	{
		referenceSet = refset;
	}
	
	
	//-----------------------------------------------------------------//
	//-----------------------------------------------------------------//
	//     Find similes within a given similarity threshold
	//-----------------------------------------------------------------//
	//-----------------------------------------------------------------//

	public void findSimilesWithinLimits(int minSim)
	{
		Vector srcs = kb.getKeyList(), tgts = null, overlap = null;
		
		String src = null, from = null, to = null;
		
		if (srcs == null) return;
		
		SymbolCounter tgt = null, common = null;
		
		for (int i = 0; i < srcs.size(); i++)
		{
			src = (String)srcs.elementAt(i);
			
			tgts = coordinates.get(src);
			
			if (tgts == null) continue;
			
			for (int j = 0; j < tgts.size(); j++)
			{
				tgt = (SymbolCounter)tgts.elementAt(j);
				
				if (tgt.value() < minSim) break;
				
				overlap = kb.getOverlappingValues(src, tgt.getSymbol());
				
				if (overlap != null && overlap.size() > 0)
				{
					from = src + ":" + tgt.getSymbol();
					to   = tgt.getSymbol() + ":" + src;
					
					for (int k = 0; k < overlap.size(); k++)
					{
						common = (SymbolCounter)overlap.elementAt(k);
						
						put(from, "share:" + common.getSymbol(), common.value());
						put(to, "share:" + common.getSymbol(), common.value());
					}
					
					System.out.println(getKeyList().size()/2 + ". " + src + " : " + tgt + "   share  " + overlap);
				}
			}
		}
	}
	
	
	//-----------------------------------------------------------------//
	//-----------------------------------------------------------------//
	//     Find similes within a given similarity threshold
	//-----------------------------------------------------------------//
	//-----------------------------------------------------------------//

	public void findAnalogiesWithinLimits(int minSubjSim, int minObjSim)
	{
		Vector srcs = kb.getKeyList(), targets = null, tgtContent = null;
		
		String src = null;
		
		if (srcs == null) return;
		
		SymbolCounter tgt = null, relation = null;
		
		for (int i = 0; i < srcs.size(); i++)
		{
			src = (String)srcs.elementAt(i);
			
			targets = coordinates.get(src);
			
			if (targets == null) continue;
			
			setSource(src);
			
			for (int j = 0; j < targets.size(); j++)
			{
				tgt = (SymbolCounter)targets.elementAt(j);
				
				if (tgt.value() < minSubjSim) break;
				
				tgtContent = kb.get(tgt.getSymbol());
				
				if (tgtContent == null) continue;
				
				for (int k = 0; k < tgtContent.size(); k++)
				{
					relation = (SymbolCounter)tgtContent.elementAt(k);
					
					findAnalogiesFor(src, tgt.getSymbol(), relation.getSymbol(), minObjSim);
				}
			}
		}
	}
	
	
	private void findAnalogiesFor(String src, String tgt, String tgtRelation, int minSim)
	{
		int colon = tgtRelation.indexOf((int)':'), sim = 0;
		
		if (colon <= 0) return;
		
		String pred      = tgtRelation.substring(0, colon);
		String tgtObject = tgtRelation.substring(colon+1);
		String srcObject = null;
		
		String from      = null;
		String to		 = null;
		
		Vector srcObjects = slots.get(pred);
		
		if (srcObjects == null) return;
		
		for (int i = 0; i < srcObjects.size(); i++)
		{
			srcObject = (String)srcObjects.elementAt(i);
			
			if (srcObject.equals(tgt)) continue;
			if (tgtObject.equals(src)) continue;
			
			if (!referenceSet.contains(src, srcObject) && !referenceSet.contains(srcObject, src))
				continue;
			
			if (!referenceSet.contains(tgt, tgtObject) && !referenceSet.contains(tgtObject, tgt))
				continue;

			sim = coordinates.getCount(srcObject, tgtObject);
			
			if (sim < minSim)
				continue;
			
			if (from == null)  from = src + ":" + tgt;
			if (to == null)      to = tgt + ":" + src;
			
			put(from,  "map:" + pred + ":" + srcObject + ":" + tgtObject, sim);
			put(to,    "map:" + pred + ":" + tgtObject + ":" + srcObject, sim);
			
			System.out.println(getKeyList().size()/2 + ". " + src + " : " + tgt + "   map  " 
									+ pred + ":" + srcObject + "  to   " + pred + ":" + tgtObject + "  (" + sim + ")");
		}
	}

	//-----------------------------------------------------------------//
	//-----------------------------------------------------------------//
	//      Useful methods
	//-----------------------------------------------------------------//
	//-----------------------------------------------------------------//

	public void setSource(String term)
	{
		if (currentSrc == null || !currentSrc.equals(term))
		{
			if (currentSrc != null)
				clearSlots();
			
			setSlotsOf(term);
		}
		
		currentSrc = term;
	}
	
	
	public void setSlotsOf(String term)
	{
		Vector relations = kb.get(term);
		
		if (relations == null) return;
		
		String rel = null, pred = null, object = null;
		
		int colon = 0;
		
		for (int i = 0; i < relations.size(); i++)
		{
			rel = ((SymbolCounter)relations.elementAt(i)).getSymbol();
			
			colon = rel.indexOf((int)':');
			
			if (colon <= 0) continue;
			
			pred   = rel.substring(0, colon);
			object = rel.substring(colon+1);
			
			slots.put(pred, object);
		}
	}
	
	
	public void clearSlots()
	{
		Vector slotnames = slots.getKeyList(), values = null;
		
		if (slotnames == null) return;
		
		for (int i  = 0; i < slotnames.size(); i++)
		{
			values = slots.get((String)slotnames.elementAt(i));
			
			if (values != null)
				values.setSize(0);
		}
	}
	
	
	//-----------------------------------------------------------------//
	//-----------------------------------------------------------------//
	//      Main application stub
	//-----------------------------------------------------------------//
	//-----------------------------------------------------------------//

	public static void main(String[] args)
	{
		
	}
}
