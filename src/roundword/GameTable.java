package roundword;

import java.util.*;

/**
 * This represents the game table, that contains 
 * some information that must be shared with the others
 * players, like the list of other players with their points,
 * or the list of written words.
 * */
public class GameTable implements Player.EventListener {

	// ------------------------------------------------------------------------
	// INTERFACES
	// ------------------------------------------------------------------------

	public interface EventListener extends java.util.EventListener {

		//Called when a new word is added
		void newWordAdded(Word w);

		//Called when a/some player/s changes its point
		void playersPointsUpdate();
	}

	// ------------------------------------------------------------------------
	// FIELDS
	// ------------------------------------------------------------------------

	/**
	 * The list of all players
	 */
	List<Player> playersList;

	/**
	 * The player that plays in the current client
	 * This must be present in the players list to
	 */
	Player ownPlayer;
	
	/**
	 * This represent the words list (i.e. the written words).
	 * If the most recent words are in the first positions
	 * */
	List<Word> words;
	
	/**
	 * The time when the word is shown to the user. To calculate the
	 * points, because they depend on the time to reply. 
	 */
	Date timeOfLastWordShowed;
	
	/**
	 * The list of listener for callback that is call when a new word is inserted
	 * */
	Set<EventListener> eventListeners;


	// ------------------------------------------------------------------------
	// METHODS 
	// ------------------------------------------------------------------------
	
	public GameTable(List<Player> playersList, Player ownPlayer) {
		eventListeners = Collections.synchronizedSet(new HashSet<EventListener>());

		this.playersList = playersList;
		for (Player p : playersList) p.addEventListener(this);
		this.ownPlayer = ownPlayer;

		words = new ArrayList<Word>();
	}
	
	public List<Player> getPlayersList() {
		return playersList;
	}

	public Player getOwnPlayer() {
		return ownPlayer;
	}
	
	public List<Word> getWordsList() {
		return words;
	}
	
	public void addWord(Word w) {
		words.add(0, w);
		for (EventListener el : eventListeners) el.newWordAdded(w);
	}
	
	public void addEventListener(EventListener listener) {
		eventListeners.add(listener);
	}

	public void removeEventListener(EventListener listener) {
		eventListeners.remove(listener);
	}

	@Override
	public void playerPointsUpdated(Player sender) {
		for (EventListener el : eventListeners) el.playersPointsUpdate();
	}


}