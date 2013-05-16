import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Peer implements Remote {
	
	String name;
	String IPaddr;
	String portno;
	String id;
	
	public Peer(String name, String IPaddr, String portno, String[] peers_contacts) {
		this.name = name;
		this.IPaddr = IPaddr;
		this.portno = portno;
		this.id = IPaddr+":"+portno;
		try {
			Remote stub = (Remote) UnicastRemoteObject.exportObject(this, 0);
			Registry registry = LocateRegistry.getRegistry();
			registry.rebind("Peer", stub);
			System.out.println("ComputeEngine bound");
		}
		catch (Exception e) {
            System.err.println("ComputeEngine exception:");
            e.printStackTrace();
        }
	}
	
	//~ public <T> T executeTask(Task<T> t) {
        //~ return t.execute();
    //~ }
	
	
}
