package roundword.net;
import roundword.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Peer {
	
	public static final long T_trans = 100; // milliseconds
	public static final long T_proc  = 100; // milliseconds
	
	Player player; // The associated player
	
	int ord;       // Usato per identificazione univoca del peer
	boolean local = false;// True solo se l'istanza corrente corrisponde al peer locale
	boolean active = true;// Se il peer è crashato o no
	
	List<Peer> peers;   // Da sta lista non togliamo nulla (almeno per ora)
	int turnHolder = 0; // All'inizio tutti sanno che il primo a giocare è il giocatore zero
	
	String IPaddr;
	int server_portno;
	
	ClientSide client;
	ServerSide server;
	
	Timer timer;
	TimerTask lastWordTask;
	TimerTask lastElectionTask;
	
	long lastSentMsgId = 0;
	
	public Peer(Player player, int ord, String IPaddr, int server_portno) {
		this.player = player;
		this.ord = ord;
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
		System.out.println(String.format("PEER LOCALE = %d", this.ord));
	}
	
	public void set_peers(List<Peer> peers) {
		assert local;
		this.peers = peers;
	}
	
	public void start() {
		assert local;
		this.start_server_side();
		this.start_client_side();
		
		/// TODO: Fai partire i vari timer
		
		if (this.isTurnHolder()) {
			// Solo il primo giocatore/peer lancia elezione (più che altro per far sapere agli altri che c'è)
			this.startTurnHolderElection();
		}
	}
	
	/* ############################## */
	/*             VARIE              */
	/* ############################## */
	public void send_msg(Msg m) {
		assert local;
		client.send_msg(m);
	}
	
	public boolean isActive() {
		return this.active;
	}
	
	public boolean isTurnHolder() {
		return this.ord == this.turnHolder;
	}
	
	public Peer getNextPeer() {
		for (int i=(this.ord+1)%this.peers.size(); i!=this.ord; i++) {
			assert i!=this.ord; // Significherebbe che non ci sono più altri peer attivi in giro...
			Peer p = peers.get(i);
			if (p.isActive()) {
				return p;
			}
		}
		return null;
	}
	
	
	
	
	
	
	
	/* ############################## */
	/*        EVENTI DI GIOCO         */
	/* ############################## */
	public void chosenWord(String word) {
		assert ord==turnHolder;
		sendWord(word);
	}
	
	
	
	
	
	/* ############################## */
	/*     COMUNICAZIONI TRA PEER     */
	/* ############################## */
	protected void sendWord(final String word) {
		System.out.println(String.format("%s) Invio Word a peer successore %s", ord, getNextPeer().ord));
		
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
		
		send_msg(new WordMsg(getNextPeer(), lastSentMsgId, word, timer, lastWordTask, peers.size()*(T_trans+T_proc)));
	}
	
	protected void forwardWord(long id, String word) {
		try{Thread.sleep(1000);} catch (Exception e) {}
		System.out.println(String.format("%s) Forwardo Word a peer successore %s", ord, getNextPeer().ord));
		send_msg(new WordMsg(getNextPeer(), id, word));
	}
	
	protected void sendWordAck() {
		lastSentMsgId++;
		for (int i=(ord+1)%peers.size(); i!=ord; i=(i+1)%peers.size()) {
			if (i==ord) break;
			System.out.println(String.format("%s) Invio WordAck a peer %s", ord, i));
			
			lastWordTask = new TimerTask() {
				@Override
				public void run() {
					System.out.println("Timer WordAck(2) scaduto!");
					/// TODO: fai partire di nuovo il messaggio di Word, con id maggiore?
					//sendWord(word);
				}
			};
			
			send_msg(new WordAckMsg(peers.get(i), lastSentMsgId, timer, lastWordTask, 2*T_trans+T_proc));
		}
	}
	
	public void startTurnHolderElection() {
		if (ord==turnHolder) {
			// Sono io il leader! Invio notizia a tutti i processi minori (in realtà tutti tranne me)
			for (int i=(ord+1)%peers.size(); i!=ord; i=(i+1)%peers.size()) {
				if (i==this.ord) break;
				System.out.println(String.format("%s) Invio TurnHolder a peer %s", ord, i));
				send_msg(new ElectionTurnHolderMsg(peers.get(i), this.ord));
			}
		} else {
			// Non sono il leader. Invio elezione a tutti quelli prima di me, a partire
			// da chi credo sia il leader attuale
			for (int i=turnHolder; i!=ord; i=(i+1)%peers.size()) {
				if (i==ord) break;
				System.out.println(String.format("%s) Invio Elezione a peer %s", ord, i));
				/// TODO: invia
			}
		}
	}
	
	
	
	
	
	
	/* ############################## */
	/*             PRIVATI            */
	/* ############################## */
	private void start_server_side() {
		server = new ServerSide(this);
	}
	
	private void start_client_side() {
        client = new ClientSide();
        client.start();
	}

	
}
