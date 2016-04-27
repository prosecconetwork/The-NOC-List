package twitterbotics;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import Categorizer.WordTable;

// A class to hold a particular module of a knowledge-base of triples
// T. Veale, 2015


public class KnowledgeBaseModule 
{
	private Hashtable kb = new Hashtable();
	
	private Vector fieldNames  = new Vector();
	private Vector fieldTables = new Vector();
	
	//-----------------------------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------------------------//
	//   Constructors
	//-----------------------------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------------------------//
	
	public KnowledgeBaseModule(String filename, int keyPosition)
	{
		loadKnowledgeBaseFrom(filename, keyPosition);
	}
	
	public KnowledgeBaseModule(String filename)
	{
		loadKnowledgeBaseFrom(filename, 0);
	}
	
	//-----------------------------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------------------------//
	//  Accessors
	//-----------------------------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------------------------//

	// Get the list of fields that describe concepts (keys) in this knowledge-base module
	
	public Vector getFieldNames()
	{
		return fieldNames;
	}

	// Get the values associated with a specific field of a key concept
	
	public Vector getFieldValues(String fieldName, String key)
	{
		for (int f = 0; f < fieldNames.size(); f++)
		{
			if (fieldName.equals(fieldNames.elementAt(f)))
			{
				Hashtable field = (Hashtable)fieldTables.elementAt(f);
				
				return (Vector)field.get(key);
			}
		}
		
		return null;
	}
	
	
	public String getFirstValue(String fieldName, String key)
	{
		Vector values = getFieldValues(fieldName, key);
		
		if (values == null || values.size() < 1)
			return null;
		else
			return (String)values.elementAt(0);
	}
	
	
	// Check whether a key concept has a given value for a given field
	
	public boolean hasFieldValue(String fieldName, String key, String value)
	{
		for (int f = 0; f < fieldNames.size(); f++)
		{
			if (fieldName.equals(fieldNames.elementAt(f)))
			{
				Hashtable field = (Hashtable)fieldTables.elementAt(f);
				
				Vector values = (Vector)field.get(key);
				
				if (values == null || values.size() == 0)
					return false;
				
				for (int v = 0; v < values.size(); v++)
					if (value.equals(values.elementAt(v)))
						return true;		
			}
		}
		
		return false;
	}
	
	// Return a list of all the key concepts that have fields/values in this knowledge module
	
	public Vector getKeyConcepts()
	{
		Vector longest = null;
		
		for (int f = 0; f < fieldTables.size(); f++)
		{
			Hashtable field = (Hashtable)fieldTables.elementAt(f);
			
			Vector list =  (Vector)field.get("*keylist*");
			
			if (list != null && (longest == null || list.size() > longest.size()))
				longest = list;
		}
		
		return longest;
	}
		
	// return a list of key concepts with a given value in a given field
	
	public Vector getAllKeysWithFieldValue(String fieldname, String value)
	{
		Vector matchingKeys = new Vector();

		if (value == null) 
			return matchingKeys;
		else
			value = value.intern();
		
		for (int f = 0; f < fieldTables.size(); f++)
		{
			String name = (String)fieldNames.elementAt(f);
			
			if (!name.equals(fieldname)) continue;
		
			Hashtable field = (Hashtable)fieldTables.elementAt(f);
			
			Vector keys =  (Vector)field.get("*keylist*");
			
			if (keys == null) break;
			
			for (int k = 0; k < keys.size(); k++)
			{
				String key = (String)keys.elementAt(k);
				
				Vector values = (Vector)field.get(key);
				
				if (values != null && values.contains(value))
					matchingKeys.add(key);
			}
		}
		
		return matchingKeys;
	}
	
	
	//-----------------------------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------------------------//
	//   Useful public tools
	//-----------------------------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------------------------//

	// Turn a field value into a hashtag
	
	public String hashtagify(String phrase)
	{
		if (phrase.indexOf((int)' ') < 0)
			return "#" + phrase;
		
		StringBuffer tagged = new StringBuffer("#"); 
		
		char prev = ' ', curr = ' ';
		
		for (int i = 0; i < phrase.length(); i++)
		{
			prev = curr;
			curr = phrase.charAt(i);
			
			if ((prev == ' ' || prev == '.' || prev == '_') && Character.isLowerCase(curr))
				curr = Character.toUpperCase(curr);
			
			if (curr != ' ' && curr != '\"' && curr != '.' && curr != '-' && curr != '\'')
				tagged.append(curr);
		}
		
		return tagged.toString();
	}

	
	// Get the intersection of two lists of concepts
	
	public Vector intersect(Vector v1, Vector v2)
	{
		if (v1 == null || v1.size() == 0)
			return null;
		
		if (v2 == null || v2.size() == 0)
			return null;
		
		Hashtable seen = new Hashtable();
		
		for (int i = 0; i < v2.size(); i++)
			seen.put(v2.elementAt(i), "seen");
		
		Vector common = new Vector();
		
		for (int i = 0; i < v1.size(); i++)
			if (seen.get(v1.elementAt(i)) != null)
				common.add(v1.elementAt(i));
		
		return common;
	}
	
	
	// Get the union of two lists of concepts
	
	public Vector union(Vector v1, Vector v2)
	{
		if (v1 == null || v1.size() == 0)
			return v2;
		
		if (v2 == null || v2.size() == 0)
			return v1;
		
		Hashtable seen = new Hashtable();
		
		Vector union = new Vector();

		
		for (int i = 0; i < v1.size(); i++)
		{
			seen.put(v1.elementAt(i), "seen");		
			union.add(v1.elementAt(i));
		}
			
		for (int i = 0; i < v2.size(); i++)
			if (seen.get(v2.elementAt(i)) != null)
				union.add(v2.elementAt(i));
		
		return union;
	}
	
	
	// Get the union of two lists of concepts
	
	public Vector difference(Vector v1, Vector v2)
	{
		if (v1 == null || v1.size() == 0)
			return null;
		
		if (v2 == null || v2.size() == 0)
			return v1;
		
		Hashtable seen = new Hashtable();
		
		Vector difference = new Vector();

		
		for (int i = 0; i < v2.size(); i++)
			seen.put(v2.elementAt(i), "seen");		
			
		for (int i = 0; i < v1.size(); i++)
			if (seen.get(v1.elementAt(i)) == null)
				difference.add(v1.elementAt(i));
		
		return difference;
	}
	
	//-----------------------------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------------------------//
	//   Invert a Field to get a table mapping from values to key concepts
	//     e.g. invert the Positive Talking Points field to map from positive adjectives to people
	//-----------------------------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------------------------//

	public Hashtable getInvertedField(String givenField)
	{
		return getInvertedField(givenField, new Hashtable());
	}
	
	public Hashtable getInvertedField(String givenField, Hashtable inversion)
	{
		Vector invertedKeys = new Vector();
		
		inversion.put("*keylist*", invertedKeys);
		
		for (int f = 0; f < fieldTables.size(); f++)
		{
			String fieldName      = (String)fieldNames.elementAt(f);
			
			if (!fieldName.equals(givenField))
				continue;
			
			Hashtable fieldTable  = (Hashtable)fieldTables.elementAt(f);
			
			Vector keylist =  (Vector)fieldTable.get("*keylist*"), values = null, entries = null;
			
			if (keylist == null) continue;
			
			String key = null, value = null;
			
			for (int k = 0; k < keylist.size(); k++)
			{
				key    = (String)keylist.elementAt(k);
				values = (Vector)fieldTable.get(key);
				
				if (values == null) continue;
				
				for (int v = 0; v < values.size(); v++)
				{
					value = (String)values.elementAt(v);
					
					entries = (Vector)inversion.get(value);
					
					if (entries == null)
					{
						entries = new Vector();
						inversion.put(value, entries);
						
						invertedKeys.add(value);
					}
					
					if (!entries.contains(key))
						entries.add(key);
				}
			}	
		}
		
		return inversion;
	}
	
	//-----------------------------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------------------------//
	//   Return an ordered list of the most similar key concepts to a given key, most similar first
	//-----------------------------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------------------------//

	public Vector getSimilarConcepts(String given)
	{
		return getSimilarConcepts(given, fieldNames);
	}
	
	
	public Vector getSimilarConcepts(String given, Vector fieldNames)
	{
		Vector keys = getKeyConcepts();
		
		Vector[] scale = new Vector[fieldNames.size()*10];
		
		String other = null;
		
		for (int k = 0; k < keys.size(); k++)
		{
			other = (String)keys.elementAt(k);
			
			if (other.equals(given)) continue;
			
			Vector overlap = getOverlappingFields(given, other, fieldNames);
			
			if (overlap == null || overlap.size() == 0) continue;
			
			int simScore = overlap.size();
			
			if (simScore >= scale.length) simScore = scale.length-1;
			
			if (scale[simScore] == null)
				scale[simScore] = new Vector();
			
			scale[simScore].add(other);
		}
		
		Vector rank = new Vector();
		
		for (int s = scale.length-1; s > 0; s--)
		{
			if (scale[s] == null) continue;
			
			for (int v = 0; v < scale[s].size(); v++)
				rank.add(scale[s].elementAt(v));
		}
		
		return rank;
	}
	
	//-----------------------------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------------------------//
	//   Return a list of overlapping fieldname:fieldvalue pairs for two key concepts
	//-----------------------------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------------------------//
	
	public Vector getOverlappingFields(String key1, String key2)
	{
		return getOverlappingFields(key1, key2, fieldNames);
	}
	
	
	public Vector getOverlappingFields(String key1, String key2, Vector salient)
	{
		String fieldName     = null;
		Hashtable fieldTable = null;
		
		Vector overlap       = new Vector();
		
		for (int n = 0; n < fieldNames.size(); n++)
		{
			fieldName = (String)fieldNames.elementAt(n);
			
			if (salient != fieldNames && !salient.contains(fieldName))
				continue;
			
			fieldTable = (Hashtable)fieldTables.elementAt(n);
			
			Vector common = intersect((Vector)fieldTable.get(key1), (Vector)fieldTable.get(key2));
			
			if (common == null) continue;
			
			for (int c = 0; c < common.size(); c++)
			{
				overlap.add(fieldName + "=" + (String)common.elementAt(c));
			}
		}
		
		return overlap;
	}
	
	
	//-----------------------------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------------------------//
	//   Load Knowledge-Base Module from a Given File
	//-----------------------------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------------------------//

	private void loadKnowledgeBaseFrom(String filename)
	{
		loadKnowledgeBaseFrom(filename, 0);
	}
	
	
	private void loadKnowledgeBaseFrom(String filename, int keyPosition)
	{
		FileInputStream input;

		try {
		    input = new FileInputStream(filename);
		    
		    loadKnowledgeBaseFrom(input, keyPosition);
		}
		catch (IOException e)
		{
			System.out.println("Cannot find/load knowledge file: " + filename);
			
			e.printStackTrace();
		}
	}
	
		

	private void loadKnowledgeBaseFrom(InputStream stream, int keyPosition)
	{
		String line = null;
		
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
			
			loadFieldNames(input.readLine());
			
			while ( (line = input.readLine()) != null)  // Read a line at a time
			{
				parseFieldsIntoKB(fieldNames, line, keyPosition);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
	private void parseFieldsIntoKB(Vector fieldNames, String line, int keyPosition)
	{
		StringTokenizer values = new StringTokenizer(line, "\t", true);
		
		int fieldNumber = 0;
		
		Vector valueSets = new Vector();
		
		for (int f = 0; f < fieldNames.size(); f++)
			valueSets.add("");
		
		while (values.hasMoreTokens())
		{
			String token = values.nextToken();
			
			if (token.equals("\t"))
			{
				fieldNumber++;
				
				if (fieldNumber >= valueSets.size())
					break;
				else
					continue;
			}
			
			token = token.trim();
			
			if (token.length() > 0)
				valueSets.setElementAt(token, fieldNumber);
		}
				
		String key = ((String)valueSets.elementAt(keyPosition)).trim().intern();
		
		for (int v = 0; v < valueSets.size(); v++)
		{
			setFieldsInKB((Hashtable)fieldTables.elementAt(v), key, (String)valueSets.elementAt(v));
		}
	}
	
	
	
	private Vector setFieldsInKB(Hashtable field, String key, String valueSet)
	{
		if (valueSet == "") return null;
				
		StringTokenizer values = new StringTokenizer(valueSet, ",", false);
		
		Vector fieldValues = (Vector)field.get(key);
		
		if (fieldValues == null) 
		{
			Vector keyList = (Vector)field.get("*keylist*");
			
			if (keyList == null)
			{
				keyList = new Vector();
				field.put("*keylist*", keyList);
			}
			
			keyList.add(key);
			
			fieldValues = new Vector();
		}
		
		field.put(key, fieldValues);
		
		while (values.hasMoreTokens())
		{
			String value = values.nextToken().trim().intern();
			
			if (!fieldValues.contains(value))
				fieldValues.add(value);
		}
		
		return fieldValues;
	}
	
	
	
	private Vector loadFieldNames(String line)
	{
		StringTokenizer names = new StringTokenizer(line, "\t");
		
		fieldNames.setSize(0);
		fieldTables.setSize(0);
		
		String previous = "", current = "";
		
		Hashtable prevTable = null, currTable = null;
		
		while (names.hasMoreTokens())
		{
			previous  = current;
			current   = names.nextToken().intern();
			
			prevTable = currTable;
			currTable = new Hashtable();
			
			fieldNames.add(current);
			
			if (current == previous && prevTable != null)
				currTable = prevTable;
			
			fieldTables.add(currTable);
		}
		
		return fieldNames;
	}
	
	
	
	//-----------------------------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------------------------//
	//   Application Stub:  Examples of how to load and access different knowledge modules
	//-----------------------------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------------------------//
	
	public static void main(String[] args)
	{
		String kdir = "/Users/tonyveale/Dropbox/CodeCamp2015/TSV Lists/";
		
		KnowledgeBaseModule NOC          = new KnowledgeBaseModule(kdir + "Veale's The NOC List.txt", 0);
		KnowledgeBaseModule CATEGORIES   = new KnowledgeBaseModule(kdir + "Veale's Category Hierarchy.txt", 0);
		KnowledgeBaseModule CLOTHES      = new KnowledgeBaseModule(kdir + "Veale's clothing line.txt", 1);  // 1 is the column number of the key value
		KnowledgeBaseModule CREATIONS    = new KnowledgeBaseModule(kdir + "Veale's creations.txt", 0);
		KnowledgeBaseModule DOMAINS      = new KnowledgeBaseModule(kdir + "Veale's domains.txt", 0);
		KnowledgeBaseModule WORLDS       = new KnowledgeBaseModule(kdir + "Veale's fictional worlds.txt", 0);
		KnowledgeBaseModule VEHICLES     = new KnowledgeBaseModule(kdir + "Veale's vehicle fleet.txt", 1);  // 1 is the column number of the key value
		KnowledgeBaseModule WEAPONS	     = new KnowledgeBaseModule(kdir + "Veale's weapon arsenal.txt", 1);  // 1 is the column number of the key value
		KnowledgeBaseModule PLACES       = new KnowledgeBaseModule(kdir + "Veale's place elements.txt", 0);
		
		KnowledgeBaseModule SUPERLATIVES = new KnowledgeBaseModule(kdir + "superlatives.txt", 0);
		
		System.out.println(SUPERLATIVES.getFieldValues("Superlative", "expensive"));
		
		System.out.println(NOC.getFieldNames());
		
		System.out.println(NOC.getFieldValues("Portrayed By", "Abraham Lincoln"));
		
		System.out.println(CATEGORIES.getFieldValues("Super Category", "President"));
		
		System.out.println(CLOTHES.getFieldValues("Determiner", "Armani T-shirt"));
		System.out.println(CLOTHES.getFieldValues("Covers", "bathrobe and socks"));
		
		System.out.println(VEHICLES.getFieldValues("Determiner", "1931 Isotta-Fraschini limousine"));
		System.out.println(VEHICLES.getFieldValues("Affordances", "flying hovercar"));

		System.out.println(WEAPONS.getFieldValues("Determiner", "retractable Adamantium claws"));
		System.out.println(WEAPONS.getFieldValues("Affordances", "retractable Adamantium claws"));
		System.out.println(WEAPONS.getFieldValues("Affordances", "air gun concealed as a cane"));
		System.out.println(WEAPONS.getFieldValues("Determiner", "aphrodisiac overdose"));

		System.out.println(CREATIONS.getFieldValues("Creation Action", "Christianity"));
		System.out.println(CREATIONS.getFieldValues("Creation Type", "Christianity"));

		System.out.println(NOC.getKeyConcepts());
		
		System.out.println(DOMAINS.getFieldValues("Type", "American history"));
		
		System.out.println(WORLDS.getFieldValues("Type", "The Empire Strikes Back"));
		
		System.out.println(PLACES.getFieldValues("Fictive Status", "Alderaan"));
		System.out.println(PLACES.getFieldValues("Place Type", "Alderaan"));
		
		Vector fictionalCharacters = NOC.getAllKeysWithFieldValue("Fictive Status", "fictional");
		
		System.out.println(fictionalCharacters);
		
		Vector fathers = NOC.getAllKeysWithFieldValue("Category", "Father");
		
		Vector fictionalFathers = NOC.intersect(fathers, fictionalCharacters);
		
		Vector GLucas = NOC.getAllKeysWithFieldValue("Creator", "George Lucas");
		
		System.out.println(fictionalFathers);
				
		Vector attributeFields = new Vector();
		
		attributeFields.add("Negative Talking Points");
		attributeFields.add("Positive Talking Points");
		
		System.out.println(NOC.getSimilarConcepts("Osama Bin Laden"));
		
		System.out.println(NOC.getOverlappingFields("Osama Bin Laden", "Albus Dumbledore"));

		System.out.println(NOC.difference(NOC.getSimilarConcepts("Darth Vader", attributeFields), GLucas));
		System.out.println(NOC.difference(NOC.getSimilarConcepts("Darth Vader", attributeFields), fictionalCharacters));
		System.out.println(NOC.difference(NOC.getSimilarConcepts("The Joker", attributeFields), fictionalCharacters));
		System.out.println(NOC.intersect(NOC.getSimilarConcepts("Osama Bin Laden", attributeFields), fictionalCharacters));

	}

	
	
}
