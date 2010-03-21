package no.uio.ifi.nora.extraction.transformations;

import org.jdom.*;
import java.util.*;
import no.uio.ifi.nora.extraction.*;

public abstract class Transformation implements PipelineElement
{
	/**
	 * This will be called by process(), to get this pipeline stage name for XML reporting.
	 * @return The pipeline stage name.
	 */
	protected abstract String getStageName();
	/**
	 * This will be called by process() to get this pipeline stage version for XML reporting.
	 * @return The pipeline stage version.
	 */
	protected abstract String getStageVersion();
	
	protected abstract Document transform(Document d) throws Exception;

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
	
	public final Document process(Document d) throws Exception
	{
		d = transform(d);
		
		//leave our footprint on the xml:
		
		//build the app element
		Element app = new Element("app");
		app.setAttribute("id", getStageName());
		app.setAttribute("ver", getStageVersion());
		
		//find or add the postprocessors element:
		Element postprocessors = findPostProcessors(d);
		postprocessors.addContent(app);
		return d;
	}
}
