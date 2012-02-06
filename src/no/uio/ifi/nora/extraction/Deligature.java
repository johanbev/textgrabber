package no.uio.ifi.nora.extraction;


/**
 * 
 * @author olasba
 *
 */
public class Deligature extends BodyPermuter
{
	@Override
	protected String permute(String patient) 
	{
		String[] substs = { "\u2026", "...", "\ufb00", "ff", "\ufb03", "ffi", "\ufb04", "ffl", "\ufb01", "fi", "\ufb02", "fl", "\ufb05", "ft", "\u0133", "ij", "\u0132", "IJ", "\ufb06", "st", "\ufb05", "sT", "\u01c7", "LJ", "\u01c8", "Lj", "\u01c9", "lj", "\u01ca", "NJ", "\u01cb", "Nj", "\u01cc", "nj", "\ue03b", "ch", "\ue03a", "ck", "\ue038", "fk", "\ue039", "ft", "\ue04d", "ss", "\u02a3", "ts", "\u02a3", "dz", "\u02a4", "dZ", "\u02a8", "tc", "\u02a7", "tZ", "\u02a5", "dZh", "\u00a8A", "\u00c4", "A\u00a8", "\u00c4", "\u00a8E", "\u00cb", "E\u00a8", "\u00cb", "\u00a8I", "\u00cf", "I\u00a8", "\u00cf", "\u00a8O", "\u00d6", "O\u00a8", "\u00d6", "\u00a8U", "\u00dc", "U\u00a8", "\u00dc", "\u00a8Y", "\u0178", "Y\u00a8", "\u0178", "\u00a8a", "\u00e4", "a\u00a8", "\u00e4", "\u00a8e", "\u00eb", "e\u00a8", "\u00eb", "\u00a8i", "\u00ef", "i\u00a8", "\u00ef", "\u00a8o", "\u00f6", "o\u00a8", "\u00f6", "\u00a8u", "\u00fc", "u\u00a8", "\u00fc", "\u00a8y", "\u00ff", "y\u00a8", "\u00ff", "\u02c6A", "\u00c2", "A\u02c6", "\u00c2", "\u02c6E", "\u00ca", "E\u02c6", "\u00ca", "\u02c6I", "\u00ce", "I\u02c6", "\u00ce", "\u02c6O", "\u00d4", "O\u02c6", "\u00d4", "\u02c6U", "\u00db", "U\u02c6", "\u00db", "\u02c6a", "\u00e2", "a\u02c6", "\u00e2", "\u02c6e", "\u00ea", "e\u02c6", "\u00ea", "\u02c6i", "\u00ee", "i\u02c6", "\u00ee", "\u02c6o", "\u00f4", "o\u02c6", "\u00f4", "\u02c6u", "\u00fb", "u\u02c6", "\u00fb", "\u02c7L", "L\u2019", "L\u02c7", "L\u2019", "\u02c7S", "\u0160", "S\u02c7", "\u0160", "\u02c7Z", "\u017d", "Z\u02c7", "\u017d", "\u02c7d", "d\u2019", "d\u02c7", "d\u2019", "\u02c7l", "l\u2019", "l\u02c7", "l\u2019", "\u02c7s", "\u0161", "s\u02c7", "\u0161", "\u02c7t", "t\u2019", "t\u02c7", "t\u2019", "\u02c7z", "\u017e", "z\u02c7", "\u017e", "\u00b0A", "\u00c5", "A\u00b0", "\u00c5", "\u00b0U", "\u016e", "U\u00b0", "\u016e", "\u00b0a", "\u00e5", "a\u00b0", "\u00e5", "\u00b0u", "\u016f", "u\u00b0", "\u016f" };
		// ! tildes cause problems with URLs
		//String[] tildes = { "A", "\u00c3", "A", "\u00c3", "N", "\u00d1", "N", "\u00d1", "O", "\u00d5", "O", "\u00d5", "a", "\u00e3", "a", "\u00e3", "n", "\u00f1", "n", "\u00f1", "o", "\u00f5", "o", "\u00f5" };
		for (int i = 0; i < substs.length; i += 2) {
			patient = patient.replaceAll(substs[i], substs[i+1]);
		}
		return patient;
	}

	@Override
	protected String getAppVer() 
	{
		return "0.1";
	}
	
	@Override
	protected String getAppName()
	{
		return "Deligature";
	}
}
