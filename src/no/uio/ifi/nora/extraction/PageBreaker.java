package no.uio.ifi.nora.extraction;


import java.util.*;

import org.jdom.*;

import no.uio.ifi.nora.extraction.transformations.*;


public class PageBreaker extends PushTransformation {

	@Override
	protected String getStageName() {
		return "PageBreaker";
	}

	@Override
	protected String getStageVersion() {
		return "0.1";
	}

	@Override
	protected List<Content> transform(Content c, Document d) 
	{
		LinkedList<Content> contentList = new LinkedList<Content>();
		
		if(!(c instanceof Text)) //return anything not a Text intact.
		{
			contentList.add(c);
			return contentList;
		}
		
		Text t = (Text)c;
		//Split the text on the pagebreaks.
		
		String[] pages = t.getText().split("<<p>>");
		
		for(String page : pages)
		{
			Element nPage = new Element("page");
			nPage.addContent(page);
			contentList.add(nPage);
		}
		
		return contentList;
		}

}
