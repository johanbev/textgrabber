package no.uio.ifi.nora.examples;

import java.util.List;

import org.jdom.*;
import org.jdom.output.*;
import org.jdom.filter.*;

public class InterTags {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception
	{
		String a = "primus";
		String b = "pares";
		
		Document d = new Document();
		Element e = new Element("body");
		e.addContent(new Text(a));
		e.addContent(new Element("Rakanishu").setText("aet"));
		e.addContent(new Text(b));
		d.addContent(e);
		
		System.out.println(e.getText());
		
		Element el = (Element)d.getContent(new ElementFilter("body")).get(0);
		
		System.out.println(el.getText());
		new XMLOutputter().output(d, System.out);
		//program is now done
		}
}