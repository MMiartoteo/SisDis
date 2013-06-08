package roundword.net;
import roundword.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

public interface ServerSideInterface extends Remote {
	String sayHello() throws RemoteException;
	String ElectionInit() throws RemoteException;
	String ElectionSetTurnHolder(int turnHolder) throws RemoteException;
	String word(long turnId, Word word, long millisecondToReply, byte winnerOrd, Set<Byte> crashedPeerOrds) throws RemoteException;
	String wordAck(long turnId) throws RemoteException;
}
