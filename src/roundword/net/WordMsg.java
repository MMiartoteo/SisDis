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
	public WordMsg(Peer to, long id, Word word, Timer timer, TimerTask timerTask, long delay) {
		super(to);
		this.id = id;
		this.word = word;
		this.timer = timer;
		this.timerTask = timerTask;
		this.delay = delay;
	}
	
	/* Questa viene usata da chi fa solo forwarding */
	public WordMsg(Peer to, long id, Word word) {
		super(to);
		this.id = id;
		this.word = word;
	}
	
	public String execute() throws Exception {
		Registry registry = LocateRegistry.getRegistry(dest_host, dest_portno);
		ServerSideInterface stub = (ServerSideInterface) registry.lookup("ServerSide");
		String response = null;
		try {
			if (timer != null) {
				timer.schedule(timerTask, delay); // delay is in milliseconds
			}
			response = stub.word(id, this.word);
		} catch (Exception e) {
			throw new CrashException("Crash durante invio di messaggio Word");
			/// TODO: CATTURA e fai qualcosa
		} finally {
			return response;
		}
	}
}
