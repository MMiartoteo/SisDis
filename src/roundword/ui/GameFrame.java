package roundword.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import roundword.GameTable;

public class GameFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	
	GameTable gameTable;

	/**
	 * Create the frame.
	 */
	public GameFrame(GameTable gameTable) {
		this.gameTable = gameTable;
		initialize();
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		setBackground(UIConstants.BackgroundColor);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 491, 300);
		contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		contentPane.setBackground(UIConstants.BackgroundColor);
		
		JPanel infoBar = new JPanel();
		infoBar.setBackground(UIConstants.InfoBarBackgroundColor);
		infoBar.setBorder(new MatteBorder(0, 0, 1, 0, UIConstants.BordersColor));
		contentPane.add(infoBar, BorderLayout.NORTH);
		infoBar.setLayout(new BorderLayout(0, 0));
		
		JPanel panPlayerInfo = new PlayerInfoPanel();
		infoBar.add(panPlayerInfo, BorderLayout.WEST);
		panPlayerInfo.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		JPanel panTimeInfo = new TimePanel();
		infoBar.add(panTimeInfo);
		panTimeInfo.setAlignmentX(Component.RIGHT_ALIGNMENT);
		panTimeInfo.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0,0));
		
		JPanel panGame = new GamePanel(gameTable);
		panGame.setBorder(new EmptyBorder(10, 10, 10, 10));
		panel.add(panGame, BorderLayout.CENTER);
		
		JPanel panPlayersInfo = new PlayersListPanel(gameTable);
		contentPane.add(panPlayersInfo, BorderLayout.EAST);
		panPlayersInfo.setAlignmentX(Component.RIGHT_ALIGNMENT);
		
		JPanel controlsBar = new JPanel();
		controlsBar.setBackground(UIConstants.ControlBarBackgroundColor);
		controlsBar.setBorder(new MatteBorder(1, 0, 0, 0, UIConstants.BordersColor));
		contentPane.add(controlsBar, BorderLayout.SOUTH);
		controlsBar.setLayout(new BorderLayout(0, 0));
		
		JButton btnExit = new JButton("Esci");
		btnExit.setAlignmentX(Component.CENTER_ALIGNMENT);
		controlsBar.add(btnExit, BorderLayout.EAST);
	}

}
