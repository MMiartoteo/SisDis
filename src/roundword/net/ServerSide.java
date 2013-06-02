package roundword.net;
import roundword.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


public class ServerSide implements ServerSideInterface {
	Peer peer;
	GameTable gameTable;
	
	public ServerSide(Peer peer, GameTable gameTable) {
		this.peer = peer;
		this.gameTable = gameTable;
		try {
			ServerSideInterface stub = (ServerSideInterface) UnicastRemoteObject.exportObject(this, 0);
			Registry registry = LocateRegistry.createRegistry(peer.server_portno);
			registry.rebind("ServerSide", stub);
			System.out.println("ComputeEngine bound");
		}
		catch (Exception e) {
            System.err.println("ComputeEngine exception:");
            e.printStackTrace();
        }
        finally {
			System.out.println("Server ready");
		}
	}
	
	public String sayHello() {
		System.out.println(String.format("Ricevuto HELLO"));
		// Fai il forward se non sei tu il turnista (e setta la parola nella gui)
		if (!peer.isTurnHolder()) {
			System.out.println("Il peer attuale NON è il detentore del turno. Faccio forwarding.");
			//~ peer.lastSeenMsgId = id;
			peer.forwardHello();
			//~ gameTable.addWord(word, 100); /// TODO <--- Poi metti vero valore per secondi
		}
		// Altrimenti se sei il turnista vuol dire che è l'ack che è tornato indietro nell'anello
		else {
			System.out.println(String.format("HELLO è tornato indietro!"));
			//~ if (id == peer.lastSentMsgId) {
				//~ System.out.println("L'ack è corretto, cancello il relativo timer.");
			peer.helloTask.cancel();
			
			/// DEBUG: Comincio elezione (poi non servirà nellav versione finale)
			peer.startTurnHolderElection();
			
			//~ System.out.println("E invio Ack finale a tutti i peer per segnare il cambio turno (e per il sec. guasto).");
			//~ peer.sendWordAck();
			//~ }
			//~ else {
				//~ System.out.println("La parola è vecchia, faccio solo il forward.");
				//~ //peer.lastSeenMsgId = id;
				//~ peer.forwardWord(id, word);
			//~ }
		}
		// Inoltre, questo significa che il turnHolder non è morto
		peer.rescheduleTurnHolderTimer();
		return String.format("Hello, world! I am %s in %s:%s", peer.player.getNickName(), peer.IPaddr, peer.server_portno);
		//~ /// ONLY FOR DEBUG
		//~ System.out.println(String.format("Ricevuto HELLO"));
		//~ peer.startTurnHolderElection();
        //~ return String.format("Hello, world! I am %s in %s:%s", peer.player.getNickName(), peer.IPaddr, peer.server_portno);
    }
    
    public String ElectionInit() {
		System.out.println(String.format("Ricevuto ElectionInit"));
		if (! peer.electionActive) {
			System.out.println(String.format("Comincio anche io l'elezione."));
			peer.startTurnHolderElection();
		}
		System.out.println(String.format("Rispondo al mittente con un return immediato."));
		return "ok";
	}
	
	public String ElectionSetTurnHolder(int newTurnHolder) {
		System.out.println(String.format("Ricevuto SetTurnHolder. Setto %d come turnHolder.", newTurnHolder));
		//peer.turnHolder = turnHolder;
		gameTable.setTurnHolder(peer.peers.get(newTurnHolder).player);
		if (peer.firstPhaseElectionTask != null) {
			peer.firstPhaseElectionTask.cancel();
			peer.firstPhaseElectionTask = null;
		}
		if (peer.secondPhaseElectionTask != null) {
			peer.secondPhaseElectionTask.cancel();
			peer.secondPhaseElectionTask = null;
		}
		peer.electionActive = false;
		peer.rescheduleTurnHolderTimer();
		return "ok";
	}
	
	public String word(long id, Word word) {
		System.out.println(String.format("Ricevuto Word \"%s\" id=%d", word, id));
		// Fai il forward se non sei tu il turnista (e setta la parola nella gui)
		if (!peer.isTurnHolder()) {
			System.out.println("Il peer attuale NON è il detentore del turno. Faccio forwarding.");
			peer.lastSeenMsgId = id;
			peer.forwardWord(id, word);
			System.out.println("--");
			gameTable.addWord(word, 100); /// TODO <--- Poi metti vero valore per secondi
		}
		// Altrimenti se sei il turnista vuol dire che è l'ack che è tornato indietro nell'anello
		else {
			System.out.println(String.format("La word è tornata indietro! lastSentMsgid=%d", peer.lastSentMsgId));
			if (id == peer.lastSentMsgId) {
				System.out.println("L'ack è corretto, cancello il relativo timer.");
				peer.lastWordTask.cancel();
				System.out.println("E invio Ack finale a tutti i peer per segnare il cambio turno (e per il sec. guasto).");
				peer.sendWordAck();
			}
			else {
				System.out.println("La parola è vecchia, faccio solo il forward.");
				//peer.lastSeenMsgId = id;
				peer.forwardWord(id, word);
			}
		}
		// Inoltre, questo significa che il turnHolder non è morto
		peer.rescheduleTurnHolderTimer();
		return "ok";
	}
	
	public String wordAck(long id) {
		System.out.println(String.format("Ricevuto WordAck2 dal turnHolder"));
		if (id == peer.lastSeenMsgId) {
			System.out.println("L'ack è corretto, cancello il relativo timer.");
			peer.lastWordTask.cancel();
			// Next Turn
			System.out.println("E passo al prossimo turno.");
			peer.nextTurn();
		}
		else {
			System.out.println("L'ack è vecchio, ignoro il messaggio.");
		}
		// Inoltre, questo significa che il turnHolder non è morto
		/// NOTA: Questo può essere un po' ridondante visto che c'è già il timer lastWordTask.. togliere lastWordtask forse?
		peer.rescheduleTurnHolderTimer();
		return "ok";
	}
}
