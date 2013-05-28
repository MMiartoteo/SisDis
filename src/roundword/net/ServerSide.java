package roundword.net;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


public class ServerSide implements ServerSideInterface {
	Peer peer;
	
	public ServerSide(Peer peer) {
		this.peer = peer;
		try {
			ServerSideInterface stub = (ServerSideInterface) UnicastRemoteObject.exportObject(this, 0);
			Registry registry = LocateRegistry.createRegistry(peer.server_portno);
			registry.rebind("ServerSide", stub);
			System.out.println("ComputeEngine bound");
		}
		catch (Exception e) {
            System.err.println("ComputeEngine exception:");
            e.printStackTrace();
        }
        finally {
			System.out.println("Server ready");
		}
	}
	
	public String sayHello() {
        return String.format("Hello, world! I am %s in %s:%s", peer.player.getNickName(), peer.IPaddr, peer.server_portno);
    }
}
