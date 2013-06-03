package roundword.test;

import roundword.Dictionary;
import roundword.GameTable;
import roundword.Player;
import roundword.Word;
import roundword.ui.GameFrame;
import roundword.Constants;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Tests {

	private static Dictionary loadDictionary() {
		Dictionary ris = null;
		try {
			ris = new Dictionary(Constants.dictionaryPath);
		} catch (IOException ex) {
			System.err.println("Error: Impossible to load the dictionary");
			System.exit(-1);
		}
		return ris;
	}

	public static void fakePlayers_test() {

		Dictionary d = loadDictionary();

		List<Player> players = new ArrayList<Player>();
		Player localPlayer = new Player("CiccioBomba", 2);
		Player aLittleDeadPlayer = new Player("QuasiMorto", 3);
		players.add(new Player("Stupido", 0));
		players.add(localPlayer);
		players.add(new Player("Rimbambito", 2));
		players.add(aLittleDeadPlayer);

		GameTable table = new GameTable(players, localPlayer, d);
		GameFrame frame = new GameFrame(table);
		frame.setVisible(true);

		/* Test fake players */
		try {
			Thread th = new Thread(new FakePlayers(table, Constants.dictionaryPath, aLittleDeadPlayer));
			th.start();
		} catch (Exception e) {
		}
	}

	public static void dictionary_test() {
		try {
			Dictionary d = new Dictionary(Constants.dictionaryPath);
			Set<String> ws = d.getWordSet();
			for (String w : ws) {
				if (w.length() > 0) {
					Word w2 = new Word(w);
					System.out.println(w2 + " " + w2.getSubWordBeforeLastSyllable() + "-" + w2.getLastSyllableSubWord());
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
