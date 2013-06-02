package roundword;

import org.json.JSONArray;
import roundword.Dictionary;
import roundword.GameTable;
import roundword.Player;
import roundword.Word;
import roundword.net.Peer;
import roundword.test.FakePlayer;
import roundword.ui.GameFrame;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Starter {

	// ------------------------------------------------------------------------
	// INTERFACES
	// ------------------------------------------------------------------------

	public interface EventListener extends java.util.EventListener {

		void messageUpdate(String msg);

		void gameStarted();

		void gameFailedToStart(String msg);

	}

	// ------------------------------------------------------------------------
	// FIELDS
	// ------------------------------------------------------------------------

	EventListener eventListener;
	Dictionary dictionary;

	// ------------------------------------------------------------------------
	// CONSTRUCTORS
	// ------------------------------------------------------------------------

	public Starter() throws Exception {
		try {
			dictionary = new Dictionary(Constants.dictionaryPath);
		} catch (IOException ex) {
			throw new Exception("Can't load the dictionary");
		}
	}

	// ------------------------------------------------------------------------
	// METHODS
	// ------------------------------------------------------------------------

	public void setMessageUpdateListener(EventListener eventListener) {
		this.eventListener = eventListener;
	}

	public void startGame(String player_name, int portno, String registrarURL, boolean artificial) {

		int SEC_WAIT = 2;

		/// 1 - Contatta il registrar centrale
		/*
		 * Per ottenere la lista dei giocatori, dei loro peer associati
		 * e l'ordine di gioco.
		 * */
		String response = null;
		while (true) {
			if (eventListener != null) eventListener.messageUpdate("Contacting registrar...");

			HttpURLConnection connection = null;
			try {
				URL serverAddress = new URL(String.format(registrarURL + "/%s/%s", player_name, portno));
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
				if (eventListener != null) eventListener.messageUpdate(response);
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
					if (eventListener != null) eventListener.gameFailedToStart(String.format("The nickname %s has already been taken. Choose a different nickname.", player_name));
					return;
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
			if (eventListener != null) {
				eventListener.gameFailedToStart("Il player/peer locale non è presente nella lista riportata dal registrar! Sei rimasto fuori dal gioco!");
			}
			return;
		}

		GameTable table = new GameTable(players, localPlayer, dictionary);

		p.setLocal();
		p.setPeers(peers);
		p.setGameTable(table);

		// Start local peer
		p.start();

		if (eventListener != null) {
			eventListener.gameStarted();
		}


		/// Lo stato condiviso è composto da:
		/*
		 * 1) La lista dei giocatori presenti con i loro punteggi
		 * 2) La lista delle parole fino ad ora giocate (vuota all'inizio)
		 * ...
		 * */

		/* Start game */

		if (artificial) {
			try {
				Thread t = new Thread(new FakePlayer(table, Constants.dictionaryPath));
				t.start();
			} catch (Exception ex) {
				if (eventListener != null) eventListener.gameFailedToStart("Impossible to create a fake player");
			}
		} else {
			GameFrame frame = new GameFrame(table);
			frame.setVisible(true);
		}
	}

}
