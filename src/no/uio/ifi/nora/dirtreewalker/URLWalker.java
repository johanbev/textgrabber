package no.uio.ifi.nora.dirtreewalker;

import java.io.*;

import no.uio.ifi.nora.*;
import no.uio.ifi.nora.extraction.*;

public class URLWalker 
{
	Pipeline pipeline = NoraFactory.newPipeline();
	LuceneAdapter la = new LuceneAdapter();
	
	public String getFullTextFromNoraPDF(String url){
        try{
                String XMLResult = pipeline.getXMLRepresentation(url); //Extract from URL and get XMLRep
                return la.getDocument(XMLResult);
        }catch(Exception ex){
                return null;
        }
	}
	
	public static void main(String[] args) throws Exception
	{
		URLWalker u = new URLWalker();
		BufferedReader br = new BufferedReader(new FileReader(new File(args[0])));
		
		String line;
		int fNO = 0;
		while((line = br.readLine()) != null)
		{
			String url = line.split("\"")[5];
			String res = u.getFullTextFromNoraPDF(url);
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(args[1] + fNO++)));
			if(res != null)
				bw.write(res);	
			bw.close();
			System.out.println("Done extracting " + url);
	    }
		
		br.close();
	}
}
