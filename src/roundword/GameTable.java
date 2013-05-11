package roundword;

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
	
	List<Word> writtenWords;
	
	/**
	 * The time when the word is shown to the user. This is to calculate the
	 * points, because they depend on the time to reply. 
	 */
	Date timeOfLastWordShowed;
	
	
}
