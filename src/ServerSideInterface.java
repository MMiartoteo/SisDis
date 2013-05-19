import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerSideInterface extends Remote {
	String sayHello() throws RemoteException;
}
