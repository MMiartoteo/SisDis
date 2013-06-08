package roundword.net;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Timer;
import java.util.TimerTask;

public class ElectionInitMsg extends Msg {
	
	//~ public static final int T_trans = 100; // milliseconds
	//~ public static final int T_proc  = 100; // milliseconds
	//~ public static final int T = 2*T_trans + T_proc;
	
	Timer timer;
	TimerTask timerTask1;
	TimerTask timerTask2;
	long delay;
	
	public ElectionInitMsg(Peer sourcePeer, Peer destPeer, Timer timer, TimerTask timerTask1, TimerTask timerTask2, long delay) {
		super(sourcePeer, destPeer);
		this.timer = timer;
		this.timerTask1 = timerTask1;
		this.timerTask2 = timerTask2;
		this.delay = delay;
	}
	
	public String execute() throws CrashException {
		try {
			Registry registry = LocateRegistry.getRegistry(destPeer.IPaddr, destPeer.serverPortno);
			ServerSideInterface stub = (ServerSideInterface) registry.lookup("ServerSide");
			
			// Provo a contattare il peer
			String result = stub.ElectionInit();
			System.out.println(result);
			
			if (result.equals("ok")) {
				// Mi è arrivata una risposta dal un possibile coordinatore. Di sicuro non sono io
				// Ma ora aspetto il messaggio di conferma dal coordinatore.
				synchronized (sourcePeer) {
					if (timerTask1 != null && timerTask2 != null) {
						try {
							timer.schedule(timerTask2, delay);
						} catch (IllegalStateException e) {
							// ignora, e non schedularlo di nuovo.
						}
						// Cancello il timer precedente:
						timerTask1.cancel();
						timerTask1 = null;
					}
				}
				return result;
			} else {
				throw new Exception(String.format("Ricevuta una risposta assurda dal server: ''", result));
			}
		} catch (java.rmi.ConnectException e) {
			// riprova solo se l'eccezione era di connessione fallita
			System.out.println("Msg Election Init Fallito. QUESTO PEER E' MORTO!");
		} catch (java.rmi.ConnectIOException e) {
			// riprova solo se l'eccezione era di connessione fallita
			System.out.println("Msg Election Init Fallito. QUESTO PEER E' MORTO!");
		} catch (java.rmi.UnmarshalException e) {
			// riprova solo se l'eccezione era di connessione fallita
			System.out.println("Msg Election Init Fallito.");
		} catch (Exception e) {
			System.out.println("ECCEZIONE");
			e.printStackTrace();
			System.exit(1);
		}
		throw new CrashException("Impossibile inviare messaggio!");
	}
}
