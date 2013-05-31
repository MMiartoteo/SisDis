package roundword.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import roundword.GameTable;
import roundword.Word;

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
		
		JPanel panPlayerInfo = new PlayerInfoPanel(gameTable);
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
		
		JPanel controlsBarContainer = new JPanel();
		controlsBarContainer.setBackground(UIConstants.ControlBarBackgroundColor);
		controlsBarContainer.setBorder(new MatteBorder(1, 0, 0, 0, UIConstants.BordersColor));
		contentPane.add(controlsBarContainer, BorderLayout.SOUTH);
		controlsBarContainer.setLayout(new BorderLayout(0, 0));

		JPanel controlsBar = new JPanel();
		controlsBar.setLayout(new BorderLayout(0, 0));
		controlsBar.setBorder(new EmptyBorder(3, 3, 3, 3));
		controlsBarContainer.add(controlsBar, BorderLayout.CENTER);

		JButton btnExit = new JButton("Esci");
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) { exit(); }
		});
		btnExit.setAlignmentX(Component.CENTER_ALIGNMENT);
		controlsBar.add(btnExit, BorderLayout.EAST);

		pack();
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (dim.width - getSize().width)/2;
		int y = (dim.height - getSize().height)/2;
		setLocation(x, y);

	}

	void exit() {
		this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}

}
