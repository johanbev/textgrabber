package no.uio.ifi.nora.extraction.transformations;

import org.jdom.*;
import org.jdom.filter.*;
import java.util.*;
import no.uio.ifi.nora.xml.*;
/**
 * Represents a textual transformation, ie. we operate only on the body text and do not create or modify XML-nodes.
 * @author johanbev
 *
 */
public abstract class TextualTransformation extends Transformation 
{
	/**
	 * If true, the transformation will be called on all children of the body text.
	 */
	protected boolean recuresive;
	
	/**
	 * The actual transformation
	 * @param s The string to be transformed.
	 * @return Transformed string to be put back into xml.
	 * @throws Exception 
	 */
	protected abstract String transform(String s) throws Exception;
	
	protected final Document transform(Document d) throws Exception
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
		
		List<Content> bodyContents = XMLUtils.makeContentList(body.getContent());
		
		recHelper(bodyContents);

		return d;
	}
	
	private void recHelper(List<Content> contents) throws Exception
	{
		for(Content content : contents)
		{
			if( content instanceof Element)
			{
				Element e = (Element)content;
				//e.setText(transform(e.getText()));
				if (recuresive)
				{
					recHelper(XMLUtils.makeContentList(e.getContent()));
				}
				else
				{
					e.setText(transform(e.getText()));
				}
			}
			if( content instanceof Text)
			{
				Text e = (Text)content;
				e.setText(transform(e.getText()));
			}	
			
		}
	}
	public TextualTransformation()
	{
		recuresive = false;
	}
}
