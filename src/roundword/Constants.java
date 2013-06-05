package roundword;

import java.io.IOException;
import java.util.Random;

public class Constants {
	
	public static final int PointsForWrongWord = -80;
	public static final int PointsForNotReply = -60;
	public static final int PointsForPreviouslyAddedWord = -100;

	public static final int WordToDisplay = 6;

	public static final String dictionaryPath = "dictionaries/it.txt";

	public static final String[] CasualNames = {
			"Ramazzore", "Strapaccioni", "Bomba88", "Sorpreso", "Stilografo", "Trasandato", "Tremolo",
			"Strigi", "Paparazzo", "Perdente46", "Lillo", "Greg", "Python", "C++", "Java"};

	public static final int TimeoutMilliseconds = 5000;

	public static final double PointsPerMilliseconds = 0.02;


	/* Save the information from a game to another */
	public static String nickName;
	public static int portNumber;
	public static String registrarURL;

	public static Dictionary dictionary;

	static {
		Random rnd = new Random();
		nickName = Constants.CasualNames[rnd.nextInt(Constants.CasualNames.length)];
		portNumber = 6000 + rnd.nextInt(1000);
		registrarURL = "http://localhost:8080";

		try {
			dictionary = new Dictionary(Constants.dictionaryPath);
		} catch (IOException ex) {
			System.out.println("Can't load the dictionary");
			System.exit(-1);
		}
	}


}
