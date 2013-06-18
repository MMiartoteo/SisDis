package roundword.net;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ElectionSetTurnHolderMsg extends Msg {

	int turnHolder;
	
	public ElectionSetTurnHolderMsg(Peer sourcePeer, Peer destPeer, int turnHolder) {
		super(sourcePeer, destPeer);
		this.turnHolder = turnHolder;
	}
	
	public String execute() throws CrashException {
		try {
			Registry registry = LocateRegistry.getRegistry(destPeer.IPaddr, destPeer.serverPortno);
			ServerSideInterface stub = (ServerSideInterface) registry.lookup("ServerSide");
			return stub.ElectionSetTurnHolder(this.turnHolder);
		} catch (java.rmi.ConnectException e) {
			System.out.println("Msg TurnHolder Fallito. QUESTO PEER E' MORTO!");
		} catch (java.rmi.ConnectIOException e) {
			System.out.println("Msg TurnHolder Fallito. QUESTO PEER E' MORTO!");
		} catch (java.rmi.UnmarshalException e) {
			System.out.println("Msg TurnHolder Fallito. QUESTO PEER E' MORTO!");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		throw new CrashException("Impossibile inviare messaggio!");
	}
}
