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

		public enum WordAddedState {OK, TIMEOUT_ELAPSED, NO_IN_DICTIONARY, SYLLABE_INCORRECT, PREVIOUSLY_ADDED};

		/**
		 * Called when a new word {@code word} is added by the player {@code player}
		 * @param player is the player that inserted the word
		 * @param word is the inserted word
		 * @param milliseconds is the time that the player spend to think and write the word
		 * @param state the state of the word
		* */
		void newWordAdded(Player player, Word word, long milliseconds, WordAddedState state);

		//Called when a/some player/s changes its point
		void playersPointsUpdate();

		//Called when the playing player changes
		void turnHolderChanged(Player oldTurnHolder, Player newTurnHolder);

		//Called when the game finished
		void gameFinished(Player winnerPlayer, List<Player> players);
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
	Player turnHolder;

	/**
	 * This represent the words list (i.e. the written words).
	 * If the most recent words are in the first positions
	 * */
	List<Word> words;

	/**
	 * The list of listener for callback that is call when a new word is inserted
	 * */
	Set<EventListener> eventListeners;

	/**
	 * Dictionary. To check if a word is valid or not
	 */
	Dictionary dictionary;


	boolean isGameFinished = false;
	Player winnerPlayer;


	// ------------------------------------------------------------------------
	// CONSTRUCTORS 
	// ------------------------------------------------------------------------

	/**
	 * Create a game table. The player that is playing is the first player of the player list
	 *
	 * param: playersList the list of all players
	 * param: localPlayer the player that plays in the current client
	 * param: dictionary an instance of the class Dictionary, to check if a word is valid or not
	 * */
	public GameTable(List<Player> playersList, Player localPlayer, Dictionary dictionary) {
		this(playersList, localPlayer, playersList.get(0), dictionary);
	}

	/**
	 * Create a game table.
	 *
	 * param: playersList the list of all players
	 * param: localPlayer the player that plays in the current client
	 * param: turnHolder the player that is playing
	 * param: dictionary an instance of the class Dictionary, to check if a word is valid or not
	 * */
	public GameTable(List<Player> playersList, Player localPlayer, Player turnHolder, Dictionary dictionary) {

		if (playersList == null) throw new NullPointerException("playersList must be not null");
		this.playersList = playersList;

		if (localPlayer == null) throw new NullPointerException("localPlayer must be not null");
		this.localPlayer = localPlayer;

		if (turnHolder == null) throw new NullPointerException("turnHolder must be not null");
		this.turnHolder = turnHolder;

		if (dictionary == null) throw new NullPointerException("dictionary must be not null");
		this.dictionary = dictionary;

		eventListeners = Collections.synchronizedSet(new HashSet<EventListener>());
		words = new ArrayList<Word>();

		boolean localPlayerFounded = false;
		boolean turnHolderFounded = false;
		for (Player p : playersList) {
			if (p == localPlayer) localPlayerFounded = true;
			if (p == turnHolder) turnHolderFounded = true;
			p.addEventListener(this);
		}

		//Check for errors
		if (!localPlayerFounded) throw new IllegalArgumentException("the localPlayer must be in the playersList");
		if (!turnHolderFounded) throw new IllegalArgumentException("the turnHolder must be in the playersList");
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

	public Player getTurnHolder() {
		return turnHolder;
	}

	/** This can be called only by method of this class.
	 *  In this way the nextTurn for example can call this one without checking some things that
	 *  the nextTurn knows.
	 * */
	public void setTurnHolder(Player turnHolder) {
		Player oldTurnHolder = this.turnHolder;


		if (this.turnHolder == turnHolder) return;
		if (turnHolder == null) throw new NullPointerException("turnHolder is null");

		//Check that is active
		if (!turnHolder.isActive()) throw new IllegalArgumentException("the turnHolder must be active");

		//Check for errors
		boolean turnHolderFounded = false;
		for (Player p : playersList) {
			if (p == turnHolder) turnHolderFounded = true;
		}
		if (!turnHolderFounded) throw new IllegalArgumentException("the turnHolder must be in the playersList");

		this.turnHolder = turnHolder;

		//Callbacks call
		for (EventListener el : eventListeners) el.turnHolderChanged(oldTurnHolder, turnHolder);

	}
	
	public List<Word> getWordsList() {
		return words;
	}

	/**
	 * Add a new word to the game table. Is assumed that the current playing player is the author of this word
	 *
	 * param: word the inserted word. With {@code null} it means that the player don't write anything and
	 * in this case the user will have a negative point addition.
	 * param: millisecondToReply the milliseconds that the user takes to write the word (used to calculate the points)
	 * */
	public void addWord(Word w, long millisecondToReply, boolean checkGameEnd) {

		String lastWordSyl = (words.size() > 0)? words.get(0).getLastSyllableSubWord() : null;
		EventListener.WordAddedState state = EventListener.WordAddedState.OK;

		if (w == null) { //the player doesn't write anything
			turnHolder.setLastResponseWrong(true);
			turnHolder.addPoints(Constants.PointsForNotReply);
			state = EventListener.WordAddedState.TIMEOUT_ELAPSED;
		} else {
			String wordStr = w.toString();
			if (dictionary.contains(w)) {
				if (lastWordSyl == null) { //No words before this
					turnHolder.setLastResponseWrong(false);
					words.add(0, w);
					turnHolder.addPoints(w.getValue() + (int) Math.round(Constants.PointsPerMilliseconds * millisecondToReply));
					state = EventListener.WordAddedState.OK;
				} else if (words.contains(w)) {
					turnHolder.setLastResponseWrong(false);
					turnHolder.addPoints(Constants.PointsForPreviouslyAddedWord);
					state = EventListener.WordAddedState.PREVIOUSLY_ADDED;
				} else if (wordStr.length() >= lastWordSyl.length()
						    && (wordStr.substring(0, lastWordSyl.length()).compareTo(lastWordSyl) == 0)) {
					turnHolder.setLastResponseWrong(false);
					words.add(0, w);
					turnHolder.addPoints(w.getValue() + (int) Math.round(Constants.PointsPerMilliseconds * millisecondToReply));
					state = EventListener.WordAddedState.OK;
				} else {
					turnHolder.setLastResponseWrong(true);
					turnHolder.addPoints(Constants.PointsForWrongWord);
					state = EventListener.WordAddedState.SYLLABE_INCORRECT;
				}
			} else { //the player insert a word that isn't in the dictionary
				turnHolder.setLastResponseWrong(true);
				turnHolder.addPoints(Constants.PointsForWrongWord);
				state = EventListener.WordAddedState.NO_IN_DICTIONARY;
			}
		}

		if (checkGameEnd && gameCouldFinish()) {
			isGameFinished = true;
			System.out.println("###################################");
			System.out.println("GAMETABLE: \n" + "La partita può terminare, vincitore: " + calculateTheWinner());
			System.out.println("###################################");
		}

		//Callbacks call
		Player p = turnHolder; //the listener can change the turn, we save it to make sure that is right
		for (EventListener el : eventListeners) el.newWordAdded(p, w, millisecondToReply, state);
	}

	/**
	 * Check if the game must terminate. True if the game must be ended.
	 * */
	public boolean gameCouldFinish() {
		boolean allNullWord = true;
		boolean weAreAlone = true;
		for (Player p : playersList) {
			if (p.isActive() && !p.isLastResponseWrong) allNullWord = false;
			if (p.isActive() && p != localPlayer) weAreAlone = false;
		}
		return allNullWord || weAreAlone;
	}

	/**
	 * Set the temporary winner of the game. This could change if someone dies. The real player is determined when the
	 * {@code finishTheGame} is called.
	 */
	public void setWinner(Player w) {

		//Check for errors
		boolean playerFounded = false;
		for (Player p : playersList) {
			if (p == w) playerFounded = true;
		}
		if (!playerFounded) throw new IllegalArgumentException("the player must be in the playersList");

		isGameFinished = true;
		winnerPlayer = w;
	}

	public Player getWinner() {
		return winnerPlayer;
	}

	/**
	 * Calculate the winner. This must be done only by a single user when this game is done in multiplayer
	 * @return
	 */
	public Player calculateTheWinner() {
		Player winnerPlayer = null;
		for (Player p : playersList) {
			if (p.isActive()) {
				if (winnerPlayer == null) winnerPlayer = p;
				else if (winnerPlayer.getPoints() < p.getPoints()) winnerPlayer = p;
			}
		}
		return winnerPlayer;
	}

	/**
	 * Confirm the winner, ending the game (i.e. a commit for the winner)
	 */
	public void finishTheGame() {
		for (EventListener el : eventListeners) el.gameFinished(winnerPlayer, playersList);
	}

	public boolean isGameFinished() {
		return isGameFinished;
	}

	// Listeners --------------------------------------------------------------
	
	public void addEventListener(EventListener listener) {
		if (listener == null) throw new NullPointerException("listener is null");
		eventListeners.add(listener);
	}

	public void removeEventListener(EventListener listener) {
		if (listener == null) throw new NullPointerException("listener is null");
		eventListeners.remove(listener);
	}

	@Override
	public void playerPointsUpdated(Player sender) {
		for (EventListener el : eventListeners) el.playersPointsUpdate();
	}


}
