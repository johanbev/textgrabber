package no.uio.ifi.nora.xml;

import java.util.*;
import org.jdom.*;
import org.jdom.filter.*;

/**
 * Collection of handy utils for working with XML and JDOM.
 * @author johanbev
 *
 */
public class XMLUtils 
{
	/**
	 * Make a ListLinked<Content> out of a unsafe list. 
	 * @param l Input list to be made into a LinkedList<Content>
	 * @return A LinkedList<Content> of the elements in list.
	 */
	public static LinkedList<Content> makeContentList(List l)
	{
		LinkedList<Content> ll = new LinkedList<Content>();
		
		for (Object o : l)
		{
			
			if( ! (o instanceof Content))
			{
				throw new ClassCastException("Cannot generate content list " + o.toString() + " is not of type content ");
			}
			ll.add((Content) o);
		}
		
		return ll;
	}
	
	
	public static Element selectBody(Document d)
	{
		//Select body element.
		Element body = null;
		
		try
		{
			body = (Element)d.getRootElement().getContent(new ElementFilter("body")).get(0);
		}
		catch (IndexOutOfBoundsException e)
		{
			throw new IllegalStateException("The XML-Document did not contain a body element", e);
		}
		
		return body;
	}
}
