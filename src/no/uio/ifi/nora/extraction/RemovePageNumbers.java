package no.uio.ifi.nora.extraction;

import no.uio.ifi.nora.extraction.transformations.TextualTransformation;


/**
 * 
 * @author johanbev
 *
 */
public class RemovePageNumbers extends TextualTransformation {

	@Override
	protected String getStageName() {
		return "Depager";
	}

	@Override
	protected String getStageVersion() {
		return "0.4";
	}

	@Override
	protected String transform(String s) 
	{
		//Pattern p = Pattern.compile("<<p>>\\n\\d+\\n");
		String rep = "(\\s*\\n+\\s*\\d+\\s*\\n+\\z)?(\\A\\n+\\s*\\d+\\n+)?";
		s = s.replaceAll(rep, "");
		return s;
	}

}
