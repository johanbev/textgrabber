package no.uio.ifi.nora.xml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * Used to write XMLWritable
 * @author johanbev, tobiasvl
 */
public class XMLWriter 
{
	/**
	 * Write the XML document to file
	 * @param doc The JDOM XML document to write to file
	 * @param targetFile Target file of the XML
	 * @throws IOException
	 */
	public void write(Document doc, File targetFile) throws IOException
	{
		BufferedWriter buf = new BufferedWriter(new FileWriter(targetFile));

		try 
		{
			Format f = Format.getPrettyFormat(); // make XML pretty (indent etc)
			XMLOutputter xmlout = new XMLOutputter(f);
			String s = xmlout.outputString(doc);
			s = s.replaceAll("<!\\[CDATA\\[", "").replaceAll("]]>", ""); // we assume safe XML now; get rid of CDATA tag
			buf.write(s);
		}

		finally
		{
			if(buf != null)
				buf.close();
		}
	}

	/**
	 * Write the XML document to stream
	 * @param doc The JDOM XML document to write to stream
	 * @param stream Target stream of the XML
	 * @throws IOException
	 */
	public void write(Document doc, OutputStreamWriter stream) throws IOException
	{
		BufferedWriter buf = new BufferedWriter(stream);
		Format f = Format.getPrettyFormat();
		XMLOutputter xmlout = new XMLOutputter(f);
		String s = xmlout.outputString(doc).replaceAll("<!\\[CDATA\\[", "").replaceAll("]]>", ""); // we assume safe XML now; get rid of CDATA
		buf.write(s);
		buf.close();
	}
	
	public void write(String s, OutputStreamWriter stream) throws IOException
	{
		BufferedWriter buf = new BufferedWriter(stream);
		buf.write(s);
		buf.close();
	}

	/**
	 * Remove invalid XML character code points (shamelessly stolen from the internet)
	 * @param s String to strip invalid XML characters from
	 * @return The string stripped of invalid XML characters
	 */
	public static String removeInvalidXMLCharacters(String s)
	{
		StringBuilder out = new StringBuilder(); // Used to hold the output.
		int codePoint; // Used to reference the current character.
		int i=0;

		while(i<s.length()) {
			codePoint = s.codePointAt(i); // This is the unicode code of the character.

			if ((codePoint == 0x9) || // Consider testing larger ranges first to improve speed.
					(codePoint == 0xA) ||
					(codePoint == 0xD) ||
					((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
					((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) ||
					((codePoint >= 0x10000) && (codePoint <= 0x10FFFF))) {
				out.append(Character.toChars(codePoint));
			}				

			i+= Character.charCount(codePoint); // Increment with the number of code units (java chars) needed to represent a Unicode char.  
		}

		return out.toString();
	} 
}
