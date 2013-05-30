import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import roundword.*;
import roundword.ui.*;
import roundword.net.*;
import roundword.test.*;

import java.net.*;
import java.io.*;
import java.util.Set;

import org.json.JSONArray;

public class Main {

	public static void main(String[] args) {
		
		int SEC_WAIT = 2;

		/// 0 - Leggi parametri del giocatore e del peer locale
		String player_name = args[0];
		int portno = Integer.parseInt(args[1]); // TODO <--- REGISTRALA ANCHE NEL PEER?
		System.out.println(player_name + ", " + portno);

		/// 1 - Contatta il registrar centrale
		/*
		 * Per ottenere la lista dei giocatori, dei loro peer associati
		 * e l'ordine di gioco.
		 * */
		String response = null;
		while (true) {
			System.out.println("Contacting registrar...");

			HttpURLConnection connection = null;
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
				if (response.split("\n")[0].equals("nickname-present")) {
					System.out.println(String.format("The nickname %s has already been taken. Choose a different nickname.", player_name));
					System.exit(1);
				}
				else if (response.split("\n")[0].equals("start")) break;
			}

			try {
				Thread.sleep(SEC_WAIT*1000);
			} catch(InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		}

		// Estrai lista dei peer/giocatori partecipanti dal Json
		JSONArray peers_players = new JSONArray((response.split("\n")[1]));
		System.out.println(peers_players);

		List<Player> players = new ArrayList<Player>();
		List<Peer> peers = new ArrayList<Peer>();

		Player localPlayer = null; // player locale
		Peer p = null;           // peer locale

		for (int i=0; i<peers_players.length(); ++i) {
			JSONArray player_info = peers_players.getJSONArray(i);
			int player_ord = i; //player_info.getInt(0); <-- per ora ignoriamo l'ord dato dal registrar
			JSONArray infos = player_info.getJSONArray(1);
			String p_host = infos.getString(0);
			int p_portno = Integer.parseInt(infos.getString(1));
			String p_name = infos.getString(2);

			Player new_player = new Player(p_name, player_ord);
			players.add(new_player);

			Peer new_peer = new Peer(new_player, p_host, p_portno);
			peers.add(new_peer);

			if (player_name.equals(p_name) && portno == p_portno) {
				localPlayer = new_player;
				p = new_peer;
			}
			else {
				//peers.add(new_peer);
			}
		}
		if (localPlayer == null || p == null) {
			System.out.println("Il player/peer locale non è presente nella lista riportata dal registrar! Sei rimasto fuori dal gioco!");
			System.exit(1);
		}

		p.setLocal();

		// Add peer list to local peer
		p.set_peers(peers);

		// Start local peer
		p.start();


		/// PROVA INVIO MESSAGGIO HELLO AL PEER ACCANTO
		//try {Thread.sleep(2000);} catch (Exception e) {System.exit(2);} // aspetto per far settare il server dall'altro lato
		// Provo ad inviare un Hello al primo dei peer in lista
		p.send_msg(new HelloMsg(p.getNextPeer()));



		if (p.isTurnHolder()) p.chosenWord("CASA");

		
		/// Lo stato condiviso è composto da:
		/*
		 * 1) La lista dei giocatori presenti con i loro punteggi
		 * 2) La lista delle parole fino ad ora giocate (vuota all'inizio)
		 * ...
		 * */
		
		
		
		/*
		 * Qui si dovrebbe chiamare il server per dargli la disponibilita',
		 * aspettare la risposta e poi avviare il gioco
		 * */

//		List<Player> players = new ArrayList<Player>();
//		Player localPlayer = new Player("CiccioBomba", 1);
//		players.add(new Player("Stupido", 0));
//		players.add(localPlayer);
//		players.add(new Player("Rimbambito", 2));

		try {
			Dictionary d = new Dictionary(Constants.dictionaryPath);
			GameTable table = new GameTable(players, localPlayer, d);

			//Test interfaccia
			GameFrame frame = new GameFrame(table);
			frame.setVisible(true);

			//Test finti giocatori che aggiungono parole
			Thread th = new Thread(new FakePlayers(table, Constants.dictionaryPath));
			th.start();

		} catch (IOException ex) {
			System.err.println("Impossible to load the dictionary");
			System.exit(-1);
		}




		//Test caricamento dizionario e sillabe

//		try {
//			Dictionary d = new Dictionary(Constants.dictionaryPath);
//			Set<String> ws = d.getWordSet();
//			for (String w : ws) {
//				if (w.length() > 0) {
//					Word w2 = new Word(w);
//					System.out.println(w2 + " " + w2.getSubWordBeforeLastSyllable() + "-" + w2.getLastSyllableSubWord());
//				}
//			}
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}


	}

}
