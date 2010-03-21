package no.uio.ifi.nora.examples;

import no.uio.ifi.nora.LuceneAdapter;
import no.uio.ifi.nora.extraction.NoraFactory;
import no.uio.ifi.nora.extraction.Pipeline;

public class NoraExample 
{
	public static void main(String[] args) throws Exception
	{
		Pipeline pipeline = NoraFactory.newPipeline(); //Get a pipeline instance for use with Nora
		LuceneAdapter la = new LuceneAdapter(); //Create new Adapter to read xml and create human readable text
		
		String XMLResult = pipeline.getXMLRepresentation("http://www.ub.uit.no/munin/bitstream/10037/262/1/article.pdf"); //Extract from URL and get XMLRep
		System.out.println(XMLResult);
		System.out.println(la.getDocument(XMLResult)); //Parse it with LuceneAdapter, Remember this is an UTF-8 String
		
	}
}
