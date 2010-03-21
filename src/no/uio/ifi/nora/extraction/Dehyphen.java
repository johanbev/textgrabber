package no.uio.ifi.nora.extraction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import no.uio.ifi.nora.extraction.transformations.TextualTransformation;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * Dehyphenates based on wordlist in norahyph.xml
 * @author olasba
 */

public class Dehyphen extends TextualTransformation {

	/* Dehyphenates words at linebreaks, based on orthographic rules and
	(optionally) word frequencies, which are loaded from norahyph.xml by 
	default.
	*/
	
	HashMap<String,Integer> hyph;
	Pattern nonalpha;
	Pattern hyscan;
		
	public Dehyphen() {
		hyph = new HashMap<String,Integer>(400000,(float)0.85);
		nonalpha = Pattern.compile("[^a-zA-Z]");
		String noralph = "a-zA-Z\u00e6\u00f8\u00e5\u00c6\u00d8\u00c5";
		hyscan = Pattern.compile("["+noralph+"]+[-"+noralph+"]?["+noralph+"]+[^-"+noralph+"]");
		try {
			load(new File("no/uio/ifi/nora/norahyph.xml"));
		}
		catch (Exception e) {
			System.err.println("Dehyphen data file not found; proceeding without");
		}
	}
	
	private String keylarize(String key) {
		Matcher m = nonalpha.matcher(key);
		key = m.replaceAll(""); // s/[^a-zA-Z]//g;
		return key.toLowerCase();
	}

	public void debugdump() { 
		System.out.println(hyph.toString()); 
		System.out.println(hyph.size());
		}
	
	/* main() routine runs a simple test case
	*/
	public static void main(String[] args) throws FileNotFoundException, IOException, JDOMException {
		//String testinput1 = "Nettverks-teorien informasjons-behandlingen, informasjons-behandlingen? informasjonsbehandlingen;;ba:lu7-;helse-Norge .";
		
		Dehyphen testo = new Dehyphen();
		//testo.addWords(testinput1);
		//System.out.println(testo.keepHyphen("informasjons", "behandlingen") ? "yes" : "no");
		//System.out.println(testo.keepHyphen("hElsE", "nOrGe") ? "yes" : "no");
		
		if (args.length>0) {
			testo.load(new File(args[0]));
			testo.debugdump();
		}
		
		String test7 = "bla bla earnout-\ntilfeller Q-\nBio q-\nBio Q\nbio eye-\ndiseases bla bla oste-\nklokke frontier-myto-\nlogien frontier-\nmytologien Frontier-\nmytologien fronte-\nmytologien TV-\nslave helse-\nNorge innkant-\nNorge bruk-\nog-kast (fanges opp orto-\ngrafisk) bla\nbla bla-\nbla ";
		
		System.out.println(testo.transform(test7));
		
	}
	
	public boolean keepHyphen(String before, String after) {
		String key = keylarize(before+after);
		int pos = -1;
		
		if ((before.length()+after.length())<5) {
			return false;
		}
		if (before.contains("-") || after.contains("-")) {
			return true;
		}
		//char[] aftarr = after.toCharArray();
		if ( Character.isUpperCase(before.charAt(before.length()-1)) 
			|| Character.isUpperCase(after.charAt(0)) ) {
			return true; // TV-slave, helse-Norge
		}
		if (!hyph.containsKey(key)) {
			return false;
		} else {
			pos = hyph.get(key).intValue();
			if (before.length()==pos) {
				return true;
			} else {
				return false;
			}
		}
	

	}

	public void load(File file) throws JDOMException, FileNotFoundException {
		Document doc;
		try { doc = new SAXBuilder().build(file);
		} catch (IOException e) { return;
		}
		Element root = doc.getRootElement();
		List<Element> felems = (List<Element>) root.getChildren();
		String key = "";
		String n = "-1";
		int len = -1;
		int res = -7000;
		for (Element felem : felems) {
			key = felem.getAttribute("k").getValue();
			List<Element> velems = felem.getChildren();
			len = velems.size();
			ArrayList<String> vari = new ArrayList<String>(len*2+1);
			for (Element velem : velems) {
				n = velem.getAttribute("n").getValue();
				vari.add(velem.getText());
				vari.add(n);
			}
			res = hyphenPos(vari);
			if (res > 0) {
				hyph.put(key, res);
			}
			felem.removeContent(); // preserve memory the hackish way?
		}
	}
	
	private int hyphenPos(ArrayList<String> vari) {
		int pos = -1;
		int max = 0;
		int cur = 0;
		String winner = "";
		
		for (int i=0; i<vari.size(); i+=2) {
			cur = Integer.parseInt(vari.get(i+1));
			if (cur > max) {
				max = cur;
				winner = vari.get(i);
				pos = winner.indexOf("-");
			}
		}
		if (	pos > 0 &&
				!hyph.containsKey(keylarize(winner)) &&
				keepHyphen(winner.substring(0,pos), winner.substring(pos+1)) ) {
			return -1; // will be caught by orthographic rules anyway
		} else {
			return pos;
		}
	}

	@Override
	protected String transform(String s) {
		String[] lines = s.split("\\r?\\n");
		String before = "";
		String after = "";
		String out = "";
		String line = "";
		String noralph = "a-zA-Z\u00e6\u00f8\u00e5\u00c6\u00d8\u00c5";
		Pattern eolhy = Pattern.compile("(["+noralph+"]+(-["+noralph+"])*)-\\s*$");
		Pattern bolhy = Pattern.compile("^\\s*(["+noralph+"]+(-["+noralph+"])*)");
			
		Matcher m;
		Matcher b;
		for (int l=0; l<lines.length; l++) {
			line = lines[l];
			if (before != "") {
				b = bolhy.matcher(line);
				if (!b.find()) {
					line = before + " " + line;
				} else {
					after = b.group(1);
					line = line.substring(b.end(1));
					if (keepHyphen(before, after)) {
						line = before + "-" + after + line;
					} else {
						line = before + after + line;
					}
				}
			}
			
			m = eolhy.matcher(line);
			if (m.find()) {
				before = m.group(1);
				out += line.substring(0, m.start(1));
			} else {
				before = "";
				out += line + "\n";
			}
		}
		return out;
	}
	
	@Override
   protected String getStageName() {
		return "Dehyphen";
	}
	@Override
	protected String getStageVersion() {
		return "0.3";
	}

}
