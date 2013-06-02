package roundword.net;
import java.util.Queue;
import java.util.concurrent.*;

public class ClientSide extends Thread {

	public static final int SleepTimeOnEmptyQueue = 200;

	private BlockingQueue<Msg> msgQ;
	
	public ClientSide() {
		msgQ = new LinkedBlockingQueue<Msg>();
	}
	
	public void run() {
		Msg m = null;

        System.out.println("Client started!");

        while (true) {
			System.out.println("Client cycle!");

			try {
				m = msgQ.poll(NetConstants.BlockingQueueTimeoutSeconds, TimeUnit.SECONDS);
			} catch (InterruptedException e) { }

			if (m == null) continue;

			System.out.println("STO CHIAMANDO SEND_MSG_RMI");
			send_msg_rmi(m);
			System.out.println("SEND_MSG_RMI HA FINITO");
		}
    }
    
    public void send_msg(Msg m) {
		msgQ.add(m);
	}
	
	private void send_msg_rmi(Msg m) {
		System.out.println("Provo a inviare messaggio " + m);
		try {
			String response = m.execute();
			System.out.println("response: " + response);
		} catch (CrashException e) {
			System.out.println("Setto il peer come non Active");
			m.dest_peer.active = false;
		} catch (Exception e) {
			System.err.println("Client exception: " + e.toString());
			e.printStackTrace();
		}
		// NOTA: Qui poi dovrai beccare gli errori di connessione per sgamare peer "crashed"
	}
}
