package roundword.test;

import roundword.GameTable;
import roundword.Player;
import roundword.Word;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class FakePlayers implements Runnable, GameTable.EventListener {

		List<String> dictionary;

		GameTable t;
		volatile boolean localPlayerIsPlaying;

		public FakePlayers(GameTable gameTable, String dictionaryPath) throws IOException {
			this.t = gameTable;
			localPlayerIsPlaying = (t.getTurnHolder() == t.getLocalPlayer());
			this.t.addEventListener(this);
			loadDictionary(dictionaryPath);
		}

		public void run() {
			Random rnd = new Random();
			while (true) {

				//Wait if the own player is playing
				synchronized (this) {
					if (localPlayerIsPlaying) try { wait(); } catch (InterruptedException e) {}
				}

				//Simulate that the other player is thinking
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) { }

				Word w = null;

				if (t.getWordsList().size() == 0) {
					w = new Word(dictionary.get(rnd.nextInt(dictionary.size())));
				} else {
					String syllabeToFind = t.getWordsList().get(0).getLastSyllableSubWord();
					for (String s : dictionary) {
						if ((s.length() >= syllabeToFind.length())
								&& (syllabeToFind.compareTo(s.substring(0, syllabeToFind.length())) == 0)
								&& (rnd.nextDouble() < 0.1)) {
							w = new Word(s);
						}
					}

				}

				//Add the word
				System.out.println("PlayingPlayer: " + t.getTurnHolder() + ": " + t.getTurnHolder().getPoints());

				if (w != null) System.out.println("Word: " + w + ": " + w.getValue());
				else System.out.println("No words founded");
				t.addWord(w, 1);

				System.out.println("PlayingPlayer: " + t.getTurnHolder() + ": " + t.getTurnHolder().getPoints());
				t.nextTurn();
				System.out.println("NextTurn \n--\n");

			}
		}

		public void newWordAdded(Word w) {
			if (t.getTurnHolder() == t.getLocalPlayer()) t.nextTurn();
		}
		public void playersPointsUpdate() {}
		synchronized public void turnHolderChanged(Player oldTurnHolder, Player newTurnHolder) {
			if (newTurnHolder == t.getLocalPlayer()) localPlayerIsPlaying = true;
			if (oldTurnHolder == t.getLocalPlayer()) {
				localPlayerIsPlaying = false;
				notify();
			}

		}

		private void loadDictionary(String path) throws IOException {
			dictionary = new ArrayList<String>();
			BufferedReader br = new BufferedReader(new FileReader(path));
			String word = br.readLine();
			while(word != null) {
				dictionary.add(word.toUpperCase());
				word = br.readLine();
			}
			br.close();
		}

}