package no.uio.ifi.nora;

import java.io.File;
import java.io.FileReader;
import java.io.StringReader;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;



import no.uio.ifi.nora.extraction.NoraFactory;
import no.uio.ifi.nora.extraction.Pipeline;

import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * For reading XML-files and presenting them in human-readable form.
 * @author johanbev
 *
 */
public class LuceneAdapter 
{

	protected float knownConfidence;
	protected float foreignConfidence;
	protected String knownLanguages;
	private final String defaultKnownLanguages = "en,no,fr,se,dk,de,es";
	private final XPathFactory factory = XPathFactory.newInstance();
	private final XPath xPath = factory.newXPath();
	
	
	/**
	 * XPathCode for selecting nodes, used by getDocument()
	 */
	protected String XPathCode;

	private String getXPath()
	{
		String[] languages = knownLanguages.split(",");

		StringBuilder xpath = new StringBuilder();

		xpath.append("/document/body/page[( @conf>=" + knownConfidence + " and (");
		for(String s : languages)
		{
			xpath.append(" @lang='" + s + "' or");
		}
		xpath.setLength(xpath.length() -3); //remove last or clause

		xpath.append(")) or @conf>= " + foreignConfidence + " ]/text()");

		return xpath.toString();

	}
	/**
	 * Initialises the LuceneAdapter with default settings, selecting only the text of page<> nodes,
	 * ignoring pages where the confidence score from language detection is too low.
	 */
	public LuceneAdapter()
	{
		knownConfidence = 1.2f;
		foreignConfidence = 1.45f;
		knownLanguages = defaultKnownLanguages;
		XPathCode = getXPath();
	}
	
	/**
	 * Initialises the LuceneAdapter with default known languages and given confidences.
	 * @param knownConfidence Known languages must score equal to this or higher.
	 * @param foreignConfidence Unknown languages must score equal to this or higher.
	 */
	public LuceneAdapter(float knownConfidence, float foreignConfidence)
	{
		this.knownConfidence = knownConfidence;
		this.foreignConfidence = foreignConfidence;
		knownLanguages = defaultKnownLanguages;
		XPathCode = getXPath();
	}
	
	/**
	 * Initialises the LuceneAdapter with given XPathCode
	 * @param XPathCode the XPathCode used by LuceneAdaper to extract text. This will internally be evaluated by getDocument(), 
	 * resulting in a NODESET, which is then contatenated to a string.
	 */
	public LuceneAdapter(String XPathCode)
	{
		this.XPathCode = XPathCode;
	}

	/**
	 * Glue together XML-file given XPathCode.
	 * @param f XML-file to be extracted from
	 * @return Extracted Document as human presentable
	 */
	public String getDocument(File f) throws Exception
	{

		FileReader fr = new FileReader(f);

		NodeList res = (NodeList)xPath.evaluate(XPathCode, new InputSource(fr), 
				XPathConstants.NODESET);

		StringBuilder docBuilder = new StringBuilder();
		for(int i = 0; i < res.getLength(); i++)
		{

			docBuilder.append(res.item(i).getTextContent());

		}
		fr.close();
		
		res = null;
		return docBuilder.toString();

	}
	/**
	 * Glue together XML-file given XPathCode.
	 * @param s XML-String to be extracted from
	 * @return Extracted Document as human presentable
	 * @throws Exception
	 */
	public String getDocument(String s) throws Exception
	{
		StringReader sr = new StringReader(s);
	
		NodeList res = (NodeList)xPath.evaluate(XPathCode, new InputSource(sr),
				XPathConstants.NODESET);
		
		StringBuilder docBuilder = new StringBuilder();
		for(int i = 0; i < res.getLength(); i++)
		{
			docBuilder.append(res.item(i).getTextContent());
		}
		
		res = null;
		sr.close();	
		return docBuilder.toString();
	}

	/**
	 * For testing and development only
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{
		Pipeline pipeline = NoraFactory.newPipeline();
		LuceneAdapter la = new LuceneAdapter();
		
		String res = pipeline.getXMLRepresentation("http://www.ub.uit.no/munin/bitstream/10037/1147/1/thesis.pdf");

		System.out.println(la.getDocument(res));
	}
}