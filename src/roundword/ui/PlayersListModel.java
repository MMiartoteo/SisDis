package roundword.ui;

import java.util.List;

import javax.swing.AbstractListModel;
import roundword.Player;

public class PlayersListModel extends AbstractListModel {
	private static final long serialVersionUID = 1L;

	public class PlayerInfo {
		Player player;
		boolean isPlaying;

		public PlayerInfo(Player player, boolean isPlaying) {
			this.player = player;
			this.isPlaying = isPlaying;
		}

		public Player getPlayer() {
			return player;
		}

		public boolean isPlaying() {
			return isPlaying;
		}

		@Override
		public String toString() {
			StringBuilder b = new StringBuilder();
			b.append("<html>");
			if (isPlaying) b.append("<b>");
			b.append(player.toString());
			if (isPlaying) b.append("</b>");
			b.append("</html>");
			return b.toString();
		}
	}
	
	List<Player> players;
	Player playingPlayer;
	
	public PlayersListModel(List<Player> players, Player playingPlayer) {
		this.players = players;
		this.playingPlayer = playingPlayer;
	}
	
	public int getSize() {
		return players.size();
	}

	public Object getElementAt(int index) {
		Player p = players.get(index);
		return new PlayerInfo(p, (playingPlayer == p));
	}

	public void playingPlayerChanges(Player oldPlayingPlayer, Player newPlayingPlayer) {
		this.playingPlayer = newPlayingPlayer;
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i) == oldPlayingPlayer) super.fireContentsChanged(this, i, 1);
			if (players.get(i) == newPlayingPlayer) super.fireContentsChanged(this, i, 1);
		}
	}

	public void elementRefreshed() {
		super.fireContentsChanged(this, 0, getSize());
	}
}