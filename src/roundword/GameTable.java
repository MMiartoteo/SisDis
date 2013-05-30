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

		//Called when the playing player changes
		void playingPlayerChanged(Player oldPlayingPlayer, Player newPlayingPlayer);
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
	 * This must be present in the players list too
	 */
	Player localPlayer;

	/**
	 * The player that is playing
	 * This must be present in the players list too
	 */
	Player playingPlayer;
	
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
	// CONSTRUCTORS 
	// ------------------------------------------------------------------------

	/**
	 * Create a game table. The player that is playing is the first player of the player list
	 *
	 * param: playersList the list of all players
	 * param: localPlayer the player that plays in the current client
	 * */
	public GameTable(List<Player> playersList, Player localPlayer) {
		this(playersList, localPlayer, playersList.get(0));
	}

	/**
	 * Create a game table.
	 *
	 * param: playersList the list of all players
	 * param: localPlayer the player that plays in the current client
	 * param: playingPlayer the player that is playing
	 * */
	public GameTable(List<Player> playersList, Player localPlayer, Player playingPlayer) {

		if (playersList == null) throw new NullPointerException("playersList must be not null");
		this.playersList = playersList;

		if (localPlayer == null) throw new NullPointerException("localPlayer must be not null");
		this.localPlayer = localPlayer;

		if (playingPlayer == null) throw new NullPointerException("playingPlayer must be not null");
		this.playingPlayer = playingPlayer;

		eventListeners = Collections.synchronizedSet(new HashSet<EventListener>());
		words = new ArrayList<Word>();

		boolean localPlayerFounded = false;
		boolean playingPlayerFounded = false;
		for (Player p : playersList) {
			if (p == localPlayer) localPlayerFounded = true;
			if (p == playingPlayer) playingPlayerFounded = true;
			p.addEventListener(this);
		}

		//Check for errors
		if (!localPlayerFounded) throw new IllegalArgumentException("the localPlayer must be in the playersList");
		if (!playingPlayerFounded) throw new IllegalArgumentException("the playingPlayer must be in the playersList");
	}

	// ------------------------------------------------------------------------
	// METHODS 
	// ------------------------------------------------------------------------

	public List<Player> getPlayersList() {
		return playersList;
	}

	public Player getLocalPlayer() {
		return localPlayer;
	}

	public Player getPlayingPlayer() {
		return playingPlayer;
	}

	public void setPlayingPlayer(Player playingPlayer) {
		if (playingPlayer == null) throw new NullPointerException("playingPlayer must be not null");

		if (this.playingPlayer != playingPlayer) {
			Player oldPlayingPlayer = this.playingPlayer;
			this.playingPlayer = playingPlayer;

			//Check for errors
			boolean playingPlayerFounded = false;
			for (Player p : playersList) {
				if (p == playingPlayer) playingPlayerFounded = true;
			}
			if (!playingPlayerFounded) throw new IllegalArgumentException("the playingPlayer must be in the playersList");

			//Callbacks call
			for (EventListener el : eventListeners) el.playingPlayerChanged(oldPlayingPlayer, playingPlayer);
		}
	}
	
	public List<Word> getWordsList() {
		return words;
	}

	/**
	 * Add a new word to the game table. Is assumed that the current playing player is the author of this word
	 * */
	public void addWord(Word w) {
		if (w == null) throw new NullPointerException("the word must be not null");
		words.add(0, w);
		playingPlayer.addPoints(w.getValue());

		//Callbacks call
		for (EventListener el : eventListeners) el.newWordAdded(w);
	}

	/**
	 * We go to the next turn, the playing player will be the next of the player list.
	 * */
	public void nextTurn() {
		Player oldPlayingPlayer = playingPlayer;

		boolean playingPlayerFounded = false;
		Iterator<Player> i = playersList.iterator();
		while (i.hasNext()) {
			if (oldPlayingPlayer == i.next()) {
				playingPlayerFounded = true;
				break;
			}
		}
		playingPlayer = (i.hasNext()) ? i.next() : playersList.get(0);
		if (!playingPlayerFounded) new RuntimeException("the next turn can't found the current playing player");

		//Callbacks call
		for (EventListener el : eventListeners) el.playingPlayerChanged(oldPlayingPlayer, playingPlayer);
	}

	// Listeners --------------------------------------------------------------
	
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