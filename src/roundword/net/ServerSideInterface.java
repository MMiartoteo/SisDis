package roundword.net;
import roundword.*;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerSideInterface extends Remote {
	String sayHello() throws RemoteException;
	String ElectionInit() throws RemoteException;
	String ElectionSetTurnHolder(int turnHolder) throws RemoteException;
	String word(long id, Word word) throws RemoteException;
	String wordAck(long id) throws RemoteException;
}
