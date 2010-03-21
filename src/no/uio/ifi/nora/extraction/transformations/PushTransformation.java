package no.uio.ifi.nora.extraction.transformations;

import java.util.ArrayList;
import java.util.List;
import org.jdom.*;
import no.uio.ifi.nora.xml.*;

/**
 * This class represent an order preserving  structural transformation, ie. on the XML structure, and not only on the document text.
 * @author johanbev
 *
 */
public abstract class PushTransformation extends Transformation
{

	protected abstract List<Content> transform (Content c, Document d);
	
	protected void done(Document d)
	{
		
	}
	
	@Override
	protected final Document transform(Document d) throws Exception 
	{
		Element body = XMLUtils.selectBody(d);
		
		List<Content> cList = XMLUtils.makeContentList(body.getContent());
		
		List<Content> newClist = new ArrayList<Content>();
		
		for (Content c : cList)
		{
			newClist.addAll(transform(c, d));
		}
		
		done(d); //Notify subclasses we are done.
	
		body.removeContent();
		body.setContent(newClist);
		
		return d;
	}

}
