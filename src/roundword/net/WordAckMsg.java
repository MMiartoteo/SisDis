package roundword.net;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Timer;
import java.util.TimerTask;

public class WordAckMsg extends Msg {
	
	long id;
	
	public WordAckMsg(Peer sourcePeer, Peer destPeer, long id) {
		super(sourcePeer, destPeer);
		this.id = id;
	}
	
	public String execute() throws CrashException {
		try {
			Registry registry = LocateRegistry.getRegistry(destPeer.IPaddr, destPeer.serverPortno);
			ServerSideInterface stub = (ServerSideInterface) registry.lookup("ServerSide");
			return stub.wordAck(id);
		} catch (java.rmi.ConnectException e) {
			System.out.println("Msg WordAckMsg Fallito. QUESTO PEER E' MORTO!");
		} catch (java.rmi.ConnectIOException e) {
			System.out.println("Msg WordAckMsg Fallito. QUESTO PEER E' MORTO!");
		} catch (java.rmi.UnmarshalException e) {
			System.out.println("Msg WordAckMsg Fallito. QUESTO PEER E' MORTO!");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		throw new CrashException("Crash durante invio di messaggio WordAck");
	}
}
