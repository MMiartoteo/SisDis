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
				
				/// TODO: Setta un primo timer tempo T per aspettare risposta
				/// da ALMENO UN peer possibile coordinatore. Se scade, devi
				/// settare te stesso come coordinatore e dirlo a quelli dopo di te.
				
				String result = stub.ElectionInit();
				if (result.equals("ok")) {
					// Mi è arrivata una risposta dal un possibile coordinatore. Di sicuro non sono io
					// Ma ora aspetto il messaggio coordinatore.
					/// TODO: Cancellare il timer precedente
					/// Settare TIMER per messaggio SetTurnHolder
					/// Se non arriva, devi indire una nuova elezione
				} else {
					throw new Exception(String.format("Ricevuta una risposta assurda dal server: ''", result));
				}
			} catch (java.rmi.ConnectException e) {
				// riprova solo se l'eccezione era di connessione fallita
				System.out.println("Msg Election Init Fallito. QUESTO PEER E' MORTO!");
			}
			Thread.sleep(100);
		}
		throw new CrashException("Impossibile inviare messaggio!");
	}
}
