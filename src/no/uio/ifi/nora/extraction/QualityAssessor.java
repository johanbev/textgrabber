package no.uio.ifi.nora.extraction;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
/**
 * Aims to provide a way to score files or strings on how much they resemble natural language.
 * Currently EXPERIMENTAL!
 * @deprecated
 * @author johanbev
 *
 */
public class QualityAssessor 
{
	private double score()
	{
		return 0.33 * baddestBadness + 0.66 * (totalBadness / lines); //FIXME AND SOME NORMALIZING CONSTANT
	}

	private final double max(double a, double b)
	{
		if (a > b) return a; return b;
	}

	double currentBadness = 0;
	double baddestBadness = 0;
	double totalBadness = 0;
	double lines = 0;
	//this will be somewhat monstrous
	private void check(String s)
	{
		char newline = '\n';
		char space = ' ';
		int lineChars = 0;
		int lineSpaces = 0;
		int currentWordLength = 0;

		for( int i = 0; i < s.length(); i++)
		{
			char currentChar = s.charAt(i);

			if(currentChar == space)
			{
				//FIXME have a book of stopwords here to reduce some overbadness
				lineSpaces++;
				currentBadness += scoreSpaces(currentWordLength);
				currentWordLength = 0;
				continue;
			}
			if(currentChar != newline)
			{
				lineChars++;
				currentWordLength++;
				continue;
			}

			//we have now read a line of text
			baddestBadness = max(baddestBadness,currentBadness);
			totalBadness += currentBadness;
			currentBadness = 0;
			lines++;
		}	   	
	}

	/**
	 * Gives a score based on number of chars and spaces
	 * @param chars
	 * @param spaces
	 * @return
	 */
	private double scoreSpaces(int chars)
	{
		final double avgWordLength = 6.6d; //FIXME taken right out of the blue, should be adjusted/confirmed!
		final int badness = 66; //FIXME ditto
		return Math.pow((1 - (chars / avgWordLength)), 2) * badness;
	}

	public double score(File f) throws IOException
	{
		FileReader fr = new FileReader(f);
		BufferedReader br = new BufferedReader(fr);
		String line;
		StringBuilder sb = new StringBuilder();

		while((line = br.readLine()) != null)
		{
			sb.append(line + '\n');
		}

		check(sb.toString());

		return score();
	}

	public static void main(String[] args) throws Exception
	{
		QualityAssessor qa = new QualityAssessor();
		File f = new File(args[0]);
		System.out.println(qa.score(f));
	}
}