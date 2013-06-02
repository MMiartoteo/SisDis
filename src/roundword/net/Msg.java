package roundword.net;

public abstract class Msg {
	
	Peer dest_peer;
	String dest_host;
	int dest_portno;
	
	public Msg(Peer dest_peer) {
		this.dest_peer = dest_peer;
		this.dest_host = dest_peer.IPaddr;
		this.dest_portno = dest_peer.server_portno;
	}
	
	public String toString() {
		return String.format("(%s)%s:%s", getClass().getName(), dest_host, dest_portno);
	}
	
	public abstract String execute() throws Exception;
}
