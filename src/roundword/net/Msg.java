package roundword.net;

public abstract class Msg {
	
	String dest_host;
	int dest_portno;
	
	public Msg(Peer to) {
		this.dest_host = to.IPaddr;
		this.dest_portno = to.server_portno;
	}
	
	public String toString() {
		return String.format("(%s)%s:%s", getClass().getName(), this.dest_host, this.dest_portno);
	}
	
	public abstract String execute() throws Exception;
}
