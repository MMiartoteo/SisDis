package roundword;


import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

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

	String nickName;
	
	int ord; // Ordinale del player. Univoco. Dato dal registrar
	
	int points;
	
	//IP Address, or other information to contact him

	/**
	 * The callback that is call when a player change its points
	 */
	Set<EventListener> eventListeners;

	// ------------------------------------------------------------------------
	// METHODS
	// ------------------------------------------------------------------------

	public Player(String nickname, int ord) {
		eventListeners = Collections.synchronizedSet(new HashSet<EventListener>());
		this.nickName = nickname;
		this.ord = ord;
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
	
	public void addEventListener(EventListener listener) {
		eventListeners.add(listener);
	}

	public void removeEventListener(EventListener listener) {
		eventListeners.remove(listener);
	}

	@Override
	public String toString() {
		return nickName;
	}
	
}
