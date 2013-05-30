package roundword.net;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


public class ServerSide implements ServerSideInterface {
	Peer peer;
	
	public ServerSide(Peer peer) {
		this.peer = peer;
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
		/// ONLY FOR DEBUG
        return String.format("Hello, world! I am %s in %s:%s", peer.player.getNickName(), peer.IPaddr, peer.server_portno);
    }
    
    public String ElectionInit() {
		/// TODO
		return "ok";
	}
	
	public String ElectionTurnHolder(int turnHolder) {
		System.out.println(String.format("Ricevuto TurnHolder. Setto %d come turnHolder.", turnHolder));
		peer.turnHolder = turnHolder;
		return "ok";
	}
	
	public String word(long id, String word) {
		System.out.println(String.format("Ricevuto Word \"%s\"", word));
		// Fai il forward se non sei tu il turnista
		if (!peer.isTurnHolder()) {
			peer.lastSeenMsgId = id;
			peer.forwardWord(id, word);
		}
		// Altrimenti se sei il turnista vuol dire che è l'ack che è tornato indietro nell'anello
		else {
			System.out.println("La word è tornata indietro!");
			if (id == peer.lastSentMsgId) {
				System.out.println("L'ack è corretto, cancello il relativo timer.");
				peer.lastWordTask.cancel();
				System.out.println("E invio Ack finale a tutti i peer per segnare il cambio turno (e per il sec. guasto).");
				peer.sendWordAck();
			}
			else {
				System.out.println("L'ack è vecchio, ignoro il messaggio.");
			}
		}
		return "ok";
	}
	
	public String wordAck(long id) {
		System.out.println(String.format("Ricevuto WordAck2"));
		if (id == peer.lastSeenMsgId) {
			System.out.println("L'ack è corretto, cancello il relativo timer.");
			peer.lastWordTask.cancel();
			/// TODO: NUOVO TURNO!
			System.out.println("E passo al prossimo turno.");
			peer.nextTurn();
		}
		else {
			System.out.println("L'ack è vecchio, ignoro il messaggio.");
		}
		return "ok";
	}
}
