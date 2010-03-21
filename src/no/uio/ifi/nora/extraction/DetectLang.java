package no.uio.ifi.nora.extraction;



import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * 
 * @author gisleyt
 * @deprecated We are not (really) allowed to do this by Google, however the results are superior to
 * the nutch-based solution.
 */



public class DetectLang extends BodyPermuter {

	//private final String URL_STRING = "http://ajax.googleapis.com/ajax/services/language/detect?v=1.0&q=";
	
	@Override
	protected String getAppName() {
		return "DetectLang";
	}

	@Override
	protected String getAppVer() {
		return "0.2.1";
	}

	private final String ENCODING = "UTF-8";
	String URL_STRING = "http://ajax.googleapis.com/ajax/services/language/detect?v=1.0&q=";
	
	@Override
	protected String permute(String s) throws Exception 
	{
		String[] pageBreakArray = null;
		//String[] pageBreakArray;
		pageBreakArray = s.split("<<p>>");




		for (int i = 0, pNum = 0 ; i < pageBreakArray.length ; i++) 
		{
			if (pageBreakArray[i].equals(""))
				continue;
			
			
			StringBuilder url = new StringBuilder();
			url.append(URL_STRING);
			String newString = makeURLFriendly(pageBreakArray[i]);

			

			url.append(newString);
			Thread.sleep(50);
			HttpURLConnection uc = (HttpURLConnection) new URL(url.toString()).openConnection();

			String result = toString(uc.getInputStream());			

			int confidenceIndex = result.indexOf("confidence");
			int langIndex = result.indexOf("language");		
			String lang = result.substring(langIndex+11,langIndex+13);
			
		
			
			
			String conf = result.substring(confidenceIndex+12,confidenceIndex+15);
			if(conf.equalsIgnoreCase("ata"))
			{
				Thread.sleep(2500);
				i--; continue;
			}
			String pageXML;
			pageXML = "\n<page number=\"" + pNum++ +"\" lang=\""+lang+"\" conf=\""+conf+"\">\n";

			pageBreakArray[i] = pageXML+pageBreakArray[i]+"</page>";

			uc.getInputStream().close();
		}

		//return s;
		
		StringBuilder newS = new StringBuilder();
		for (int i=0; i < pageBreakArray.length ; i++) {
			newS.append(pageBreakArray[i]);
			//newS.append("<<p>>");
		}
		
		return newS.toString();
	}

	
	
    private  String toString(InputStream inputStream) throws Exception {
    	StringBuilder outputBuilder = new StringBuilder();
    	try {
    		String string;
    		if (inputStream != null) {
    			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, ENCODING));
    			while (null != (string = reader.readLine())) {
    				outputBuilder.append(string).append('\n');
    			}
    		}
    	} catch (Exception ex) {
    		throw new Exception("[google-api-translate-java] Error reading translation stream.", ex);
    	}
    	return outputBuilder.toString();
    }
	
    private String makeURLFriendly(String s) throws Exception {
    	
    		String encodedurl = s;
    		encodedurl = encodedurl.replaceAll("<\\\\?.+>", ""); 
    		encodedurl = URLEncoder.encode(encodedurl,ENCODING);
			if (encodedurl.length() > 1500) {
				int lastIndex = encodedurl.substring(0,1500).lastIndexOf("%");
				encodedurl = encodedurl.substring(0,lastIndex);
				
				}
    		return encodedurl;
    	
    }
  
}
