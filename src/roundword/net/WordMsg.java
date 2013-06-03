package roundword.net;
import roundword.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Timer;
import java.util.TimerTask;

public class WordMsg extends Msg {
	
	long id;
	Word word;
	Timer timer;
	TimerTask timerTask;
	long delay;
	
	/* Questa viene usata dal turnista, per avere anche un tempo di attesa */
	public WordMsg(Peer sourcePeer, Peer destPeer, long id, Word word, Timer timer, TimerTask timerTask, long delay) {
		super(sourcePeer, destPeer);
		this.id = id;
		this.word = word;
		this.timer = timer;
		this.timerTask = timerTask;
		this.delay = delay;
	}
	
	/* Questa viene usata da chi fa solo forwarding */
	public WordMsg(Peer sourcePeer, Peer destPeer, long id, Word word) {
		super(sourcePeer, destPeer);
		this.id = id;
		this.word = word;
	}
	
	private void sendToNext() throws CrashException {
		if (timer != null) {
			sourcePeer.send_msg(new WordMsg(sourcePeer, destPeer.getNextActivePeer(), id, word, timer, timerTask, delay));
		} else {
			sourcePeer.send_msg(new WordMsg(sourcePeer, destPeer.getNextActivePeer(), id, word));
		}
	}
	
	public String execute() throws CrashException {
		try {
			Registry registry = LocateRegistry.getRegistry(destPeer.IPaddr, destPeer.serverPortno);
			ServerSideInterface stub = (ServerSideInterface) registry.lookup("ServerSide");
			String response = null;
			if (timer != null) {
				timer.schedule(timerTask, delay); // delay is in milliseconds
			}
			return stub.word(id, this.word);
		} catch (java.rmi.ConnectException e) {
			// Prova a inviare a quello dopo ancora...
			System.out.println("Msg Word Fallito. QUESTO PEER E' MORTO! Provo a inviare al Peer dopo.");
			sendToNext();
		} catch (java.rmi.ConnectIOException e) {
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
