package roundword.ui;

import java.util.List;

import javax.swing.AbstractListModel;
import roundword.Player;

public class PlayersListModel extends AbstractListModel {
	private static final long serialVersionUID = 1L;
	
	List<Player> players;
	
	public PlayersListModel(List<Player> players) {
		this.players = players;
	}
	
	public int getSize() {
		return players.size();
	}
	public Object getElementAt(int index) {
		return players.get(index);
	}
}