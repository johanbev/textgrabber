package no.uio.ifi.nora.extraction;

import java.util.LinkedList;
import java.util.List;

import no.uio.ifi.nora.extraction.transformations.PushTransformation;
import no.uio.ifi.nora.ngrams.*;
import org.jdom.*;
import org.jdom.filter.*;

/**
 * Uses N-Gram model of language identification based on the utility bundled with Nutch.
 * @author johanbev
 *
 */
public class LanguageDetector extends PushTransformation
{
	
	LanguageIdentifier li = new LanguageIdentifier();
	
	@Override
	protected String getStageName() {
		return "LanguageDetector";
	}

	@Override
	protected String getStageVersion() {
		return "0.5";
	}
	
	private int analysisLength;
	
	/**
	 * Creates an instance with the default analysisLength (2000),
	 * which is the number of characters sent to LanguageIdentifer per page to be identified.
	 * The higher this is, the more accurate the identification will be,
	 * however the analysis will take longer.
	 */
	public LanguageDetector()
	{
		analysisLength = 2000;
	}
	
	/**
	 * Creates an instance with the supplied analysisLength
	 * @param analysisLength Number of characters per page sent to LanguageIdentifier
	 * The higher this is, the more accurate the identification will be,
	 * however the analysis will take longer.
	 */
	public LanguageDetector(int analysisLength)
	{
		this.analysisLength = analysisLength;
	}
	
	
	
	/**
	 * Reads all pages and attempts to classify the language of each page with confidence score.
	 * This score is later used to assess the quality of the extracted text.
	 */
	protected String permute(String s) throws Exception 
	{
		String[] pageBreakArray = null;
		pageBreakArray = s.split("<<p>>");
		for (int i = 0, pNum = 0 ; i < pageBreakArray.length ; i++) 
		{
			
			String noXML = pageBreakArray[i].substring(0).replaceAll("<\\\\?.+>", ""); 
			String[] result = li.identifyAndScore(new StringBuilder(noXML.substring(0, Math.min(analysisLength, noXML.length())))).split("\\s+");
			
			if (result[0].equals( "")) //The languageIdentifier will return "" if it has no match, use "xx" here to prevent problems downwards.
			{
				result[0] = "xx";
			}
			
			String pageXML = "\n<page number=\"" + pNum++ +"\" lang=\""+result[0]+"\" conf=\""+result[1]+"\">\n";
			pageBreakArray[i] = pageXML+pageBreakArray[i]+"</page>";
		}
		StringBuilder newString = new StringBuilder();
		
		for (int i=0; i < pageBreakArray.length ; i++) {
			newString.append(pageBreakArray[i]);
		
		}
		return newString.toString();
	}

	@Override
	protected List<Content> transform(Content c, Document d) 
	{
		LinkedList<Content> contentList = new LinkedList<Content>();
		if(! (c instanceof Element))
		{
			contentList.add(c);
			return contentList;
		}
		if(!((Element)c).getName().equals("page"))
		{
			contentList.add(c);
			return contentList;
		}
		
		Element currentPage = (Element)c;
		
		String[] result = li.identifyAndScore(new StringBuilder(currentPage.getText())).split("\\s+");
		
		if (result[0].equals( "")) //The languageIdentifier will return "" if it has no match, use "xx" here to prevent problems downwards.
		{
			result[0] = "xx";
		}
		
		currentPage.setAttribute("lang", result[0]);
		currentPage.setAttribute("conf", result[1]);
		
		contentList.add(c);
		return contentList;
		
	}
	
}
