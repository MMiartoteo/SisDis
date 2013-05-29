package roundword.net;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Timer;
import java.util.TimerTask;

public class WordAckMsg extends Msg {
	
	long id;
	//~ Timer timer;
	//~ TimerTask timerTask;
	//~ long delay;
	
	public WordAckMsg(Peer to, long id) {//, Timer timer, TimerTask timerTask, long delay) {
		super(to);
		this.id = id;
		//~ this.timer = timer;
		//~ this.timerTask = timerTask;
		//~ this.delay = delay;
	}
	
	public String execute() throws Exception {
		Registry registry = LocateRegistry.getRegistry(dest_host, dest_portno);
		ServerSideInterface stub = (ServerSideInterface) registry.lookup("ServerSide");
		String response = null;
		try {
			response = stub.wordAck(id);
		} catch (Exception e) {
			throw new CrashException("Crash durante invio di messaggio WordAck");
			/// TODO: CATTURA e fai qualcosa
		} finally {
			//~ if (timer != null) {
				//~ timer.schedule(timerTask, delay); // delay is in milliseconds
			//~ }
			return response;
		}
	}
}
