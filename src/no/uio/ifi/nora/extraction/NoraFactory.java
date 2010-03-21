package no.uio.ifi.nora.extraction;
import java.util.LinkedList;

/**
 * This class is a factory for Pipeline instances for the integration with NORA.
 * @author johanbev
 *
 */
public class NoraFactory {
	/**
	 * Creates a new pipeline instance with the default settings for NORA.
	 * @return A new Pipeline instance with the default settings for NORA.
	 */
	///Changes in the delivery to Morten is to go here.
	///This should produce a pipeline instance with the grabbers and procs
	///and settings we recommend for morten.
	//At present, we do not add any post-procs here.
	public static Pipeline newPipeline()
	{
		LinkedList<PipelineElement> postProc = new LinkedList<PipelineElement>();
		
		postProc.add(new PageBreaker());
		postProc.add(new Debox());
		postProc.add(new RepairWindows1252());
		postProc.add(new LanguageDetector());
		
		postProc.add(new Dehyphen());
		postProc.add(new RemovePageNumbers());
		
		return new Pipeline(new TextGrabber(), postProc);
	}
}