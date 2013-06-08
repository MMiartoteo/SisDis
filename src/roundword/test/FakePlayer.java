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

public class FakePlayer extends Thread implements GameTable.EventListener {

	List<String> dictionary;

	GameTable t;

	volatile boolean localPlayerIsPlaying;
	volatile boolean stop = false;
	Thread deadThread;


	public FakePlayer(GameTable gameTable, String dictionaryPath) throws IOException {
		this.t = gameTable;
		localPlayerIsPlaying = (t.getTurnHolder() == t.getLocalPlayer());
		this.t.addEventListener(this);
		loadDictionary(dictionaryPath);
	}

	public void run() {
		Random rnd = new Random();
		loop: while (!stop) {

			//Wait if the own player is playing
			synchronized (this) {
				while (!localPlayerIsPlaying) {
					try {
						wait();
					} catch (InterruptedException e) {
						if (stop) break loop; //We need to exit if our hour has became
					}
				}
			}

			//Simulate that we are thinking
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) { }

			Word w = null;

			if (rnd.nextDouble() < 0.4) {

				if (t.getWordsList().size() == 0) {
					w = new Word(dictionary.get(rnd.nextInt(dictionary.size())));
				} else {
					String syllabeToFind = t.getWordsList().get(0).getLastSyllableSubWord();
					for (String s : dictionary) {
						if ((s.length() >= syllabeToFind.length())
								&& (syllabeToFind.compareTo(s.substring(0, syllabeToFind.length())) == 0)
								&& (rnd.nextDouble() < 0.05)) {
							w = new Word(s);
						}
					}

				}

			}

			//Add the word
			if (w != null) System.out.println("Word: " + w + ": " + w.getValue());
			else System.out.println("No words founded");
			t.addWord(w, rnd.nextInt(5), true);

			//Wait for the next turn
			localPlayerIsPlaying = false;

		}

		/*
			Facendo morire qui il fakeplayer, e se chi aveva il turno al momento della fine del gioco era proprio il
			un fakeplayer, allora succederà che questo invii il messaggio word a tutti, e poi appena riceve il messaggio
			word di ritorno comincia a mandare gli ack a tutti, compreso se stesso, dove chiama la finishGame, e quindi
			terminerà. In questo modo però gli altri nodi devono risvegliarsi e mandare il messaggio di word.
		 */
		//System.exit(0);
		deadThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) { }
				System.exit(0);
			}
		});
		deadThread.start();
	}

	public void newWordAdded(Player p, Word w, long milliseconds, WordAddedState state) {
		String word = "";
		if (w != null) word = w.toString();
		System.out.println(p + " inserted '" + word);
	}

	public void playersPointsUpdate() {}

	synchronized public void turnHolderChanged(Player oldTurnHolder, Player newTurnHolder) {
		if (newTurnHolder == t.getLocalPlayer()) {
			localPlayerIsPlaying = true;
			notify();
		}
	}

	public void gameFinished(Player winnerPlayer, List<Player> players) {
		stop = true;
		this.interrupt();
	}

	public void failure(String msg) {
		System.out.println("Errore: " + msg);
		System.exit(-1);
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