package roundword.net;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.*;

public class ClientSide extends Thread {
	
	BlockingQueue msgQ;
	
	public ClientSide() {
		msgQ = new LinkedBlockingDeque(2^20);
	}
	
	public void run() {
        System.out.println("Client started!");
        while (true) {
			System.out.println("Client cycle!");
			Msg m = null;
			try {
				m = (Msg) msgQ.poll(NetConstants.BlockingQueueTimeoutSeconds, TimeUnit.SECONDS);
			}
			catch (InterruptedException e) {
				// ??
			}
			if (m == null) continue;
			send_msg_rmi(m);
		}
    }
    
    public void send_msg(Msg m) {
		// Incoda (bloccante?)
		try {msgQ.offer(m, NetConstants.BlockingQueueTimeoutSeconds, TimeUnit.SECONDS);}
		catch (InterruptedException e) {}
	}
	
	private void send_msg_rmi(Msg m) {
		try {
			Registry registry = LocateRegistry.getRegistry(m.dest_host, NetConstants.LocateRegistryPort);
			ServerSideInterface stub = (ServerSideInterface) registry.lookup("ServerSide");
			
			String response = "";
			switch (m.type) {
				case HELLO: response = stub.sayHello(); break;
				default: throw new Exception("Wrong message name "+m.type);
			}
			
			System.out.println("response: " + response);
		} catch (Exception e) {
			System.err.println("Client exception: " + e.toString());
			e.printStackTrace();
		}
	}
}
