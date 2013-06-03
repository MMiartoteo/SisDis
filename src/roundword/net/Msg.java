package roundword.net;

public abstract class Msg {
	
	final Peer sourcePeer;
	final Peer destPeer;
	
	public Msg(Peer sourcePeer, Peer destPeer) {
		this.sourcePeer = sourcePeer;
		this.destPeer = destPeer;
	}
	
	public String toString() {
		return String.format("(%s)%s:%s", getClass().getName(), destPeer.IPaddr, destPeer.serverPortno);
	}
	
	public abstract String execute() throws Exception;
}
