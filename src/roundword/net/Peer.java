package roundword.net;
import roundword.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Peer implements GameTable.EventListener {
	
	public static final int T_trans = 100; // milliseconds
	public static final int T_proc  = 100; // milliseconds
	
	Player player;        // The associated player
	GameTable gameTable; // The game table (the game logic controller)
	
	//int ord;       // Usato per identificazione univoca del peer
	boolean local = false;// True solo se l'istanza corrente corrisponde al peer locale
	boolean active = true;// Se il peer è crashato o no
	
	List<Peer> peers;   // Da sta lista non togliamo nulla (almeno per ora)
	//int turnHolder = 0; // All'inizio tutti sanno che il primo a giocare è il giocatore zero
	
	String IPaddr;
	int server_portno;
	
	ClientSide client;
	ServerSide server;
	
	Timer timer;
	TimerTask helloTask;
	TimerTask lastWordTask;     // Per catturare la morte dei forwarder del token "Word"
	TimerTask lastElectionTask; // Per catturare la morte del turnHolder attuale
	TimerTask firstPhaseElectionTask; // Per catturare la morte dei turnHolder candidati durante l'elezione
	TimerTask secondPhaseElectionTask; // Per catturare la morte del turnHolder nella seconda fase dell'elezione
	
	long lastSentMsgId = 0;
	long lastSeenMsgId = 0;
	
	boolean electionActive = false;
	
	public Peer(Player player, String IPaddr, int server_portno) {
		this.player = player;
		//this.gameTable = gameTable;
		//this.ord = ord;
		this.IPaddr = IPaddr;
		this.server_portno = server_portno;
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
		this.peers = peers;
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
		
		// Il turnHolder iniziale invia Hello per aspettare che
		// tutti i Peer si siano configurati e siano pronti
		// a ricevere e inviare messaggi
		if (this.isTurnHolder()) {
			sendHello();
		}
		
		// Fai partire i vari timer per beccare la morte del turnHolder
		rescheduleTurnHolderTimer();
	}
	
	/* ############################## */
	/*             VARIE              */
	/* ############################## */
	public void send_msg(Msg m) {
		assert local;
		client.send_msg(m);
	}
	
	public void rescheduleTurnHolderTimer() {
		if (lastElectionTask != null) lastElectionTask.cancel();
		lastElectionTask = new TimerTask() {
			@Override
			public void run() {
				System.out.println("LEADER (turnHolder) MORTO! INDICO RI-ELEZIONE!");
				startTurnHolderElection();
				rescheduleTurnHolderTimer();
			}
		};
		timer.schedule(lastElectionTask, 10000); /// TODO <--- metti delay corretto!
	}
	
	public boolean isActive() {
		return this.active;
	}
	
	public boolean isTurnHolder() {
		return player == gameTable.getTurnHolder();
	}
	
	public int getOrd() {
		return this.player.getOrd();
	}
	
	public Player getTurnHolder() {
		return this.gameTable.getTurnHolder();
	}
	
	public Peer getNextActivePeer() {
		for (int i=(getOrd()+1)%peers.size(); i!=getOrd(); i++) {
			assert i!=getOrd(); // Significherebbe che non ci sono più altri peer attivi in giro...
			Peer p = peers.get(i);
			if (p.isActive()) {
				return p;
			}
		}
		return null;
	}
	
	
	
	
	
	
	
	/* ##############################  */
	/*       EVENTI DI GAMETABLE       */
	/* ##############################  */
	public void newWordAdded(Word word, long milliseconds, WordAddedState state) {
		if (!this.isTurnHolder()) return; //We manage only the word created by the local player
		System.out.println("EVENTO newWordAdded");
		sendWord(word);
	}
	public void playersPointsUpdate() {}
	public void turnHolderChanged(Player oldTurnHolder, Player newTurnHolder) {}
	
	
	
	
	
	
	/* ############################## */
	/*   COMUNICAZIONI COL GAMETABLE  */
	/* ############################## */
	protected void nextTurn() {
		for (int i=(getTurnHolder().getOrd()+1)%peers.size(); ; i=(i+1)%peers.size()) {
			if (!peers.get(i).isActive()) continue;
			assert i!=getTurnHolder().getOrd(); // <-- Significa che non ci sono più Peer attivi!
			gameTable.setTurnHolder(peers.get(i).player);
			return;
		}
	}
	
	
	
	
	
	
	/* ############################## */
	/*     COMUNICAZIONI TRA PEER     */
	/* ############################## */
	
	/* ------ HELLO ------ */
	protected void sendHello() {
		System.out.println(String.format("%s) Invio HELLO a peer successore %s", getOrd(), getNextActivePeer().getOrd()));
		
		//lastSentMsgId++;
		
		// Setta il timerTask nel caso in cui il messaggio non torna (ack non ricevuto)
		helloTask = new TimerTask() {
			@Override
			public void run() {
				System.out.println("HELLO NON E' TORNATO INDIETRO! ERRORE TERRIBILE! TERMINO PARTITA!");
				System.exit(-1);
			}
		};
		timer.schedule(helloTask, 10000*peers.size()*(T_trans+T_proc));
		
		// Invia il messaggio di Hello al peer successivo
		send_msg(new HelloMsg(getNextActivePeer()));
	}
	
	protected void forwardHello() {
		//try{Thread.sleep(1000);} catch (Exception e) {}
		System.out.println(String.format("%s) Forwardo HELLO a peer successore %s", getOrd(), getNextActivePeer().getOrd()));
		send_msg(new HelloMsg(getNextActivePeer()));
	}
	
	/* ------ WORD ------ */
	protected void sendWord(final Word word) {
		System.out.println(String.format("%s) Invio Word a peer successore %s", getOrd(), getNextActivePeer().getOrd()));
		
		lastSentMsgId++;
		
		// Setta il timerTask nel caso in cui il messaggio non torna (ack non ricevuto)
		lastWordTask = new TimerTask() {
			@Override
			public void run() {
				System.out.println("Timer Word Ack scaduto!");
				/// TODO: fai partire di nuovo il messaggio di Word, con id maggiore?
				sendWord(word);
			}
		};
		
		send_msg(new WordMsg(getNextActivePeer(), lastSentMsgId, word, timer, lastWordTask, peers.size()*(T_trans+T_proc)));
	}
	
	protected void forwardWord(long id, Word word) {
		//try{Thread.sleep(1000);} catch (Exception e) {}
		System.out.println(String.format("%s) Forwardo Word a peer successore %s", getOrd(), getNextActivePeer().getOrd()));
		send_msg(new WordMsg(getNextActivePeer(), id, word));
		
		System.out.println(String.format("%s) E aspetto un Ack dal peer di turno %s", getOrd(), getTurnHolder().getOrd()));
		lastWordTask = new TimerTask() {
			@Override
			public void run() {
				System.out.println("Timer WordAck2 scaduto! Il coordinatore è morto, INDIRE ELEZIONE!!");
				/// TODO: INDIRE ELEZIONE!
			}
		};
		timer.schedule(lastWordTask, peers.size() * (T_trans + T_proc));
	}
	
	protected void sendWordAck() {
		//lastSentMsgId++;
		// Il msg Word ha fatto tutto il giro, ora invio ack a tutti (tranne me)
		for (int i=(getOrd()+1)%peers.size(); i!=getOrd(); i=(i+1)%peers.size()) {
			if (!peers.get(i).isActive()) continue;
			if (i==getOrd()) break;
			System.out.println(String.format("%s) Invio WordAck a peer %s", getOrd(), i));
			send_msg(new WordAckMsg(peers.get(i), lastSentMsgId));//, timer, lastWordTask, 2*T_trans+T_proc));
		}
		// Io già posso settare il turno nel prossimo giocatore
		nextTurn();
	}
	
	/* ------ ELECTION ------ */
	protected void startTurnHolderElection() {
		System.out.println(String.format("START ELECTION. Il turno attualmente è di %d", gameTable.getTurnHolder().getOrd()));
		if (electionActive) {
			System.out.println("ATTENZIONE!! ELEZIONE ERA GIA' IN CORSO. IGNORO startTurnHolderElection()");
			return;
		}
		electionActive = true;
		if (isTurnHolder()) {
			// Sono io il leader! Invio notizia a tutti i processi minori (in realtà tutti tranne me)
			for (int i=(getOrd()+1)%peers.size(); i!=getOrd(); i=(i+1)%peers.size()) {
				if (!peers.get(i).isActive()) continue;
				if (i==getOrd()) break;
				System.out.println(String.format("%s) Invio SetTurnHolder a peer %s", getOrd(), i));
				send_msg(new ElectionSetTurnHolderMsg(peers.get(i), getOrd()));
			}
			electionActive = false; // In questo caso l'elezione finisce subito.
		} else {
			// Non sono il leader. Invio elezione a tutti quelli prima di me, a partire
			// da chi credo sia il leader attuale
			for (int i=getTurnHolder().getOrd(); i!=getOrd(); i=(i+1)%peers.size()) {
				if (!peers.get(i).isActive()) continue;
				if (i==getOrd()) break;
				System.out.println(String.format("%s) Invio ElectionInit a peer %s", getOrd(), i));
				
				// Il delay nell'elezione del leader è sempre il seguente:
				long delay = 2*T_trans + T_proc;
				
				// Setta un primo timer tempo T per aspettare risposta
				// da ALMENO UN peer possibile coordinatore.
				if (firstPhaseElectionTask == null) {
					firstPhaseElectionTask = new TimerTask() {
						@Override
						public void run() {
							// Se scade, devi settare te stesso come coordinatore e dirlo a quelli dopo di te.
							System.out.println("Timer ElectionInit1 scaduto! Setto me come TurnHolder e lo dico a tutti.");
							gameTable.setTurnHolder(player);
							electionActive = false;
							startTurnHolderElection();
						}
					};
					timer.schedule(firstPhaseElectionTask, delay);
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
				
				send_msg(new ElectionInitMsg(peers.get(i), timer, firstPhaseElectionTask, secondPhaseElectionTask, delay));
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
