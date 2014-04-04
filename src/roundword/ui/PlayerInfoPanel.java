package roundword.ui;

import roundword.GameTable;
import roundword.Player;
import roundword.Word;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.border.EmptyBorder;
import java.awt.Component;
import java.util.List;
import javax.swing.Box;

public class PlayerInfoPanel extends JPanel implements GameTable.EventListener {
	private static final long serialVersionUID = 1L;

	GameTable gameTable;

	JLabel lblPoints;
	JLabel lblNickname;

	/**
	 * Create the panel.
	 */
	public PlayerInfoPanel(GameTable gameTable) {

		this.gameTable = gameTable;
		gameTable.addEventListener(this);

		setBackground(UIConstants.InfoBarBackgroundColor);
		setLayout(new BorderLayout(0, 0));
		
		JLabel lblInformazioniGiocatore = new JLabel("Player Info");
		lblInformazioniGiocatore.setForeground(UIConstants.TextInfoColor);
		lblInformazioniGiocatore.setFont(UIConstants.TextInfoFont);
		add(lblInformazioniGiocatore, BorderLayout.NORTH);
		
		JPanel panel = new JPanel();
		panel.setBackground(UIConstants.InfoBarBackgroundColor);
		add(panel, BorderLayout.CENTER);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		lblNickname = new JLabel(gameTable.getLocalPlayer().toString());
		lblNickname.setForeground(UIConstants.TextColor);
		lblNickname.setFont(UIConstants.TextNormalFont.deriveFont(30f));
		panel.add(lblNickname);
		
		JPanel panelPoints = new JPanel();
		panelPoints.setBackground(UIConstants.InfoBarBackgroundColor);
		panelPoints.setBorder(new EmptyBorder(8, 0, 0, 0));
		panel.add(panelPoints);
		panelPoints.setLayout(new BoxLayout(panelPoints, BoxLayout.X_AXIS));
		
		Component horizontalStrut = Box.createHorizontalStrut(20);
		panelPoints.add(horizontalStrut);
		
		lblPoints = new JLabel(String.valueOf(gameTable.getLocalPlayer().getPoints()));
		lblPoints.setForeground(UIConstants.TextColor);
		lblPoints.setFont(UIConstants.TextNormalFont.deriveFont(16f));
		panelPoints.add(lblPoints);
		
		JLabel lblPointslabel = new JLabel("pt");
		lblPointslabel.setFont(UIConstants.TextNormalFont.deriveFont(15f));
		lblPointslabel.setForeground(UIConstants.TextColor);
		panelPoints.add(lblPointslabel);

	}

	@Override
	public void newWordAdded(Player p, Word w, long milliseconds, WordAddedState state) {

	}

	@Override
	public void playersPointsUpdate() {
		lblPoints.setText(String.valueOf(gameTable.getLocalPlayer().getPoints()));
	}

	@Override
	public void turnHolderChanged(Player oldTurnHolder, Player newTurnHolder) {

	}

	@Override
	public void gameFinished(Player winnerPlayer, List<Player> players) {

	}

	@Override
	public void failure(String msg) {

	}

}
