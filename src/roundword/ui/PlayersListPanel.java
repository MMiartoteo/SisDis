package roundword.ui;

import javax.swing.JPanel;
import javax.swing.JList;
import javax.swing.AbstractListModel;
import java.awt.BorderLayout;
import javax.swing.ListSelectionModel;
import java.awt.Dimension;
import java.util.List;

import javax.swing.border.MatteBorder;

import roundword.GameTable;
import roundword.Player;

public class PlayersListPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	JList playersList;
	
	AbstractListModel model;

	/**
	 * Create the panel.
	 */
	public PlayersListPanel() { this(null); }
	public PlayersListPanel(GameTable gameTable) {
		setPreferredSize(new Dimension(200, 300));
		setLayout(new BorderLayout(0, 0));
		setBackground(UIConstants.BackgroundColor);
		
		playersList = new JList();
		playersList.setBorder(new MatteBorder(0, 1, 0, 0, UIConstants.BordersColor));
		playersList.setCellRenderer(new PlayersListElement());
		playersList.setBackground(UIConstants.BackgroundColor);
		playersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		if (gameTable != null) playersList.setModel(new PlayersListModel(gameTable.getPlayersList()));
		add(playersList);

	}
	
	public void setModel(List<Player> players) {
		model = new PlayersListModel(players);
		playersList.setModel(model);
	}

}
