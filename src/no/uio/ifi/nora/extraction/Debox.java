package no.uio.ifi.nora.extraction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.util.concurrent.*;

import org.jdom.*;

import no.uio.ifi.nora.extraction.transformations.*;
import no.uio.ifi.nora.xml.XMLUtils;



/**
 * @author johanbev
 */
public class Debox extends PushTransformation {
	
	
	Map<Document, DeboxSession> ongoingSessions;
	
	public Debox()
	{
		ongoingSessions = new ConcurrentHashMap<Document, DeboxSession>();
	}
	
	@Override
	protected String getStageName() {
		return "Debox";
	}

	@Override
	protected String getStageVersion() {
		return "0.2";
	}

	
	
	
	
	@Override
	protected List<Content> transform(Content c, Document d) 
	{	
		DeboxSession currentSession;
		
		if(!ongoingSessions.containsKey(d))
		{
			currentSession = new DeboxSession();
			ongoingSessions.put(d, currentSession);
		}
		else
		{
			currentSession = ongoingSessions.get(d);
		}
		
		LinkedList<Content>  contentList = new LinkedList<Content>();
		String eText;
		
		if(c instanceof Element)
		{
			for( Content inner : XMLUtils.makeContentList(((Element)c).getContent()))
			{
				contentList.addAll(transform(inner, d));
			}
			LinkedList<Content> finalList = new LinkedList<Content>();
			((Element) c).setContent(contentList);
			finalList.add(c);
			return finalList;
		}
		
		eText = ((Text)c).getText();
		currentSession.doc = eText;
		currentSession.parseMetaTags();
		contentList = currentSession.processText();
		
		currentSession.pNum++;
		
		return contentList;
	}
	
	@Override
	protected void done(Document d)
	{
		ongoingSessions.remove(d);
	}

	/**@Override
	protected String permute(String s) throws Exception 
	{
		DeboxSession st = new DeboxSession();
		st.doc = s;
		st.parseMetaTags();
		st.processText();
		return st.processedText.toString();
	}*/
	
	

}

class Box
{
	float x;
	float y;
	float w;
	float fontSize;

	public Box(float x, float y, float w, float fontSize) {
		super();
		this.x = x;
		this.y = y;
		this.w = w;
		this.fontSize = fontSize;
	}

	static Box parseBox(String s)
	{
		if( s.equals("<<p>>"))
		{
			return null;
		}


		//<<box 117.828 181.48999 21.639755 17.0 >>
		String[] fields = s.split("\\s+");
		if(fields.length != 6)
		{
			System.err.println("BAD BOX! " + s);
			return null;
		}
		return new Box(Float.parseFloat(fields[1]),
				Float.parseFloat(fields[2]),
				Float.parseFloat(fields[3]),
				Float.parseFloat(fields[4]));
	}


}
class DeboxSession
{

	String doc;
	ArrayList<Box> tags = new ArrayList<Box>();
	ArrayList<String> contents = new ArrayList<String>();
	
	
	int pNum;
	
	
	public DeboxSession()
	{
		pNum = 0;
	}

	/**
	 * Split the doc into tags and the content between each tag.
	 *
	 */
	void parseMetaTags()
	{
		tags = new ArrayList<Box>();
		contents = new ArrayList<String>();
		
		int tagStart = 0;
		int tagEnd = -1;

		//get the first tag:
		tagStart = doc.indexOf("<<", tagStart);
		tagEnd = doc.indexOf(">>", tagStart) + 2;
		if(tagStart == -1 || tagEnd == -1)
			return;
		tags.add(Box.parseBox(doc.substring(tagStart, tagEnd)));
		while(true)
		{
			tagStart = doc.indexOf("<<", tagEnd);
			if(tagStart == -1)
				break;
			contents.add(doc.substring(tagEnd, tagStart));
			tagEnd = doc.indexOf(">>", tagStart) + 2;
			tags.add(Box.parseBox(doc.substring(tagStart, tagEnd)));
		}
		if(tagEnd < doc.length())
		{
			contents.add(doc.substring(tagEnd));
		}
	}

	private float determineMostCommonSize(ArrayList<Box> tags, ArrayList<String> content)
	{

		Map<Float,Integer> sizes = new HashMap<Float,Integer>();

		for(int i = 0; i < tags.size(); i++)
		{
			Box currentTag = tags.get(i);
			Integer current = sizes.get(currentTag.fontSize);
			if(current == null)
			{
				sizes.put(currentTag.fontSize, content.get(i).length());
			}
			else
			{
				sizes.put(currentTag.fontSize, current + content.get(i).length());
			}
		}

		Set<Float> s = sizes.keySet();
		int biggest = -1;
		float biggestSize = -1;
		for(float f : s)
		{
			if(sizes.get(f) > biggest)
			{
				biggest = sizes.get(f);
				biggestSize = f;
			}
		}

		return biggestSize;

		/* or in common lisp
		 * (defun dermine-most-common-size (tags content)
		 * 	 (car (sort (reduce #'union ......))))
		 */
	}

	/**
	 * Tags PoD adaptively. Aims to separate body-text from all other annotations 
	 * 
	 *
	 */
	LinkedList<Content> processText()
	{
		LinkedList<Content> contentList = new LinkedList<Content>();
		/*
		 * We use an adaptive strategy, trying to set apart body text from all other annotations
		 * including footnote anchors appearing in running text: i.e. "(...) which is a Minkowski metric",
		 * as this might destroy full text phrase search, where the footnote-anchor appears inside the phrase and not at either end
		 *
		 */

		int i = 0;
		float titleSize = -1;
		int numChars = 0;
		
		if(pNum == 0)
		{
			//First page is a special page, anything appearing before the body text
			//cannot break any search, so it can be safely tagged as body text, to also make
			//this searchable, which we possibly want.
			
			for(; i < contents.size() && tags.get(i) != null; i++)
			{
				Box currentBox = tags.get(i);
				float currentSize = currentBox.fontSize;
				String currentContent = contents.get(i);
				if(currentContent.length() == 0)
					continue; //just drop all empty boxes
	
	
				//Case of new text or bigger text on frontpage, can safely be put untagged in document
				if( titleSize < currentSize)
				{
					titleSize = currentSize;
					contentList.add(new Text(currentContent));
				}
				//We now have the possibility of a footnote anchor, but also subtitle, list of authors etc.
				if(currentSize < titleSize)
				{
					//Assume boxes two chars or less are anchors:
					if(currentContent.length() <= 2)
					{
						contentList.add(new Element("footnote").addContent(new Text(currentContent)));
					}
					else
					{
						contentList.add(new Text(currentContent));
					
					}
				}
	
				numChars += currentContent.length();
			}
		}
		
		//Now read each page and determine which fontsize the bodyText has.
		for(; i < contents.size(); i++)
		{
			ArrayList<Box> currentTags = new ArrayList<Box>();
			ArrayList<String> currentContents = new ArrayList<String>();

			//consume the page
			while(i < contents.size() && tags.get(i) != null)
			{
				currentTags.add(tags.get(i));
				currentContents.add(contents.get(i++));
			}

			//determine size: @johanbev, this could infact mistag if there are very large amounts of footnotes, running text?

			float bodySize = determineMostCommonSize(currentTags, currentContents);

			//process each tag, tagging things smaller than bodysize as footnote, bigger as title

			for(int j = 0; j < currentTags.size(); j++)
			{
				Box currentBox = currentTags.get(j);
				float currentSize = currentBox.fontSize;
				String currentContent = currentContents.get(j);

				if(currentSize == bodySize)
				{
					contentList.add(new Text(currentContent));
					
					continue;
				}

				if(currentSize > bodySize)
				{
					contentList.add(new Element("title").addContent(new Text(currentContent)));
					
				}

				if(currentSize < bodySize)
				{	
					contentList.add(new Element("footnote").addContent(new Text(currentContent)));
					
				}
			}

			//write the page separator
		}
		
		return contentList;
	}
}