package no.uio.ifi.nora.extraction;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class LigRepair { // extends BodyPermuter
/*
Heuristic repair of ligatures, based on the wordlist ligs.txt

You may rebuild the datafile with this lovely bash-grep-perl combo:
for f in "ff" "fi" "fl" "ffi" "ffl"; do grep $f no-en-words | perl -ne 'chomp; print; print " ";' >> ligs.txt; echo >> ligs.txt; done

 */

// BufferedReader d = new BufferedReader(new InputStreamReader(in));

String[] ligs = { "ff", "fi", "fl", "ffi", "ffl", "\u00e6", "\u00f8", "\u00e5" };
HashMap<String,Integer> ligd;
Pattern munged;
Pattern ctrl;

public LigRepair() {
	String noralph = "a-zA-Z\u00e6\u00f8\u00e5\u00c6\u00d8\u00c5";
	String ctrlchar = "\\u0000-\\u0008\\u000b\\u000c\\u000d\\u000e-\\u001f\\u007f";
		// exclude TAB and LF, include DEL (0x7f)
		// NB: CR (u000d) should be excluded in the case of CR-LF line endings
	ctrl = Pattern.compile("["+ctrlchar+"]");
	munged = Pattern.compile("["+noralph+"]*+["+ctrlchar+"]["+noralph+"]+");
	
	try { 
		load();
	} catch (Exception e) {
			
		// ligd = new HashMap<String,Integer>();
		System.err.println("LigRepair data file not found or malformed.");
	}
}

	public void load() throws IOException, FileNotFoundException {
		InputStream is = this.getClass().
				getClassLoader().getResourceAsStream("no/uio/ifi/nora/ligs.txt");
		BufferedReader datafile = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		ligd = new HashMap<String,Integer>();

		String line = "";
		String key = "";
		String[] words;
		String[] parts;

		for (int l = 0; (line = datafile.readLine())!=null ; l++) {
			words = line.split("\\s+");
			for (String word : words) {
				if (word.length()>3) {
					parts = word.split(ligs[l]);
					if (parts.length==2) {
						key = parts[0] + "|" + parts[1];
						ligd.put(key, l);
					}
				}
			}
			
		}
		
		datafile.close();

	} // load()

	public String repairLig(String s) {
		if (ligd==null || ligd.size()==0) {
			return s;
		}
		ArrayList<ArrayList<String>> freqs = new ArrayList<ArrayList<String>>();
		for (int i=0; i<ligs.length; i++) {
			freqs.add(new ArrayList<String>());
		}
		String[] mapping = new String[ligs.length];
		
		Matcher m = munged.matcher(s);
		Matcher c;
		String found = "";
		String key = "";
		ArrayList<String> sublist;
		while (m.find()) {
			found = m.group(0);
			c = ctrl.matcher(found);
			c.find();
			key = found.substring(0, c.start()) + "|" + found.substring(c.end());
			if (ligd.containsKey(key)) {
				int lignum = ligd.get(key).intValue();
				int idx = -1;
				sublist = freqs.get(lignum);
				if ((idx = sublist.indexOf(c.group())) >= 0) {
					sublist.set(idx+1, Integer.toString(1+Integer.parseInt(sublist.get(idx+1))));
				} else {
					sublist.add(c.group(0));
					sublist.add("1");
				}
			} else { // no match
				//System.err.print(key + " ");
			}
		}
		ArrayList<String> ligf;
		for (int i=0; i<mapping.length; i++) {
			int max = -1;
			int maxidx = -1;
			int cur = 0;
			ligf = freqs.get(i);
			for (int j=0; j<(ligf.size()-1); j+=2) {
				cur = Integer.parseInt(ligf.get(j+1));
				if (cur > max) {
					max = cur;
					maxidx = j;
				}
			}
			if (maxidx >= 0) {
				mapping[i] = ligf.get(maxidx);
			} else {
				mapping[i] = "";
			}
		}
		ArrayList<String> trafo = new ArrayList<String>();
		for (int i=0; i<mapping.length; i++) {
			trafo.add(mapping[i]);
			trafo.add(ligs[i]);
			// System.err.print(Character.codePointAt(mapping[i], 0) + " ");
		}
		//System.err.println(trafo.toString());
		
		trafo.trimToSize();
		int len = mapping.length*2;
		for (int i=0; i<16; i+=2) { // i<trafo.size() doesn't appear to work
			if (trafo.get(i)!="") {
				s = s.replace(trafo.get(i), trafo.get(i+1));
			}
		}
		return s;
	}
	
	//@Override
   protected String getAppName() {
		return "LigRepair";
	}
	//@Override
	protected String getAppVer() {
		return "0.1";
	}

	//@Override
	protected String permute(String s) {
		return repairLig(s);
	}
	
	public static void main(String[] args) {
		String test3 = "a\u0001ect a\u0001ably  \u0002gure \u0012gur \u0012gur  de\u0003ate a\u0004nity a\u0005uence supere\u0005uent fortre\u0012elige fly\u0009ller";
		// de\u0002ne
		LigRepair testy = new LigRepair();
		System.out.println(testy.repairLig(test3));
	
	}
	
}
