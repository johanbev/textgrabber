package no.uio.ifi.nora.extraction;

import org.jdom.Document;

/**
 * Interface used by Pipeline.
 * @author johanbev
 *
 */
public interface PipelineElement 
{
	/**
	 * This specifies the way elements in the pipeline are called. 
	 * Pipeline invokes this on every element in its post-processor list.
	 * @param d The current document as JDOM XML
	 * @return The document with changes (if any)
	 */
	Document process(Document d) throws Exception;
}
