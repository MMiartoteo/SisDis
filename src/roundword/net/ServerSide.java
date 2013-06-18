package roundword.net;
import roundword.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Set;


public class ServerSide implements ServerSideInterface {
	private Peer peer;
	private GameTable gameTable;
	
	public ServerSide(Peer peer, GameTable gameTable) {
		this.peer = peer;
		this.gameTable = gameTable;
		try {
			ServerSideInterface stub = (ServerSideInterface) UnicastRemoteObject.exportObject(this, 0);
			Registry registry = LocateRegistry.createRegistry(peer.serverPortno);
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
		System.out.println(String.format("Ricevuto HELLO, setto il peer come Ready e faccio partire il gioco."));
		
		System.out.println(String.format("Cancello timer di hello!"));
		if (peer.helloTask != null) {
			peer.helloTask.cancel();
			peer.helloTask = null;
		}
		
		peer.setReady();
		peer.starter.startGame();

		// Fai il forward se non sei tu il turnista
		if (!peer.isTurnHolder()) {
			System.out.println("Il peer attuale NON è il detentore del turno. Faccio forwarding.");
			peer.forwardHello();
		}

		// Altrimenti se sei il turnista vuol dire che è l'ack che è tornato indietro nell'anello
		else {
			System.out.println(String.format("HELLO è tornato indietro!"));
			/// DEBUG: Comincio elezione (poi non servirà nella versione finale), perché in realtà sono io il turnista!
			peer.startTurnHolderElection();
		}

		// Inoltre, questo significa che il turnHolder non è morto
		peer.rescheduleTurnHolderTimer();

		return String.format("Hello, world! I am %s in %s:%s", peer.player.getNickName(), peer.IPaddr, peer.serverPortno);
    }
    
    public String ElectionInit() {
		synchronized (peer) {

			assert peer.isReady(); /// NOTA: se ciò è falso è perché il turnHolder è morto prima di inviare Hello
			System.out.println(String.format("Ricevuto ElectionInit"));

			// Comincio l'elezione se non l'avevo già iniziata in precedenza
			if (! peer.electionActive) {
				System.out.println(String.format("Comincio anche io l'elezione."));
				peer.startTurnHolderElection();
			}

			System.out.println(String.format("Rispondo al mittente con un return immediato."));
		}

		return "ok";
	}
	
	public String ElectionSetTurnHolder(int newTurnHolder) {
		synchronized (peer) {
			assert peer.isReady();

			if (peer.getTurnHolder().getOrd() != newTurnHolder) {
				System.out.println("Stavo ancora aspettando un word ack, ma è cambiato il turno. Faccio come se l'avessi ricevuto, cancello lastWordTask.");
				if (peer.lastWordTask != null) {
					peer.lastWordTask.cancel();
					peer.lastWordTask = null;
				}
			}

			System.out.println(String.format("Ricevuto SetTurnHolder. Setto %d come turnHolder.", newTurnHolder));
			gameTable.setTurnHolder(peer.peers.get(newTurnHolder).player);

			// Elimino eventuali timer Elezione in attesa
			if (peer.firstPhaseElectionTask != null) {
				peer.firstPhaseElectionTask.cancel();
				peer.firstPhaseElectionTask = null;
			}
			if (peer.secondPhaseElectionTask != null) {
				peer.secondPhaseElectionTask.cancel();
				peer.secondPhaseElectionTask = null;
			}

			// L'elezione è terminata
			peer.electionActive = false;

			// Inoltre, questo significa che il turnHolder non è morto
			peer.rescheduleTurnHolderTimer();

		}
		
		return "ok";
	}


	public String word(long turnId, Word word, long remainingTimeMilliseconds, byte winnerOrd, Set<Byte> crashedPeerOrds) {
		synchronized (peer) {

			assert peer.isReady();
			//assert msgOriginatorOrd == peer.getTurnHolder().getOrd(); /// NOTA: <<--- PER DEBUG!
			assert turnId == peer.turnId || turnId == peer.turnId + 1;

			System.out.println(String.format("Ricevuto Word \"%s\" id=%d", word, turnId));

			System.out.println(String.format("Aggiorno la lista dei peer morti"));
			boolean somethingChange = peer.updateCrashedPeers(crashedPeerOrds);

			// Fai il forward se non sei tu il turnista (e setta la parola nella gui)
			if (!peer.isTurnHolder()) {
				System.out.println("Il peer attuale NON è il detentore del turno. Faccio forwarding, e cancello timer WordAck2.");
				if (peer.lastWordTask != null) {
					peer.lastWordTask.cancel();
					peer.lastWordTask = null;
				}
				peer.forwardWord(turnId, word, remainingTimeMilliseconds, winnerOrd);

				/* settiamo il vincitore, lo facciamo anche quando non è la prima volta che vediamo il messaggio word, perché
				 * il turnholder potrebbe aver cambiato idea su chi sia il vincitore.
				 */
				if (winnerOrd != -1) {
					System.out.println("######## Ho ricevuto un candidato per essere vincitore: " + peer.peers.get(winnerOrd).player);
					gameTable.setWinner(peer.peers.get(winnerOrd).player);
				}

				if (turnId == peer.turnId) {
					System.out.println(String.format("Questa parola mi è stata RIMANDATA DI NUOVO, non la riaggiungo al gameTable."));
				} else {
					assert turnId == peer.turnId + 1;
					peer.turnId = turnId;
					gameTable.addWord(word, remainingTimeMilliseconds, false);
				}
				//~ if (msgOriginatorOrd != peer.lastSeenMsgOriginatorOrd) {
					//~ gameTable.addWord(word, remainingTimeMilliseconds);
					//~ peer.lastSeenMsgOriginatorOrd = msgOriginatorOrd;
				//~ } else {
					//~ System.out.println(String.format("Questa parola mi è stata RIMANDATA DI NUOVO, non la riaggiungo al gameTable."));
				//~ }
			}

			// Altrimenti se sei il turnista vuol dire che è l'ack che è tornato indietro nell'anello
			else {
				//assert msgOriginatorOrd == peer.getOrd();
				System.out.println(String.format("La word è tornata indietro! turnId=%d", peer.turnId));
				//peer.lastSeenMsgOriginatorOrd = msgOriginatorOrd;

				// Controllo l'id del messaggio, per scoprire se ho ricevuto un ack vecchio o corretto
				if (turnId == peer.turnId) {
					System.out.println("L'ack è corretto, cancello il relativo timer.");
					if (peer.lastWordTask != null) {
						peer.lastWordTask.cancel();
						peer.lastWordTask = null;
					}

					if (winnerOrd != -1 && somethingChange) {
						/* Nel caso in cui sia un messaggio di vittoria e questo ritorna, ma facendo il giro qualcuno è morto
						* ed io non lo sapevo, allora reinvio il messaggio word, in modo che venga calcolato il nuovo vincitore */
						System.out.println("Reinvio il messaggio di word di fine gioco, perché qualcuno è morto durante la consegna.");
						peer.sendWord(word, remainingTimeMilliseconds);

					} else {
						System.out.println("E invio Ack finale a tutti i peer per segnare il cambio turno (e per il sec. guasto).");
						peer.sendWordAck();
					}
				}

				// In questo caso l'ack è per una parola vecchia, faccio forwarding
				// Ad esempio, vuol dire che il turnista ha re-inviato la parola per scaduto timeout
				else {
					System.out.println("La parola è vecchia, faccio solo il forward.");
					//peer.lastSeenMsgId = id;
					peer.forwardWord(turnId, word, remainingTimeMilliseconds, winnerOrd);
				}
			}

			// Inoltre, questo significa che il turnHolder non è morto
			peer.rescheduleTurnHolderTimer();

		}

		return "ok";
	}
	
	public String wordAck(long turnId) {
		synchronized (peer) {

			assert peer.isReady();
			assert !peer.isTurnHolder();

			System.out.println(String.format("Ricevuto WordAck2 dal turnHolder"));

			// Controllo l'id del messaggio, per sgamare ack vecchi
			if (turnId == peer.turnId) {
				System.out.println("L'ack è corretto, cancello il relativo timer.");
				if (peer.lastWordTask != null) {
					peer.lastWordTask.cancel();
					peer.lastWordTask = null;
				}
				// Next Turn
				System.out.println("E passo al prossimo turno.");
				peer.nextTurn();
			}

			// E' l'ack di un turno vecchio, ERRORE GRAVE!
			/// NOTA: TEORICAMENTE STA COSA NON DOVREBBE MAI SUCCEDERE...
			else {
				System.out.println("L'ack è vecchio, ignoro il messaggio.");
			}

			// Inoltre, questo significa che il turnHolder non è morto
			/// NOTA: Questo può essere un po' ridondante visto che c'è già il timer lastWordTask.. togliere lastWordtask forse?
			peer.rescheduleTurnHolderTimer();

		}
		
		return "ok";
	}
}
