package no.uio.ifi.nora.dirtreewalker;

import java.io.File;
import java.io.FileFilter;

/**
 * @author tobiasvl
 *
 * Filter for .pdf files in extraction
 */
public class PDFFilter implements FileFilter {
	public boolean accept(File file) {
		return ((file.isFile() && file.getName().endsWith(".pdf")) || file.isDirectory());
	}
}