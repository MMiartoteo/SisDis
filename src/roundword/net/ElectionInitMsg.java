package roundword.net;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ElectionInitMsg extends Msg {
	
	public static final int T_trans = 100; // milliseconds
	public static final int T_proc  = 100; // milliseconds
	public static final int T = 2*T_trans + T_proc;
	
	public ElectionInitMsg(Peer to) {
		super(to);
	}
	
	public String execute() throws Exception {
		for (int i=0; i<10; ++i) {
			try {
				Registry registry = LocateRegistry.getRegistry(dest_host, dest_portno);
				ServerSideInterface stub = (ServerSideInterface) registry.lookup("ServerSide");
				return stub.ElectionInit();
			} catch (java.rmi.ConnectException e) {
				// riprova solo se l'eccezione era di connessione fallita
				System.out.println("Msg Election Init Fallito...");
			}
			Thread.sleep(100);
		}
		throw new CrashException("Impossibile inviare messaggio!");
	}
}
