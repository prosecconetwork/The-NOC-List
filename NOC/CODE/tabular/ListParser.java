/*
 * Created on May 1, 2006
 *
 * Parse a LISP-like list into a Vector format
 */

package tabular;

/**
 * @author Tony Veale
 *
 */

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;


public class ListParser 
{
	//-------------------------------------------------------------------//
	//-------------------------------------------------------------------//
	// Print Behaviour
	//-------------------------------------------------------------------//
	//-------------------------------------------------------------------//

	public static String toString(Object obj)
	{
		if (obj == null)
			return "()";
		else
		if (obj instanceof Vector)
			return toString((Vector)obj, new StringBuffer());
		else
			return obj.toString();
	}
	
	
	public static String toString(Vector list, StringBuffer buff)
	{
		if (list == null || list.size() == 0)
			return "()";
		
		buff.append("(");
		
		Object item = null;
		
		for (int i = 0; i < list.size(); i++)
		{
			if (i > 0)
				buff.append(" ");
			
			item = list.elementAt(i);
			
			if (item == null)
				buff.append("()");
			else
			if (item instanceof Vector)
				buff.append(toString((Vector)item));
			else
				buff.append(item.toString());
		}
		
		buff.append(")");
		
		return buff.toString();
	}
	
	
	//-------------------------------------------------------------------//
	//-------------------------------------------------------------------//
	// List Comparator
	//-------------------------------------------------------------------//
	//-------------------------------------------------------------------//

	public static boolean areSameSet(Vector list1, Vector list2)
	{
		if (list1 == null) return list2 == null;
		if (list2 == null) return list1 == null;
		
		if (list1.size() != list2.size()) return false;
		
		Object element = null;
		
		for (int i = 0; i < list1.size(); i++)
		{
			element = list1.elementAt(i);
			
			if (!list2.contains(element)) return false;
		}
		
		return true;
	}
	
	//-------------------------------------------------------------------//
	//-------------------------------------------------------------------//
	// List parser
	//-------------------------------------------------------------------//
	//-------------------------------------------------------------------//
	
	public static Object parse(String list)
	{
		Vector gather = new Vector();
		
		if (list == null || list.length() == 0) return gather;
		
		String text   = list.trim();
		
		if (text.length() == 0) return gather;
			
		parse(text, gather, 0, 0);
		
		if (gather != null && gather.size() > 0)
			return gather.elementAt(0);
		else	
			return null;
	}	
	
	
	public static Vector parseFile(String fname)
	{
		Vector gather = new Vector();
			
		parse(loadText(fname), gather, 0, 0);
			
		return gather;
	}	
	
	
	private static int parse(String text, Vector list, int pos, int level)
	{
		int len = text.length(), start = -1, quoted = 0;
		
		char curr = ' ';
		
		boolean literal = false;
		
		while (pos < len)
		{
			curr = text.charAt(pos);
			
			if (curr == '"')
			{
				if (!literal)
				{
					literal = true;
					
					if (start >= 0 && start < pos)
					{
						list.addElement(addQuotes(quoted, text.substring(start, pos).intern()));
						
						quoted=0;
					
						start = -1;
					}
					
					start = pos+1;
				}
				else
				{
					if (start >= 0 && start < pos)
					{
						list.addElement(addQuotes(quoted, text.substring(start, pos).intern()));
						
						quoted=0;
					
						start = -1;
					}
				
					literal = false;
				}
			}
			else
			if (literal)
			{
				pos++;
			
				continue;
			}
			else
			if (curr == ' ' || curr == ',' || curr == '\t' || curr == '\n')
			{
				if (start >= 0 && start < pos)
				{
					list.addElement(addQuotes(quoted, text.substring(start, pos).intern()));
					
					quoted=0;
				
					start = -1;
				}
			}
			else
			if (curr == '\'')
			{
				quoted++;
			}
			else
			if (curr == '(' || curr == '[')
			{
				if (start >= 0 && start < pos)
				{
					list.addElement(addQuotes(quoted, text.substring(start, pos).intern()));
					
					quoted=0;
					start = -1;
				}
				
				Vector sub = new Vector();
					
				list.add(addQuotes(quoted, sub));
				
				quoted = 0;
				
				pos    = parse(text, sub, pos+1, level+1);
			}
			else
			if (curr == ')' || curr == ']')
			{
				if (start >= 0 && start < pos)
				{
					list.addElement(addQuotes(quoted, text.substring(start, pos).intern()));
					
					quoted=0;
					
					start = -1;
				}
				
				if (level == 0)
					System.out.println("Too Many Right Parens: " 
										+ text.substring(0, pos) 
										+ "***" + text.substring(pos));
				else
					return pos;
			}
			else
			{
				if (start < 0) start = pos;
			}

			pos++;
		}
		
		if (start >= 0)
			list.addElement(addQuotes(quoted, text.substring(start, text.length()).intern()));
		
		return pos;
	}
	
	
	private static Object addQuotes(int numQuotes, Object item)
	{
		item = castRepresentation(item);
		
		if (numQuotes == 0)
			return item;
		
		Vector wrap = new Vector();
		
		wrap.addElement("quote");
		
		wrap.addElement(addQuotes(numQuotes-1, item));
		
		return wrap;
	}
	
	
	
	// generate an Integer or Double or Boolean entry for string
	
	private static Object castRepresentation(Object form)
	{
		if (form == null || !(form instanceof String))
			return form;
		
		if (form == "true")
			return new Boolean(true);
		else
		if (form == "false")
			return new Boolean(false);
		
		String text = (String)form;
		
		if (text.length() == 0)
			return text;
		
		if (!text.startsWith(".") && !Character.isDigit(text.charAt(0)))
			return text;
		
		if (!Character.isDigit(text.charAt(text.length()-1)))
			return text;
		
		try {
			if (text.indexOf((int)'.') >= 0)
				return new Double(Double.parseDouble(text));
			else
				return new Integer(Integer.parseInt(text));
		}
		catch (Exception e)
		{
			return text;
		}
	}
	
	//--------------------------------------------------------------------//
	//--------------------------------------------------------------------//
	// Load a text file into memory as a large String of text
	//--------------------------------------------------------------------//
	//--------------------------------------------------------------------//
	
	private static String loadText(String filename)
	{
		FileInputStream input;

		try {
		    input = new FileInputStream(filename);
		    
		    return loadText(input);
		}
		catch (IOException e)
		{
			System.out.println("Cannot find/load text file: " + filename);
			
			e.printStackTrace();
		}
		
		return "";
	}
	
		

	private static String loadText(InputStream stream)
	{
		String line = null;
		
		StringBuffer store = new StringBuffer();
		
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(stream));
			
			while ( (line = input.readLine()) != null)  // Read a line at a time
			{
				int comment = line.indexOf("//");
				
				if (comment == 0)
					continue;
				else
				if (comment > 0)
					store.append(line.substring(0, comment)).append("\n");
				else
					store.append(line).append("\n");
			}

		   input.close();  // close connection to the data source
		}
		catch (IOException e) 
		{
			System.out.println("Exception while reading text file:\n " + e.toString());
				 
			e.printStackTrace();
		}
		
		store.append(" ");
		
		return store.toString();
	}
	
	
	//-------------------------------------------------------------------//
	//-------------------------------------------------------------------//
	// Main routine for test purposes
	//-------------------------------------------------------------------//
	//-------------------------------------------------------------------//

	public static void main(String[] args)
	{
		String dir      = "/Users/tonyveale/Desktop/Lexical Resources/";

		String catDir	= dir + "Categories/Fingerprints/";
		
		String example = " ((a) '(b 1.5 true 10 \"c d\"))";
		
		Object list    = ListParser.parse(example);
		
		System.out.println("in:  " + example);
		System.out.println("mid: " + list);
		System.out.println("out: " + ListParser.toString(list));
		
		Vector flist = ListParser.parseFile(catDir + "similes.list");
		
		System.out.println("\nFile: " + flist);
		
		System.out.println("\nFile: " + ListParser.toString(flist));
		
	}
}
