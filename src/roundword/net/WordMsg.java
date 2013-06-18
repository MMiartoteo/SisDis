package roundword.net;
import roundword.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class WordMsg extends Msg {
	
	long id;
	Word word;
	long remainingTimeMilliseconds;
	byte winnerOrd;
	Timer timer;
	TimerTask timerTask;
	long delay;
	//byte msgOriginatorOrd;
	
	/* Questa viene usata dal turnista, per avere anche un tempo di attesa */
	public WordMsg(Peer sourcePeer, Peer destPeer, long id, Word word, long remainingTimeMilliseconds, byte winnerOrd, Timer timer, TimerTask timerTask, long delay) {
		super(sourcePeer, destPeer);
		this.id = id;
		this.word = word;
		this.remainingTimeMilliseconds = remainingTimeMilliseconds;
		this.winnerOrd = winnerOrd;
		this.timer = timer;
		this.timerTask = timerTask;
		this.delay = delay;
		//this.msgOriginatorOrd = (byte)sourcePeer.getOrd();
	}

	/* Questa viene usata da chi fa solo forwarding */
	public WordMsg(Peer sourcePeer, Peer destPeer, long id, Word word, long remainingTimeMilliseconds, byte winnerOrd) {//, byte msgOriginatorOrd) {
		super(sourcePeer, destPeer);
		this.id = id;
		this.word = word;
		this.remainingTimeMilliseconds = remainingTimeMilliseconds;
		this.winnerOrd = winnerOrd;
		//this.msgOriginatorOrd = msgOriginatorOrd;
	}
	
	private void sendToNext() throws CrashException {
//		if (timer != null) {
//			sourcePeer.send_msg(new WordMsg(sourcePeer, destPeer.getNextActivePeer(), id, word, remainingTimeMilliseconds, winnerOrd, timer, timerTask, delay));
//		} else {
			sourcePeer.send_msg(new WordMsg(sourcePeer, destPeer.getNextActivePeer(), id, word, remainingTimeMilliseconds, winnerOrd));//, msgOriginatorOrd));
//		}
	}
	
	public String execute() throws CrashException {
		try {
			Registry registry = LocateRegistry.getRegistry(destPeer.IPaddr, destPeer.serverPortno);
			ServerSideInterface stub = (ServerSideInterface) registry.lookup("ServerSide");
			Set<Byte> crashedOrds;

			synchronized (sourcePeer) {

				if (timer != null) {
					//timerTask.cancel();
					timer.schedule(timerTask, delay); // delay is in milliseconds
				}

				crashedOrds = sourcePeer.getCrashedPeerOrds();
			}

			return stub.word(id, word, remainingTimeMilliseconds, winnerOrd, crashedOrds);

		} catch (java.rmi.ConnectException e) {
			// Prova a inviare a quello dopo ancora...
			System.out.println("Msg Word Fallito. QUESTO PEER E' MORTO! Provo a inviare al Peer dopo.");
			sendToNext();
		} catch (java.rmi.ConnectIOException e) {
			// Prova a inviare a quello dopo ancora...
			System.out.println("Msg Word Fallito. QUESTO PEER E' MORTO! Provo a inviare al Peer dopo.");
			sendToNext();
		} catch (java.rmi.UnmarshalException e) {
			// Prova a inviare a quello dopo ancora...
			System.out.println("Msg Word Fallito. QUESTO PEER E' MORTO! Provo a inviare al Peer dopo.");
			sendToNext();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		throw new CrashException("Impossibile inviare messaggio!");
	}
}
