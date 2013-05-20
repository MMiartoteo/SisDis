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
import roundword.Word;

public class PlayersListPanel extends JPanel implements GameTable.EventListener {
	private static final long serialVersionUID = 1L;
	
	JList playersList;

	PlayersListModel model;

	public static final Dimension MinimumDimension = new Dimension(150, 100);

	/**
	 * Create the panel.
	 */
	public PlayersListPanel() { this(null); }
	public PlayersListPanel(GameTable gameTable) {
		setLayout(new BorderLayout(0, 0));
		setBackground(UIConstants.BackgroundColor);
		setMinimumSize(MinimumDimension);
		setPreferredSize(MinimumDimension);
		if (gameTable != null) model = new PlayersListModel(gameTable.getPlayersList());
		
		playersList = new JList();
		playersList.setBorder(new MatteBorder(0, 1, 0, 0, UIConstants.BordersColor));
		playersList.setCellRenderer(new PlayersListElement());
		playersList.setBackground(UIConstants.BackgroundColor);
		playersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		if (model != null) playersList.setModel(model);
		add(playersList);

		gameTable.addEventListener(this);

	}
	
	public void setModel(List<Player> players) {
		model = new PlayersListModel(players);
		playersList.setModel(model);
	}

	@Override
	public void newWordAdded(Word w) {

	}

	@Override
	public void playersPointsUpdate() {
		model.elementRefreshed();
	}
}
