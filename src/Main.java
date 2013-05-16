import java.util.List;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;
import roundword.*;
import roundword.ui.*;

public class Main {

	public static void main(String[] args) {
		
		Peer p = new Peer("p1", "100", "9000", null);
		
		/*
		 * Qui si dovrebbe chiamare il server per dargli la disponibilita',
		 * aspettare la risposta e poi avviare il gioco
		 * */
		
		GameTable table = new GameTable();
		List<Player> pl = table.getPlayersList();
		pl.add(new Player("Miro"));
		pl.add(new Player("Ciccio"));
		pl.add(new Player("Tizio"));
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
				String[] words = {"CHIODO", "DORATO", "TOMO", "MODERNO", "NOVE", "VELI"};
				for (int i = 0; i < words.length; i++) {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) { }
					t.addWord(new Word(words[i]));
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
