package roundword.test;

import roundword.GameTable;
import roundword.Player;
import roundword.Word;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class FakePlayers extends Thread implements GameTable.EventListener {

		List<String> dictionary;

		GameTable t;
		volatile boolean localPlayerIsPlaying;
		volatile boolean stop = false;
		Player aLittleDeadPlayer;

		Runnable endTimeListener;

		public FakePlayers(GameTable gameTable, String dictionaryPath, Player aLittleDeadPlayer) throws IOException {
			this.t = gameTable;
			this.aLittleDeadPlayer = aLittleDeadPlayer;
			localPlayerIsPlaying = (t.getTurnHolder() == t.getLocalPlayer());
			this.t.addEventListener(this);
			loadDictionary(dictionaryPath);
		}

		public void run() {
			Random rnd = new Random();
			int played = 0;

			while (!stop) {

				//Wait if the own player is playing
				synchronized (this) {
					while (localPlayerIsPlaying) try { wait(); } catch (InterruptedException e) {
						if (stop) return;
					}
				}

				//Simulate that the other player is thinking
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) { }

				Word w = null;
				Player playingPlayer = t.getTurnHolder();

				if (rnd.nextDouble() < 0.1) {

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

				}

				//Add the word
				System.out.println("PlayingPlayer: " + t.getTurnHolder() + ": " + t.getTurnHolder().getPoints());

				if (w != null) System.out.println("Word: " + w + ": " + w.getValue());
				else System.out.println("No words founded");
				t.addWord(w, 1);

				System.out.println("PlayingPlayer: " + t.getTurnHolder() + ": " + t.getTurnHolder().getPoints());
				nextTurn();
				System.out.println("NextTurn \n--\n");

				if (++played > 8 && aLittleDeadPlayer != null) {
					if (playingPlayer == aLittleDeadPlayer) {
						aLittleDeadPlayer.setActiveStatus(false);
					}
				}

			}

		}

		/**
		 * We go to the next turn, the playing player will be the next of the player list. If you want to set the turn
		 * to another player, for example after a catastrophic event, you can use the {@code setTurnHolder}.
		 * */
		public void nextTurn() {
			Player oldTurnHolder = t.getTurnHolder();
			Player newTurnHolder = null;

			boolean turnHolderFounded = false;
			Iterator<Player> i = t.getPlayersList().iterator();
			while (i.hasNext()) {
				if (oldTurnHolder == i.next()) {
					turnHolderFounded = true;
					break;
				}
			}
			if (!turnHolderFounded) throw new RuntimeException("can't found the current playing player");

			//Find the first active player to assign the turn
			Player tempP;
			if (!i.hasNext()) i = t.getPlayersList().iterator(); //rewind
			while (i.hasNext()) {

				tempP = i.next();
				if (tempP.isActive()) {
					newTurnHolder = tempP;
					break;
				}

				if (tempP == oldTurnHolder) throw new RuntimeException("no player founded that can get the turn");

				if (!i.hasNext()) i = t.getPlayersList().iterator(); //rewind
			}

			t.setTurnHolder(newTurnHolder);

			System.out.println("######### NEXT TURN #########");
			System.out.println(String.format("ORA TOCCA A: %s %s", t.getTurnHolder().getNickName(), t.getTurnHolder().getOrd()));
		}

		public void newWordAdded(Player p, Word w, long milliseconds, WordAddedState state) {
			if (p == t.getLocalPlayer()) nextTurn();
		}

		public void playersPointsUpdate() {

		}

		synchronized public void turnHolderChanged(Player oldTurnHolder, Player newTurnHolder) {
			if (newTurnHolder == t.getLocalPlayer()) localPlayerIsPlaying = true;
			else if (oldTurnHolder == t.getLocalPlayer()) {
				localPlayerIsPlaying = false;
				notify();
			}

		}

		public void gameFinished(Player winnerPlayer, List<Player> players) {
			stop = true;
			this.interrupt();
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


		public void setEndTimeListener(Runnable listener) {

		}

}
