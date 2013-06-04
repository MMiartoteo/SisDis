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

		//Called when a new word {@code word} is added by the player {@code player}
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

	public void setTurnHolder(Player turnHolder) {
		setTurnHolder(turnHolder, false);
	}

	/** This can be called only by method of this class.
	 *  In this way the nextTurn for example can call this one without checking some things that
	 *  the nextTurn knows.
	 * */
	private void setTurnHolder(Player turnHolder, boolean avoidChecks) {
		Player oldTurnHolder = this.turnHolder;

		if (!avoidChecks) {
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
		}

		this.turnHolder = turnHolder;

		if (!isGameFinished && gameCouldFinish()) {
			finishTheGame();
		}

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
	public void addWord(Word w, long millisecondToReply) {

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

	private void finishTheGame() {
		isGameFinished = true;

		Player winnerPlayer = null;
		for (Player p : playersList) {
			if (winnerPlayer == null) winnerPlayer = p;
			else if (winnerPlayer.getPoints() < p.getPoints()) winnerPlayer = p;
		}
		for (EventListener el : eventListeners) el.gameFinished(winnerPlayer, playersList);
	}

	/**
	 * We go to the next turn, the playing player will be the next of the player list. If you want to set the turn
	 * to another player, for example after a catastrophic event, you can use the {@code setTurnHolder}.
	 * */
	public void nextTurn() {
		Player oldTurnHolder = turnHolder;
		Player newTurnHolder = null;

		boolean turnHolderFounded = false;
		Iterator<Player> i = playersList.iterator();
		while (i.hasNext()) {
			if (oldTurnHolder == i.next()) {
				turnHolderFounded = true;
				break;
			}
		}
		if (!turnHolderFounded) throw new RuntimeException("can't found the current playing player");

		//Find the first active player to assign the turn
		Player tempP;
		if (!i.hasNext()) i = playersList.iterator(); //rewind
		while (i.hasNext()) {

			tempP = i.next();
			if (tempP.isActive()) {
				newTurnHolder = tempP;
				break;
			}

			if (tempP == oldTurnHolder) throw new RuntimeException("no player founded that can get the turn");

			if (!i.hasNext()) i = playersList.iterator(); //rewind
		}

		setTurnHolder(newTurnHolder, true);

		System.out.println("######### NEXT TURN #########");
		System.out.println(String.format("ORA TOCCA A: %s %s", turnHolder.getNickName(), turnHolder.getOrd()));
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
