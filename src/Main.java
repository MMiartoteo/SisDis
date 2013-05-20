import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import roundword.*;
import roundword.ui.*;
import roundword.net.*;

public class Main {

	public static void main(String[] args) {
		
		Peer p = new Peer("p1", "100", "9000", null);
		p.send_msg(new Msg("127.0.0.1", Msg.MsgType.HELLO, ""));
		
		/*
		 * Qui si dovrebbe chiamare il server per dargli la disponibilita',
		 * aspettare la risposta e poi avviare il gioco
		 * */

		Player ownPlayer = new Player("Miro");
		List<Player> pl = new ArrayList<Player>();
		pl.add(ownPlayer);
		pl.add(new Player("Ciccio"));
		pl.add(new Player("Tizio"));
		GameTable table = new GameTable(pl, ownPlayer);
		table.addWord(new Word("CASA"));
		table.addWord(new Word("SALE"));
		table.addWord(new Word("LETTO"));
		table.addWord(new Word("TORCHIO"));
		
		//Test interfaccia
		GameFrame frame = new GameFrame(table);
		frame.setVisible(true);
		
		//Test finti giocatori che aggiungono parole
		class FakeWordAdder implements Runnable {
			
			GameTable t;
			
			public FakeWordAdder(GameTable gameTable) {
				this.t = gameTable;
			}
			
			public void run() {
				Random rnd = new Random();
				String[] words = {"CHIODO", "DORATO", "TOMO", "MODERNO", "NOVE", "VELI"};
				for (int i = 0; i < words.length; i++) {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) { }
					t.addWord(new Word(words[i]));
					t.getOwnPlayer().setPoints(t.getOwnPlayer().getPoints() + rnd.nextInt(10000000));
				}
			}
		}
		Thread th = new Thread(new FakeWordAdder(table));
		th.start();
		

		//Test caricamento dizionario e sillabe	
		/*
		try {
			Dictionary d = new Dictionary(Constants.dictionaryPath);
			Set<String> ws = d.getWordSet();
			for (String w : ws) {
				Word w2 = new Word(w);
				System.out.println(w2 + " " + w2.getSubWordBeforeLastSyllable() + "-" + w2.getLastSyllableSubWord());
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
	}

}
