//THIS CLASS IS DEPRECATED!

package no.uio.ifi.nora.dirtreewalker;

import java.io.File;
import java.util.Calendar;

import no.uio.ifi.nora.xml.XMLWritable;

import org.jdom.Attribute;
import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;

public class XMLExtractionReport implements XMLWritable
{
	Element metaData;
	String extractionAgent = "TextGrabber";
	String agentVersion = "0.1"; //Maybe have some automatic versioning here
	Throwable error;
	String body;
	File originalPDF;

	public Document getDocument()
	{
		Element root = new Element("document");
		Document doc = new Document(root);

		//add the extractionAgent
		Element eAg = new Element("extractionAgent");
		Element agent = new Element("agent");
		agent.setAttribute(new Attribute("id", extractionAgent));
		agent.setAttribute(new Attribute("version", agentVersion));

		Element timeStamp = new Element("timeStamp");
		timeStamp.setText(Calendar.getInstance().getTime().toString());
		eAg.addContent(agent);
		eAg.addContent(timeStamp);
		root.addContent(eAg);

		Element origPDF = new Element("originalPDF");
		origPDF.setText(originalPDF.getName());
		root.addContent(origPDF);

		if(error != null)
		{
			Element error = new Element("error");

			Element message = new Element("message").setText(this.error.getMessage());
			//error.addContent(stackTrace);
			error.addContent(message);
			root.addContent(error);
		}

		if(metaData != null)
		{
			root.addContent(metaData);
		}

		if(body != null)
		{
			body = body.replaceAll("]]>", "))>");
			CDATA cdata = new CDATA(body);
			Element bodyXML = new Element("body");
			bodyXML.setContent(cdata);
			root.addContent(bodyXML);

		}
		return doc;
	}

	public XMLExtractionReport(String body, Element metaData, File originalPDF)
	{
		this.originalPDF = originalPDF;
		this.metaData = metaData;
		this.body = body;
	}

	public XMLExtractionReport(Element metaData, File originalPDF) 
	{
		this.originalPDF = originalPDF;
		this.metaData = metaData;
	}


	public XMLExtractionReport(Throwable error, File originalPDF) {
		super();
		this.error = error;
		this.originalPDF = originalPDF;
	}

	public XMLExtractionReport()
	{
		super();
	}
}