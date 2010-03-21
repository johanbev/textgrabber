package no.uio.ifi.nora.extraction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * Gathers statistics on hyphenated words (all the words, in fact).
 * @author olasba
 */

public class HyphenStat {
	
	HashMap<String,ArrayList<String>> forms;
	Pattern nonalpha;
	Pattern hyscan;
		
	public HyphenStat() {
		forms = new HashMap<String,ArrayList<String>>(1900000,(float)0.85);
//		forms = new HashMap<String,String[]>(700000,0.8);
		nonalpha = Pattern.compile("[^a-zA-Z]");
		String noralph = "a-zA-Z\u00e6\u00f8\u00e5\u00c6\u00d8\u00c5";
		hyscan = Pattern.compile("["+noralph+"]+[-"+noralph+"]?["+noralph+"]+[^-"+noralph+"]");
	}
	
	private String keylarize(String key) {
		Matcher m = nonalpha.matcher(key);
		key = m.replaceAll(""); // s/[^a-zA-Z]//g;
		return key.toLowerCase();
	}
	
	private void addWord(String word) {
		int i = -1;
		String key = keylarize(word);
		if (word.length()<5) 
			return;

		if (!forms.containsKey(key)) {
			// first occurrence
			forms.put(key, new ArrayList<String>(3));
		}	
		ArrayList<String> vari = forms.get(key);
		i = vari.indexOf(word);
		if ( i < 0 ) { // first occurrence of this word form subvariant
			vari.add(word);
			vari.add("1"); // System.out.print(word);
		} else {
			String val = vari.get(i+1);
			vari.set(i+1, 
				Integer.toString(Integer.parseInt(val)+1)
			); // word form variant counts are stored as strings due to strong typing
			// System.out.print(word);
		}
		vari.trimToSize();
	}
	
	public void addWords(String words) {
		String word = "";
		Matcher m = hyscan.matcher(words);
		while (m.find()) {
			word = m.group();
			word = word.substring(0, word.length()-1); // chop
			if (word.length()<50) {
				addWord(word);
			}
		}
	}
	
	public void debugdump() { System.out.println(forms.toString()); }
	
	// simple test case
	public static void main(String[] args) throws FileNotFoundException, IOException, JDOMException {
		String testinput1 = "Nettverks-teorien informasjons-behandlingen, informasjons-behandlingen? informasjonsbehandlingen;;ba:lu7-;helse-Norge .";
		
		HyphenStat testo = new HyphenStat();
		testo.addWords(testinput1);
		System.out.println(testo.keepHyphen("informasjons", "behandlingen") ? "yes" : "no");
		System.out.println(testo.keepHyphen("hElsE", "nOrGe") ? "yes" : "no");
		testo.debugdump();
		
		if (args.length>1) {
			testo.load(new File(args[1]));
		}
		if (args.length>0) {
			//LineNumberReader infr = new FileReader(args[0]);
			String thisLine;
			FileInputStream fin =  new FileInputStream(args[0]);
			BufferedReader myInput = new BufferedReader(new InputStreamReader(fin));
			while ((thisLine = myInput.readLine()) != null) {  
				testo.addWords(thisLine);
				// strb.append(thisLine+"\r\n");
			}
			testo.save(new File("testohyp.xml"));
		}
		
	
	}
	
	public boolean keepHyphen(String before, String after) {
		if (before.contains("-") || after.contains("-")) {
			return true;
		}
		String hyp = before + "-" + after;
		String nullhyp = before + after;
		String key = keylarize(nullhyp);
		
		if (!forms.containsKey(key)) {
			return false;
		} else {
			ArrayList<String> vari = forms.get(key);
			int hypcount = -1;
			int nullcount = -1;
			int hypidx = vari.indexOf(hyp);
			int nullidx = vari.indexOf(nullhyp);
			if (hypidx < 0) {
				return false;
			} else if (nullidx < 0) {
				return true;
			} else {
				hypcount = Integer.parseInt(vari.get(hypidx+1));
				nullcount = Integer.parseInt(vari.get(nullidx+1));
				if (hypcount < nullcount) {
					return false;
				} else {
					return true;
				}
			}
		}
	}
	
	public void load(File file) throws JDOMException, IOException, FileNotFoundException {
		Document doc = new SAXBuilder().build(file);
		Element root = doc.getRootElement();
		List<Element> felems = (List<Element>) root.getChildren();
		String key = "";
		String n = "-1";
		int len = -1;
		for (Element felem : felems) {
			key = felem.getAttribute("k").getValue();
			List<Element> velems = felem.getChildren();
			len = velems.size();
			ArrayList<String> vari = new ArrayList<String>(len);
			for (Element velem : velems) {
				n = velem.getAttribute("n").getValue();
				vari.add(velem.getText());
				vari.add(n);
			}
			vari.trimToSize();
			forms.put(key, vari);
		}
	}
	
	public void save(File file) throws FileNotFoundException, UnsupportedEncodingException {
		PrintStream outs = new PrintStream(file, "UTF-8");
		outs.println("<forms>");
		Set<String> keys = forms.keySet();
		for (String key : keys) {
			outs.println("<f k=\"" + key + "\">");
			ArrayList<String> vari = forms.get(key);
			for (int i=0; i<vari.size(); i+=2) {
				outs.println("<v n=\""+vari.get(i+1)+"\">"+vari.get(i)+"</v>");
			}
			outs.println("</f>");
		}
		outs.println("</forms>");
		outs.close();
	}
	
}


