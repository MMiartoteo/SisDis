package roundword.peer;

public class Peer {
	
	public interface Task<T> {
		T execute();
	}
	
	//~ String name;
	//~ String IPaddr;
	//~ String portno;
	//~ String id;
	
	ClientSide client;
	ServerSide server;
	
	public Peer(String name, String IPaddr, String portno, String[] peers_contacts) {
		//~ this.name = name;
		//~ this.IPaddr = IPaddr;
		//~ this.portno = portno;
		//~ this.id = IPaddr+":"+portno;
		this.start_server_side();
		this.start_client_side();
	}
	
	public void start_server_side() {
		server = new ServerSide();
	}
	
	public void start_client_side() {
        client = new ClientSide();
        client.start();
	}
}
