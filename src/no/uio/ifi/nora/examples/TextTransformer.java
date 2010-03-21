package no.uio.ifi.nora.examples;

import no.uio.ifi.nora.extraction.Debox;
import no.uio.ifi.nora.extraction.Dehyphen;
import no.uio.ifi.nora.extraction.PageBreaker;
import no.uio.ifi.nora.extraction.transformations.TextualTransformation;
import org.jdom.*;
import org.jdom.output.*;

public class TextTransformer extends TextualTransformation {

	@Override
	protected String transform(String s) 
	{
		return s.toUpperCase();
	}

	@Override
	protected String getStageName() 
	{
		return "ToUpCase";
	}

	@Override
	protected String getStageVersion() {
		return "1.0";
	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception 
	{
		TextTransformer tt = new TextTransformer();
		tt.recuresive = false;
		
		PageBreaker pb = new PageBreaker();
		Debox debox = new Debox();
		Dehyphen dehyp = new Dehyphen();
		
		Document d = new Document();
		
		Element doc = new Element("document");
		Element e = new Element("body");
		
		doc.addContent(e);
		
		//<<box 117.828 181.48999 21.639755 17.0 >>
		
		e.addContent(new Text("<<box 0.0 0.0 0.0 17.0 >> I'm a text, infor-" +
				"\nmation is long etc. <<box 0.0 0.0 0.0 15.0 >>1"));
		
		e.addContent(new Element("tag").addContent("<<box 0.0 0.0 0.0 17.0 >>I'm a deeper text"));
		e.addContent(new Text("<<box 0.0 0.0 0.0 17.0 >>I'm the epilouge <<p>> <<box 0.0 0.0 0.0 17.0 >>this epilouge has many pages <<p>><<box 0.0 0.0 0.0 17.0 >> indeed"));
			
		d.setRootElement(doc);
		
		new XMLOutputter(Format.getPrettyFormat()).output(d, System.out);	
		
		
		
		d = pb.process(d);
		
		new XMLOutputter(Format.getPrettyFormat()).output(d, System.out);	

		d = debox.process(d);
		//d = tt.process(d);
		
		d = dehyp.process(d);
		

		
		new XMLOutputter(Format.getPrettyFormat()).output(d, System.out);		
	}

}
