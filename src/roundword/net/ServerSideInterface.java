package roundword.net;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerSideInterface extends Remote {
	String sayHello() throws RemoteException;
	String ElectionInit() throws RemoteException;
	String ElectionTurnHolder(int turnHolder) throws RemoteException;
	String word(long id, String word) throws RemoteException;
	String wordAck(long id) throws RemoteException;
}
