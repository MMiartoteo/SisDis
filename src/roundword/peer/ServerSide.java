package roundword.peer;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


public class ServerSide implements ServerSideInterface {
	public ServerSide() {
		try {
			ServerSideInterface stub = (ServerSideInterface) UnicastRemoteObject.exportObject(this, 0);
			Registry registry = LocateRegistry.createRegistry(8000);
			registry.rebind("ServerSide", stub);
			System.out.println("ComputeEngine bound");
		}
		catch (Exception e) {
            System.err.println("ComputeEngine exception:");
            e.printStackTrace();
        }
	}
	
	public String sayHello() {
        return "Hello, world!";
    }
}
