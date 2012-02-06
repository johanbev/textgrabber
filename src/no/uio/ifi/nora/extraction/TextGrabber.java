package no.uio.ifi.nora.extraction;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import no.uio.ifi.nora.xml.XMLWriter;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.util.PDFTextStripper;
import org.jdom.Attribute;
import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Text;

/**
 * Does the main extraction from PDFs. @see getDocument
 * @author johanbev, tobiasvl
 *
 */
public class TextGrabber
{
	
	//We only need one instance of this:
	private LigRepair rep = new LigRepair();
	
	/**
	 * Extract a PDFDocument from the given input stream.
	 * @param is The input stream of a PDF Document.
	 * @param f The file where the original PDF was, will be written to XML, this may be null.
	 * @param throwing If true, getDocument() will rethrow exceptions.
	 * @return JDOM XML of extracted PDF, with metadata and exceptions (if any).
	 * @throws Exception
	 */
	public Document getDocument(InputStream is, File f, Boolean throwing) throws Exception
	{
		COSDocument cosd = null;

		//The xml doc to be built:
		Document d = new Document();
		Element root = new Element("document");  
		d.addContent(root);

		try
		{
			PDFParser pdfParse = new PDFParser(is);
			pdfParse.parse();

			cosd = pdfParse.getDocument(); //get the result

			String docText = textStrip(cosd);
			//LigRepair rep = new LigRepair();
			docText = rep.repairLig(docText); 
				// doesn't fit neatly into the 'pipeline' paradigm

			// Build the xml representation
			PDDocument pd = pdfParse.getPDDocument();
			PDDocumentInformation pdm = pd.getDocumentInformation();

			//add the MetaData 
			root.addContent(getMetaData(pd));

			// add the extractionAgent
			Element eAg = addExtractionMetadata();
			root.addContent(eAg);

			// add footprint fror the pdf file we extracted from if available
			if (f != null)
			{
				Element origPDF = new Element("originalPDF");
				origPDF.setText(f.getName());
				root.addContent(origPDF);
			}

			// add the actual body text.
			if (docText != null)
			{

				// make safe XML:
				docText = docText.replaceAll("&", "&amp;");
				//docText = docText.replaceAll("]]>", "))>"); // for intermediate CDATA step
				docText = docText.replaceAll("([^>]|^)>([^>]|$)", "$1&gt;$2").replaceAll("([^<]|^)<([^<]|$)", "$1&lt;$2"); // preserve metadata tags

			

				docText = XMLWriter.removeInvalidXMLCharacters(docText); // encoding

				
				Element bodyXML = new Element("body");

				bodyXML.setContent(new Text(docText));
				root.addContent(bodyXML);
			}

			return d;
		}
		catch (Exception e)
		{
			Element error = new Element("error");
			Element message = new Element("message").setText(XMLWriter.removeInvalidXMLCharacters(e.getMessage()));
			error.addContent(message);
			root.addContent(error);

			if (throwing) {
				throw new Exception(e);
			}

			System.err.println("Error  threw " + e.getMessage());
			return d;
		}

		finally 
		{
			if (cosd != null) {
				cosd.close();
			}

			is.close();	
		}
	}

	/**
	 * Make XML element with metadata about the extraction
	 * @return "extractionAgent" XML element with metadata about the extraction
	 */
	private Element addExtractionMetadata()
	{
		Element eAg = new Element("extractionAgent");
		Element agent = new Element("agent");

		agent.setAttribute(new Attribute("id", "TextGrabber"));
		agent.setAttribute(new Attribute("version", "0.2"));

		Element timeStamp = new Element("timeStamp");
		timeStamp.setText(Calendar.getInstance().getTime().toString());

		eAg.addContent(agent);
		eAg.addContent(timeStamp);
		return eAg;
	}

	/**
	 * Strips away PDF metadata, adds page separator metatag
	 * @param cods The document
	 * @return The document as a string
	 * @throws IOException
	 */
	protected String textStrip(COSDocument cods) throws IOException
	{
		PDFTextStripper stripper = new PDFTextStripper();
		stripper.setPageSeparator( "<<p>>" ); // mark pagebreaks w/form feed //maybe paremeterize this

		String docText = stripper.getText(new PDDocument(cods));

		return docText;
	}

	/**
	 * Retrieves metadata from PDF, adds it to "metaData" XML element
	 * @param pd PDF document
	 * @return The "metaData" XML element
	 */
	private Element getMetaData(PDDocument pd) 
	{
		PDDocumentInformation pdm = pd.getDocumentInformation();
		Element metaData = new Element("metadata");

		String aut = pdm.getAuthor();
		if (aut != null)
			metaData.addContent(new Element("Author").setText(XMLWriter.removeInvalidXMLCharacters(aut)));

		String creator = pdm.getCreator();
		if (creator != null)
			metaData.addContent(new Element("Creator").setText(XMLWriter.removeInvalidXMLCharacters(creator)));

		String title = pdm.getTitle();
		if(title != null)
			metaData.addContent(new Element("Title").setText( XMLWriter.removeInvalidXMLCharacters(title)));

		String producer = pdm.getProducer();
		if(producer != null)
			metaData.addContent(new Element("Producer").setText(XMLWriter.removeInvalidXMLCharacters(producer)));

		return metaData;
	}

	/**
	 * Does the extraction from a PDF. Harvests metadata. 
	 * Returns a JDOM XML Document with the results. Will log errors to the XML file. 
	 * 
	 * @param is InputStream set up on our pdf file.
	 * @return JDOM XML document
	 * @throws Exception
	 * 
	 */
	public Document getDocument(InputStream is) throws Exception
	{
		return getDocument(is, null, true );
	}
}
