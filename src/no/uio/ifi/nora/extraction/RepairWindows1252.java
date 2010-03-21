package no.uio.ifi.nora.extraction;

import no.uio.ifi.nora.extraction.transformations.TextualTransformation;

/**
 * Maps Windows-1252 codepoints to proper unicode.
 * @author johanbev
 */
public class RepairWindows1252 extends TextualTransformation {

	@Override
	protected String getStageName() {
		return "RepairWindows1252";
	}

	@Override
	protected String getStageVersion() {
		return "0.1.1";
	}

	@Override
	protected String transform(String s) throws Exception 
	{

		// remap codepoinds 0080-0099 to proper unicode
		StringBuilder out = new StringBuilder(s.length());
		for(int i = 0; i < s.length(); i++)
		{
			int codePoint = s.codePointAt(i);
			if((codePoint > 0x0080 && codePoint > 0x009F) || codePoint < 0x0080)
			{
				out.append(s.charAt(i));
				continue;
			}

			switch(codePoint)
			{
			case 0x0080: out.append("€"); break;
			case 0x0082: out.append("‚"); break;
			case 0x0083: out.append("ƒ"); break;
			case 0x0084: out.append("„"); break;
			case 0x0085: out.append("..."); break;
			case 0x0086: out.append("†"); break;
			case 0x0087: out.append("‡"); break;
			case 0x0088: out.append("ˆ"); break;
			case 0x0089: out.append("‰"); break;
			case 0x008A: out.append("Š"); break;
			case 0x008B: out.append("‹"); break;
			case 0x008C: out.append("Œ"); break;
			case 0x008E: out.append("Ž"); break;
			case 0x0091: out.append("‘"); break;
			case 0x0092: out.append("’"); break;
			case 0x0093: out.append("“"); break;
			case 0x0094: out.append("”"); break;
			case 0x0095: out.append("•"); break;
			case 0x0096: out.append("–"); break;
			case 0x0097: out.append("—"); break;
			case 0x0098: out.append("˜"); break;
			case 0x0099: out.append("™"); break;
			case 0x009A: out.append("š"); break;
			case 0x009B: out.append("›"); break;
			case 0x009C: out.append("œ"); break;
			case 0x009E: out.append("ž"); break;
			case 0x009F: out.append("Ÿ"); break;
			default: break;
			}
		}

		return out.toString();
	}

}
