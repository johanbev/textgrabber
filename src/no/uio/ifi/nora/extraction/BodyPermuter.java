package no.uio.ifi.nora.extraction;

import java.util.List;

import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;


/**
 * This class is intended to be extended for all pipeline processes who just want to modify the body
 * ie. just using regexp-replace.
 * @author johanbev
 *
 */
public abstract class BodyPermuter implements PipelineElement
{
	private CDATA c;
	
	/**
	 * This will be called by process(), to get appNae
	 * @return The current apps Name
	 */
	protected abstract String getAppName();
	/**
	 * This will be called by process() to get appVer
	 * @return The current apps Version.
	 */
	protected abstract String getAppVer();
	
	/**
	 * This will be called by process() to do the actual manipulation on the b-text
	 * @param s The body text
	 * @return The new body text.
	 */
	protected abstract String permute(String s) throws Exception; 
	
	
	/**
	 * Gets the body text from a Document
	 * @param d The document to get from
	 * @return The text
	 */
	protected String getBodyText(Document d)
	{
		//Get the body element:
		Element root = d.getRootElement();
		List l = root.getContent();
		Element e = null;
		for(Object o : l)
		{
			if(o instanceof Element)
			{
				Element t = (Element)o;
				if(t.getName().equals("body"))
				{
					e = t; break;
				}	
			}
		}
		
		if(e == null) //if there was no body element
			throw new IllegalStateException("No body element in XML");
		CDATA C = (CDATA)e.getContent().get(0);
		c = C;
		return C.getText();
	}
	
	/**
	 * Finds or creates a PostProcessors Element.
	 * @param d The Document to search in.
	 * @return The postprocessors-element or a new one if none was present.
	 */
	protected final Element findPostProcessors(Document d)
	{
		Element root = d.getRootElement();
		List l = root.getContent();
		Element e = null;
		for(Object o : l)
		{
			if(o instanceof Element)
			{
				Element t = (Element)o;
				if(t.getName().equals("postprocessors"))
				{
					e = t; break;
				}	
			}
		}
		
		if(e == null) //if there was no postprocessors
		{
			Element post =  new Element("postprocessors");
			root.addContent(post);
			return post;
		}
		return e;
	}
	
	/**
	 * Sets the body text
	 * @param c The CDATA instance in the body elem.
	 * @param s The new string to be set.
	 */
	protected void setBodyText(CDATA c, String s)
	{
		c.setText(s);
	}
	
	/**
	 * Sets up the permutation and updates the XML.
	 */
	public final Document process(Document d) throws Exception
	{
		//Get the body element:
		String s = getBodyText(d);
		
		//do the actual string maniputaltion.
		
		setBodyText(c, permute(s));

		//leave our footprint on the xml:
		
		//build the app element
		Element app = new Element("app");
		app.setAttribute("id", getAppName());
		app.setAttribute("ver", getAppVer());
		
		//find or add the postprocessors element:
		Element postprocessors = findPostProcessors(d);
		postprocessors.addContent(app);
		
		//d.getRootElement().addContent(app);
		
		return d;
	}
}