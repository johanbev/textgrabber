package no.uio.ifi.nora.dirtreewalker;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import java.util.concurrent.*;



import no.uio.ifi.nora.extraction.NoraFactory;
import no.uio.ifi.nora.extraction.Pipeline;
import no.uio.ifi.nora.xml.XMLWriter;

/**
 * 
 * @author tobiasvl, johanbev
 *
 */
public class DirTreeWalker {
	File inputPath;
	File outputPath;
	ArrayList<File> files;
	XMLWriter xmlWriter;
	Pipeline pipeline;
	
	ExecutorService exec;

	int errNo = 0;  //serial num for errors.

	/**
	 * 
	 * @param args [input_path, output_path, error_log_path] (last arg not mandatory)
	 */
	public static void main(String[] args) throws Exception {
		if (args.length < 2) { // Check number of arguments
			System.out.println("Usage: java DirTreeWalker input_path " +
			"output_path [error_log_path]"); 

		} else if (args.length == 2) { // /dev/null for errors FIXME stderr?
			new DirTreeWalker(args[0], args[1], "/dev/null").dirTreeWalker();

		} else {
			new DirTreeWalker(args[0], args[1], args[2]).dirTreeWalker();
		}
	}

	public DirTreeWalker(String inputPath, String outputPath, String errorWriter) throws Exception, IOException 
	{
		this.inputPath = new File(inputPath);
		this.outputPath = new File(outputPath);
		xmlWriter = new XMLWriter();

		if (!this.outputPath.isDirectory()) {
			throw new Exception("Output path must be a directory");
		}

		//initialize pipeline:

		pipeline = NoraFactory.newPipeline();
		
		
		
		/* Construct and prepeare Executor */
		exec = Executors.newFixedThreadPool(16);

	}
	
	
	ArrayList<File> inputFiles = new ArrayList<File>();
	ArrayList<File> outputFiles = new ArrayList<File>();
	PDFFilter filter = new PDFFilter();
	private void getFileRec(File cur, String InputPath, String OutputPath, String path)
	{
		
		for(File f : cur.listFiles())
		{
			if( f.isDirectory())
			{
				inputFiles.add(f);
				outputFiles.add(new File(OutputPath + File.separator + path + File.separator + f.getName()));
				getFileRec(f, InputPath, OutputPath, path + File.separator + f.getName());
				continue;
			}
			if( filter.accept(f))
			{
				inputFiles.add(f);
				outputFiles.add(new File(OutputPath + File.separator + path + File.separator + f.getName() + ".xml"));
			}
		}
	}
	

	private int done = 1;

	public void dirTreeWalker() throws Exception {
		
			if (inputPath.isDirectory()) 
			{
				getFileRec(inputPath, inputPath.getAbsolutePath(), outputPath.getAbsolutePath(), "");
			}
			else 
			{
				throw new Exception("Input path invalid");
			}
			
			int i = 0;
			for(File file : inputFiles)
			{
				
				if (file.isDirectory())
				{
					File newDir = outputFiles.get(i);
					newDir.mkdirs();
					i++;
					continue;
				}
				
				File out = outputFiles.get(i); 
				
				ExtractionJob ej = new ExtractionJob(file, out);
				exec.execute(ej);
				i++;

			}
			
			exec.shutdown();
		
	}

	/**
	 * This class represent one Extraction Job.
	 * 
	 * @author	johanbev
	 *
	 */
	class ExtractionJob implements Runnable
	{	
		File in;
		File out;


		/**
		 * 
		 * @param in The file to extract
		 * @param fw The FileWriter to write to, will be closed when done.
		 * @param errorWriter The FileWriter to log errors.
		 */
		public ExtractionJob(File in, File out)
		{
			this.in = in;
			this.out = out;

		}
		/**
		 * The actual extraction and errorhandling.
		 * 
		 * @throws Nothing, this will eat and log all exceptions!
		 */
		private void extract()
		{
			try
			{	
				String s = pipeline.getXMLRepresentationNoWorkers(in);
				OutputStreamWriter osw = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(out)) , "UTF-8");
				if (s != null) 
				{ 
					xmlWriter.write(s,osw);	
				}
				osw.close();
			}
			catch (Exception e)
			{
				System.err.println("Error with file " + in.getName() + "threw " + e.getMessage());
				e.printStackTrace(System.err);
			}
		}
		/**
		 * Starts the extraction.
		 */
		public void run()
		{
			this.extract();
			done++;
		}

	}
}