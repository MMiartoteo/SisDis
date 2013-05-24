import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import roundword.*;
import roundword.ui.*;
import roundword.net.*;

import java.net.*;
import java.io.*;

public class Main {

	public static void main(String[] args) {
		
		int SEC_WAIT = 2;
		
		/// 0 - Leggi parametri del giocatore e del peer locale
		String player_name = args[0];
		String portno = args[1]; // TODO <--- REGISTRALA ANCHE NEL PEER?
		System.out.println(player_name + ", " + portno);
		
		/// 1 - Contatta il registrar centrale
		/* 
		 * Per ottenere la lista dei giocatori, dei loro peer associati
		 * e l'ordine di gioco.
		 * */
		while (true) {
			System.out.println("Contacting registrar...");
			
			HttpURLConnection connection = null;
			String response = null;
			try {
				URL serverAddress = new URL(String.format("http://localhost:8080/%s/%s", player_name, portno));
				connection = (HttpURLConnection)serverAddress.openConnection();
				connection.setRequestMethod("GET");
				connection.setDoOutput(true);
				connection.setReadTimeout(10000);
				connection.connect();
				BufferedReader rd  = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = rd.readLine()) != null) {
				  sb.append(line + "\n");
				}
				response = sb.toString();
				System.out.println(response);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (ProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally{
				//close the connection, set all objects to null
				connection.disconnect();
				if (response.split("\n")[0].equals("start")) break;
			}
			
			try {
				Thread.sleep(SEC_WAIT*1000);
			} catch(InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		}
		
		/// 2 - Contatta ogni signolo peer per dirgli "okay ci sono"
		
		/// 3 - Lo stato condiviso è composto da:
		/*
		 * 1) La lista dei giocatori presenti con i loro punteggi
		 * 2) La lista delle parole fino ad ora giocate (vuota all'inizio)
		 * ...
		 * */
		
		
		
		
		
		
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
