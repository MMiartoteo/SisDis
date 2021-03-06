package roundword.net;
import roundword.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Set;
import java.util.HashSet;

public class Peer implements GameTable.EventListener {
	
	Starter starter;
	Player player;        // The associated player
	GameTable gameTable; // The game table (the game logic controller)
	
	boolean local = false;// True solo se l'istanza corrente corrisponde al peer locale
	boolean ready = false;// True solo se il Peer ha ricevuto Hello
	
	List<Peer> peers;   // Da sta lista non togliamo nulla (almeno per ora)
	
	String IPaddr;
	int serverPortno;
	
	ClientSide client;
	ServerSide server;
	
	Timer timer;
	TimerTask helloTask;
	TimerTask lastWordTask;     // Per catturare la morte dei forwarder del token "Word"
	TimerTask lastElectionTask; // Per catturare la morte del turnHolder attuale
	TimerTask firstPhaseElectionTask; // Per catturare la morte dei turnHolder candidati durante l'elezione
	TimerTask secondPhaseElectionTask; // Per catturare la morte del turnHolder nella seconda fase dell'elezione
	
	// Id del turno. Viene incrementato dal turnholder prima di inviare la nuova
	// parola per la prima volta. Ogni re-invio mantiene lo stesso id.
	long turnId = 0;
	
	boolean electionActive = false;
	
	public Peer(Starter starter, Player player, String IPaddr, int serverPortno) {
		this.starter = starter;
		this.player = player;
		this.IPaddr = IPaddr;
		this.serverPortno = serverPortno;
		this.timer = new Timer("Timer Messaggi");
	}
	
	
	/* ############################## */
	/*        FUNZIONI INIZIALI       */
	/* ############################## */
	public void setLocal() {
		assert !local;
		local = true;
		System.out.println(String.format("PEER LOCALE = %d", getOrd()));
	}
	
	public void setPeers(List<Peer> peers) {
		assert local;
		for (Peer p : peers) {
			p.peers = peers;
		}
	}
	
	public void setGameTable(GameTable gameTable) {
		assert local;
		this.gameTable = gameTable;
		gameTable.addEventListener(this);
	}
	
	public void start() {
		assert local;
		assert gameTable!=null;
		this.start_server_side();
		this.start_client_side();
		
		// Il turnHolder iniziale invia Hello in cerchio, per aspettare 
		// che tutti i Peer si siano configurati e siano pronti
		// a ricevere e inviare messaggi
		if (this.isTurnHolder()) {
			sendHello();
		} else {
			if (helloTask != null) helloTask.cancel();
			helloTask = new TimerTask() {
				@Override
				public void run() {
					System.out.println("HELLO NON E' TORNATO INDIETRO!");
					System.exit(-1);
				}
			};
			long delay = 10*peers.size()*(NetConstants.T_trans+NetConstants.T_proc);
			timer.schedule(helloTask, delay);
			System.out.println(String.format("%s) Aspetto %s secondi", getOrd(), String.valueOf(delay/1000.0)));
		}
		
	}
	
	
	/* ############################## */
	/*             VARIE              */
	/* ############################## */
	public void send_msg(Msg m) {
		assert local;
		client.send_msg(m);
	}
	
	public synchronized void rescheduleTurnHolderTimer() {
		assert local;
		if (lastElectionTask != null) lastElectionTask.cancel();
		lastElectionTask = new TimerTask() {
			@Override
			public void run() {
				System.out.println("LEADER (turnHolder) MORTO! INDICO RI-ELEZIONE!");
				startTurnHolderElection();
				rescheduleTurnHolderTimer();
			}
		};

		timer.schedule(lastElectionTask, peers.size() * peers.size() * (NetConstants.T_trans+NetConstants.T_proc) + NetConstants.T_extratime);
	}
	
	public boolean isActive() {
		return player.isActive();
	}
	
	public void setReady() {
		ready = true;
	}
	
	public boolean isReady() {
		return ready;
	}

	public void setActiveStatus(boolean active) {
		player.setActiveStatus(active);
	}

	public boolean isTurnHolder() {
		return player == gameTable.getTurnHolder();
	}
	
	public int getOrd() {
		return player.getOrd();
	}
	
	public Player getTurnHolder() {
		return gameTable.getTurnHolder();
	}
	
	public synchronized Peer getNextActivePeer() {
		for (int i=(getOrd()+1)%peers.size(); ; i=(i+1)%peers.size()) {
			Peer p = peers.get(i);
			if (p.isActive()) {
				return p;
			}
		}
	}
	
	public synchronized Set<Byte> getCrashedPeerOrds() {
		Set<Byte> crashedPeerOrds = new HashSet<Byte>();
		for (int i=(getOrd()+1)%peers.size(); i!=getOrd(); i=(i+1)%peers.size()) {
			Peer p = peers.get(i);
			if (!p.isActive()) {
				crashedPeerOrds.add((byte)p.getOrd());
			}
		}
		return crashedPeerOrds;
	}
	
	public synchronized boolean updateCrashedPeers(Set<Byte> crashedPeerOrds) {
		boolean somethingChanged = false;

		//DEBUG
		for (byte b : crashedPeerOrds) {
			System.out.println(String.format("MORTO: %s", b));
		}

		if (crashedPeerOrds.contains(this.getOrd())) {
			gameTable.forceEndGame("La tua connessione è troppo lenta, sei stato buttato fuori il gioco!");
		}

		for (int i=(getOrd()+1)%peers.size(); i!=getOrd(); i=(i+1)%peers.size()) {
			Peer p = peers.get(i);
			if (crashedPeerOrds.contains((byte)p.getOrd())) {

				//DEBUG
				System.out.println(String.format("Setto peer %s morto!", p.getOrd()));

				if (p.isActive()) somethingChanged = true;
				p.setActiveStatus(false);
			}
		}

		return somethingChanged;
	}

	public synchronized boolean iAmAlone() {
		System.out.print("iAmAlone?");
		boolean ris = true;
		for (Peer p : peers) if (p != this && p.isActive()) ris = false;
		System.out.println(" " + ris);
		return ris;
	}
	
	
	/* ##############################  */
	/*       EVENTI DI GAMETABLE       */
	/* ##############################  */
	public synchronized void newWordAdded(Player p, Word word, long milliseconds, WordAddedState state) {
		if (!this.isTurnHolder()) return; // We manage only the word created by the local player
		System.out.println("EVENTO newWordAdded");
		turnId++; // Qui incremento l'id del turno
		sendWord(word, milliseconds);
	}
	public void playersPointsUpdate() {}
	public void turnHolderChanged(Player oldTurnHolder, Player newTurnHolder) {}
	public synchronized void gameFinished(Player winnerPlayer, List<Player> players) {

		if (firstPhaseElectionTask != null) {
			firstPhaseElectionTask.cancel();
			firstPhaseElectionTask = null;
		}
		if (secondPhaseElectionTask != null) {
			secondPhaseElectionTask.cancel();
			secondPhaseElectionTask = null;
		}
		if (lastWordTask != null) {
			lastWordTask.cancel();
			lastWordTask = null;
		}
		if (lastElectionTask != null) {
			lastElectionTask.cancel();
			lastElectionTask = null;
		}

		System.out.println("##################################################");
		System.out.println("Partita terminata, vincitore: " + winnerPlayer);
		System.out.println("##################################################");
	}
	public synchronized void failure(String msg) {

	}
	
	
	/* ############################## */
	/*   COMUNICAZIONI COL GAMETABLE  */
	/* ############################## */
	protected synchronized void nextTurn() {
		if (gameTable.isGameFinished()) {
			gameTable.finishTheGame();
		} else {
			for (int i=(getTurnHolder().getOrd()+1)%peers.size(); ; i=(i+1)%peers.size()) {
				if (!peers.get(i).isActive()) continue;
				assert i!=getTurnHolder().getOrd(); // <-- Significa che non ci sono più Peer attivi!
				gameTable.setTurnHolder(peers.get(i).player);
				return;
			}
		}
	}
	
	
	/* ############################## */
	/*     COMUNICAZIONI TRA PEER     */
	/* ############################## */
	
	/* ---------------------- */
	/* ------- HELLO -------- */
	/* ---------------------- */
	protected void sendHello() {
		System.out.println(String.format("%s) Invio HELLO a peer successore %s", getOrd(), getNextActivePeer().getOrd()));
		
		// Setta il timerTask nel caso in cui il messaggio non torna (ack non ricevuto)
		if (helloTask != null) helloTask.cancel();
		helloTask = new TimerTask() {
			@Override
			public void run() {
			System.out.println("HELLO NON E' TORNATO INDIETRO!");
			System.exit(-1);
			}
		};
		long delay = 10*peers.size()*(NetConstants.T_trans+NetConstants.T_proc);
		timer.schedule(helloTask, delay);
		System.out.println(String.format("%s) Aspetto %s secondi", getOrd(), String.valueOf(delay/1000.0)));
		
		// Invia il messaggio di Hello al peer successivo
		send_msg(new HelloMsg(this, getNextActivePeer()));
	}
	
	protected void forwardHello() {
		System.out.println(String.format("%s) Forwardo HELLO a peer successore %s", getOrd(), getNextActivePeer().getOrd()));
		send_msg(new HelloMsg(this, getNextActivePeer()));
	}
	
	/* ---------------------- */
	/* -------- WORD -------- */
	/* ---------------------- */
	protected synchronized void sendWord(final Word word, final long milliseconds) {
		System.out.println(String.format("%s) Invio Word a peer successore %s", getOrd(), getNextActivePeer().getOrd()));
		
		//turnId++;

		// Calcola il vincitore
		byte winnerOrd = -1;
		if (gameTable.isGameFinished()) {
			gameTable.setWinner(gameTable.calculateTheWinner());
			winnerOrd = (byte)gameTable.getWinner().getOrd();
		}
		
		// Setta il timerTask nel caso in cui il messaggio non torna (ack non ricevuto)
		if (lastWordTask != null) lastWordTask.cancel();
		lastWordTask = new TimerTask() {
			@Override
			public void run() {
			System.out.println("Timer Word Ack scaduto! Faccio ripartire la Word.");
			sendWord(word, milliseconds);
			}
		};
		long delay = peers.size()*(NetConstants.T_trans+NetConstants.T_proc);
		send_msg(new WordMsg(this, getNextActivePeer(), turnId, word, milliseconds, winnerOrd, timer, lastWordTask, delay));
	}
	
	protected synchronized void forwardWord(long turnId, Word word, long milliseconds, byte winnerOrd) {
		System.out.println(String.format("%s) Forwardo Word a peer successore %s", getOrd(), getNextActivePeer().getOrd()));
		send_msg(new WordMsg(this, getNextActivePeer(), turnId, word, milliseconds, winnerOrd));
		
		System.out.println(String.format("%s) E aspetto un Ack dal peer di turno %s", getOrd(), getTurnHolder().getOrd()));
		if (lastWordTask != null) lastWordTask.cancel();
		lastWordTask = new TimerTask() {
			@Override
			public void run() {
			System.out.println("Timer WordAck2 scaduto! Il coordinatore è morto, INDIRE ELEZIONE!!");
			startTurnHolderElection();
			}
		};
		timer.schedule(lastWordTask, peers.size()*(NetConstants.T_trans+NetConstants.T_proc));
		/// NOTA: Anche qui, teoricamente sarebbe meglio far partire il
		/// timer mentre invii davvero il messaggio (dentro la execute() del msg)
	}
	
	protected synchronized void sendWordAck() {
		// Il msg Word ha fatto tutto il giro, ora invio ack a tutti (tranne me)
		for (int i=(getOrd()+1)%peers.size(); i!=getOrd(); i=(i+1)%peers.size()) {
			if (!peers.get(i).isActive()) continue;
			if (i==getOrd()) break;
			System.out.println(String.format("%s) Invio WordAck a peer %s", getOrd(), i));
			send_msg(new WordAckMsg(this, peers.get(i), turnId));//, timer, lastWordTask, 2*T_trans+T_proc));
		}
		// Io già posso settare il turno nel prossimo giocatore
		nextTurn();
	}

	/* ---------------------- */
	/* ------ ELECTION ------ */
	/* ---------------------- */
	protected synchronized void startTurnHolderElection() {
		System.out.println(String.format("START ELECTION. Il turno attualmente è di %d", gameTable.getTurnHolder().getOrd()));

		if (iAmAlone()) {
			System.out.println("Sono rimasto da solo setto me stesso come vincitore");
			if (!gameTable.isGameFinished()) {
				gameTable.setWinner(this.player);
				gameTable.finishTheGame();
			}
			return;
		}

		if (electionActive) {
			System.out.println("Elezione già in corso, ignoro startTurnHolderElection()");
			return;
		}
		
		electionActive = true;
		
		// Se sono io il leader, invio notizia a tutti i processi minori (in realtà tutti tranne me)
		if (isTurnHolder()) {
			for (int i=(getOrd()+1)%peers.size(); ; i=(i+1)%peers.size()) {
				if (!peers.get(i).isActive()) continue;
				if (i==getOrd()) break;
				System.out.println(String.format("%s) Invio SetTurnHolder a peer %s", getOrd(), i));
				send_msg(new ElectionSetTurnHolderMsg(this, peers.get(i), getOrd()));
			}
			electionActive = false; // In questo caso l'elezione finisce subito.
		}
		
		// Se non sono il leader, invio elezione a tutti quelli prima di me, 
		// a partire da chi credo sia il leader attuale
		else {
			for (int i=getTurnHolder().getOrd(); i!=getOrd(); i=(i+1)%peers.size()) {
				if (!peers.get(i).isActive()) continue;
				if (i==getOrd()) break;
				System.out.println(String.format("%s) Invio ElectionInit a peer %s", getOrd(), i));
				
				// Il delay nell'elezione del leader è sempre il seguente:
				long delay = 2*NetConstants.T_trans + NetConstants.T_proc;
				
				// Setta un primo timer tempo T per aspettare risposta
				// da ALMENO UN peer possibile coordinatore.
				if (firstPhaseElectionTask == null) {
					firstPhaseElectionTask = new TimerTask() {
						@Override
						public void run() {

							System.out.println("Timer ElectionInit1 scaduto!");

							/* Questo è il caso in cui io sono il coordinatore, ma alla fine sono rimasto da solo,
							* quindi posso far subito terminare la partita. */
							if (iAmAlone()) {
								System.out.println("Sono rimasto da solo, termino la partita");
								gameTable.setWinner(player);
								gameTable.finishTheGame();
								return;
							}

							// Se scade, devi settare te stesso come coordinatore e dirlo a quelli dopo di te.
							System.out.println("Setto me come TurnHolder e lo dico a tutti.");
							gameTable.setTurnHolder(player);

							if (gameTable.isGameFinished()) {
								System.out.println("Il gioco è finito, quindi devo solamente inviare il messaggio di word nullo per il vincitore");
								sendWord(null, 0); //questo verrà poi scartato perchè l'id non è cambiato
							}

							electionActive = false;
							startTurnHolderElection();
						}
					};
					timer.schedule(firstPhaseElectionTask, delay);
				} else {
					System.out.println("** ATTENZIONE! 'firstPhaseElectionTask' era già non null...");
				}
				/// NOTA: qui sarebbe meglio schedulare il timer task solo
				/// dopo aver spedito almeno il primo messaggio.
				/// Ma metto qui per semplicità, per ora almeno.
				
				// Timer per aspettare un messaggio SetTurnHolder
				// Dopo che avrò ricevuto la prima risposta da qualcuno
				if (secondPhaseElectionTask == null) {
					secondPhaseElectionTask = new TimerTask() {
						@Override
						public void run() {
							// Se non arriva, devi indire una nuova elezione
							System.out.println("Timer ElectionInit2 scaduto! Indico una nuova elezione.");
							startTurnHolderElection();
						}
					};
				} else {
					//secondPhaseElectionTask = null;
				}
				
				send_msg(new ElectionInitMsg(this, peers.get(i), timer, firstPhaseElectionTask, secondPhaseElectionTask, delay));
			}
		}
	}
	
	
	/* ############################## */
	/*             PRIVATI            */
	/* ############################## */
	
	private void start_server_side() {
		server = new ServerSide(this, gameTable);
	}
	
	private void start_client_side() {
        client = new ClientSide();
        client.start();
	}

	
}
