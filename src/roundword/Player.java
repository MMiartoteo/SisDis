package roundword;


import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Player {

	// ------------------------------------------------------------------------
	// INTERFACES
	// ------------------------------------------------------------------------

	public interface EventListener extends java.util.EventListener {
		void playerPointsUpdated(Player sender);
	}

	// ------------------------------------------------------------------------
	// FIELDS
	// ------------------------------------------------------------------------

	/**
	 * The position of the player, decided by a registrar at the begin of the game. This is a convenience variable
	 * that is useful to have a numeric id that is valid for all the player of the game, even they are in another
	 * client. Univocal.
	 * */
	int ord;

	/**
	 * Determines if the player is active or not. For example the player could left the game, or can lost the internet
	 * connection if the game is player via the web.
	 */
	boolean active;

	String nickName;
	
	int points;
	
	boolean isLastResponseWrong;

	/**
	 * The callback that is call when a player change its points
	 */
	Set<EventListener> eventListeners;

	// ------------------------------------------------------------------------
	// METHODS
	// ------------------------------------------------------------------------

	public Player(String nickname, int ord) {
		if (ord < 0) throw new IllegalArgumentException("ord must be non-negative");
		if (nickname == null) throw new NullPointerException("nickname is null");
		if (nickname.length() == 0) throw new IllegalArgumentException("nickname lenght is 0");
		
		System.out.println(String.format("Creato player %s %d", nickname, ord));
		
		eventListeners = Collections.synchronizedSet(new HashSet<EventListener>());
		this.nickName = nickname;
		this.ord = ord;
		this.active = true;
		this.isLastResponseWrong = false;
		points = 0;
	}
	
	public void setPoints(int points) {
		if (this.points != points) {
			this.points = points;
			for (EventListener el : eventListeners) el.playerPointsUpdated(this);
		}
	}

	public void addPoints(int points) {
		setPoints(this.points + points);
	}
	
	public int getPoints() {
		return points;
	}
	
	public String getNickName() {
		return nickName;
	}

	public int getOrd() {
		return ord;
	}

	public boolean isActive() {
		return active;
	}

	public void setActiveStatus(boolean active) {
		this.active = active;
	}
	
	public void addEventListener(EventListener listener) {
		if (listener == null) throw new NullPointerException("listener is null");
		eventListeners.add(listener);
	}

	public void removeEventListener(EventListener listener) {
		if (listener == null) throw new NullPointerException("listener is null");
		eventListeners.remove(listener);
	}

	public void setLastResponseWrong(boolean b) {
		isLastResponseWrong = b;
	}

	public boolean isLastResponseWrong() {
		return isLastResponseWrong;
	}

	@Override
	public String toString() {
		return nickName;
	}
	
}
