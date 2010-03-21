package no.uio.ifi.nora.extraction;


/**
 * 
 * @author johanbev
 *
 */
public class Depager extends BodyPermuter {

	@Override
	protected String getAppName() {
		return "Depager";
	}

	@Override
	protected String getAppVer() {
		return "0.3";
	}

	@Override
	protected String permute(String s) 
	{
		//Pattern p = Pattern.compile("<<p>>\\n\\d+\\n");
		String rep = "(\\s*\\n+\\s*\\d+\\s*\\n+)?<<p>>(\\n+\\s*\\d+\\n+)?";
		s = s.replaceAll(rep, "<<p>>");
		return s;
	}

}
