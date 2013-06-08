package roundword.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import roundword.GameTable;
import roundword.Player;
import roundword.Starter;
import roundword.Word;

public class GameFrame extends JFrame implements GameTable.EventListener {
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	
	GameTable gameTable;

	JLabel lblMessages;

	/**
	 * Create the frame.
	 */
	public GameFrame(GameTable gameTable) {
		this.gameTable = gameTable;
		initialize();
		pack();
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		setBackground(UIConstants.BackgroundColor);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		contentPane.setBackground(UIConstants.BackgroundColor);
		
		JPanel infoBar = new JPanel();
		infoBar.setBackground(UIConstants.InfoBarBackgroundColor);
		infoBar.setBorder(new MatteBorder(0, 0, 1, 0, UIConstants.BordersColor));
		contentPane.add(infoBar, BorderLayout.NORTH);
		infoBar.setLayout(new BorderLayout(0, 0));

		PlayerInfoPanel panPlayerInfo = new PlayerInfoPanel(gameTable);
		infoBar.add(panPlayerInfo, BorderLayout.WEST);
		panPlayerInfo.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		TimePanel panTimeInfo = new TimePanel();
		infoBar.add(panTimeInfo);
		panTimeInfo.setAlignmentX(Component.RIGHT_ALIGNMENT);
		panTimeInfo.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0,0));

		GamePanel panGame = new GamePanel(gameTable, panTimeInfo);
		panGame.setBorder(new EmptyBorder(10, 10, 10, 10));
		panel.add(panGame, BorderLayout.CENTER);
		
		JPanel panPlayersInfo = new PlayersListPanel(gameTable);
		contentPane.add(panPlayersInfo, BorderLayout.EAST);
		panPlayersInfo.setAlignmentX(Component.RIGHT_ALIGNMENT);
		
		JPanel controlsBarContainer = new JPanel();
		controlsBarContainer.setBackground(UIConstants.ControlBarBackgroundColor);
		controlsBarContainer.setBorder(new MatteBorder(1, 0, 0, 0, UIConstants.BordersColor));
		contentPane.add(controlsBarContainer, BorderLayout.SOUTH);
		controlsBarContainer.setLayout(new BorderLayout(0, 0));

		JPanel controlsBar = new JPanel();
		controlsBar.setLayout(new BorderLayout(0, 0));
		controlsBar.setBorder(new EmptyBorder(3, 3, 3, 3));
		controlsBarContainer.add(controlsBar, BorderLayout.CENTER);

		lblMessages = new JLabel("");
		lblMessages.setBorder(new EmptyBorder(0, 5, 0, 00));
		controlsBar.add(lblMessages, BorderLayout.WEST);

		JButton btnExit = new JButton("Esci");
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				exit();
			}
		});
		controlsBar.add(btnExit, BorderLayout.EAST);

		pack();
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (dim.width - getSize().width)/2;
		int y = (dim.height - getSize().height)/2;
		setLocation(x, y);

		gameTable.addEventListener(this);

	}

	void exit() {
		this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}

	@Override
	public void newWordAdded(Player p, Word w, long milliseconds, WordAddedState state) {
		if (p == gameTable.getLocalPlayer()) {
			if (state == WordAddedState.TIMEOUT_ELAPSED) {
				lblMessages.setText("<html><div style=\"width: 1000\">Non sei riuscito ad inserire una parola</div></html>");
			} else if (state == WordAddedState.NO_IN_DICTIONARY) {
				lblMessages.setText("<html><div style=\"width: 1000\">Hai inserito una parola sconosciuta</div></html>");
			} else if (state == WordAddedState.SYLLABE_INCORRECT) {
				lblMessages.setText("<html><div style=\"width: 1000\">Hai inserito una parola che non iniziava con l'ultima sillaba della precedente</div></html>");
			} else if (state == WordAddedState.PREVIOUSLY_ADDED) {
				lblMessages.setText("<html><div style=\"width: 1000\">Hai inserito una parola già precedentemente inserita</div></html>");
			} else if (state == WordAddedState.OK) {
				lblMessages.setText("<html><div style=\"width: 1000\">Hai inserito la parola: " + w + "</div></html>");
			} else {
				assert(false);
			}
		} else {
			if (state == WordAddedState.TIMEOUT_ELAPSED) {
				lblMessages.setText("<html><div style=\"width: 1000\"><b>" + p + "</b> non è riuscito ad inserire una parola</div></html>");
			} else if (state == WordAddedState.NO_IN_DICTIONARY) {
				lblMessages.setText("<html><div style=\"width: 1000\"><b>" + p + "</b> ha inserito una parola sconosciuta</div></html>");
			} else if (state == WordAddedState.SYLLABE_INCORRECT) {
				lblMessages.setText("<html><div style=\"width: 1000\"><b>" + p + "</b> ha inserito una parola che non iniziava con l'ultima sillaba della precedente</div></html>");
			} else if (state == WordAddedState.PREVIOUSLY_ADDED) {
				lblMessages.setText("<html><div style=\"width: 1000\"><b>" + p + "</b> ha inserito una parola già precedentemente inserita</div></html>");
			} else if (state == WordAddedState.OK) {
				lblMessages.setText("<html><div style=\"width: 1000\"><b>" + p + "</b> ha inserito la parola: " + w + "</div></html>");
			} else {
				assert(false);
			}
		}
	}

	@Override
	public void playersPointsUpdate() {

	}

	@Override
	public void turnHolderChanged(Player oldTurnHolder, Player newTurnHolder) {
	}

	@Override
	public void gameFinished(final Player winnerPlayer, List<Player> players) {

		final Component c = this;
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				String wStr = "<div style=\"font-size: 40px\">";

				if (winnerPlayer == gameTable.getLocalPlayer()) {
					wStr += "Hai vinto!</div>";
				} else {
					wStr += "Hai perso!</div><br/><div style=\"font-size: 20px\"> Il vincitore è: <b>"
							+ winnerPlayer.toString() + "</b></div>";
				}

				JOptionPane.showMessageDialog(c,
						"<html><div style=\"font-size: 20px; text-align:center\">Gioco terminato<br/>" + wStr + "</div></html>",
						"", JOptionPane.PLAIN_MESSAGE);

				//Exit
				System.exit(0);

				//Back to main menu
				//this.setVisible(false);
				//this.dispose();
				//Starter.startMainMenuGame();
			}
		});
		t.start();

	}

	@Override
	public void failure(final String msg) {
		final Component c = this;
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				JOptionPane.showMessageDialog(c, "Il gioco è stato terminato: " + msg, "", JOptionPane.PLAIN_MESSAGE);

				//Exit
				System.exit(0);
			}
		});
		t.start();
	}
}
