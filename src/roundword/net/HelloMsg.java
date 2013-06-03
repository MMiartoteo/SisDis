package roundword.net;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class HelloMsg extends Msg {
	
	public HelloMsg(Peer sourcePeer, Peer destPeer) {
		super(sourcePeer, destPeer);
	}
	
	public String execute() throws Exception {
		// Il messaggio di Hello va provato qualche volta, per dare
		// il tempo agli altri server di essersi settati.
		for (int i=0; i<NetConstants.HelloRetry; ++i) {
			try {
				Registry registry = LocateRegistry.getRegistry(destPeer.IPaddr, destPeer.serverPortno);
				ServerSideInterface stub = (ServerSideInterface) registry.lookup("ServerSide");
				return stub.sayHello();
			} catch (java.rmi.ConnectException e) {
				// riprova solo se l'eccezione era di connessione fallita
				System.out.println("Hello fallito...");
			} catch (java.rmi.ConnectIOException e) {
				// riprova solo se l'eccezione era di connessione fallita
				System.out.println("Hello fallito...");
			}
			Thread.sleep(NetConstants.HelloRetryMilliseconds);
		}
		throw new Exception("Impossibile inviare messaggio Hello!");
	}
}
