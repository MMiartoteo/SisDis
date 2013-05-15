package roundword;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This represents the game table, that contains 
 * some information that must be shared with the others
 * players, like the list of other players with their points,
 * or the list of written words.
 * */
public class GameTable {

	List<Player> players;
	
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
	 * The callback that is call when a new word is inserted
	 * */
	Runnable onNewWordAddedListener;
	
	
	public GameTable() {
		players = new ArrayList<Player>();
		words = new ArrayList<Word>();
	}
	
	public List<Player> getPlayersList() {
		return players;
	}
	
	public List<Word> getWordsList() {
		return words;
	}
	
	public void addWord(Word w) {
		words.add(0, w);
		if (onNewWordAddedListener != null) onNewWordAddedListener.run();
	}
	
	public void setOnNewWordAddedListener(Runnable listener) {
		onNewWordAddedListener = listener;
	}

}
