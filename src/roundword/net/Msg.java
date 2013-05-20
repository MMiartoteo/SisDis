package roundword.net;

public class Msg {
	
	public enum MsgType {
		HELLO
	};
	
	String dest_host;
	MsgType type;
	String args;
	
	public Msg(String dest_host, MsgType type, String args) {
		this.dest_host = dest_host;
		this.type = type;
		this.args = args;
	}
}
