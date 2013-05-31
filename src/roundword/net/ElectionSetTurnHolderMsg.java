package roundword.net;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ElectionSetTurnHolderMsg extends Msg {
	
	public static final int T_trans = 100; // milliseconds
	public static final int T_proc  = 100; // milliseconds
	public static final int T = 2*T_trans + T_proc;
	
	int turnHolder;
	
	public ElectionSetTurnHolderMsg(Peer to, int turnHolder) {
		super(to);
		this.turnHolder = turnHolder;
	}
	
	public String execute() throws Exception {
		for (int i=0; i<1; ++i) {
			try {
				Registry registry = LocateRegistry.getRegistry(dest_host, dest_portno);
				ServerSideInterface stub = (ServerSideInterface) registry.lookup("ServerSide");
				return stub.ElectionSetTurnHolder(this.turnHolder);
			} catch (java.rmi.ConnectException e) {
				// riprova solo se l'eccezione era di connessione fallita
				System.out.println("Msg TurnHolder Fallito...");
			}
			Thread.sleep(1000);
		}
		throw new CrashException("Impossibile inviare messaggio!");
	}
}
