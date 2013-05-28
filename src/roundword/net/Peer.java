package roundword.net;
import roundword.*;
import java.util.List;

public class Peer {
	
	public interface Task<T> {
		T execute();
	}
	
	Player player;
	int ord; // Usato per elezione leader
	String IPaddr;
	int server_portno;
	public List<Peer> peers;
	ClientSide client;
	ServerSide server;
	
	public Peer(Player player, int ord, String IPaddr, int server_portno) {
		this.player = player;
		this.ord = ord;
		this.IPaddr = IPaddr;
		this.server_portno = server_portno;
	}
	
	public void add_peer_list(List<Peer> peers) {
		// Chiamata solo sul peer locale
		this.peers = peers;
	}
	
	public void start() {
		// Chiamata solo sul peer locale
		this.start_server_side();
		this.start_client_side();
	}
	
	public void send_msg(Msg m) {
		// Chiamata solo sul peer locale
		client.send_msg(m);
	}
	
	private void start_server_side() {
		server = new ServerSide(this);
	}
	
	private void start_client_side() {
        client = new ClientSide();
        client.start();
	}

	
}
