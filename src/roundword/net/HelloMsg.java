package roundword.net;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class HelloMsg extends Msg {
	
	public HelloMsg(Peer to) {
		super(to);
	}
	
	public String execute() throws Exception {
		// Il messaggio di Hello va provato qualche volta, per dare
		// il tempo agli altri server di essersi settati.
		for (int i=0; i<10; ++i) {
			try {
				Registry registry = LocateRegistry.getRegistry(dest_host, dest_portno);
				ServerSideInterface stub = (ServerSideInterface) registry.lookup("ServerSide");
				return stub.sayHello();
			} catch (java.rmi.ConnectException e) {
				// riprova solo se l'eccezione era di connessione fallita
				System.out.println("Hello fallito...");
			}
			Thread.sleep(1000);
		}
		throw new Exception("Impossibile inviare messaggio Hello!");
	}
}
